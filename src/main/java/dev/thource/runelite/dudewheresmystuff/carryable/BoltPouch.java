package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Var;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;

/** BoltPouch is responsible for tracking which bolts the player has in the bolt pouch. */
@Slf4j
public class BoltPouch extends CarryableStorage {

  private static final int[] BOLT_TYPE_VARBITS = {
    VarbitID.XBOWS_POUCH_SLOT1,
    VarbitID.XBOWS_POUCH_SLOT2,
    VarbitID.XBOWS_POUCH_SLOT3,
    VarbitID.XBOWS_POUCH_SLOT4
  };
  private static final int[] BOLT_COUNT_VARBITS = {
    VarbitID.XBOWS_POUCH_NUM1,
    VarbitID.XBOWS_POUCH_NUM2,
    VarbitID.XBOWS_POUCH_NUM3,
    VarbitID.XBOWS_POUCH_NUM4
  };
  private static final int[] BOLT_ITEM_IDS = {
    -1,
    ItemID.BOLT,
    ItemID.XBOWS_CROSSBOW_BOLTS_BLURITE,
    ItemID.XBOWS_CROSSBOW_BOLTS_IRON,
    ItemID.XBOWS_CROSSBOW_BOLTS_STEEL,
    ItemID.XBOWS_CROSSBOW_BOLTS_MITHRIL,
    ItemID.XBOWS_CROSSBOW_BOLTS_ADAMANTITE,
    ItemID.XBOWS_CROSSBOW_BOLTS_RUNITE,
    ItemID.XBOWS_CROSSBOW_BOLTS_SILVER, // 8
    ItemID.POISON_BOLT,
    ItemID.XBOWS_CROSSBOW_BOLTS_BLURITE_POISONED,
    ItemID.XBOWS_CROSSBOW_BOLTS_IRON_POISONED,
    ItemID.XBOWS_CROSSBOW_BOLTS_STEEL_POISONED,
    ItemID.XBOWS_CROSSBOW_BOLTS_MITHRIL_POISONED,
    ItemID.XBOWS_CROSSBOW_BOLTS_ADAMANTITE_POISONED,
    ItemID.XBOWS_CROSSBOW_BOLTS_RUNITE_POISONED,
    ItemID.XBOWS_CROSSBOW_BOLTS_SILVER_POISONED, // 16
    ItemID.POISON_BOLT_,
    ItemID.XBOWS_CROSSBOW_BOLTS_BLURITE_POISONED_,
    ItemID.XBOWS_CROSSBOW_BOLTS_IRON_POISONED_,
    ItemID.XBOWS_CROSSBOW_BOLTS_STEEL_POISONED_,
    ItemID.XBOWS_CROSSBOW_BOLTS_MITHRIL_POISONED_,
    ItemID.XBOWS_CROSSBOW_BOLTS_ADAMANTITE_POISONED_,
    ItemID.XBOWS_CROSSBOW_BOLTS_RUNITE_POISONED_,
    ItemID.XBOWS_CROSSBOW_BOLTS_SILVER_POISONED_, // 24
    ItemID.POISON_BOLT__,
    ItemID.XBOWS_CROSSBOW_BOLTS_BLURITE_POISONED__,
    ItemID.XBOWS_CROSSBOW_BOLTS_IRON_POISONED__,
    ItemID.XBOWS_CROSSBOW_BOLTS_STEEL_POISONED__,
    ItemID.XBOWS_CROSSBOW_BOLTS_MITHRIL_POISONED__,
    ItemID.XBOWS_CROSSBOW_BOLTS_ADAMANTITE_POISONED__,
    ItemID.XBOWS_CROSSBOW_BOLTS_RUNITE_POISONED__,
    ItemID.XBOWS_CROSSBOW_BOLTS_SILVER_POISONED__, // 32
    ItemID.OPAL_BOLT,
    ItemID.XBOWS_CROSSBOW_BOLTS_BLURITE_TIPPED_JADE,
    ItemID.PEARL_BOLT,
    ItemID.XBOWS_CROSSBOW_BOLTS_STEEL_TIPPED_REDTOPAZ,
    ItemID.XBOWS_CROSSBOW_BOLTS_MITHRIL_TIPPED_SAPPHIRE,
    ItemID.XBOWS_CROSSBOW_BOLTS_MITHRIL_TIPPED_EMERALD,
    ItemID.XBOWS_CROSSBOW_BOLTS_ADAMANTITE_TIPPED_RUBY,
    ItemID.XBOWS_CROSSBOW_BOLTS_ADAMANTITE_TIPPED_DIAMOND, // 40
    ItemID.XBOWS_CROSSBOW_BOLTS_RUNITE_TIPPED_DRAGONSTONE,
    ItemID.XBOWS_CROSSBOW_BOLTS_RUNITE_TIPPED_ONYX,
    ItemID.XBOWS_CROSSBOW_BOLTS_BRONZE_TIPPED_OPAL_ENCHANTED,
    ItemID.XBOWS_CROSSBOW_BOLTS_BLURITE_TIPPED_JADE_ENCHANTED,
    ItemID.XBOWS_CROSSBOW_BOLTS_IRON_TIPPED_PEARL_ENCHANTED,
    ItemID.XBOWS_CROSSBOW_BOLTS_STEEL_TIPPED_REDTOPAZ_ENCHANTED,
    ItemID.XBOWS_CROSSBOW_BOLTS_MITHRIL_TIPPED_SAPPHIRE_ENCHANTED,
    ItemID.XBOWS_CROSSBOW_BOLTS_MITHRIL_TIPPED_EMERALD_ENCHANTED, // 48
    ItemID.XBOWS_CROSSBOW_BOLTS_ADAMANTITE_TIPPED_RUBY_ENCHANTED,
    ItemID.XBOWS_CROSSBOW_BOLTS_ADAMANTITE_TIPPED_DIAMOND_ENCHANTED,
    ItemID.XBOWS_CROSSBOW_BOLTS_RUNITE_TIPPED_DRAGONSTONE_ENCHANTED,
    ItemID.XBOWS_CROSSBOW_BOLTS_RUNITE_TIPPED_ONYX_ENCHANTED,
    ItemID.XBOWS_GRAPPLE_TIP_BOLT_MITHRIL_ROPE,
    ItemID.BARBED_BOLT,
    ItemID.DTTD_BONE_CROSSBOW_BOLT,
    ItemID.SLAYER_BROAD_BOLT, // 56
    ItemID.SLAYER_BROAD_BOLT_AMETHYST,
    ItemID.DRAGON_BOLTS,
    ItemID.DRAGON_BOLTS_P,
    ItemID.DRAGON_BOLTS_P_,
    ItemID.DRAGON_BOLTS_P__,
    ItemID.DRAGON_BOLTS_UNENCHANTED_OPAL,
    ItemID.DRAGON_BOLTS_UNENCHANTED_JADE,
    ItemID.DRAGON_BOLTS_UNENCHANTED_PEARL, // 64
    ItemID.DRAGON_BOLTS_UNENCHANTED_TOPAZ,
    ItemID.DRAGON_BOLTS_UNENCHANTED_SAPPHIRE,
    ItemID.DRAGON_BOLTS_UNENCHANTED_EMERALD,
    ItemID.DRAGON_BOLTS_UNENCHANTED_RUBY,
    ItemID.DRAGON_BOLTS_UNENCHANTED_DIAMOND,
    ItemID.DRAGON_BOLTS_UNENCHANTED_DRAGONSTONE,
    ItemID.DRAGON_BOLTS_UNENCHANTED_ONYX,
    ItemID.DRAGON_BOLTS_ENCHANTED_OPAL, // 72
    ItemID.DRAGON_BOLTS_ENCHANTED_JADE,
    ItemID.DRAGON_BOLTS_ENCHANTED_PEARL,
    ItemID.DRAGON_BOLTS_ENCHANTED_TOPAZ,
    ItemID.DRAGON_BOLTS_ENCHANTED_SAPPHIRE,
    ItemID.DRAGON_BOLTS_ENCHANTED_EMERALD,
    ItemID.DRAGON_BOLTS_ENCHANTED_RUBY,
    ItemID.DRAGON_BOLTS_ENCHANTED_DIAMOND,
    ItemID.DRAGON_BOLTS_ENCHANTED_DRAGONSTONE, // 80
    ItemID.DRAGON_BOLTS_ENCHANTED_ONYX,
    ItemID.BARROWS_KARIL_AMMO,
  };

  BoltPouch(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.BOLT_POUCH, plugin);

    resetItems();
  }

  @Override
  public boolean onVarbitChanged(VarbitChanged varbitChanged) {
    var updated = false;

    for (int i = 0; i < BOLT_TYPE_VARBITS.length; i++) {
      var countVar = Var.bit(varbitChanged, BOLT_COUNT_VARBITS[i]);
      var typeVar = Var.bit(varbitChanged, BOLT_TYPE_VARBITS[i]);
      if (!countVar.wasChanged() && !typeVar.wasChanged()) {
        continue;
      }

      var client = plugin.getClient();
      var boltStack = items.get(i);
      if (countVar.wasChanged()) {
        var newCount = countVar.getValue(client);
        if (newCount != boltStack.getQuantity()) {
          boltStack.setQuantity(newCount);
          updated = true;
        }
      }

      if (typeVar.wasChanged()) {
        int boltItemId = getBoltItemId(typeVar.getValue(client));
        if (boltItemId != boltStack.getId()) {
          boltStack.setId(boltItemId, plugin);
          updated = true;
        }
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
  protected void resetItems() {
    for (int i = 0; i < BOLT_TYPE_VARBITS.length; i++) {
      items.add(new ItemStack(-1, 0, plugin));
    }
  }
}
