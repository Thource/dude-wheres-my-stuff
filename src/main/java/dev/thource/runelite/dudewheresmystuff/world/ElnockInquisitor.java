package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import net.runelite.api.ItemID;

public class ElnockInquisitor extends WorldStorage {

  private final ItemStack netStack;
  private final ItemStack magicNetStack;
  private final ItemStack repellentStack;
  private final ItemStack jarStack;

  protected ElnockInquisitor(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.ELNOCK_INQUISITOR, plugin);

    hasStaticItems = true;

    netStack = new ItemStack(ItemID.BUTTERFLY_NET, plugin);
    magicNetStack = new ItemStack(ItemID.MAGIC_BUTTERFLY_NET, plugin);
    repellentStack = new ItemStack(ItemID.IMP_REPELLENT, plugin);
    jarStack = new ItemStack(ItemID.IMPLING_JAR, plugin);

    items.add(netStack);
    items.add(magicNetStack);
    items.add(repellentStack);
    items.add(jarStack);
  }

  @Override
  public boolean onVarbitChanged() {
    boolean updated = false;

    int newNet = plugin.getClient().getVarbitValue(11767);
    int newRepellent = plugin.getClient().getVarbitValue(11768);
    int newJars = plugin.getClient().getVarbitValue(11770);

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

    if (newRepellent != repellentStack.getQuantity()) {
      repellentStack.setQuantity(newRepellent);
      updated = true;
    }

    if (newJars != jarStack.getQuantity()) {
      jarStack.setQuantity(newJars);
      updated = true;
    }

    return updated;
  }
}
