package dev.thource.runelite.dudewheresmystuff.death;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemStackService;
import dev.thource.runelite.dudewheresmystuff.Region;
import dev.thource.runelite.dudewheresmystuff.Storage;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.Tab;
import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageManager;
import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageType;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageManager;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
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
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import org.apache.commons.lang3.math.NumberUtils;

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

  @Getter private final Deathbank deathbank;
  long startMs = 0L;
  @Setter private CarryableStorageManager carryableStorageManager;
  @Setter private CoinsStorageManager coinsStorageManager;
  @Inject private ClientThread clientThread;
  @Inject private WorldMapPointManager worldMapPointManager;
  @Setter private int startPlayedMinutes = -1;
  private boolean dying;
  private WorldPoint deathLocation;
  private List<ItemStack> deathItems;
  private Item[] oldInventoryItems;

  @Inject
  private DeathStorageManager(
      DudeWheresMyStuffPlugin plugin,
      DudeWheresMyStuffConfig config,
      Client client,
      ItemManager itemManager,
      ConfigManager configManager,
      Notifier notifier) {
    super(client, itemManager, configManager, config, notifier, plugin);

    deathbank = new Deathbank(DeathStorageType.UNKNOWN_DEATHBANK, client, itemManager);
    storages.add(deathbank);
  }

  @Override
  public long getTotalValue() {
    return storages.stream()
        .filter(
            s ->
                (s.getType() == DeathStorageType.DEATHPILE
                        && !((Deathpile) s).hasExpired(plugin.isPreviewModeEnabled()))
                    || (s.getType() != DeathStorageType.DEATHPILE
                        && ((Deathbank) s).getLostAt() == -1L))
        .mapToLong(Storage::getTotalValue)
        .sum();
  }

  @Override
  public boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    if (!enabled) {
      return false;
    }

    if (itemContainerChanged.getContainerId() == InventoryID.INVENTORY.getId()) {
      return updateInventoryItems(itemContainerChanged.getItemContainer().getItems());
    } else if (itemContainerChanged.getContainerId() == 525) {
      updateDeathbankItems(itemContainerChanged.getItemContainer().getItems());
      return true;
    }

    return false;
  }

  private void updateDeathbankItems(Item[] items) {
    int deathbankVarp = client.getVarpValue(261);
    DeathStorageType deathbankType =
        Arrays.stream(DeathStorageType.values())
            .filter(
                s ->
                    s.getDeathBankLockedState() == deathbankVarp
                        || s.getDeathBankUnlockedState() == deathbankVarp)
            .findFirst()
            .orElse(DeathStorageType.UNKNOWN_DEATHBANK);
    deathbank.setType(deathbankType);
    deathbank.setLocked(deathbankType.getDeathBankLockedState() == deathbankVarp);
    deathbank.setLastUpdated(System.currentTimeMillis());
    deathbank.getItems().clear();

    for (Item item : items) {
      if (item.getId() == -1) {
        continue;
      }

      ItemComposition itemComposition = itemManager.getItemComposition(item.getId());
      deathbank
          .getItems()
          .add(
              new ItemStack(
                  item.getId(),
                  itemComposition.getName(),
                  item.getQuantity(),
                  itemManager.getItemPrice(item.getId()),
                  itemComposition.getHaPrice(),
                  itemComposition.isStackable()));
    }
  }

  private boolean updateInventoryItems(Item[] items) {
    boolean updated = false;

    if (oldInventoryItems != null
        && client.getLocalPlayer() != null
        && deathbank.getType() == DeathStorageType.ZULRAH
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
      if (itemToRemove.getId() == -1) {
        continue;
      }

      long quantityToRemove = itemToRemove.getQuantity();

      Iterator<ItemStack> listIterator = listToRemoveFrom.iterator();
      while (listIterator.hasNext() && quantityToRemove > 0) {
        ItemStack inventoryItem = listIterator.next();

        if (inventoryItem.getId() != itemToRemove.getId()) {
          continue;
        }

        long qtyToRemove = Math.min(quantityToRemove, inventoryItem.getQuantity());
        quantityToRemove -= qtyToRemove;
        inventoryItem.setQuantity(inventoryItem.getQuantity() - qtyToRemove);
        if (inventoryItem.getQuantity() == 0) {
          listIterator.remove();
        }
      }
    }
  }

  private void removeItemsFromList(List<ItemStack> listToRemoveFrom, Item[] itemsToRemove) {
    for (Item itemToRemove : itemsToRemove) {
      if (itemToRemove.getId() == -1) {
        continue;
      }

      long quantityToRemove = itemToRemove.getQuantity();

      Iterator<ItemStack> listIterator = listToRemoveFrom.iterator();
      while (listIterator.hasNext() && quantityToRemove > 0) {
        ItemStack inventoryItem = listIterator.next();

        if (inventoryItem.getId() != itemToRemove.getId()) {
          continue;
        }

        long qtyToRemove = Math.min(quantityToRemove, inventoryItem.getQuantity());
        quantityToRemove -= qtyToRemove;
        inventoryItem.setQuantity(inventoryItem.getQuantity() - qtyToRemove);
        if (inventoryItem.getQuantity() == 0) {
          listIterator.remove();
        }
      }
    }
  }

  void clearDeathbank(boolean wasLost) {
    if (wasLost && !deathbank.getItems().isEmpty()) {
      Deathbank db = new Deathbank(deathbank.getType(), client, itemManager);
      db.setLostAt(System.currentTimeMillis());
      db.getItems().addAll(deathbank.getItems());
      storages.add(db);
    }

    deathbank.reset();
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
  public boolean onGameTick() {
    boolean updated = false;

    int playedMinutes = client.getVarcIntValue(526);
    if (playedMinutes != startPlayedMinutes) {
      if (startPlayedMinutes == -1) {
        refreshMapPoints();
      }

      startPlayedMinutes = playedMinutes;
      startMs = System.currentTimeMillis();
      updated = true;
    }
    if (startPlayedMinutes == -1) {
      return false;
    }

    Integer savedPlayedMinutes =
        configManager.getRSProfileConfiguration(
            DudeWheresMyStuffConfig.CONFIG_GROUP, "minutesPlayed", int.class);
    if (savedPlayedMinutes == null || savedPlayedMinutes != getPlayedMinutes()) {
      configManager.setRSProfileConfiguration(
          DudeWheresMyStuffConfig.CONFIG_GROUP, "minutesPlayed", getPlayedMinutes());
    }

    if (deathbank.getLastUpdated() != -1L) {
      Widget itemWindow = client.getWidget(602, 3);
      if (itemWindow != null && client.getVarpValue(261) == -1) {
        clearDeathbank(false);
        updated = true;
      }
    }

    updateWorldMapPoints();
    return processDeath() || updated;
  }

  private void updateWorldMapPoints() {
    if (storages.stream()
        .filter(Deathpile.class::isInstance)
        .map(Deathpile.class::cast)
        .anyMatch(
            deathpile -> {
              if (deathpile.worldMapPoint == null) {
                return !deathpile.hasExpired(plugin.isPreviewModeEnabled());
              }
              if (deathpile.hasExpired(plugin.isPreviewModeEnabled())) {
                return true;
              }
              if (deathpile.worldMapPoint.getTooltip() == null) {
                return false;
              }

              deathpile.worldMapPoint.setTooltip(
                  "Deathpile (" + deathpile.getExpireText(plugin.isPreviewModeEnabled()) + ")");
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
        .forEach(s -> s.getCoinStack().setQuantity(0));
    carryableStorageManager.getStorages().stream()
        .filter(
            s ->
                s.getType() == CarryableStorageType.LOOTING_BAG
                    || s.getType() == CarryableStorageType.SEED_BOX)
        .forEach(s -> s.getItems().clear());

    ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
    if (inventory != null) {
      removeItemsFromList(deathItems, inventory.getItems());
    }

    ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
    if (equipment != null) {
      removeItemsFromList(deathItems, equipment.getItems());
    }

    Optional<DeathStorageType> deathbankType = getDeathbankType(deathRegion);
    if (deathbankType.isPresent()) {
      deathbank.setType(deathbankType.get());
      deathbank.setLastUpdated(System.currentTimeMillis());
      deathbank.setLocked(true);
      deathbank.getItems().clear();
      deathbank.getItems().addAll(deathItems);
    } else {
      if (client.getAccountType() == AccountType.ULTIMATE_IRONMAN) {
        storages.add(
            new Deathpile(
                client, itemManager, getPlayedMinutes(), deathLocation, this, deathItems));
      }
    }

    refreshMapPoints();
  }

  private Optional<DeathStorageType> getDeathbankType(Region deathRegion) {
    if (deathRegion == null) {
      return Optional.empty();
    }

    if (deathRegion == Region.BOSS_VORKATH) {
      return Optional.of(
          Quest.DRAGON_SLAYER_II.getState(client) == QuestState.IN_PROGRESS
              ? DeathStorageType.QUEST_DS2
              : DeathStorageType.VORKATH);
    } else if (deathRegion == Region.BOSS_NIGHTMARE) {
      // TODO: work out how to differentiate between nightmare and phosani's
      return Optional.of(DeathStorageType.NIGHTMARE);
    }
    // TODO: add quest checking

    return Arrays.stream(DeathStorageType.values())
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
  public boolean onItemDespawned(ItemDespawned itemDespawned) {
    WorldPoint worldPoint = itemDespawned.getTile().getWorldLocation();
    TileItem despawnedItem = itemDespawned.getItem();

    AtomicBoolean updated = new AtomicBoolean(false);
    AtomicLong quantityToRemove = new AtomicLong(despawnedItem.getQuantity());
    if (quantityToRemove.get() == 65535) {
      quantityToRemove.set(Long.MAX_VALUE);
    }

    storages.stream()
        .filter(Deathpile.class::isInstance)
        .map(Deathpile.class::cast)
        .filter(deathpile -> !deathpile.hasExpired(plugin.isPreviewModeEnabled()))
        .filter(deathpile -> deathpile.getWorldPoint().equals(worldPoint))
        .forEach(
            (Deathpile deathpile) -> {
              if (quantityToRemove.get() == 0) {
                return;
              }

              Iterator<ItemStack> listIterator = deathpile.getItems().iterator();
              while (listIterator.hasNext() && quantityToRemove.get() > 0) {
                ItemStack itemStack = listIterator.next();
                if (itemStack.getId() != despawnedItem.getId()) {
                  continue;
                }

                updated.set(true);
                long qtyToRemove = Math.min(quantityToRemove.get(), itemStack.getQuantity());
                quantityToRemove.addAndGet(-qtyToRemove);
                itemStack.setQuantity(itemStack.getQuantity() - qtyToRemove);
                if (itemStack.getQuantity() <= 0) {
                  listIterator.remove();
                }
              }
            });

    return updated.get();
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
    if (plugin.isPreviewModeEnabled()) {
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
                        && s.getType() != CarryableStorageType.RUNE_POUCH)
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

    return ItemStackService.compound(itemStacks, false);
  }

  @Override
  public void save() {
    if (!enabled) {
      return;
    }

    saveDeathpiles();
    saveDeathbank();
    saveLostDeathbanks();
  }

  private void saveDeathbank() {
    String data =
        deathbank.getType().getConfigKey()
            + ";"
            + deathbank.isLocked()
            + ";"
            + deathbank.getLastUpdated()
            + ";"
            + deathbank.getItems().stream()
                .map(i -> i.getId() + "," + i.getQuantity())
                .collect(Collectors.joining("="));

    configManager.setRSProfileConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, getConfigKey() + ".deathbank", data);
  }

  private void saveDeathpiles() {
    configManager.setRSProfileConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        getConfigKey() + "." + DeathStorageType.DEATHPILE.getConfigKey(),
        storages.stream()
            .filter(Deathpile.class::isInstance)
            .map(
                s -> {
                  Deathpile d = (Deathpile) s;
                  return d.getPlayedMinutesAtCreation()
                      + ";"
                      + d.getWorldPoint().getX()
                      + ","
                      + d.getWorldPoint().getY()
                      + ","
                      + d.getWorldPoint().getPlane()
                      + ";"
                      + d.getItems().stream()
                          .map(i -> i.getId() + "," + i.getQuantity())
                          .collect(Collectors.joining("="));
                })
            .collect(Collectors.joining("$")));
  }

  private void saveLostDeathbanks() {
    configManager.setRSProfileConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        getConfigKey() + ".lostdeathbanks",
        storages.stream()
            .filter(s -> s instanceof Deathbank && s != deathbank)
            .map(
                s -> {
                  Deathbank d = (Deathbank) s;
                  return d.getType().getConfigKey()
                      + ";"
                      + d.getLostAt()
                      + ";"
                      + d.getItems().stream()
                          .map(i -> i.getId() + "," + i.getQuantity())
                          .collect(Collectors.joining("="));
                })
            .collect(Collectors.joining("$")));
  }

  @Override
  public void load(String profileKey) {
    if (!enabled) {
      return;
    }

    loadDeathpiles(profileKey);
    loadDeathbank(profileKey);
    loadLostDeathbanks(profileKey);
  }

  private void loadDeathbank(String profileKey) {
    clearDeathbank(false);

    String data =
        configManager.getConfiguration(
            DudeWheresMyStuffConfig.CONFIG_GROUP,
            profileKey,
            getConfigKey() + ".deathbank",
            String.class);
    if (data == null) {
      return;
    }

    String[] dataSplit = data.split(";");
    if (dataSplit.length != 4) {
      return;
    }

    String deathbankKey = dataSplit[0];
    DeathStorageType deathStorageType =
        Arrays.stream(DeathStorageType.values())
            .filter(s -> Objects.equals(s.getConfigKey(), deathbankKey))
            .findFirst()
            .orElse(null);
    if (deathStorageType == null) {
      return;
    }

    long lastUpdate = NumberUtils.toLong(dataSplit[2], 0);
    if (lastUpdate == 0) {
      return;
    }

    List<ItemStack> items = new ArrayList<>();
    for (String itemData : dataSplit[3].split("=")) {
      String[] itemDataSplit = itemData.split(",");
      if (itemDataSplit.length != 2) {
        continue;
      }

      int itemId = NumberUtils.toInt(itemDataSplit[0], 0);
      int quantity = NumberUtils.toInt(itemDataSplit[1], 0);
      if (itemId != 0 && quantity != 0) {
        ItemStack item = new ItemStack(itemId, client, clientThread, itemManager);
        item.setQuantity(quantity);
        items.add(item);
      }
    }
    if (items.isEmpty()) {
      return;
    }

    deathbank.setType(deathStorageType);
    deathbank.setLastUpdated(lastUpdate);
    deathbank.getItems().clear();
    deathbank.getItems().addAll(items);
    deathbank.setLocked(Objects.equals(dataSplit[1], "true"));
  }

  private void loadLostDeathbanks(String profileKey) {
    String data =
        configManager.getConfiguration(
            DudeWheresMyStuffConfig.CONFIG_GROUP,
            profileKey,
            getConfigKey() + ".lostdeathbanks",
            String.class);
    if (data == null) {
      return;
    }

    String[] banksData = data.split("\\$");
    for (String banksDatum : banksData) {
      loadLostDeathbank(banksDatum);
    }
  }

  private void loadLostDeathbank(String data) {
    String[] dataSplit = data.split(";");
    if (dataSplit.length != 3) {
      return;
    }

    String deathbankKey = dataSplit[0];
    DeathStorageType deathStorageType =
        Arrays.stream(DeathStorageType.values())
            .filter(s -> Objects.equals(s.getConfigKey(), deathbankKey))
            .findFirst()
            .orElse(null);
    if (deathStorageType == null) {
      return;
    }

    long lostAt = NumberUtils.toLong(dataSplit[1], -1L);
    if (lostAt == -1L) {
      return;
    }

    List<ItemStack> items = new ArrayList<>();
    for (String itemData : dataSplit[2].split("=")) {
      String[] itemDataSplit = itemData.split(",");
      if (itemDataSplit.length != 2) {
        continue;
      }

      int itemId = NumberUtils.toInt(itemDataSplit[0], 0);
      int quantity = NumberUtils.toInt(itemDataSplit[1], 0);
      if (itemId != 0 && quantity != 0) {
        ItemStack item = new ItemStack(itemId, client, clientThread, itemManager);
        item.setQuantity(quantity);
        items.add(item);
      }
    }
    if (items.isEmpty()) {
      return;
    }

    Deathbank loadedDeathbank = new Deathbank(deathStorageType, client, itemManager);
    loadedDeathbank.setLostAt(lostAt);
    loadedDeathbank.getItems().addAll(items);
    storages.add(loadedDeathbank);
  }

  @Override
  public void reset() {
    oldInventoryItems = null;
    storages.clear();
    storages.add(deathbank);
    clearDeathbank(false);
    enable();
    refreshMapPoints();
  }

  void refreshMapPoints() {
    if (worldMapPointManager == null || plugin.isPreviewModeEnabled()) {
      return;
    }

    worldMapPointManager.removeIf(DeathWorldMapPoint.class::isInstance);

    AtomicInteger index = new AtomicInteger(1);
    storages.stream()
        .filter(Deathpile.class::isInstance)
        .map(Deathpile.class::cast)
        .filter(deathpile -> !deathpile.hasExpired(plugin.isPreviewModeEnabled()))
        .forEach(
            deathpile -> {
              deathpile.worldMapPoint =
                  new DeathWorldMapPoint(
                      deathpile.getWorldPoint(), itemManager, index.getAndIncrement());
              worldMapPointManager.add(deathpile.getWorldMapPoint());
            });
  }

  private void loadDeathpiles(String profileKey) {
    String data =
        configManager.getConfiguration(
            DudeWheresMyStuffConfig.CONFIG_GROUP,
            profileKey,
            getConfigKey() + "." + DeathStorageType.DEATHPILE.getConfigKey(),
            String.class);
    if (data == null) {
      return;
    }

    String[] pilesData = data.split("\\$");
    for (String pilesDatum : pilesData) {
      loadDeathpile(pilesDatum);
    }

    refreshMapPoints();
  }

  private void loadDeathpile(String data) {
    String[] dataSplit = data.split(";");
    if (dataSplit.length != 3) {
      return;
    }

    String[] worldPointSplit = dataSplit[1].split(",");
    if (worldPointSplit.length != 3) {
      return;
    }

    String[] itemSplit = dataSplit[2].split("=");

    List<ItemStack> items = new ArrayList<>();
    for (String itemData : itemSplit) {
      String[] itemDataSplit = itemData.split(",");
      if (itemDataSplit.length != 2) {
        continue;
      }

      int itemId = NumberUtils.toInt(itemDataSplit[0], 0);
      int quantity = NumberUtils.toInt(itemDataSplit[1], 0);
      if (itemId != 0 && quantity != 0) {
        ItemStack item = new ItemStack(itemId, client, clientThread, itemManager);
        item.setQuantity(quantity);
        items.add(item);
      }
    }
    if (items.isEmpty()) {
      return;
    }

    storages.add(
        new Deathpile(
            client,
            itemManager,
            NumberUtils.toInt(dataSplit[0], 0),
            new WorldPoint(
                NumberUtils.toInt(worldPointSplit[0], 0),
                NumberUtils.toInt(worldPointSplit[1], 0),
                NumberUtils.toInt(worldPointSplit[2], 0)),
            this,
            items));
  }
}
