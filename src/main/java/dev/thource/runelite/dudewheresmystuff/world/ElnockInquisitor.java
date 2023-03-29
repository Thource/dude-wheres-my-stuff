package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import net.runelite.api.ItemID;

/** ElnockInquisitor is responsible for tracking imp catching tools stored at Elnock Inquisitor. */
public class ElnockInquisitor extends WorldStorage {

  private final ItemStack netStack;
  private final ItemStack magicNetStack;

  protected ElnockInquisitor(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.ELNOCK_INQUISITOR, plugin);

    hasStaticItems = true;

    varbits = new int[]{11768, 11770};
    varbitItemOffset = 2;

    netStack = new ItemStack(ItemID.BUTTERFLY_NET, plugin);
    magicNetStack = new ItemStack(ItemID.MAGIC_BUTTERFLY_NET, plugin);

    items.add(netStack);
    items.add(magicNetStack);
    items.add(new ItemStack(ItemID.IMP_REPELLENT, plugin));
    items.add(new ItemStack(ItemID.IMPLING_JAR, plugin));
  }

  @Override
  public boolean onVarbitChanged() {
    boolean updated = super.onVarbitChanged();

    int newNet = plugin.getClient().getVarbitValue(11767);

    if (newNet == 0 && (netStack.getQuantity() != 0 || magicNetStack.getQuantity() != 0)) {
      netStack.setQuantity(0);
      magicNetStack.setQuantity(0);
      updated = true;
    } else if (newNet == 1 && netStack.getQuantity() != 1) {
      netStack.setQuantity(1);
      magicNetStack.setQuantity(0);
      updated = true;
    } else if (newNet == 2 && magicNetStack.getQuantity() != 1) {
      netStack.setQuantity(0);
      magicNetStack.setQuantity(1);
      updated = true;
    }

    return updated;
  }
}
