package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.SaveFieldFormatter;
import dev.thource.runelite.dudewheresmystuff.SaveFieldLoader;
import dev.thource.runelite.dudewheresmystuff.carryable.BottomlessBucket;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import lombok.Setter;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.config.ConfigManager;

/** Leprechaun is responsible for tracking the player's leprechaun-stored farming equipment. */
public class Leprechaun extends WorldStorage {

  private static final int[] WATERING_CAN_IDS = {
      -1,
      ItemID.WATERING_CAN_0,
      ItemID.WATERING_CAN_1,
      ItemID.WATERING_CAN_2,
      ItemID.WATERING_CAN_3,
      ItemID.WATERING_CAN_4,
      ItemID.WATERING_CAN_5,
      ItemID.WATERING_CAN_6,
      ItemID.WATERING_CAN_7,
      ItemID.WATERING_CAN_8,
      ItemID.ZEAH_WATERINGCAN
  };
  private final ItemStack rakes;
  private final ItemStack seedDibbers;
  private final ItemStack spades;
  private final ItemStack secateurs;
  private final ItemStack wateringCan;
  private final ItemStack trowels;
  private final ItemStack plantCures;
  private final ItemStack bottomlessBucket;
  private final ItemStack buckets;
  private final ItemStack composts;
  private final ItemStack superComposts;
  private final ItemStack ultraComposts;
  // 0 = normal, 1 = magic
  private int secateursType;
  // WATERING_CAN_IDS
  private int wateringCanType;
  // 0 = none, 1 = empty, 2 = compost, 3 = supercompost, 4 = ultracompost
  private int bottomlessBucketType;
  private int bottomlessBucketCharges;
  @Setter private BottomlessBucket bottomlessBucketStorage;

  /** A constructor. */
  public Leprechaun(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.LEPRECHAUN, plugin);

    hasStaticItems = true;

    rakes = new ItemStack(ItemID.RAKE, plugin);
    seedDibbers = new ItemStack(ItemID.DIBBER, plugin);
    spades = new ItemStack(ItemID.SPADE, plugin);
    secateurs = new ItemStack(ItemID.SECATEURS, plugin);
    wateringCan = new ItemStack(ItemID.WATERING_CAN_0, plugin);
    trowels = new ItemStack(ItemID.GARDENING_TROWEL, plugin);
    plantCures = new ItemStack(ItemID.PLANT_CURE, plugin);
    bottomlessBucket = new ItemStack(ItemID.BOTTOMLESS_COMPOST_BUCKET, plugin);
    buckets = new ItemStack(ItemID.BUCKET_EMPTY, plugin);
    composts = new ItemStack(ItemID.BUCKET_COMPOST, plugin);
    superComposts = new ItemStack(ItemID.BUCKET_SUPERCOMPOST, plugin);
    ultraComposts = new ItemStack(ItemID.BUCKET_ULTRACOMPOST, plugin);

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

  public static int[] getWateringCanIds() {
    return WATERING_CAN_IDS.clone();
  }

  @Override
  protected ArrayList<String> getSaveValues() {
    ArrayList<String> saveValues = super.getSaveValues();

    saveValues.add(SaveFieldFormatter.format(secateursType));
    saveValues.add(SaveFieldFormatter.format(wateringCanType));
    saveValues.add(SaveFieldFormatter.format(bottomlessBucketType));
    saveValues.add(SaveFieldFormatter.format(bottomlessBucketCharges));

    return saveValues;
  }

  @Override
  protected void loadValues(ArrayList<String> values) {
    super.loadValues(values);

    secateursType = SaveFieldLoader.loadInt(values, secateursType);
    wateringCanType = SaveFieldLoader.loadInt(values, wateringCanType);
    bottomlessBucketType = SaveFieldLoader.loadInt(values, bottomlessBucketType);
    bottomlessBucketCharges = SaveFieldLoader.loadInt(values, bottomlessBucketCharges);
  }

  @Override
  public boolean onVarbitChanged() {
    boolean updated = updateRakes();

    if (updateSeedDibbers()) {
      updated = true;
    }
    if (updateSpades()) {
      updated = true;
    }
    if (updateSecateurs()) {
      updated = true;
    }
    if (updateWateringCan()) {
      updated = true;
    }
    if (updateTrowels()) {
      updated = true;
    }
    if (updatePlantCures()) {
      updated = true;
    }
    if (updateBottomlessBucket()) {
      updated = true;
    }
    if (updateBuckets()) {
      updated = true;
    }
    if (updateComposts()) {
      updated = true;
    }
    if (updateSuperComposts()) {
      updated = true;
    }
    if (updateUltraComposts()) {
      updated = true;
    }

    return updated;
  }

  private boolean updateBottomlessBucket() {
    int type = plugin.getClient().getVarbitValue(7915);
    int charges = plugin.getClient().getVarbitValue(7916);

    return setBottomlessBucketVars(type, charges);
  }

  private boolean updateRakes() {
    int quantity =
        plugin.getClient().getVarbitValue(1435) + (plugin.getClient().getVarbitValue(8357) * 2);

    if (quantity == rakes.getQuantity()) {
      return false;
    }

    rakes.setQuantity(quantity);
    return true;
  }

  private boolean updateSeedDibbers() {
    int quantity =
        plugin.getClient().getVarbitValue(1436) + (plugin.getClient().getVarbitValue(8358) * 2);

    if (quantity == seedDibbers.getQuantity()) {
      return false;
    }

    seedDibbers.setQuantity(quantity);
    return true;
  }

  private boolean updateSpades() {
    int quantity =
        plugin.getClient().getVarbitValue(1437) + (plugin.getClient().getVarbitValue(8361) * 2);

    if (quantity == spades.getQuantity()) {
      return false;
    }

    spades.setQuantity(quantity);
    return true;
  }

  private boolean updateSecateurs() {
    boolean updated = false;
    int quantity =
        plugin.getClient().getVarbitValue(1438) + (plugin.getClient().getVarbitValue(8359) * 2);

    if (quantity != secateurs.getQuantity()) {
      secateurs.setQuantity(quantity);
      updated = true;
    }

    if (setSecateursType(plugin.getClient().getVarbitValue(1848))) {
      updated = true;
    }

    return updated;
  }

  private boolean updateTrowels() {
    int quantity =
        plugin.getClient().getVarbitValue(1440) + (plugin.getClient().getVarbitValue(8360) * 2);

    if (quantity == trowels.getQuantity()) {
      return false;
    }

    trowels.setQuantity(quantity);
    return true;
  }

  private boolean updatePlantCures() {
    int quantity = plugin.getClient().getVarbitValue(6268);

    if (quantity == plantCures.getQuantity()) {
      return false;
    }

    plantCures.setQuantity(quantity);
    return true;
  }

  private boolean updateBuckets() {
    int quantity =
        plugin.getClient().getVarbitValue(1441)
            + (plugin.getClient().getVarbitValue(4731) * 32)
            + (plugin.getClient().getVarbitValue(6265) * 256);

    if (quantity == buckets.getQuantity()) {
      return false;
    }

    buckets.setQuantity(quantity);
    return true;
  }

  private boolean updateComposts() {
    int quantity =
        plugin.getClient().getVarbitValue(1442) + (plugin.getClient().getVarbitValue(6266) * 256);

    if (quantity == composts.getQuantity()) {
      return false;
    }

    composts.setQuantity(quantity);
    return true;
  }

  private boolean updateSuperComposts() {
    int quantity =
        plugin.getClient().getVarbitValue(1443) + (plugin.getClient().getVarbitValue(6267) * 256);

    if (quantity == superComposts.getQuantity()) {
      return false;
    }

    superComposts.setQuantity(quantity);
    return true;
  }

  private boolean updateUltraComposts() {
    int quantity = plugin.getClient().getVarbitValue(5732);

    if (quantity == ultraComposts.getQuantity()) {
      return false;
    }

    ultraComposts.setQuantity(quantity);
    return true;
  }

  private boolean updateWateringCan() {
    int wateringCanState = plugin.getClient().getVarbitValue(1439);
    if (wateringCanState >= WATERING_CAN_IDS.length) {
      return false;
    }

    return setWateringCanType(wateringCanState);
  }

  private boolean setSecateursType(int secateursType) {
    this.secateursType = secateursType;

    int secateursId =
        secateursType == 1 ? ItemID.FAIRY_ENCHANTED_SECATEURS : ItemID.SECATEURS;

    if (secateurs.getId() == secateursId) {
      return false;
    }

    secateurs.setId(secateursId, plugin);
    return true;
  }

  private boolean setWateringCanType(int wateringCanType) {
    this.wateringCanType = wateringCanType;
    int wateringCanId = WATERING_CAN_IDS[wateringCanType];
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

    wateringCan.setId(wateringCanId, plugin);
    wateringCan.setQuantity(1);
    return true;
  }

  private boolean setBottomlessBucketVars(int bottomlessBucketType, int bottomlessBucketCharges) {
    final int oldBottomlessBucketCharges = this.bottomlessBucketCharges;
    this.bottomlessBucketType = bottomlessBucketType;
    this.bottomlessBucketCharges = bottomlessBucketCharges;

    int itemId = ItemID.BOTTOMLESS_COMPOST_BUCKET;

    if (bottomlessBucketType == 1) {
      bottomlessBucket.setName("Bottomless compost bucket");
    } else if (bottomlessBucketType != 0) {
      itemId = ItemID.BOTTOMLESS_COMPOST_BUCKET_FILLED;
    }

    if (oldBottomlessBucketCharges == bottomlessBucketCharges
        && itemId == bottomlessBucket.getId()) {
      return false;
    }

    bottomlessBucket.setId(itemId);
    String name = "Bottomless compost bucket";
    if (bottomlessBucketCharges > 0) {
      name += " (" + bottomlessBucketCharges + " ";
      if (bottomlessBucketType == 2) {
        name += "compost)";
      } else if (bottomlessBucketType == 3) {
        name += "supercompost)";
      } else if (bottomlessBucketType == 4) {
        name += "ultracompost)";
      }
    }
    bottomlessBucket.setName(name);
    bottomlessBucket.setQuantity(bottomlessBucketType == 0 ? 0 : 1);
    if (bottomlessBucketStorage != null) {
      bottomlessBucketStorage.updateCompost(bottomlessBucketType, bottomlessBucketCharges);

      if (bottomlessBucketStorage.getStoragePanel() != null) {
        bottomlessBucketStorage.getStoragePanel().refreshItems();
        SwingUtilities.invokeLater(bottomlessBucketStorage.getStoragePanel()::update);
      }
    }
    return true;
  }

  @Override
  public void reset() {
    super.reset();
    secateursType = 0;
    wateringCanType = 0;
    bottomlessBucketType = 0;
  }

  @Override
  public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
    super.load(configManager, managerConfigKey, profileKey);

    setSecateursType(secateursType);
    setWateringCanType(wateringCanType);
    setBottomlessBucketVars(bottomlessBucketType, bottomlessBucketCharges);
  }
}
