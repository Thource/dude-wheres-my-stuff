package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import net.runelite.api.ItemID;

public class Annette extends WorldStorage {

  private final ItemStack nets;

  protected Annette(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.ANNETTE, plugin);

    hasStaticItems = true;

    nets = new ItemStack(ItemID.DRIFT_NET, plugin);
    items.add(nets);
  }

  @Override
  public boolean onVarbitChanged() {
    int newValue = plugin.getClient().getVarbitValue(243);
    if (newValue == nets.getQuantity()) {
      return false;
    }

    nets.setQuantity(newValue);
    return true;
  }
}
