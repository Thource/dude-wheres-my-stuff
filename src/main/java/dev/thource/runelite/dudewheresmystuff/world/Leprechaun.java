package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

/** Leprechaun is responsible for tracking the player's leprechaun-stored farming equipment. */
public class Leprechaun extends WorldStorage {

  private static final int[] WATERING_CAN_IDS = {
    -1,
    ItemID.WATERING_CAN,
    ItemID.WATERING_CAN1,
    ItemID.WATERING_CAN2,
    ItemID.WATERING_CAN3,
    ItemID.WATERING_CAN4,
    ItemID.WATERING_CAN5,
    ItemID.WATERING_CAN6,
    ItemID.WATERING_CAN7,
    ItemID.WATERING_CAN8,
    ItemID.GRICOLLERS_CAN
  };
  private final ItemStack rakes;
  private final ItemStack seedDibbers;
  private final ItemStack spades;
  private final ItemStack secateurs;
  private final ItemStack wateringCan;
  private final ItemStack trowels;
  private final ItemStack plantCures;
  private int bottomlessBucketCharges;
  private final ItemStack bottomlessBucket;
  private final ItemStack buckets;
  private final ItemStack composts;
  private final ItemStack superComposts;
  private final ItemStack ultraComposts;

  /** A constructor. */
  public Leprechaun(Client client, ClientThread clientThread, ItemManager itemManager) {
    super(WorldStorageType.LEPRECHAUN, client, itemManager);

    rakes = new ItemStack(ItemID.RAKE, client, clientThread, itemManager);
    seedDibbers = new ItemStack(ItemID.SEED_DIBBER, client, clientThread, itemManager);
    spades = new ItemStack(ItemID.SPADE, client, clientThread, itemManager);
    secateurs = new ItemStack(ItemID.SECATEURS, client, clientThread, itemManager);
    wateringCan = new ItemStack(ItemID.WATERING_CAN, client, clientThread, itemManager);
    trowels = new ItemStack(ItemID.GARDENING_TROWEL, client, clientThread, itemManager);
    plantCures = new ItemStack(ItemID.PLANT_CURE, client, clientThread, itemManager);
    bottomlessBucket =
        new ItemStack(ItemID.BOTTOMLESS_COMPOST_BUCKET, client, clientThread, itemManager);
    buckets = new ItemStack(ItemID.EMPTY_BUCKET, client, clientThread, itemManager);
    composts = new ItemStack(ItemID.COMPOST, client, clientThread, itemManager);
    superComposts = new ItemStack(ItemID.SUPERCOMPOST, client, clientThread, itemManager);
    ultraComposts = new ItemStack(ItemID.ULTRACOMPOST, client, clientThread, itemManager);

    items.add(rakes);
    items.add(seedDibbers);
    items.add(spades);
    items.add(secateurs);
    items.add(wateringCan);
    items.add(trowels);
    items.add(plantCures);
    items.add(bottomlessBucket);
    items.add(buckets);
    items.add(composts);
    items.add(superComposts);
    items.add(ultraComposts);
  }

  @Override
  public boolean onVarbitChanged() {
    AtomicBoolean updated = new AtomicBoolean(false);

    if (updateRakes()) {
      updated.set(true);
    }
    if (updateSeedDibbers()) {
      updated.set(true);
    }
    if (updateSpades()) {
      updated.set(true);
    }
    if (updateSecateurs()) {
      updated.set(true);
    }
    if (updateWateringCan()) {
      updated.set(true);
    }
    if (updateTrowels()) {
      updated.set(true);
    }
    if (updatePlantCures()) {
      updated.set(true);
    }
    if (updateBottomlessBucket()) {
      updated.set(true);
    }
    if (updateBuckets()) {
      updated.set(true);
    }
    if (updateComposts()) {
      updated.set(true);
    }
    if (updateSuperComposts()) {
      updated.set(true);
    }
    if (updateUltraComposts()) {
      updated.set(true);
    }

    return updated.get();
  }

  private boolean updateBottomlessBucket() {
    int type = client.getVarbitValue(7915);

    int charges = 0;
    int itemId = ItemID.BOTTOMLESS_COMPOST_BUCKET;

    if (type == 1) {
      bottomlessBucket.setName("Bottomless compost bucket");
    } else if (type != 0) {
      itemId = ItemID.BOTTOMLESS_COMPOST_BUCKET_22997;
      charges = client.getVarbitValue(7916);
    }

    if (charges == bottomlessBucketCharges && itemId == bottomlessBucket.getId()) {
      return false;
    }

    bottomlessBucketCharges = charges;
    bottomlessBucket.setId(itemId);
    String name = "Bottomless compost bucket";
    if (charges > 0) {
      name += " (" + charges + " ";
      if (type == 2) {
        name += "compost)";
      } else if (type == 3) {
        name += "supercompost)";
      } else if (type == 4) {
        name += "ultracompost)";
      }
    }
    bottomlessBucket.setName(name);
    bottomlessBucket.setQuantity(type == 0 ? 0 : 1);
    return true;
  }

  private boolean updateRakes() {
    int quantity = client.getVarbitValue(1435) + (client.getVarbitValue(8357) * 2);

    if (quantity == rakes.getQuantity()) {
      return false;
    }

    rakes.setQuantity(quantity);
    return true;
  }

  private boolean updateSeedDibbers() {
    int quantity = client.getVarbitValue(1436) + (client.getVarbitValue(8358) * 2);

    if (quantity == seedDibbers.getQuantity()) {
      return false;
    }

    seedDibbers.setQuantity(quantity);
    return true;
  }

  private boolean updateSpades() {
    int quantity = client.getVarbitValue(1437) + (client.getVarbitValue(8361) * 2);

    if (quantity == spades.getQuantity()) {
      return false;
    }

    spades.setQuantity(quantity);
    return true;
  }

  private boolean updateSecateurs() {
    int secateursId = client.getVarbitValue(1848) == 1 ? ItemID.MAGIC_SECATEURS : ItemID.SECATEURS;
    int quantity = client.getVarbitValue(1438) + (client.getVarbitValue(8359) * 2);

    if (quantity == secateurs.getQuantity() && secateurs.getId() == secateursId) {
      return false;
    }

    if (secateurs.getId() != secateursId) {
      secateurs.setId(secateursId);
      secateurs.populateFromComposition(itemManager);
    }

    secateurs.setQuantity(quantity);
    return true;
  }

  private boolean updateTrowels() {
    int quantity = client.getVarbitValue(1440) + (client.getVarbitValue(8360) * 2);

    if (quantity == trowels.getQuantity()) {
      return false;
    }

    trowels.setQuantity(quantity);
    return true;
  }

  private boolean updatePlantCures() {
    int quantity = client.getVarbitValue(6268);

    if (quantity == plantCures.getQuantity()) {
      return false;
    }

    plantCures.setQuantity(quantity);
    return true;
  }

  private boolean updateBuckets() {
    int quantity =
        client.getVarbitValue(1441)
            + (client.getVarbitValue(4731) * 32)
            + (client.getVarbitValue(6265) * 256);

    if (quantity == buckets.getQuantity()) {
      return false;
    }

    buckets.setQuantity(quantity);
    return true;
  }

  private boolean updateComposts() {
    int quantity = client.getVarbitValue(1442) + (client.getVarbitValue(6266) * 256);

    if (quantity == composts.getQuantity()) {
      return false;
    }

    composts.setQuantity(quantity);
    return true;
  }

  private boolean updateSuperComposts() {
    int quantity = client.getVarbitValue(1443) + (client.getVarbitValue(6267) * 256);

    if (quantity == superComposts.getQuantity()) {
      return false;
    }

    superComposts.setQuantity(quantity);
    return true;
  }

  private boolean updateUltraComposts() {
    int quantity = client.getVarbitValue(5732);

    if (quantity == ultraComposts.getQuantity()) {
      return false;
    }

    ultraComposts.setQuantity(quantity);
    return true;
  }

  private boolean updateWateringCan() {
    int wateringCanState = client.getVarbitValue(1439);
    if (wateringCanState >= WATERING_CAN_IDS.length) {
      return false;
    }

    int wateringCanId = WATERING_CAN_IDS[wateringCanState];
    if (wateringCanId == -1) {
      if (wateringCan.getQuantity() == 0) {
        return false;
      }

      wateringCan.setQuantity(0);
      return true;
    }

    if (wateringCan.getQuantity() == 1 && wateringCan.getId() == wateringCanId) {
      return false;
    }

    wateringCan.setId(wateringCanId);
    wateringCan.setQuantity(1);
    wateringCan.populateFromComposition(itemManager);
    return true;
  }

  @Override
  public void reset() {
    for (ItemStack item : items) {
      item.setQuantity(0);
    }
    lastUpdated = -1;
    enable();
  }

  @Override
  public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
    List<ItemStack> loadedItems = loadItems(configManager, managerConfigKey, profileKey);
    if (loadedItems == null || loadedItems.isEmpty()) {
      return;
    }

    loadedItems.stream()
        .filter(i -> i.getId() != -1)
        .forEach(
            loadedItem -> {
              if (Arrays.stream(WATERING_CAN_IDS).anyMatch(i -> i == loadedItem.getId())) {
                wateringCan.setId(loadedItem.getId());
                wateringCan.setQuantity(loadedItem.getQuantity());
                wateringCan.populateFromComposition(itemManager);
                return;
              }

              if (loadedItem.getId() == ItemID.MAGIC_SECATEURS
                  || loadedItem.getId() == ItemID.SECATEURS) {
                secateurs.setId(loadedItem.getId());
                secateurs.setQuantity(loadedItem.getQuantity());
                secateurs.populateFromComposition(itemManager);
                return;
              }

              for (ItemStack item : items) {
                if (loadedItem.getId() == item.getId()) {
                  item.setQuantity(loadedItem.getQuantity());
                  break;
                }
              }
            });
  }
}
