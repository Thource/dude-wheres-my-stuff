package dev.thource.runelite.dudewheresmystuff.death;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemStackUtils;
import dev.thource.runelite.dudewheresmystuff.Region;
import dev.thource.runelite.dudewheresmystuff.Storage;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.Tab;
import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageManager;
import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageType;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageManager;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageType;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.vars.AccountType;
import net.runelite.api.widgets.Widget;
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
  @Setter private int startPlayedMinutes = -1;
  private boolean dying;
  private WorldPoint deathLocation;
  private List<ItemStack> deathItems;
  private Item[] oldInventoryItems;

  @Inject
  private DeathStorageManager(DudeWheresMyStuffPlugin plugin) {
    super(plugin);

    storages.add(new DeathItems(plugin, this));
  }

  @Override
  public long getTotalValue() {
    return storages.stream()
        .filter(s -> s.getType() != DeathStorageType.DEATH_ITEMS)
        .filter(
            s ->
                (s.getType() == DeathStorageType.DEATHPILE && !((Deathpile) s).hasExpired())
                    || (s.getType() != DeathStorageType.DEATHPILE
                        && ((Deathbank) s).getLostAt() == -1L))
        .mapToLong(Storage::getTotalValue)
        .sum();
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
    boolean updated = false;

    int playedMinutes = client.getVarcIntValue(526);
    if (playedMinutes != startPlayedMinutes) {
      if (startPlayedMinutes == -1) {
        refreshMapPoints();
      }

      startPlayedMinutes = playedMinutes;
      startMs = System.currentTimeMillis();
    }
    if (startPlayedMinutes == -1) {
      return;
    }

    Integer savedPlayedMinutes =
        configManager.getRSProfileConfiguration(
            DudeWheresMyStuffConfig.CONFIG_GROUP, "minutesPlayed", int.class);
    if (savedPlayedMinutes == null || savedPlayedMinutes != getPlayedMinutes()) {
      configManager.setRSProfileConfiguration(
          DudeWheresMyStuffConfig.CONFIG_GROUP, "minutesPlayed", getPlayedMinutes());
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

  private boolean checkItemsLostOnDeathWindow() {
    if (deathbank == null) {
      Widget deathbankTextWidget = client.getWidget(4, 3);
      if (deathbankTextWidget != null) {
        Widget textWidget = deathbankTextWidget.getChild(3);
        if (textWidget != null) {
          String deathbankText = Text.removeTags(textWidget.getText()).replace(" ", "");

          // check for unsafe death message
          if (deathbankText.contains("theywillbedeleted")) {
            DeathbankType type =
                Arrays.stream(DeathbankType.values())
                    .filter(
                        t ->
                            t.getDeathWindowLocationText() != null
                                && deathbankText.contains(t.getDeathWindowLocationText()))
                    .findFirst()
                    .orElse(DeathbankType.UNKNOWN);

            deathbank = new Deathbank(type, plugin, this);
            storages.add(deathbank);
            SwingUtilities.invokeLater(() -> deathbank.createStoragePanel(this));
            deathbank.setLastUpdated(System.currentTimeMillis());
            deathbank.setLocked(
                type != DeathbankType.ZULRAH
                    || plugin.getClient().getAccountType() != AccountType.ULTIMATE_IRONMAN);
            deathbank.getItems().add(new ItemStack(ItemID.MYSTERY_BOX, 1, plugin));

            return true;
          }
        }
      }
    }

    return false;
  }

  @Override
  protected void updateStorages(List<? extends DeathStorage> storages) {
    if (!storages.isEmpty()) {
      save();

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
    } else if (deathRegion == Region.REGION_POH) {
      // POH deaths are safe even if the player respawns in a RESPAWN_REGIONS (i.e. the house is in prifddinas)
      log.info("Died in POH");
    } else {
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

    coinsStorageManager.getStorages().stream()
        .filter(s -> s.getType() == CoinsStorageType.LOOTING_BAG)
        .forEach(
            s -> {
              s.getCoinStack().setQuantity(0);
              s.save(configManager, coinsStorageManager.getConfigKey());
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
              s.save(configManager, carryableStorageManager.getConfigKey());
              SwingUtilities.invokeLater(() -> {
                if (s.getStoragePanel() != null) {
                  s.getStoragePanel().update();
                }
              });
            });
    SwingUtilities.invokeLater(carryableStorageManager.getStorageTabPanel()::reorderStoragePanels);

    ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
    if (inventory != null) {
      removeItemsFromList(deathItems, inventory.getItems());
    }

    ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
    if (equipment != null) {
      removeItemsFromList(deathItems, equipment.getItems());
    }

    Optional<DeathbankType> deathbankType = getDeathbankType(deathRegion);
    if (deathbankType.isPresent()) {
      deathbank = new Deathbank(deathbankType.get(), plugin, this);
      storages.add(deathbank);
      SwingUtilities.invokeLater(() -> deathbank.createStoragePanel(this));
      deathbank.setLastUpdated(System.currentTimeMillis());
      deathbank.setLocked(
          deathbankType.get() != DeathbankType.ZULRAH
              || plugin.getClient().getAccountType() != AccountType.ULTIMATE_IRONMAN);
      deathbank.getItems().clear();
      deathbank.getItems().addAll(deathItems);
    } else {
      if (client.getAccountType() == AccountType.ULTIMATE_IRONMAN) {
        Deathpile deathpile =
            new Deathpile(plugin, true, getPlayedMinutes() + 59, deathLocation, this, deathItems);
        SwingUtilities.invokeLater(() -> deathpile.createStoragePanel(this));
        storages.add(deathpile);
      }
    }

    refreshMapPoints();
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
    // TODO: add quest checking

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
    TileItem despawnedItem = itemDespawned.getItem();

    AtomicLong quantityToRemove = new AtomicLong(despawnedItem.getQuantity());
    if (quantityToRemove.get() == 65535) {
      quantityToRemove.set(Long.MAX_VALUE);
    }

    List<Deathpile> updatedDeathpiles =
        storages.stream()
            .filter(Deathpile.class::isInstance)
            .map(Deathpile.class::cast)
            .filter(deathpile -> !deathpile.hasExpired())
            .filter(deathpile -> deathpile.getWorldPoint().equals(worldPoint))
            .filter(
                (Deathpile deathpile) -> {
                  if (quantityToRemove.get() == 0) {
                    return false;
                  }

                  Iterator<ItemStack> listIterator = deathpile.getItems().iterator();
                  boolean updated = false;
                  while (listIterator.hasNext() && quantityToRemove.get() > 0) {
                    ItemStack itemStack = listIterator.next();
                    if (itemStack.getId() != despawnedItem.getId()) {
                      continue;
                    }

                    updated = true;
                    long qtyToRemove = Math.min(quantityToRemove.get(), itemStack.getQuantity());
                    quantityToRemove.addAndGet(-qtyToRemove);
                    itemStack.setQuantity(itemStack.getQuantity() - qtyToRemove);
                    if (itemStack.getQuantity() <= 0) {
                      listIterator.remove();
                    }
                  }

                  return updated;
                })
            .collect(Collectors.toList());
    if (updatedDeathpiles.isEmpty()) {
      return;
    }

    ListIterator<Deathpile> iterator = updatedDeathpiles.listIterator();
    while (iterator.hasNext()) {
      Deathpile deathpile = iterator.next();

      if (deathpile.getItems().isEmpty()) {
        storages.remove(deathpile);
        iterator.remove();
      }
    }

    if (updatedDeathpiles.isEmpty()) {
      save();
      SwingUtilities.invokeLater(storageTabPanel::reorderStoragePanels);
    } else {
      updateStorages(updatedDeathpiles);
    }
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
            .filter(
                s ->
                    s.getType() != CarryableStorageType.SEED_BOX
                        && s.getType() != CarryableStorageType.LOOTING_BAG
                        && s.getType() != CarryableStorageType.RUNE_POUCH
                        && s.getType() != CarryableStorageType.BOTTOMLESS_BUCKET
                        && s.getType() != CarryableStorageType.PLANK_SACK
                        && s.getType() != CarryableStorageType.BOLT_POUCH
                        && s.getType() != CarryableStorageType.GNOMISH_FIRELIGHTER
                        && s.getType() != CarryableStorageType.MASTER_SCROLL_BOOK)
            .sorted(
                Comparator.comparingInt(
                    s -> {
                      if (s.getType() == CarryableStorageType.INVENTORY) {
                        return 0;
                      }
                      if (s.getType() == CarryableStorageType.EQUIPMENT) {
                        return 1;
                      }

                      return Integer.MAX_VALUE;
                    }))
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
            .ifPresent(gnomishFirelighter -> gnomishFirelighter.getItems().forEach(itemStacksIterator::add));
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
    clearDeathbank(false);
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
      Deathbank deathbank = Deathbank.load(plugin, this, profileKey, configurationKey.split("\\.")[2]);
      SwingUtilities.invokeLater(() -> deathbank.createStoragePanel(this));
      if (deathbank.lostAt == -1L) {
        this.deathbank = deathbank;
      }
      storages.add(deathbank);
    }
  }
}
