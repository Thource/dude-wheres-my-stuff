package dev.thource.runelite.dudewheresmystuff;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import dev.thource.runelite.dudewheresmystuff.death.Deathbank;
import dev.thource.runelite.dudewheresmystuff.death.Deathpile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
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

@Slf4j
public class DeathStorageManager extends StorageManager<DeathStorageType, DeathStorage> {

  private static final Set<Integer> RESPAWN_REGIONS = ImmutableSet.of(
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
  final Deathbank deathbank;
  public long startMs = 0L;
  CarryableStorageManager carryableStorageManager;
  CoinsStorageManager coinsStorageManager;
  @Inject
  ClientThread clientThread;
  @Inject
  WorldMapPointManager worldMapPointManager;
  int startPlayedMinutes = -1;
  private boolean dying;
  private WorldPoint deathLocation;
  private List<ItemStack> deathItems;
  private Item[] oldInventoryItems;

  @Inject
  private DeathStorageManager(DudeWheresMyStuffPlugin plugin, DudeWheresMyStuffConfig config,
      Client client, ItemManager itemManager, ConfigManager configManager, Notifier notifier) {
    super(client, itemManager, configManager, config, notifier, plugin);

    deathbank = new Deathbank(DeathStorageType.UNKNOWN_DEATHBANK, client, itemManager);
    storages.add(deathbank);
  }

  @Override
  long getTotalValue() {
    return storages.stream()
        .filter(s -> (s.getType() == DeathStorageType.DEATHPILE && !((Deathpile) s).hasExpired(
            plugin.panelContainer.previewing))
            || (s.getType() != DeathStorageType.DEATHPILE && ((Deathbank) s).getLostAt() == -1L))
        .mapToLong(Storage::getTotalValue)
        .sum();
  }

  @Override
  boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
      if (!enabled) {
          return false;
      }

    boolean updated = false;

    if (itemContainerChanged.getContainerId() == InventoryID.INVENTORY.getId()) {
      Item[] inventoryItems = itemContainerChanged.getItemContainer().getItems();
      if (oldInventoryItems != null
          && client.getLocalPlayer() != null
          && deathbank.getType() == DeathStorageType.ZULRAH
          && Region.get(client.getLocalPlayer().getWorldLocation().getRegionID())
          == Region.CITY_ZULANDRA) {
        List<ItemStack> inventoryItemsList = Arrays.stream(inventoryItems)
            .map(i -> new ItemStack(i.getId(), "", i.getQuantity(), 0, 0, true))
            .collect(Collectors.toList());
        removeItemsFromList(inventoryItemsList, oldInventoryItems);
        removeItemsFromList(deathbank.getItems(), inventoryItemsList);

        if (!inventoryItemsList.isEmpty()) {
          deathbank.lastUpdated = System.currentTimeMillis();
          updated = true;
        }

          if (deathbank.getItems().isEmpty()) {
              clearDeathbank(false);
          }
      }

      oldInventoryItems = inventoryItems;
    } else if (itemContainerChanged.getContainerId() == 525) {
      int deathbankVarp = client.getVarpValue(261);
      deathbank.type = Arrays.stream(DeathStorageType.values())
          .filter(s -> s.getDeathBankLockedState() == deathbankVarp
              || s.getDeathBankUnlockedState() == deathbankVarp)
          .findFirst().orElse(DeathStorageType.UNKNOWN_DEATHBANK);
      deathbank.locked = deathbank.type.getDeathBankLockedState() == deathbankVarp;
      deathbank.lastUpdated = System.currentTimeMillis();
      deathbank.items.clear();

      for (Item item : itemContainerChanged.getItemContainer().getItems()) {
          if (item.getId() == -1) {
              continue;
          }

        ItemComposition itemComposition = itemManager.getItemComposition(item.getId());
        deathbank.items.add(
            new ItemStack(item.getId(), itemComposition.getName(), item.getQuantity(),
                itemManager.getItemPrice(item.getId()), itemComposition.getHaPrice(),
                itemComposition.isStackable()));
      }
      updated = true;
    }

    return updated;
  }

  private void removeItemsFromList(List<ItemStack> listToRemoveFrom,
      List<ItemStack> itemsToRemove) {
    for (ItemStack itemToRemove : itemsToRemove) {
        if (itemToRemove.getId() == -1) {
            continue;
        }

      long quantityToRemove = itemToRemove.getQuantity();

      Iterator<ItemStack> listIterator = listToRemoveFrom.iterator();
      while (listIterator.hasNext()) {
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

          if (quantityToRemove == 0) {
              break;
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
      while (listIterator.hasNext()) {
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

          if (quantityToRemove == 0) {
              break;
          }
      }
    }
  }

  void clearDeathbank(boolean wasLost) {
    if (wasLost && !deathbank.getItems().isEmpty()) {
      Deathbank db = new Deathbank(deathbank.getType(), client, itemManager);
      db.lostAt = System.currentTimeMillis();
      db.items.addAll(deathbank.getItems());
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

    Integer savedPlayedMinutes = configManager.getRSProfileConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, "minutesPlayed", int.class);
      if (savedPlayedMinutes == null || savedPlayedMinutes != getPlayedMinutes()) {
          configManager.setRSProfileConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP,
              "minutesPlayed",
              getPlayedMinutes());
      }

    if (deathbank.lastUpdated != -1L) {
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
    for (DeathStorage storage : storages) {
        if (!(storage instanceof Deathpile)) {
            continue;
        }

      Deathpile deathpile = (Deathpile) storage;
      if (deathpile.worldMapPoint == null) {
        if (!deathpile.hasExpired(plugin.panelContainer.previewing)) {
          refreshMapPoints();
          break;
        }

        continue;
      }

      if (deathpile.hasExpired(plugin.panelContainer.previewing)) {
        refreshMapPoints();
        break;
      }
        if (deathpile.worldMapPoint.getTooltip() == null) {
            continue;
        }

      deathpile.worldMapPoint.setTooltip(
          "Deathpile (" + deathpile.getExpireText(plugin.panelContainer.previewing) + ")");
    }
  }

  private boolean processDeath() {
      if (client.getLocalPlayer() == null) {
          return false;
      }

    boolean updated = false;

      if (!dying || client.getBoostedSkillLevel(Skill.HITPOINTS) < 10) {
          return false;
      }

    Region deathRegion = Region.get(deathLocation.getRegionID());

    if (!RESPAWN_REGIONS.contains(client.getLocalPlayer().getWorldLocation().getRegionID())) {
      // Player has died but is still safe unless their team dies
        if (deathRegion == Region.RAIDS_THEATRE_OF_BLOOD) {
            return false;
        }

      System.out.println("Died, but did not respawn in a known respawn location: " +
          client.getLocalPlayer().getWorldLocation().getRegionID());
    } else {
      updated = true;
      clearDeathbank(true);

      if (deathRegion != Region.MG_CORRUPTED_GAUNTLET && deathRegion != Region.MG_GAUNTLET) {
        DeathStorageType deathStorageType = null;
        if (deathRegion != null) {
          if (deathRegion == Region.BOSS_VORKATH) {
            deathStorageType = Quest.DRAGON_SLAYER_II.getState(client) == QuestState.IN_PROGRESS
                ? DeathStorageType.QUEST_DS2 : DeathStorageType.VORKATH;
          } else if (deathRegion == Region.BOSS_NIGHTMARE) {
            // TODO: work out how to differentiate between nightmare and phosani's
            deathStorageType = DeathStorageType.NIGHTMARE;
          }
          // TODO: add quest checking

            if (deathStorageType == null) {
                deathStorageType = Arrays.stream(DeathStorageType.values())
                    .filter(s -> s.getRegion() == deathRegion)
                    .findFirst().orElse(null);
            }
        }

        coinsStorageManager.storages.stream()
            .filter(s -> s.getType() == CoinsStorageType.LOOTING_BAG)
            .forEach(s -> s.getCoinStack().setQuantity(0));
        carryableStorageManager.storages.stream()
            .filter(s -> s.getType() == CarryableStorageType.LOOTING_BAG
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

        if (deathStorageType == null) {
            if (client.getAccountType() == AccountType.ULTIMATE_IRONMAN) {
                storages.add(
                    new Deathpile(client, itemManager, getPlayedMinutes(), deathLocation, this,
                        deathItems));
            }
        } else {
          deathbank.type = deathStorageType;
          deathbank.lastUpdated = System.currentTimeMillis();
          deathbank.locked = true;
          deathbank.items = deathItems;
        }
      }

      refreshMapPoints();
    }

    dying = false;
    deathLocation = null;
    deathItems = null;

    return updated;
  }

  @Override
  public void onActorDeath(ActorDeath actorDeath) {
      if (client.getLocalPlayer() == null || actorDeath.getActor() != client.getLocalPlayer()) {
          return;
      }

    WorldPoint location = WorldPoint.fromLocalInstance(client,
        client.getLocalPlayer().getLocalLocation());
    List<ItemStack> items = getDeathItems().stream().filter(itemStack -> itemStack.getId() != -1)
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

    for (DeathStorage storage : storages) {
      if (!(storage instanceof Deathpile)) {
        continue;
      }

      Deathpile deathpile = (Deathpile) storage;
      if (deathpile.hasExpired(plugin.panelContainer.previewing) || !deathpile.getWorldPoint()
          .equals(worldPoint)) {
        continue;
      }

      Iterator<ItemStack> listIterator = deathpile.items.iterator();
      while (listIterator.hasNext()) {
        ItemStack itemStack = listIterator.next();
        if (itemStack.getId() != despawnedItem.getId()
            || (itemStack.getQuantity() != despawnedItem.getQuantity()
            && (itemStack.getQuantity() < 65535 || despawnedItem.getQuantity() != 65535))) {
          continue;
        }

        listIterator.remove();
        if (deathpile.items.isEmpty()) {
          storages.remove(deathpile);
          refreshMapPoints();
        }

        return true;
      }
    }

    return false;
  }

  @Override
  public String getConfigKey() {
    return "death";
  }

  @Override
  public Tab getTab() {
    return Tab.DEATH;
  }

  public int getPlayedMinutes() {
      if (plugin.panelContainer.previewing) {
          return startPlayedMinutes;
      }

    return (int) (startPlayedMinutes + ((System.currentTimeMillis() - startMs) / 60000));
  }

  List<ItemStack> getDeathItems() {
    List<ItemStack> itemStacks = carryableStorageManager.storages.stream()
        .filter(s -> s.getType() != CarryableStorageType.SEED_BOX
            && s.getType() != CarryableStorageType.LOOTING_BAG
            && s.getType() != CarryableStorageType.RUNE_POUCH)
        .sorted(Comparator.comparingInt(s -> {
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
        carryableStorageManager.storages.stream()
            .filter(s -> s.getType() == CarryableStorageType.SEED_BOX)
            .findFirst()
            .ifPresent(seedBox -> seedBox.getItems().forEach(itemStacksIterator::add));
      } else if (itemStack.getId() == ItemID.RUNE_POUCH) {
        carryableStorageManager.storages.stream()
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
      carryableStorageManager.storages.stream()
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
    String data = deathbank.getType().getConfigKey() + ";"
        + deathbank.isLocked() + ";"
        + deathbank.getLastUpdated() + ";"
        + deathbank.getItems().stream().map(i -> i.getId() + "," + i.getQuantity())
        .collect(Collectors.joining("="));

    configManager.setRSProfileConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        getConfigKey() + ".deathbank",
        data
    );
  }

  private void saveDeathpiles() {
    configManager.setRSProfileConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        getConfigKey() + "." + DeathStorageType.DEATHPILE.getConfigKey(),
        storages.stream().filter(Deathpile.class::isInstance)
            .map(s -> {
              Deathpile d = (Deathpile) s;
              return d.getPlayedMinutesAtCreation() + ";"
                  + d.getWorldPoint().getX() + "," + d.getWorldPoint().getY() + ","
                  + d.getWorldPoint().getPlane() + ";"
                  + d.getItems().stream().map(i -> i.getId() + "," + i.getQuantity())
                  .collect(Collectors.joining("="));
            }).collect(Collectors.joining("$"))
    );
  }

  private void saveLostDeathbanks() {
    configManager.setRSProfileConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        getConfigKey() + ".lostdeathbanks",
        storages.stream().filter(s -> s instanceof Deathbank && s != deathbank)
            .map(s -> {
              Deathbank d = (Deathbank) s;
              return d.getType().getConfigKey() + ";"
                  + d.getLostAt() + ";"
                  + d.getItems().stream().map(i -> i.getId() + "," + i.getQuantity())
                  .collect(Collectors.joining("="));
            }).collect(Collectors.joining("$"))
    );
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

    String data = configManager.getConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        profileKey,
        getConfigKey() + ".deathbank",
        String.class
    );
      if (data == null) {
          return;
      }

    String[] dataSplit = data.split(";");
      if (dataSplit.length != 4) {
          return;
      }

    String deathbankKey = dataSplit[0];
    DeathStorageType deathStorageType = Arrays.stream(DeathStorageType.values())
        .filter(s -> Objects.equals(s.getConfigKey(), deathbankKey))
        .findFirst().orElse(null);
      if (deathStorageType == null) {
          return;
      }

    boolean locked = Objects.equals(dataSplit[1], "true");

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
        if (itemId == 0 || quantity == 0) {
            continue;
        }

      ItemStack item = new ItemStack(itemId, client, clientThread, itemManager);
      item.setQuantity(quantity);
      items.add(item);
    }
      if (items.isEmpty()) {
          return;
      }

    deathbank.type = deathStorageType;
    deathbank.lastUpdated = lastUpdate;
    deathbank.items = items;
    deathbank.locked = locked;
  }

  private void loadLostDeathbanks(String profileKey) {
    String data = configManager.getConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        profileKey,
        getConfigKey() + ".lostdeathbanks",
        String.class
    );
      if (data == null) {
          return;
      }

    String[] banksData = data.split("\\$");
    for (String banksDatum : banksData) {
      String[] dataSplit = banksDatum.split(";");
        if (dataSplit.length != 3) {
            return;
        }

      String deathbankKey = dataSplit[0];
      DeathStorageType deathStorageType = Arrays.stream(DeathStorageType.values())
          .filter(s -> Objects.equals(s.getConfigKey(), deathbankKey))
          .findFirst().orElse(null);
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
          if (itemId == 0 || quantity == 0) {
              continue;
          }

        ItemStack item = new ItemStack(itemId, client, clientThread, itemManager);
        item.setQuantity(quantity);
        items.add(item);
      }
        if (items.isEmpty()) {
            return;
        }

      Deathbank deathbank = new Deathbank(deathStorageType, client, itemManager);
      deathbank.lostAt = lostAt;
      deathbank.items = items;
      storages.add(deathbank);
    }
  }

  @Override
  void reset() {
    oldInventoryItems = null;
    storages.clear();
    storages.add(deathbank);
    clearDeathbank(false);
    enable();
    refreshMapPoints();
  }

  void refreshMapPoints() {
      if (worldMapPointManager == null || plugin.panelContainer.previewing) {
          return;
      }

    worldMapPointManager.removeIf(DeathWorldMapPoint.class::isInstance);

    int index = 1;
    for (DeathStorage storage : storages) {
        if (!(storage instanceof Deathpile)) {
            continue;
        }

      Deathpile deathpile = (Deathpile) storage;
        if (deathpile.hasExpired(plugin.panelContainer.previewing)) {
            continue;
        }

      deathpile.worldMapPoint = new DeathWorldMapPoint(deathpile.getWorldPoint(), itemManager,
          index++);
      worldMapPointManager.add(deathpile.getWorldMapPoint());
    }
  }

  private void loadDeathpiles(String profileKey) {
    String data = configManager.getConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        profileKey,
        getConfigKey() + "." + DeathStorageType.DEATHPILE.getConfigKey(),
        String.class
    );
      if (data == null) {
          return;
      }

    String[] pilesData = data.split("\\$");
    for (String pilesDatum : pilesData) {
      String[] dataSplit = pilesDatum.split(";");
        if (dataSplit.length != 3) {
            continue;
        }

      String[] worldPointSplit = dataSplit[1].split(",");
        if (worldPointSplit.length != 3) {
            continue;
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
          if (itemId == 0 || quantity == 0) {
              continue;
          }

        ItemStack item = new ItemStack(itemId, client, clientThread, itemManager);
        item.setQuantity(quantity);
        items.add(item);
      }
        if (items.isEmpty()) {
            continue;
        }

      storages.add(
          new Deathpile(
              client,
              itemManager,
              NumberUtils.toInt(dataSplit[0], 0),
              new WorldPoint(
                  NumberUtils.toInt(worldPointSplit[0], 0),
                  NumberUtils.toInt(worldPointSplit[1], 0),
                  NumberUtils.toInt(worldPointSplit[2], 0)
              ),
              this,
              items
          )
      );
    }

    refreshMapPoints();
  }
}
