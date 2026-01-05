package dev.thource.runelite.dudewheresmystuff.death;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemStackUtils;
import dev.thource.runelite.dudewheresmystuff.Region;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.Var;
import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageManager;
import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageType;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageManager;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.MenuAction;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.Text;

/** DeathStorageManager is responsible for managing all DeathStorages. */
@Slf4j
public class DeathStorageManager extends StorageManager<DeathStorageType, DeathStorage> {

  private static final Set<Integer> RESPAWN_REGIONS =
      ImmutableSet.of(
          6457, // Kourend
          12850, // Lumbridge
          11828, // Falador
          12342, // Edgeville
          11062, // Camelot
          13150, // Prifddinas (it's possible to spawn in 2 adjacent regions)
          12894, // Prifddinas
          14642, // ToB
          12172, // Gauntlet
          12633, // death's office
          12600, // Ferox
          6705, // Civitas illa Fortis
          7316, // Colosseum lobby
          5789, // Chasm of Fire (Yama)
          5269 // Doom
          );
  private static final Set<Region> SAFE_DEATH_REGIONS =
      ImmutableSet.of(
          Region.REGION_POH,
          Region.MG_LAST_MAN_STANDING_DESERTED_ISLAND,
          Region.MG_LAST_MAN_STANDING_WILD_VARROCK);
  private final CheckPlayTimeInfoBox playTimeInfoBox = new CheckPlayTimeInfoBox(plugin);
  private final List<ExpiringDeathStorageInfoBox> expiringDeathStorageInfoBoxes = new ArrayList<>();
  @Getter private final DeathsOffice deathsOffice;
  long startMs = 0L;
  @Getter @Nullable private Deathbank deathbank = null;
  @Getter private final DeathItems deathItemsStorage;
  @Getter @Nullable private Grave grave = null;
  @Setter private CarryableStorageManager carryableStorageManager;
  @Setter private CoinsStorageManager coinsStorageManager;
  @Inject private WorldMapPointManager worldMapPointManager;
  @Getter @Setter private int startPlayedMinutes = -1;
  private DyingState dyingState = DyingState.NOT_DYING;
  private WorldArea deathPileArea;
  private List<ItemStack> deathItems;
  private DeathbankInfoBox deathbankInfoBox;
  private int entryModeTob; // 1 = entering entry mode, 2 = entry mode
  private final List<SuspendedGroundItem> itemsPickedUp = new ArrayList<>();

  @Inject
  private DeathStorageManager(DudeWheresMyStuffPlugin plugin) {
    super(plugin);

    playTimeInfoBox.setTooltip(
        "Navigate to the quest tab and swap to</br>the Character Summary tab"
            + " (brown star) to</br>track cross-client deathpiles.");

    deathItemsStorage = new DeathItems(plugin, this);
    storages.add(deathItemsStorage);
    deathsOffice = new DeathsOffice(plugin);
    storages.add(deathsOffice);
  }

  @Override
  public void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    if (!enabled) {
      return;
    }

    if (itemContainerChanged.getContainerId() == InventoryID.GRAVESTONE) {
      if (client.getWidget(672, 0) == null) {
        updateDeathbankItems(itemContainerChanged.getItemContainer().getItems());

        SwingUtilities.invokeLater(
            () ->
                plugin
                    .getClientThread()
                    .invoke(() -> updateStorages(Collections.singletonList(deathbank))));
      } else {
        updateGraveItems(itemContainerChanged.getItemContainer().getItems());

        SwingUtilities.invokeLater(
            () ->
                plugin
                    .getClientThread()
                    .invoke(() -> updateStorages(Collections.singletonList(grave))));
      }
    }
  }

  @Override
  public void onVarbitChanged(VarbitChanged varbitChanged) {
    var durationVar = Var.bit(varbitChanged, VarbitID.GRAVESTONE_DURATION);
    if (!durationVar.wasChanged()) {
      return;
    }

    if (grave == null) {
      if (durationVar.getValue(plugin.getClient()) > 0
          && client.getBoostedSkillLevel(Skill.HITPOINTS) > 0) {
        createMysteryGrave();
      }

      return;
    }

    if (grave.hasExpired()) {
      grave = null;
    }
  }

  private void updateGraveItems(Item[] items) {
    if (grave == null) {
      createMysteryGrave();
    }

    grave.getItems().clear();
    for (Item item : items) {
      if (item.getId() == -1) {
        continue;
      }

      grave.getItems().add(new ItemStack(item.getId(), item.getQuantity(), plugin));
    }
  }

  private void updateDeathbankItems(Item[] items) {
    int deathbankVarp = client.getVarpValue(VarPlayerID.IF1);
    DeathbankType deathbankType =
        Arrays.stream(DeathbankType.values())
            .filter(
                s ->
                    s.getDeathBankLockedState() == deathbankVarp
                        || s.getDeathBankUnlockedState() == deathbankVarp)
            .findFirst()
            .orElse(DeathbankType.UNKNOWN);

    if (deathbank == null) {
      deathbank = new Deathbank(deathbankType, plugin, this);
      storages.add(deathbank);
      SwingUtilities.invokeLater(() -> deathbank.createStoragePanel(this));
    } else {
      deathbank.setDeathbankType(deathbankType);
      deathbank.getItems().clear();
    }

    deathbank.setLocked(deathbankType.getDeathBankLockedState() == deathbankVarp);
    deathbank.setLastUpdated(System.currentTimeMillis());

    for (Item item : items) {
      if (item.getId() == -1) {
        continue;
      }

      deathbank.getItems().add(new ItemStack(item.getId(), item.getQuantity(), plugin));
    }
  }

  private boolean updateFromWatchers() {
    var updated = updateDeathbankFromWatcher(ItemContainerWatcher.getInventoryWatcher());

    if (plugin.getClient().getVarbitValue(VarbitID.SETTINGS_GRAVESTONE_AUTOEQUIP) == 1
        && updateDeathbankFromWatcher(ItemContainerWatcher.getWornWatcher())) {
      updated = true;
    }

    return updated;
  }

  private boolean updateDeathbankFromWatcher(ItemContainerWatcher watcher) {
    boolean updated = false;

    if (deathbank == null
        || deathbank.getDeathbankType() != DeathbankType.ZULRAH
        || client.getLocalPlayer() == null
        || Region.get(client.getLocalPlayer().getWorldLocation().getRegionID())
            != Region.CITY_ZULANDRA) {
      return false;
    }

    var itemsAddedLastTick = watcher.getItemsAddedLastTick();
    removeItemsFromList(deathbank.getItems(), itemsAddedLastTick);

    if (!itemsAddedLastTick.isEmpty()) {
      deathbank.setLastUpdated(System.currentTimeMillis());
      updated = true;
    }

    if (deathbank.getItems().isEmpty()) {
      clearDeathbank(false);
    }

    return updated;
  }

  private void removeItemsFromList(
      List<ItemStack> listToRemoveFrom, List<ItemStack> itemsToRemove) {
    for (ItemStack itemToRemove : itemsToRemove) {
      ItemStackUtils.removeItemStack(listToRemoveFrom, itemToRemove, false);
    }
  }

  private void removeItemsFromList(List<ItemStack> listToRemoveFrom, Item[] itemsToRemove) {
    for (Item item : itemsToRemove) {
      ItemStackUtils.removeItemStack(
          listToRemoveFrom, new ItemStack(item.getId(), "", item.getQuantity(), 0, 0, true), false);
    }
  }

  void clearDeathbank(boolean wasLost) {
    if (deathbank != null) {
      if (wasLost) {
        deathbank.setLostAt(System.currentTimeMillis());
      } else {
        storages.remove(deathbank);
        deathbank.deleteData(this);
      }
    }

    deathbank = null;
  }

  @Override
  public void onGameStateChanged(GameStateChanged gameStateChanged) {
    if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN
        || gameStateChanged.getGameState() == GameState.HOPPING) {
      startPlayedMinutes = -1;
      startMs = 0L;
    }
  }

  // the use of | instead of || is not an accident, each function should be executed
  @SuppressWarnings("java:S2178")
  @Override
  public void onGameTick() {
    super.onGameTick();

    updateStartPlayedMinutes();

    if (dyingState == DyingState.TICK_1) {
      dyingState = DyingState.TICK_2;
    } else if (dyingState == DyingState.TICK_2) {
      dyingState = DyingState.TICK_3;
    } else if (dyingState == DyingState.TICK_3) {
      dyingState = DyingState.RECORDING_DATA;
    } else if (dyingState == DyingState.RECORDING_DATA) {
      WorldArea worldArea =
          RemoteDeathpileAreas.getPileArea(
              client,
              WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()));
      List<ItemStack> items =
          getDeathItems().stream()
              .filter(itemStack -> itemStack.getId() != -1)
              .collect(Collectors.toList());

      if (items.isEmpty()) {
        dyingState = DyingState.NOT_DYING;
      } else {
        dyingState = DyingState.WAITING_FOR_RESPAWN;
        deathPileArea = worldArea;
        deathItems = items;
      }
    }

    if ((deathbank != null && checkIfDeathbankWindowIsEmpty())
        | processDeath()
        | checkItemsLostOnDeathWindow()
        | updateFromWatchers()) {

      SwingUtilities.invokeLater(
          () -> plugin.getClientThread().invoke(() -> updateStorages(storages)));
    }

    refreshInfoBoxes();
    updateWorldMapPoints();

    if (entryModeTob > 0) {
      WorldPoint location =
          WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
      Region region = Region.get(location.getRegionID());
      if (entryModeTob == 1) {
        if (region == Region.RAIDS_THEATRE_OF_BLOOD) {
          entryModeTob = 2;
        }
      } else if (entryModeTob == 2) {
        if (region != Region.RAIDS_THEATRE_OF_BLOOD) {
          entryModeTob = 0;
        }
      }
    }

    var listIterator = itemsPickedUp.listIterator();
    while (listIterator.hasNext()) {
      var item = listIterator.next();

      if (item.getTicksLeft() <= 1) {
        listIterator.remove();
      } else {
        item.setTicksLeft(item.getTicksLeft() - 1);
      }
    }
  }

  public Deathpile getSoonestExpiringDeathpile() {
    return getDeathpiles()
        .filter(deathpile -> !deathpile.hasExpired())
        .min(Comparator.comparing(Deathpile::getExpiryMs))
        .orElse(null);
  }

  private boolean checkIfDeathbankWindowIsEmpty() {
    Widget itemWindow = client.getWidget(602, 3);
    // This checks if the item collection window has been emptied while it was open
    if (itemWindow != null
        && !itemWindow.isHidden()
        && client.getVarpValue(VarPlayerID.IF1) == -1) {
      clearDeathbank(false);
      return true;
    }

    return false;
  }

  private void updateStartPlayedMinutes() {
    int playedMinutes = client.getVarcIntValue(526);
    if (playedMinutes != startPlayedMinutes) {
      if (startPlayedMinutes == -1) {
        refreshMapPoints();
      }

      SwingUtilities.invokeLater(storageTabPanel::reorderStoragePanels);

      startPlayedMinutes = playedMinutes;
      startMs = System.currentTimeMillis();

      if (startPlayedMinutes > 0) {
        getExpiringDeathStorages()
            .filter(ExpiringDeathStorage::isUseAccountPlayTime)
            .filter(storage -> storage.getStoragePanel() != null)
            .forEach(storage -> storage.getStoragePanel().getFooterLabel().setToolTipText(null));
      }
    }

    if (startPlayedMinutes > 0) {
      Integer savedPlayedMinutes =
          configManager.getRSProfileConfiguration(
              DudeWheresMyStuffConfig.CONFIG_GROUP, "minutesPlayed", int.class);
      if (savedPlayedMinutes == null || savedPlayedMinutes != getPlayedMinutes()) {
        configManager.setRSProfileConfiguration(
            DudeWheresMyStuffConfig.CONFIG_GROUP, "minutesPlayed", getPlayedMinutes());
      }
    }
  }

  Stream<Deathpile> getDeathpiles() {
    return storages.stream().filter(Deathpile.class::isInstance).map(Deathpile.class::cast);
  }

  Stream<ExpiringDeathStorage> getExpiringDeathStorages() {
    return storages.stream()
        .filter(ExpiringDeathStorage.class::isInstance)
        .map(ExpiringDeathStorage.class::cast);
  }

  /** Checks if any infoboxes need adding or removing. */
  public void refreshInfoBoxes() {
    refreshCheckPlayTimeInfoBox();
    refreshDeathbankInfoBox();
    refreshExpiringDeathStorageInfoBoxes();
  }

  private void pruneExpiringDeathStorageInfoBoxes(
      List<ExpiringDeathStorage> activeExpiringDeathStorages,
      InfoBoxManager infoBoxManager,
      List<InfoBox> currentInfoBoxes) {
    ListIterator<ExpiringDeathStorageInfoBox> iterator =
        expiringDeathStorageInfoBoxes.listIterator();
    while (iterator.hasNext()) {
      ExpiringDeathStorageInfoBox infoBox = iterator.next();

      if (!activeExpiringDeathStorages.contains(infoBox.getStorage())) {
        if (currentInfoBoxes.contains(infoBox)) {
          infoBoxManager.removeInfoBox(infoBox);
        }

        iterator.remove();
      }
    }
  }

  private void refreshInfoBoxImage(ExpiringDeathStorageInfoBox infoBox) {
    if (!infoBox.isImageDirty()) {
      return;
    }

    InfoBoxManager infoBoxManager = plugin.getInfoBoxManager();
    List<InfoBox> currentInfoBoxes = infoBoxManager.getInfoBoxes();
    if (currentInfoBoxes.contains(infoBox)) {
      infoBoxManager.updateInfoBoxImage(infoBox);
    }

    infoBox.setImageDirty(false);
  }

  private void refreshExpiringDeathStorageInfoBoxes() {
    InfoBoxManager infoBoxManager = plugin.getInfoBoxManager();
    List<InfoBox> currentInfoBoxes = infoBoxManager.getInfoBoxes();
    List<ExpiringDeathStorage> activeExpiringDeathStorages =
        getExpiringDeathStorages()
            .filter(storage -> !storage.hasExpired())
            .collect(Collectors.toList());

    pruneExpiringDeathStorageInfoBoxes(
        activeExpiringDeathStorages, infoBoxManager, currentInfoBoxes);

    activeExpiringDeathStorages.forEach(
        storage -> {
          if (expiringDeathStorageInfoBoxes.stream()
              .noneMatch(infoBox -> infoBox.getStorage() == storage)) {
            ExpiringDeathStorageInfoBox infoBox;
            if (storage instanceof Deathpile) {
              infoBox = new DeathpileInfoBox(plugin, (Deathpile) storage);
            } else {
              infoBox = new GraveInfoBox(plugin, (Grave) storage);
            }

            expiringDeathStorageInfoBoxes.add(infoBox);
          }
        });

    for (ExpiringDeathStorageInfoBox infoBox : expiringDeathStorageInfoBoxes) {
      if (plugin.getConfig().deathpileInfoBox()) {
        infoBox.refreshTooltip();

        refreshInfoBoxImage(infoBox);

        if (!currentInfoBoxes.contains(infoBox)) {
          infoBoxManager.addInfoBox(infoBox);
        }
      } else if (currentInfoBoxes.contains(infoBox)) {
        infoBoxManager.removeInfoBox(infoBox);
      }
    }
  }

  private void refreshDeathbankInfoBox() {
    boolean showInfoBox = deathbank != null && plugin.getConfig().deathbankInfoBox();
    boolean hasDeathbankChanged =
        (showInfoBox && deathbankInfoBox == null)
            || (deathbankInfoBox != null && deathbankInfoBox.getDeathbank() != deathbank);
    List<InfoBox> infoBoxes = plugin.getInfoBoxManager().getInfoBoxes();

    if (deathbankInfoBox != null
        && (!showInfoBox || hasDeathbankChanged)
        && infoBoxes.contains(deathbankInfoBox)) {
      plugin.getInfoBoxManager().removeInfoBox(deathbankInfoBox);
    }

    if (hasDeathbankChanged) {
      deathbankInfoBox = deathbank == null ? null : new DeathbankInfoBox(plugin, deathbank);
    }

    if (showInfoBox && !infoBoxes.contains(deathbankInfoBox)) {
      plugin.getInfoBoxManager().addInfoBox(deathbankInfoBox);
    }
  }

  private boolean doesAnyActiveExpiringDeathStorageUseAccountPlayTime() {
    return getExpiringDeathStorages()
        .filter(storage -> !storage.hasExpired())
        .anyMatch(ExpiringDeathStorage::isUseAccountPlayTime);
  }

  private void refreshCheckPlayTimeInfoBox() {
    boolean showInfoBox =
        startPlayedMinutes <= 0
            && (plugin.getConfig().deathpilesUseAccountPlayTime()
                || doesAnyActiveExpiringDeathStorageUseAccountPlayTime());
    boolean isAdded = plugin.getInfoBoxManager().getInfoBoxes().contains(playTimeInfoBox);

    if (!showInfoBox && isAdded) {
      plugin.getInfoBoxManager().removeInfoBox(playTimeInfoBox);
    } else if (showInfoBox && !isAdded) {
      plugin.getInfoBoxManager().addInfoBox(playTimeInfoBox);
    }
  }

  @Override
  public void onChatMessage(ChatMessage chatMessage) {
    super.onChatMessage(chatMessage);

    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return;
    }

    String message = Text.removeTags(chatMessage.getMessage());
    if (message.startsWith("You enter the Theatre of Blood (Entry Mode)")) {
      entryModeTob = 1;
      return;
    }

    if (dyingState != DyingState.NOT_DYING) {
      Region region = Region.get(deathPileArea.toWorldPoint().getRegionID());
      if (region == Region.RAIDS_THEATRE_OF_BLOOD
          && message.startsWith("You feel refreshed as your health is replenished")) {
        dyingState = DyingState.NOT_DYING;
        deathPileArea = null;
        deathItems = null;
        return;
      }
    }

    if (deathbank != null
        || !message.startsWith("You have items stored in an item retrieval service.")) {
      return;
    }

    String finalMessage = message.replace(" ", "");
    DeathbankType deathbankType =
        Arrays.stream(DeathbankType.values())
            .filter(type -> type.getDeathWindowLocationText() != null)
            .filter(type -> finalMessage.contains(type.getDeathWindowLocationText()))
            .findFirst()
            .orElse(DeathbankType.UNKNOWN);

    if (deathbankType == DeathbankType.UNKNOWN && client.getVarbitValue(VarbitID.IRONMAN) != 2) {
      createMysteryGrave();
      updateStorages(Collections.singletonList(grave));
      return;
    }

    createMysteryDeathbank(deathbankType);
    updateStorages(Collections.singletonList(deathbank));
  }

  private boolean checkItemsLostOnDeathWindow() {
    if (deathbank == null) {
      Widget deathbankTextWidget = client.getWidget(4, 3);
      if (deathbankTextWidget != null) {
        Widget textWidget = deathbankTextWidget.getChild(3);
        if (textWidget != null) {
          String deathbankText = Text.removeTags(textWidget.getText()).replace(" ", "");

          // check for unsafe death message
          //noinspection SpellCheckingInspection
          if (deathbankText.contains("theywillbedeleted")) {
            DeathbankType type =
                Arrays.stream(DeathbankType.values())
                    .filter(
                        t ->
                            t.getDeathWindowLocationText() != null
                                && deathbankText.contains(t.getDeathWindowLocationText()))
                    .findFirst()
                    .orElse(DeathbankType.UNKNOWN);

            createMysteryDeathbank(type);

            return true;
          }
        }
      }
    }

    return false;
  }

  private void createMysteryGrave() {
    if (grave != null) {
      return;
    }

    grave = new Grave(plugin, null, this, new ArrayList<>());
    storages.add(grave);
    grave.getItems().add(new ItemStack(ItemID.MACRO_QUIZ_MYSTERY_BOX, 1, plugin));

    SwingUtilities.invokeLater(
        () -> {
          grave.createStoragePanel(this);

          plugin.getClientThread().invoke(() -> updateStorages(Collections.singletonList(grave)));
        });
  }

  void createMysteryDeathbank(DeathbankType type) {
    deathbank = new Deathbank(type, plugin, this);
    storages.add(deathbank);
    deathbank.setLastUpdated(System.currentTimeMillis());
    deathbank.setLocked(
        type != DeathbankType.ZULRAH || client.getVarbitValue(VarbitID.IRONMAN) != 2); // not uim
    deathbank.getItems().add(new ItemStack(ItemID.MACRO_QUIZ_MYSTERY_BOX, 1, plugin));

    SwingUtilities.invokeLater(
        () -> {
          deathbank.createStoragePanel(this);

          plugin
              .getClientThread()
              .invoke(() -> updateStorages(Collections.singletonList(deathbank)));
        });
  }

  @Override
  public void updateStorages(List<? extends DeathStorage> storages) {
    if (!storages.isEmpty()) {
      storages.forEach(
          storage -> {
            if (storage.getStoragePanel() != null) {
              storage.getStoragePanel().refreshItems();
            }
          });

      SwingUtilities.invokeLater(
          () ->
              storages.forEach(
                  storage -> {
                    if (storage.getStoragePanel() != null) {
                      storage.getStoragePanel().update();
                    }
                  }));

      SwingUtilities.invokeLater(storageTabPanel::reorderStoragePanels);
    }
  }

  private void updateWorldMapPoints() {
    if (storages.stream()
        .filter(Deathpile.class::isInstance)
        .map(Deathpile.class::cast)
        .anyMatch(
            deathpile -> {
              if (deathpile.worldMapPoint == null) {
                return !deathpile.hasExpired();
              }
              if (deathpile.hasExpired()) {
                return true;
              }
              if (deathpile.worldMapPoint.getTooltip() == null) {
                return false;
              }

              deathpile.worldMapPoint.setTooltip("Deathpile (" + deathpile.getExpireText() + ")");
              return false;
            })) {
      refreshMapPoints();
    }
  }

  private boolean processDeath() {
    if (client.getLocalPlayer() == null
        || dyingState != DyingState.WAITING_FOR_RESPAWN
        || client.getBoostedSkillLevel(Skill.HITPOINTS) < 10) {
      return false;
    }

    boolean updated = false;

    Region deathPileRegion = Region.get(deathPileArea.toWorldPoint().getRegionID());

    if (!RESPAWN_REGIONS.contains(client.getLocalPlayer().getWorldLocation().getRegionID())) {
      // Player has died but is still safe unless their team dies
      if (deathPileRegion == Region.RAIDS_THEATRE_OF_BLOOD) {
        return false;
      }

      log.info(
          "Died, but did not respawn in a known respawn location: "
              + client.getLocalPlayer().getWorldLocation().getRegionID());
    } else if (!SAFE_DEATH_REGIONS.contains(deathPileRegion)) {
      updated = true;
      registerDeath(deathPileRegion);
    }

    dyingState = DyingState.NOT_DYING;
    deathPileArea = null;
    deathItems = null;

    return updated;
  }

  private void registerDeath(Region deathRegion) {
    clearDeathbank(true);

    // Don't create deathpiles/graves for gauntlet deaths
    if (deathRegion == Region.MG_CORRUPTED_GAUNTLET || deathRegion == Region.MG_GAUNTLET) {
      return;
    }

    clearCarryableStorage();

    // Remove any items that were kept on death from the death items
    Stream.of(InventoryID.INV, InventoryID.WORN)
        .map(id -> client.getItemContainer(id))
        .filter(Objects::nonNull)
        .forEach(i -> removeItemsFromList(deathItems, i.getItems()));

    Optional<DeathbankType> deathbankType = getDeathbankType(deathRegion);
    if (deathbankType.isPresent()) {
      deathbank = new Deathbank(deathbankType.get(), plugin, this);
      storages.add(deathbank);
      SwingUtilities.invokeLater(() -> deathbank.createStoragePanel(this));
      deathbank.setLastUpdated(System.currentTimeMillis());
      deathbank.setLocked(
          deathbankType.get() != DeathbankType.ZULRAH
              || client.getVarbitValue(VarbitID.IRONMAN) != 2); // not uim
      deathbank.getItems().addAll(deathItems);
    } else if (client.getVarbitValue(VarbitID.IRONMAN) == 2) { // uim
      createDeathpile(deathPileArea, deathItems);
    } else {
      createGrave(deathPileArea, deathItems);
    }

    refreshMapPoints();
  }

  private void clearCarryableStorage() {
    coinsStorageManager.getStorages().stream()
        .filter(s -> s.getType() == CoinsStorageType.LOOTING_BAG)
        .forEach(
            s -> {
              s.getCoinStack().setQuantity(0);

              if (s.getStoragePanel() != null) {
                s.getStoragePanel().refreshItems();
                SwingUtilities.invokeLater(() -> s.getStoragePanel().update());
              }
            });
    SwingUtilities.invokeLater(coinsStorageManager.getStorageTabPanel()::reorderStoragePanels);

    carryableStorageManager.getStorages().stream()
        .filter(
            s ->
                s.getType() == CarryableStorageType.LOOTING_BAG
                    || (s.getType().getEmptyOnDeathVarbit() != -1
                        && client.getVarbitValue(s.getType().getEmptyOnDeathVarbit()) == 1))
        .forEach(
            s -> {
              s.getItems().clear();
              if (s.getStoragePanel() != null) {
                s.getStoragePanel().refreshItems();
                SwingUtilities.invokeLater(() -> s.getStoragePanel().update());
              }
            });
    SwingUtilities.invokeLater(carryableStorageManager.getStorageTabPanel()::reorderStoragePanels);
  }

  Deathpile createDeathpile(WorldArea area, List<ItemStack> items) {
    boolean useAccountPlayTime = deathpilesUseAccountPlayTime();
    Deathpile deathpile = new Deathpile(plugin, useAccountPlayTime, area, this, items);
    SwingUtilities.invokeLater(() -> deathpile.createStoragePanel(this));
    storages.add(deathpile);

    return deathpile;
  }

  Grave createGrave(WorldArea area, List<ItemStack> items) {
    // If the player already has a grave, their items get added to it and the timer restarts
    if (grave != null) {
      grave.getItems().addAll(items);
      updateStorages(Collections.singletonList(grave));

      return grave;
    }

    grave = new Grave(plugin, area, this, items);
    SwingUtilities.invokeLater(() -> grave.createStoragePanel(this));
    storages.add(grave);

    return grave;
  }

  private Optional<DeathbankType> getDeathbankType(Region deathRegion) {
    if (deathRegion == null) {
      return Optional.empty();
    }

    if (deathRegion == Region.BOSS_VORKATH) {
      return Optional.of(
          Quest.DRAGON_SLAYER_II.getState(client) == QuestState.IN_PROGRESS
              ? DeathbankType.QUEST_DS2
              : DeathbankType.VORKATH);
    } else if (deathRegion == Region.BOSS_NIGHTMARE) {
      // TODO: work out how to differentiate between nightmare and phosani's
      return Optional.of(DeathbankType.NIGHTMARE);
    }

    return Arrays.stream(DeathbankType.values())
        .filter(s -> s.getRegion() == deathRegion)
        .findFirst();
  }

  @Override
  public void onActorDeath(ActorDeath actorDeath) {
    if (client.getLocalPlayer() == null
        || actorDeath.getActor() != client.getLocalPlayer()
        || entryModeTob == 2) {
      return;
    }

    dyingState = DyingState.TICK_1;
  }

  @Override
  public void onMenuOptionClicked(MenuOptionClicked menuOption) {
    if (menuOption.getMenuAction() != MenuAction.GROUND_ITEM_THIRD_OPTION
        && menuOption.getMenuAction() != MenuAction.WIDGET_TARGET_ON_GROUND_ITEM) {
      return;
    }

    var worldView = client.getTopLevelWorldView();
    if (worldView == null) {
      return;
    }

    var worldPoint =
        WorldPoint.fromScene(
            worldView, menuOption.getParam0(), menuOption.getParam1(), worldView.getPlane());
    getDeathpiles()
        .filter(deathpile -> !deathpile.hasExpired() && deathpile.getWorldPoint() != null)
        .filter(deathpile -> deathpile.getWorldPoint().equals(worldPoint))
        .findFirst()
        .ifPresent(
            (dp) -> {
              var suspendedGroundItem = new SuspendedGroundItem(menuOption.getId(), worldPoint);
              suspendedGroundItem.setTicksLeft(100);

              for (SuspendedGroundItem i : itemsPickedUp) {
                if (i.getTicksLeft() > 2) {
                  i.setTicksLeft(2);
                }
              }

              itemsPickedUp.add(suspendedGroundItem);
            });
  }

  @Override
  public void onItemDespawned(ItemDespawned itemDespawned) {
    var worldPoint = itemDespawned.getTile().getWorldLocation();
    var despawnedItem = itemDespawned.getItem();

    if (itemsPickedUp.stream()
        .noneMatch(
            i -> i.getWorldPoint().equals(worldPoint) && i.getId() == despawnedItem.getId())) {
      return;
    }

    itemsPickedUp.stream()
        .filter(i -> i.getId() == despawnedItem.getId())
        .forEach(i -> {
          if (i.getTicksLeft() > 2) {
            i.setTicksLeft(2);
          }
        });

    var updatedDeathpiles = removeFromDeathpiles(despawnedItem, worldPoint);
    if (updatedDeathpiles.isEmpty()) {
      return;
    }

    var iterator = updatedDeathpiles.listIterator();
    while (iterator.hasNext()) {
      var deathpile = iterator.next();

      if (deathpile.getItems().isEmpty()) {
        deathpile.deleteData(this);
        storages.remove(deathpile);
        iterator.remove();
      }
    }

    if (updatedDeathpiles.isEmpty()) {
      SwingUtilities.invokeLater(storageTabPanel::reorderStoragePanels);
    } else {
      updateStorages(updatedDeathpiles);
    }
  }

  /**
   * Removes the specified TileItem from all deathpiles on the specified WorldPoint.
   *
   * @param item the TileItem to remove from the deathpiles
   * @param worldPoint the WorldPoint that the deathpiles must be on
   * @return a list of deathpiles that were affected
   */
  private List<Deathpile> removeFromDeathpiles(TileItem item, WorldPoint worldPoint) {
    AtomicLong quantityToRemove = new AtomicLong(item.getQuantity());
    if (quantityToRemove.get() == 65535) {
      quantityToRemove.set(Long.MAX_VALUE);
    }

    return getDeathpiles()
        .filter(deathpile -> !deathpile.hasExpired() && deathpile.getWorldPoint() != null)
        .filter(deathpile -> deathpile.getWorldPoint().equals(worldPoint))
        .filter(
            deathpile -> {
              long itemsRemoved = deathpile.remove(item.getId(), quantityToRemove.get());
              quantityToRemove.addAndGet(-itemsRemoved);

              return itemsRemoved > 0;
            })
        .collect(Collectors.toList());
  }

  @Override
  public String getConfigKey() {
    return "death";
  }

  int getPlayedMinutes() {
    if (isPreviewManager) {
      return startPlayedMinutes;
    }

    return (int) (startPlayedMinutes + ((System.currentTimeMillis() - startMs) / 60000));
  }

  List<ItemStack> getDeathItems() {
    List<ItemStack> itemStacks =
        carryableStorageManager.getStorages().stream()
            .filter(
                s ->
                    s.getType() == CarryableStorageType.INVENTORY
                        || s.getType() == CarryableStorageType.EQUIPMENT)
            .sorted(
                Comparator.comparingInt(s -> s.getType() == CarryableStorageType.INVENTORY ? 0 : 1))
            .flatMap(s -> s.getItems().stream())
            .collect(Collectors.toList());

    return ItemStackUtils.compound(
        ItemStackUtils.filterDestroyedOnDeath(
                ItemStackUtils.explodeStorageItems(itemStacks, carryableStorageManager))
            .stream()
            .filter(i -> i.getId() != -1 && i.getQuantity() > 0)
            .collect(Collectors.toList()),
        false);
  }

  @Override
  public void load(String profileKey) {
    if (!enabled) {
      return;
    }

    loadDeathpiles(profileKey);
    loadGraves(profileKey);
    loadDeathbanks(profileKey);
    deathsOffice.load(configManager, getConfigKey(), profileKey);
  }

  @Override
  public void reset() {
    storages.removeIf(s -> s instanceof ExpiringDeathStorage || s instanceof Deathbank);
    deathbank = null;
    enable();
    refreshMapPoints();
  }

  void refreshMapPoints() {
    if (worldMapPointManager == null || isPreviewManager) {
      return;
    }

    worldMapPointManager.removeIf(DeathWorldMapPoint.class::isInstance);

    AtomicInteger index = new AtomicInteger(1);
    storages.stream()
        .filter(Deathpile.class::isInstance)
        .map(Deathpile.class::cast)
        .filter(deathpile -> !deathpile.hasExpired())
        .forEach(
            deathpile -> {
              var worldPoint =
                  deathpile.getWorldPoint() != null
                      ? deathpile.getWorldPoint()
                      : deathpile.getWorldArea().toWorldPoint();
              deathpile.worldMapPoint =
                  new DeathWorldMapPoint(worldPoint, itemManager, index.getAndIncrement());
              worldMapPointManager.add(deathpile.getWorldMapPoint());
            });
  }

  private void loadDeathpiles(String profileKey) {
    for (String configurationKey :
        configManager.getRSProfileConfigurationKeys(
            DudeWheresMyStuffConfig.CONFIG_GROUP,
            profileKey,
            getConfigKey() + "." + DeathStorageType.DEATHPILE.getConfigKey() + ".")) {
      Deathpile deathpile =
          Deathpile.load(plugin, this, profileKey, configurationKey.split("\\.")[2]);
      SwingUtilities.invokeLater(
          () -> {
            deathpile.createStoragePanel(this);

            if (deathpile.getStoragePanel() != null) {
              plugin
                  .getClientThread()
                  .invoke(
                      () -> {
                        deathpile.getStoragePanel().refreshItems();
                        SwingUtilities.invokeLater(() -> deathpile.getStoragePanel().update());
                      });
            }
          });
      storages.add(deathpile);
    }

    refreshMapPoints();
  }

  private void loadGraves(String profileKey) {
    for (String configurationKey :
        configManager.getRSProfileConfigurationKeys(
            DudeWheresMyStuffConfig.CONFIG_GROUP,
            profileKey,
            getConfigKey() + "." + DeathStorageType.GRAVE.getConfigKey() + ".")) {
      Grave loadedGrave = Grave.load(plugin, this, profileKey, configurationKey.split("\\.")[2]);
      SwingUtilities.invokeLater(
          () -> {
            loadedGrave.createStoragePanel(this);

            if (loadedGrave.getStoragePanel() != null) {
              plugin
                  .getClientThread()
                  .invoke(
                      () -> {
                        loadedGrave.getStoragePanel().refreshItems();
                        SwingUtilities.invokeLater(() -> loadedGrave.getStoragePanel().update());
                      });
            }
          });
      if (!loadedGrave.hasExpired()) {
        if (grave != null) {
          if (grave.getExpiryMs() < loadedGrave.getExpiryMs()) {
            grave.expire();
            grave = loadedGrave;
          } else {
            loadedGrave.expire();
          }
        } else {
          grave = loadedGrave;
        }
      }
      storages.add(loadedGrave);
    }

    refreshMapPoints();
  }

  private void loadDeathbanks(String profileKey) {
    for (String configurationKey :
        configManager.getRSProfileConfigurationKeys(
            DudeWheresMyStuffConfig.CONFIG_GROUP,
            profileKey,
            getConfigKey() + "." + DeathStorageType.DEATHBANK.getConfigKey() + ".")) {
      Deathbank loadedDeathbank =
          Deathbank.load(plugin, this, profileKey, configurationKey.split("\\.")[2]);

      if (loadedDeathbank == null) {
        continue;
      }

      SwingUtilities.invokeLater(() -> loadedDeathbank.createStoragePanel(this));
      if (loadedDeathbank.isActive()) {
        if (deathbank != null) {
          if (deathbank.getLastUpdated() <= loadedDeathbank.getLastUpdated()) {
            deathbank.setLostAt(System.currentTimeMillis());
            deathbank = loadedDeathbank;
          } else {
            loadedDeathbank.setLostAt(System.currentTimeMillis());
          }
        } else {
          deathbank = loadedDeathbank;
        }
      }
      storages.add(loadedDeathbank);
    }
  }

  private boolean deathpilesUseAccountPlayTime() {
    return plugin.getConfig().deathpilesUseAccountPlayTime() && startPlayedMinutes != 0;
  }

  /**
   * Deletes deathpiles/graves from the plugin.
   *
   * @param includeActive if true, even non-expired deathpiles/graves will be deleted.
   */
  public void deleteExpiringDeathStorages(boolean includeActive) {
    Iterator<DeathStorage> iterator = storages.iterator();
    while (iterator.hasNext()) {
      DeathStorage storage = iterator.next();
      if (!(storage instanceof Deathpile || storage instanceof Grave)
          || (!includeActive && !((ExpiringDeathStorage) storage).hasExpired())) {
        continue;
      }

      iterator.remove();
      storage.deleteData(this);
    }
    SwingUtilities.invokeLater(storageTabPanel::reorderStoragePanels);
  }

  /**
   * Deletes deathbanks from the plugin.
   *
   * @param includeActive if true, even non-lost deathbanks will be deleted.
   */
  public void deleteDeathbanks(boolean includeActive) {
    Iterator<DeathStorage> iterator = storages.iterator();
    while (iterator.hasNext()) {
      DeathStorage storage = iterator.next();
      if (!(storage instanceof Deathbank) || (!includeActive && ((Deathbank) storage).isActive())) {
        continue;
      }

      iterator.remove();
      storage.deleteData(this);
    }
    SwingUtilities.invokeLater(storageTabPanel::reorderStoragePanels);
  }

  void deleteStorage(ExpiringDeathStorage expiringDeathStorage) {
    storages.remove(expiringDeathStorage);
    refreshMapPoints();
    SwingUtilities.invokeLater(() -> getStorageTabPanel().reorderStoragePanels());
    expiringDeathStorage.deleteData(this);

    if (expiringDeathStorage instanceof Grave && expiringDeathStorage == grave) {
      grave = null;
    }
  }

  public void refreshDeathpileColors() {
    getDeathpiles().forEach(Deathpile::refreshColor);
  }
}
