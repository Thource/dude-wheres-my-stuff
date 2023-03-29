package dev.thource.runelite.dudewheresmystuff.death;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemStackUtils;
import dev.thource.runelite.dudewheresmystuff.Region;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.Tab;
import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageManager;
import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageType;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageManager;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.vars.AccountType;
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
          12633 // death's office
      );

  @Getter @Nullable private Deathbank deathbank = null;
  long startMs = 0L;
  @Setter private CarryableStorageManager carryableStorageManager;
  @Setter private CoinsStorageManager coinsStorageManager;
  @Inject private WorldMapPointManager worldMapPointManager;
  @Getter @Setter private int startPlayedMinutes = -1;
  private boolean dying;
  private WorldPoint deathLocation;
  private List<ItemStack> deathItems;
  private Item[] oldInventoryItems;
  private final CheckPlayTimeInfoBox playTimeInfoBox = new CheckPlayTimeInfoBox(plugin);
  private DeathbankInfoBox deathbankInfoBox;
  private final List<DeathpileInfoBox> deathpileInfoBoxes = new ArrayList<>();

  @Inject
  private DeathStorageManager(DudeWheresMyStuffPlugin plugin) {
    super(plugin);

    playTimeInfoBox.setTooltip("Navigate to the quest tab and swap to the Character Summary tab "
        + "(brown star) to track cross-client deathpiles.");

    storages.add(new DeathItems(plugin, this));
  }

  @Override
  public void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    if (enabled) {
      if (itemContainerChanged.getContainerId() == InventoryID.INVENTORY.getId()) {
        if (updateInventoryItems(itemContainerChanged.getItemContainer().getItems())) {
          updateStorages(Collections.singletonList(deathbank));
        }
      } else if (itemContainerChanged.getContainerId() == 525) {
        updateDeathbankItems(itemContainerChanged.getItemContainer().getItems());
        updateStorages(Collections.singletonList(deathbank));
      }
    }
  }

  private void updateDeathbankItems(Item[] items) {
    int deathbankVarp = client.getVarpValue(261);
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

  private boolean updateInventoryItems(Item[] items) {
    boolean updated = false;

    if (oldInventoryItems != null
        && client.getLocalPlayer() != null
        && deathbank != null
        && deathbank.getDeathbankType() == DeathbankType.ZULRAH
        && Region.get(client.getLocalPlayer().getWorldLocation().getRegionID())
        == Region.CITY_ZULANDRA) {
      List<ItemStack> inventoryItemsList =
          Arrays.stream(items)
              .map(i -> new ItemStack(i.getId(), "", i.getQuantity(), 0, 0, true))
              .collect(Collectors.toList());
      removeItemsFromList(inventoryItemsList, oldInventoryItems);
      removeItemsFromList(deathbank.getItems(), inventoryItemsList);

      if (!inventoryItemsList.isEmpty()) {
        deathbank.setLastUpdated(System.currentTimeMillis());
        updated = true;
      }

      if (deathbank.getItems().isEmpty()) {
        clearDeathbank(false);
      }
    }

    oldInventoryItems = items;
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

  @Override
  public void onGameTick() {
    super.onGameTick();

    boolean updated = false;

    int playedMinutes = client.getVarcIntValue(526);
    if (playedMinutes != startPlayedMinutes) {
      if (startPlayedMinutes == -1) {
        refreshMapPoints();
      }

      SwingUtilities.invokeLater(storageTabPanel::reorderStoragePanels);

      startPlayedMinutes = playedMinutes;
      startMs = System.currentTimeMillis();

      if (startPlayedMinutes > 0) {
        getDeathpiles()
            .filter(Deathpile::isUseAccountPlayTime)
            .filter(deathpile -> deathpile.getStoragePanel() != null)
            .forEach(
                deathpile -> deathpile.getStoragePanel().getFooterLabel().setToolTipText(null));
      }
    }
    refreshInfoBoxes();

    if (startPlayedMinutes > 0) {
      Integer savedPlayedMinutes =
          configManager.getRSProfileConfiguration(
              DudeWheresMyStuffConfig.CONFIG_GROUP, "minutesPlayed", int.class);
      if (savedPlayedMinutes == null || savedPlayedMinutes != getPlayedMinutes()) {
        configManager.setRSProfileConfiguration(
            DudeWheresMyStuffConfig.CONFIG_GROUP, "minutesPlayed", getPlayedMinutes());
      }
    }

    if (deathbank != null) {
      Widget itemWindow = client.getWidget(602, 3);
      // This checks if the item collection window has been emptied while it was open
      if (itemWindow != null && client.getVarpValue(261) == -1) {
        clearDeathbank(false);
        updated = true;
      }
    }

    updateWorldMapPoints();

    if (processDeath()) {
      updated = true;
    }

    if (checkItemsLostOnDeathWindow()) {
      updated = true;
    }

    if (updated) {
      updateStorages(storages);
    }
  }

  private Stream<Deathpile> getDeathpiles() {
    return storages.stream()
        .filter(Deathpile.class::isInstance)
        .map(Deathpile.class::cast);
  }

  /**
   * Checks if any infoboxes need adding or removing.
   */
  public void refreshInfoBoxes() {
    refreshCheckPlayTimeInfoBox();
    refreshDeathbankInfoBox();
    refreshDeathpileInfoBoxes();
  }

  private void pruneDeathpileInfoBoxes(List<Deathpile> activeDeathpiles,
      InfoBoxManager infoBoxManager, List<InfoBox> currentInfoBoxes) {
    ListIterator<DeathpileInfoBox> iterator = deathpileInfoBoxes.listIterator();
    while (iterator.hasNext()) {
      DeathpileInfoBox infoBox = iterator.next();

      if (!activeDeathpiles.contains(infoBox.getDeathpile())) {
        if (currentInfoBoxes.contains(infoBox)) {
          infoBoxManager.removeInfoBox(infoBox);
        }

        iterator.remove();
      }
    }
  }

  private void refreshDeathpileInfoBoxes() {
    InfoBoxManager infoBoxManager = plugin.getInfoBoxManager();
    List<InfoBox> currentInfoBoxes = infoBoxManager.getInfoBoxes();
    List<Deathpile> activeDeathpiles = getDeathpiles()
        .filter(deathpile -> !deathpile.hasExpired())
        .collect(Collectors.toList());

    pruneDeathpileInfoBoxes(activeDeathpiles, infoBoxManager, currentInfoBoxes);

    activeDeathpiles
        .forEach(deathpile -> {
          if (deathpileInfoBoxes.stream()
              .noneMatch(infoBox -> infoBox.getDeathpile() == deathpile)) {
            deathpileInfoBoxes.add(new DeathpileInfoBox(plugin, deathpile));
          }
        });

    for (DeathpileInfoBox infoBox : deathpileInfoBoxes) {
      if (plugin.getConfig().deathpileInfoBox()) {
        if (!currentInfoBoxes.contains(infoBox)) {
          infoBoxManager.addInfoBox(infoBox);
        }
      } else {
        if (currentInfoBoxes.contains(infoBox)) {
          infoBoxManager.removeInfoBox(infoBox);
        }
      }
    }
  }

  private void refreshDeathbankInfoBox() {
    boolean showInfoBox = deathbank != null && plugin.getConfig().deathbankInfoBox();
    boolean hasDeathbankChanged = (showInfoBox && deathbankInfoBox == null)
        || (deathbankInfoBox != null && deathbankInfoBox.getDeathbank() != deathbank);
    List<InfoBox> infoBoxes = plugin.getInfoBoxManager().getInfoBoxes();

    if (deathbankInfoBox != null && (!showInfoBox || hasDeathbankChanged)
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

  private void refreshCheckPlayTimeInfoBox() {
    boolean showInfoBox =
        startPlayedMinutes <= 0 && plugin.getConfig().deathpilesUseAccountPlayTime();
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

    if (chatMessage.getType() != ChatMessageType.GAMEMESSAGE || deathbank != null) {
      return;
    }

    String message = Text.removeTags(chatMessage.getMessage());
    if (!message.startsWith("You have items stored in an item retrieval service.")) {
      return;
    }

    String finalMessage = message.replace(" ", "");
    DeathbankType deathbankType = Arrays.stream(DeathbankType.values())
        .filter(type -> type.getDeathWindowLocationText() != null)
        .filter(type -> finalMessage.contains(type.getDeathWindowLocationText()))
        .findFirst().orElse(DeathbankType.UNKNOWN);

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

  private void createMysteryDeathbank(DeathbankType type) {
    deathbank = new Deathbank(type, plugin, this);
    storages.add(deathbank);
    SwingUtilities.invokeLater(() -> deathbank.createStoragePanel(this));
    deathbank.setLastUpdated(System.currentTimeMillis());
    deathbank.setLocked(
        type != DeathbankType.ZULRAH
            || plugin.getClient().getAccountType() != AccountType.ULTIMATE_IRONMAN);
    deathbank.getItems().add(new ItemStack(ItemID.MYSTERY_BOX, 1, plugin));
  }

  @Override
  protected void updateStorages(List<? extends DeathStorage> storages) {
    if (!storages.isEmpty()) {
      SwingUtilities.invokeLater(
          () -> storages.forEach(storage -> {
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
        || !dying
        || client.getBoostedSkillLevel(Skill.HITPOINTS) < 10) {
      return false;
    }

    boolean updated = false;

    Region deathRegion = Region.get(deathLocation.getRegionID());

    if (!RESPAWN_REGIONS.contains(client.getLocalPlayer().getWorldLocation().getRegionID())) {
      // Player has died but is still safe unless their team dies
      if (deathRegion == Region.RAIDS_THEATRE_OF_BLOOD) {
        return false;
      }

      log.info(
          "Died, but did not respawn in a known respawn location: "
              + client.getLocalPlayer().getWorldLocation().getRegionID());
    } else if (deathRegion != Region.REGION_POH) {
      // POH deaths are always safe deaths
      updated = true;
      registerDeath(deathRegion);
    }

    dying = false;
    deathLocation = null;
    deathItems = null;

    return updated;
  }

  private void registerDeath(Region deathRegion) {
    clearDeathbank(true);

    // Don't create deathpiles for gauntlet deaths
    if (deathRegion == Region.MG_CORRUPTED_GAUNTLET || deathRegion == Region.MG_GAUNTLET) {
      return;
    }

    clearCarryableStorage();

    // Remove any items that were kept on death from the death items
    Stream.of(InventoryID.INVENTORY, InventoryID.EQUIPMENT)
        .map(id -> client.getItemContainer(id)).filter(Objects::nonNull)
        .forEach(i -> removeItemsFromList(deathItems, i.getItems()));

    Optional<DeathbankType> deathbankType = getDeathbankType(deathRegion);
    if (deathbankType.isPresent()) {
      deathbank = new Deathbank(deathbankType.get(), plugin, this);
      storages.add(deathbank);
      SwingUtilities.invokeLater(() -> deathbank.createStoragePanel(this));
      deathbank.setLastUpdated(System.currentTimeMillis());
      deathbank.setLocked(
          deathbankType.get() != DeathbankType.ZULRAH
              || plugin.getClient().getAccountType() != AccountType.ULTIMATE_IRONMAN);
      deathbank.getItems().addAll(deathItems);
    } else if (client.getAccountType() == AccountType.ULTIMATE_IRONMAN) {
      createDeathpile(deathLocation, deathItems);
    }

    refreshMapPoints();
  }

  private void clearCarryableStorage() {
    coinsStorageManager.getStorages().stream()
        .filter(s -> s.getType() == CoinsStorageType.LOOTING_BAG)
        .forEach(
            s -> {
              s.getCoinStack().setQuantity(0);
              SwingUtilities.invokeLater(() -> {
                if (s.getStoragePanel() != null) {
                  s.getStoragePanel().update();
                }
              });
            });
    SwingUtilities.invokeLater(coinsStorageManager.getStorageTabPanel()::reorderStoragePanels);

    carryableStorageManager.getStorages().stream()
        .filter(
            s ->
                s.getType() == CarryableStorageType.LOOTING_BAG
                    || s.getType() == CarryableStorageType.SEED_BOX)
        .forEach(
            s -> {
              s.getItems().clear();
              SwingUtilities.invokeLater(() -> {
                if (s.getStoragePanel() != null) {
                  s.getStoragePanel().update();
                }
              });
            });
    SwingUtilities.invokeLater(carryableStorageManager.getStorageTabPanel()::reorderStoragePanels);
  }

  void createDeathpile(WorldPoint location, List<ItemStack> items) {
    boolean useAccountPlayTime = deathpilesUseAccountPlayTime();
    int expiryTime = useAccountPlayTime ? getPlayedMinutes() + 59 : 5900;
    Deathpile deathpile = new Deathpile(plugin, useAccountPlayTime, expiryTime, location, this,
        items);
    SwingUtilities.invokeLater(() -> deathpile.createStoragePanel(this));
    storages.add(deathpile);
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
    if (client.getLocalPlayer() == null || actorDeath.getActor() != client.getLocalPlayer()) {
      return;
    }

    WorldPoint location =
        WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
    List<ItemStack> items =
        getDeathItems().stream()
            .filter(itemStack -> itemStack.getId() != -1)
            .collect(Collectors.toList());
    if (items.isEmpty()) {
      return;
    }

    dying = true;
    deathLocation = location;
    deathItems = items;
  }

  @Override
  public void onItemDespawned(ItemDespawned itemDespawned) {
    WorldPoint worldPoint = itemDespawned.getTile().getWorldLocation();
    // 10 is the max telegrab range, this should stop deathpiles from disappearing at random
    if (worldPoint.distanceTo(plugin.getClient().getLocalPlayer().getWorldLocation()) > 10) {
      return;
    }

    TileItem despawnedItem = itemDespawned.getItem();

    List<Deathpile> updatedDeathpiles = removeFromDeathpiles(despawnedItem, worldPoint);
    if (updatedDeathpiles.isEmpty()) {
      return;
    }

    ListIterator<Deathpile> iterator = updatedDeathpiles.listIterator();
    while (iterator.hasNext()) {
      Deathpile deathpile = iterator.next();

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
   * @param item       the TileItem to remove from the deathpiles
   * @param worldPoint the WorldPoint that the deathpiles must be on
   * @return a list of deathpiles that were affected
   */
  private List<Deathpile> removeFromDeathpiles(TileItem item, WorldPoint worldPoint) {
    AtomicLong quantityToRemove = new AtomicLong(item.getQuantity());
    if (quantityToRemove.get() == 65535) {
      quantityToRemove.set(Long.MAX_VALUE);
    }

    return getDeathpiles()
        .filter(deathpile -> !deathpile.hasExpired())
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

  @Override
  public Tab getTab() {
    return Tab.DEATH;
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
            .filter(s -> s.getType() == CarryableStorageType.INVENTORY
                || s.getType() == CarryableStorageType.EQUIPMENT)
            .sorted(
                Comparator.comparingInt(s -> s.getType() == CarryableStorageType.INVENTORY ? 0 : 1))
            .flatMap(s -> s.getItems().stream())
            .collect(Collectors.toList());

    boolean lootingBagPresent = false;

    ListIterator<ItemStack> itemStacksIterator = itemStacks.listIterator();
    while (itemStacksIterator.hasNext()) {
      ItemStack itemStack = itemStacksIterator.next();

      if (itemStack.getId() == ItemID.SEED_BOX || itemStack.getId() == ItemID.OPEN_SEED_BOX) {
        carryableStorageManager.getStorages().stream()
            .filter(s -> s.getType() == CarryableStorageType.SEED_BOX)
            .findFirst()
            .ifPresent(seedBox -> seedBox.getItems().forEach(itemStacksIterator::add));
      } else if (itemStack.getId() == ItemID.RUNE_POUCH) {
        carryableStorageManager.getStorages().stream()
            .filter(s -> s.getType() == CarryableStorageType.RUNE_POUCH)
            .findFirst()
            .ifPresent(runePouch -> runePouch.getItems().forEach(itemStacksIterator::add));
      } else if (itemStack.getId() == ItemID.BOLT_POUCH) {
        carryableStorageManager.getStorages().stream()
            .filter(s -> s.getType() == CarryableStorageType.BOLT_POUCH)
            .findFirst()
            .ifPresent(boltPouch -> boltPouch.getItems().forEach(itemStacksIterator::add));
      } else if (itemStack.getId() == ItemID.GNOMISH_FIRELIGHTER_20278) {
        carryableStorageManager.getStorages().stream()
            .filter(s -> s.getType() == CarryableStorageType.GNOMISH_FIRELIGHTER)
            .findFirst()
            .ifPresent(gnomishFirelighter -> gnomishFirelighter.getItems()
                .forEach(itemStacksIterator::add));
      } else if (itemStack.getId() == ItemID.LOOTING_BAG
          || itemStack.getId() == ItemID.LOOTING_BAG_22586) {
        lootingBagPresent = true;
        itemStacksIterator.remove();
      }
    }

    if (lootingBagPresent) {
      carryableStorageManager.getStorages().stream()
          .filter(s -> s.getType() == CarryableStorageType.LOOTING_BAG)
          .findFirst()
          .ifPresent(lootingBag -> itemStacks.addAll(lootingBag.getItems()));
    }

    return ItemStackUtils.compound(
        itemStacks.stream()
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
    loadDeathbanks(profileKey);
  }


  @Override
  public void reset() {
    oldInventoryItems = null;
    storages.clear();
    DeathItems deathItemsStorage = new DeathItems(plugin, this);
    SwingUtilities.invokeLater(() -> deathItemsStorage.createStoragePanel(this));
    storages.add(deathItemsStorage);
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
              deathpile.worldMapPoint =
                  new DeathWorldMapPoint(
                      deathpile.getWorldPoint(), itemManager, index.getAndIncrement());
              worldMapPointManager.add(deathpile.getWorldMapPoint());
            });
  }

  private void loadDeathpiles(String profileKey) {
    for (String configurationKey : configManager.getRSProfileConfigurationKeys(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        profileKey,
        getConfigKey() + "." + DeathStorageType.DEATHPILE.getConfigKey() + ".")) {
      Deathpile deathpile = Deathpile.load(plugin, this, profileKey,
          configurationKey.split("\\.")[2]);
      SwingUtilities.invokeLater(() -> deathpile.createStoragePanel(this));
      storages.add(deathpile);
    }

    refreshMapPoints();
  }

  private void loadDeathbanks(String profileKey) {
    for (String configurationKey : configManager.getRSProfileConfigurationKeys(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        profileKey,
        getConfigKey() + "." + DeathStorageType.DEATHBANK.getConfigKey() + ".")) {
      Deathbank loadedDeathbank = Deathbank.load(plugin, this, profileKey,
          configurationKey.split("\\.")[2]);
      SwingUtilities.invokeLater(() -> loadedDeathbank.createStoragePanel(this));
      if (loadedDeathbank.getLostAt() == -1L) {
        this.deathbank = loadedDeathbank;
      }
      storages.add(loadedDeathbank);
    }
  }

  private boolean deathpilesUseAccountPlayTime() {
    return plugin.getConfig().deathpilesUseAccountPlayTime() && startPlayedMinutes != 0;
  }
}
