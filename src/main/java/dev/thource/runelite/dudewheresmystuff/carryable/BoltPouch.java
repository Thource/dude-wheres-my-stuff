package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.client.config.ConfigManager;

/** BoltPouch is responsible for tracking which bolts the player has in the bolt pouch. */
@Slf4j
public class BoltPouch extends CarryableStorage {

  private static final int[] BOLT_TYPE_VARBITS = {
      2473,
      2474,
      2475,
      2476
  };
  private static final int[] BOLT_COUNT_VARBITS = {
      2469,
      2470,
      2471,
      2472
  };
  private static final int[] BOLT_ITEM_IDS = {
      -1,
      ItemID.BRONZE_BOLTS,
      ItemID.BLURITE_BOLTS,
      ItemID.IRON_BOLTS,
      ItemID.STEEL_BOLTS,
      ItemID.MITHRIL_BOLTS,
      ItemID.ADAMANT_BOLTS,
      ItemID.RUNITE_BOLTS,
      ItemID.SILVER_BOLTS, // 8
      ItemID.BRONZE_BOLTS_P,
      ItemID.BLURITE_BOLTS_P,
      ItemID.IRON_BOLTS_P,
      ItemID.STEEL_BOLTS_P,
      ItemID.MITHRIL_BOLTS_P,
      ItemID.ADAMANT_BOLTS_P,
      ItemID.RUNITE_BOLTS_P,
      ItemID.SILVER_BOLTS_P, // 16
      ItemID.BRONZE_BOLTS_P_6061,
      ItemID.BLURITE_BOLTS_P_9293,
      ItemID.IRON_BOLTS_P_9294,
      ItemID.STEEL_BOLTS_P_9295,
      ItemID.MITHRIL_BOLTS_P_9296,
      ItemID.ADAMANT_BOLTS_P_9297,
      ItemID.RUNITE_BOLTS_P_9298,
      ItemID.SILVER_BOLTS_P_9299, // 24
      ItemID.BRONZE_BOLTS_P_6062,
      ItemID.BLURITE_BOLTS_P_9300,
      ItemID.IRON_BOLTS_P_9301,
      ItemID.STEEL_BOLTS_P_9302,
      ItemID.MITHRIL_BOLTS_P_9303,
      ItemID.ADAMANT_BOLTS_P_9304,
      ItemID.RUNITE_BOLTS_P_9305,
      ItemID.SILVER_BOLTS_P_9306, // 32
      ItemID.OPAL_BOLTS,
      ItemID.JADE_BOLTS,
      ItemID.PEARL_BOLTS,
      ItemID.TOPAZ_BOLTS,
      ItemID.SAPPHIRE_BOLTS,
      ItemID.EMERALD_BOLTS,
      ItemID.RUBY_BOLTS,
      ItemID.DIAMOND_BOLTS, // 40
      ItemID.DRAGONSTONE_BOLTS,
      ItemID.ONYX_BOLTS,
      ItemID.OPAL_BOLTS_E,
      ItemID.JADE_BOLTS_E,
      ItemID.PEARL_BOLTS_E,
      ItemID.TOPAZ_BOLTS_E,
      ItemID.SAPPHIRE_BOLTS_E,
      ItemID.EMERALD_BOLTS_E, // 48
      ItemID.RUBY_BOLTS_E,
      ItemID.DIAMOND_BOLTS_E,
      ItemID.DRAGONSTONE_BOLTS_E,
      ItemID.ONYX_BOLTS_E,
      ItemID.MITH_GRAPPLE_9419,
      ItemID.BARBED_BOLTS,
      ItemID.BONE_BOLTS,
      ItemID.BROAD_BOLTS, // 56
      ItemID.AMETHYST_BROAD_BOLTS,
      ItemID.DRAGON_BOLTS,
      ItemID.DRAGON_BOLTS_P,
      ItemID.DRAGON_BOLTS_P_21926,
      ItemID.DRAGON_BOLTS_P_21928,
      ItemID.OPAL_DRAGON_BOLTS,
      ItemID.JADE_DRAGON_BOLTS,
      ItemID.PEARL_DRAGON_BOLTS, // 64
      ItemID.TOPAZ_DRAGON_BOLTS,
      ItemID.SAPPHIRE_DRAGON_BOLTS,
      ItemID.EMERALD_DRAGON_BOLTS,
      ItemID.RUBY_DRAGON_BOLTS,
      ItemID.DIAMOND_DRAGON_BOLTS,
      ItemID.DRAGONSTONE_DRAGON_BOLTS,
      ItemID.ONYX_DRAGON_BOLTS,
      ItemID.OPAL_DRAGON_BOLTS_E, // 72
      ItemID.JADE_DRAGON_BOLTS_E,
      ItemID.PEARL_DRAGON_BOLTS_E,
      ItemID.TOPAZ_DRAGON_BOLTS_E,
      ItemID.SAPPHIRE_DRAGON_BOLTS_E,
      ItemID.EMERALD_DRAGON_BOLTS_E,
      ItemID.RUBY_DRAGON_BOLTS_E,
      ItemID.DIAMOND_DRAGON_BOLTS_E,
      ItemID.DRAGONSTONE_DRAGON_BOLTS_E, // 80
      ItemID.ONYX_DRAGON_BOLTS_E,
      ItemID.BOLT_RACK,
  };

  BoltPouch(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.BOLT_POUCH, plugin);

    items.add(new ItemStack(-1, 0, plugin));
    items.add(new ItemStack(-1, 0, plugin));
    items.add(new ItemStack(-1, 0, plugin));
    items.add(new ItemStack(-1, 0, plugin));
  }

  @Override
  public boolean onVarbitChanged() {
    boolean updated = false;

    for (int i = 0; i < BOLT_TYPE_VARBITS.length; i++) {
      int boltCount = plugin.getClient().getVarbitValue(BOLT_COUNT_VARBITS[i]);
      int boltItemId = getBoltItemId(plugin.getClient().getVarbitValue(BOLT_TYPE_VARBITS[i]));
      ItemStack boltStack = items.get(i);

      if (boltCount != boltStack.getQuantity()) {
        boltStack.setQuantity(boltCount);
        updated = true;
      }
      if (boltItemId != boltStack.getId()) {
        boltStack.setId(boltItemId, plugin);
        updated = true;
      }
    }

    return updated;
  }

  private int getBoltItemId(int varbitValue) {
    if (varbitValue < 0 || varbitValue >= BOLT_ITEM_IDS.length) {
      return -1;
    }

    return BOLT_ITEM_IDS[varbitValue];
  }

  @Override
  public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
    super.load(configManager, managerConfigKey, profileKey);

    while (items.size() < 4) {
      items.add(new ItemStack(-1, 0, plugin));
    }
  }

  @Override
  public void reset() {
    lastUpdated = -1;
    lastSaveString = null;
    enable();
  }
}
