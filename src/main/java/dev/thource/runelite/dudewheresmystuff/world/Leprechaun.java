package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.SaveFieldFormatter;
import dev.thource.runelite.dudewheresmystuff.SaveFieldLoader;
import dev.thource.runelite.dudewheresmystuff.Var;
import dev.thource.runelite.dudewheresmystuff.carryable.BottomlessBucket;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import lombok.Setter;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
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
  public boolean onVarbitChanged(VarbitChanged varbitChanged) {
    boolean updated = updateRakes(varbitChanged);

    if (updateSeedDibbers(varbitChanged)) {
      updated = true;
    }
    if (updateSpades(varbitChanged)) {
      updated = true;
    }
    if (updateSecateurs(varbitChanged)) {
      updated = true;
    }
    if (updateWateringCan(varbitChanged)) {
      updated = true;
    }
    if (updateTrowels(varbitChanged)) {
      updated = true;
    }
    if (updatePlantCures(varbitChanged)) {
      updated = true;
    }
    if (updateBottomlessBucket(varbitChanged)) {
      updated = true;
    }
    if (updateBuckets(varbitChanged)) {
      updated = true;
    }
    if (updateComposts(varbitChanged)) {
      updated = true;
    }
    if (updateSuperComposts(varbitChanged)) {
      updated = true;
    }
    if (updateUltraComposts(varbitChanged)) {
      updated = true;
    }

    return updated;
  }

  private boolean updateBottomlessBucket(VarbitChanged varbitChanged) {
    var typeVar = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_BOTTOMLESS_BUCKET_TYPE);
    var quantityVar = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_BOTTOMLESS_BUCKET_QUANTITY);

    if (!typeVar.wasChanged() && !quantityVar.wasChanged()) {
      return false;
    }

    var client = plugin.getClient();
    return setBottomlessBucketVars(typeVar.getValue(client), quantityVar.getValue(client));
  }

  private boolean updateRakes(VarbitChanged varbitChanged) {
    var var1 = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_RAKE);
    var var2 = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_EXTRARAKES);

    if (!var1.wasChanged() && !var2.wasChanged()) {
      return false;
    }

    var client = plugin.getClient();
    var quantity = var1.getValue(client) + var2.getValue(client) * 2;
    if (quantity == rakes.getQuantity()) {
      return false;
    }

    rakes.setQuantity(quantity);
    return true;
  }

  private boolean updateSeedDibbers(VarbitChanged varbitChanged) {
    var var1 = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_DIBBER);
    var var2 = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_EXTRADIBBERS);

    if (!var1.wasChanged() && !var2.wasChanged()) {
      return false;
    }

    var client = plugin.getClient();
    var quantity = var1.getValue(client) + var2.getValue(client) * 2;
    if (quantity == seedDibbers.getQuantity()) {
      return false;
    }

    seedDibbers.setQuantity(quantity);
    return true;
  }

  private boolean updateSpades(VarbitChanged varbitChanged) {
    var var1 = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_SPADE);
    var var2 = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_EXTRASPADES);

    if (!var1.wasChanged() && !var2.wasChanged()) {
      return false;
    }

    var client = plugin.getClient();
    var quantity = var1.getValue(client) + var2.getValue(client) * 2;
    if (quantity == spades.getQuantity()) {
      return false;
    }

    spades.setQuantity(quantity);
    return true;
  }

  private boolean updateSecateurs(VarbitChanged varbitChanged) {
    var var1 = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_SECATEURS);
    var var2 = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_EXTRASECATEURS);
    var typeVar = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_FAIRYSECATEURS);

    if (!var1.wasChanged() && !var2.wasChanged() && !typeVar.wasChanged()) {
      return false;
    }

    boolean updated = false;

    var client = plugin.getClient();
    var quantity = var1.getValue(client) + var2.getValue(client) * 2;
    if (quantity != secateurs.getQuantity()) {
      secateurs.setQuantity(quantity);
      updated = true;
    }

    if (setSecateursType(typeVar.getValue(client))) {
      updated = true;
    }

    return updated;
  }

  private boolean updateTrowels(VarbitChanged varbitChanged) {
    var var1 = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_TROWEL);
    var var2 = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_EXTRATROWELS);

    if (!var1.wasChanged() && !var2.wasChanged()) {
      return false;
    }

    var client = plugin.getClient();
    var quantity = var1.getValue(client) + var2.getValue(client) * 2;
    if (quantity == trowels.getQuantity()) {
      return false;
    }

    trowels.setQuantity(quantity);
    return true;
  }

  private boolean updatePlantCures(VarbitChanged varbitChanged) {
    var var = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_PLANTCURE);
    if (!var.wasChanged()) {
      return false;
    }

    var quantity = var.getValue(plugin.getClient());
    if (quantity == plantCures.getQuantity()) {
      return false;
    }

    plantCures.setQuantity(quantity);
    return true;
  }

  private boolean updateBuckets(VarbitChanged varbitChanged) {
    var var1 = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_BUCKETS);
    var var2 = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_EXTRABUCKETS);
    var var3 = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_EXTRA2BUCKETS);

    if (!var1.wasChanged() && !var2.wasChanged() && !var3.wasChanged()) {
      return false;
    }

    var client = plugin.getClient();
    var quantity = var1.getValue(client) + var2.getValue(client) * 32 + var3.getValue(client) * 256;
    if (quantity == buckets.getQuantity()) {
      return false;
    }

    buckets.setQuantity(quantity);
    return true;
  }

  private boolean updateComposts(VarbitChanged varbitChanged) {
    var var1 = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_COMPOST);
    var var2 = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_EXTRACOMPOST);

    if (!var1.wasChanged() && !var2.wasChanged()) {
      return false;
    }

    var client = plugin.getClient();
    var quantity = var1.getValue(client) + var2.getValue(client) * 256;
    if (quantity == composts.getQuantity()) {
      return false;
    }

    composts.setQuantity(quantity);
    return true;
  }

  private boolean updateSuperComposts(VarbitChanged varbitChanged) {
    var var1 = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_SUPERCOMPOST);
    var var2 = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_EXTRASUPERCOMPOST);

    if (!var1.wasChanged() && !var2.wasChanged()) {
      return false;
    }

    var client = plugin.getClient();
    var quantity = var1.getValue(client) + var2.getValue(client) * 256;
    if (quantity == superComposts.getQuantity()) {
      return false;
    }

    superComposts.setQuantity(quantity);
    return true;
  }

  private boolean updateUltraComposts(VarbitChanged varbitChanged) {
    var var = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_ULTRACOMPOST);
    if (!var.wasChanged()) {
      return false;
    }

    var quantity = var.getValue(plugin.getClient());
    if (quantity == ultraComposts.getQuantity()) {
      return false;
    }

    ultraComposts.setQuantity(quantity);
    return true;
  }

  private boolean updateWateringCan(VarbitChanged varbitChanged) {
    var var = Var.bit(varbitChanged, VarbitID.FARMING_TOOLS_WATERINGCAN);
    if (!var.wasChanged()) {
      return false;
    }

    var wateringCanState = var.getValue(plugin.getClient());
    if (wateringCanState >= WATERING_CAN_IDS.length) {
      return false;
    }

    return setWateringCanType(wateringCanState);
  }

  private boolean setSecateursType(int secateursType) {
    this.secateursType = secateursType;

    int secateursId = secateursType == 1 ? ItemID.FAIRY_ENCHANTED_SECATEURS : ItemID.SECATEURS;

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
