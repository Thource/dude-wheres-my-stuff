package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Var;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;

/**
 * PickaxeStatue is responsible for tracking which pickaxe the player has stored in the pickaxe
 * statue located in ToA (Path of Het).
 */
public class PickaxeStatue extends WorldStorage {

  private static final int[] PICKAXE_IDS = {
    ItemID.BRONZE_PICKAXE,
    ItemID.IRON_PICKAXE,
    ItemID.STEEL_PICKAXE,
    ItemID.BLACK_PICKAXE,
    ItemID.MITHRIL_PICKAXE,
    ItemID.ADAMANT_PICKAXE,
    ItemID.RUNE_PICKAXE,
    ItemID.TRAIL_GILDED_PICKAXE, // unconfirmed
    ItemID.DRAGON_PICKAXE,
    ItemID.DRAGON_PICKAXE_PRETTY, // unconfirmed
    ItemID.ZALCANO_PICKAXE, // unconfirmed
    ItemID.TRAILBLAZER_PICKAXE_NO_INFERNAL, // unconfirmed
    ItemID.INFERNAL_PICKAXE, // unconfirmed
    ItemID.TRAILBLAZER_PICKAXE, // unconfirmed
    ItemID.CRYSTAL_PICKAXE,
    ItemID.CRYSTAL_PICKAXE_INACTIVE,
    ItemID._3A_PICKAXE // unconfirmed
  };
  private ItemStack pickaxe;

  protected PickaxeStatue(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.PICKAXE_STATUE, plugin);

    pickaxe = new ItemStack(PICKAXE_IDS[0], plugin);
    items.add(pickaxe);
  }

  @Override
  public boolean onVarbitChanged(VarbitChanged varbitChanged) {
    var typeVar = Var.bit(varbitChanged, VarbitID.TOA_PICKAXE_STORED);
    if (!typeVar.wasChanged()) {
      return false;
    }

    int pickaxeId = getPickaxeId(typeVar.getValue(plugin.getClient()));
    if (pickaxe.getId() != pickaxeId) {
      pickaxe.setId(pickaxeId, plugin);
      pickaxe.setQuantity(pickaxe.getId() == PICKAXE_IDS[0] ? 0 : 1);
      return true;
    }

    return false;
  }

  private int getPickaxeId(int varbitValue) {
    if (varbitValue < 0 || varbitValue >= PICKAXE_IDS.length) {
      varbitValue = 0;
    }

    return PICKAXE_IDS[varbitValue];
  }

  @Override
  public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
    super.load(configManager, managerConfigKey, profileKey);

    if (items.isEmpty()) {
      items.add(new ItemStack(PICKAXE_IDS[0], plugin));
    }
    pickaxe = items.get(0);
    // Ensures bronze pickaxe removal changes are picked up on plugin update
    if (pickaxe.getId() == PICKAXE_IDS[0]) {
      pickaxe.setQuantity(0);
    }
  }

  @Override
  public void reset() {
    super.reset();

    pickaxe = new ItemStack(PICKAXE_IDS[0], plugin);
    items.add(pickaxe);
  }
}
