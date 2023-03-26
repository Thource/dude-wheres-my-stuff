package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;

/**
 * MasterScrollBook is responsible for tracking which scrolls the player has stored in their master
 * scroll book.
 */
@Slf4j
public class MasterScrollBook extends CarryableStorage {

  private static final int[] VARBITS = {
      5672,
      5673,
      5674,
      5675,
      5676,
      5677,
      5678,
      5679,
      5680,
      5681,
      5682,
      5683,
      5684,
      6056,
      8253,
  };

  MasterScrollBook(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.MASTER_SCROLL_BOOK, plugin);

    hasStaticItems = true;

    items.add(new ItemStack(ItemID.NARDAH_TELEPORT, 0, plugin));
    items.add(new ItemStack(ItemID.DIGSITE_TELEPORT, 0, plugin));
    items.add(new ItemStack(ItemID.FELDIP_HILLS_TELEPORT, 0, plugin));
    items.add(new ItemStack(ItemID.LUNAR_ISLE_TELEPORT, 0, plugin));
    items.add(new ItemStack(ItemID.MORTTON_TELEPORT, 0, plugin));
    items.add(new ItemStack(ItemID.PEST_CONTROL_TELEPORT, 0, plugin));
    items.add(new ItemStack(ItemID.PISCATORIS_TELEPORT, 0, plugin));
    items.add(new ItemStack(ItemID.TAI_BWO_WANNAI_TELEPORT, 0, plugin));
    items.add(new ItemStack(ItemID.IORWERTH_CAMP_TELEPORT, 0, plugin));
    items.add(new ItemStack(ItemID.MOS_LEHARMLESS_TELEPORT, 0, plugin));
    items.add(new ItemStack(ItemID.LUMBERYARD_TELEPORT, 0, plugin));
    items.add(new ItemStack(ItemID.ZULANDRA_TELEPORT, 0, plugin));
    items.add(new ItemStack(ItemID.KEY_MASTER_TELEPORT, 0, plugin));
    items.add(new ItemStack(ItemID.REVENANT_CAVE_TELEPORT, 0, plugin));
    items.add(new ItemStack(ItemID.WATSON_TELEPORT, 0, plugin));
  }

  @Override
  public boolean onVarbitChanged() {
    boolean updated = false;

    for (int i = 0; i < VARBITS.length; i++) {
      int quantity = plugin.getClient().getVarbitValue(VARBITS[i]);
      ItemStack itemStack = items.get(i);

      if (quantity != itemStack.getQuantity()) {
        itemStack.setQuantity(quantity);
        updated = true;
      }
    }

    return updated;
  }
}
