package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.ItemID;

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

    varbits = VARBITS;

    items.add(new ItemStack(ItemID.TELEPORTSCROLL_NARDAH, 0, plugin));
    items.add(new ItemStack(ItemID.TELEPORTSCROLL_DIGSITE, 0, plugin));
    items.add(new ItemStack(ItemID.TELEPORTSCROLL_FELDIP, 0, plugin));
    items.add(new ItemStack(ItemID.TELEPORTSCROLL_LUNARISLE, 0, plugin));
    items.add(new ItemStack(ItemID.TELEPORTSCROLL_MORTTON, 0, plugin));
    items.add(new ItemStack(ItemID.TELEPORTSCROLL_PESTCONTROL, 0, plugin));
    items.add(new ItemStack(ItemID.TELEPORTSCROLL_PISCATORIS, 0, plugin));
    items.add(new ItemStack(ItemID.TELEPORTSCROLL_TAIBWO, 0, plugin));
    items.add(new ItemStack(ItemID.TELEPORTSCROLL_ELF, 0, plugin));
    items.add(new ItemStack(ItemID.TELEPORTSCROLL_MOSLES, 0, plugin));
    items.add(new ItemStack(ItemID.TELEPORTSCROLL_LUMBERYARD, 0, plugin));
    items.add(new ItemStack(ItemID.TELEPORTSCROLL_ZULANDRA, 0, plugin));
    items.add(new ItemStack(ItemID.TELEPORTSCROLL_CERBERUS, 0, plugin));
    items.add(new ItemStack(ItemID.TELEPORTSCROLL_REVENANTS, 0, plugin));
    items.add(new ItemStack(ItemID.TELEPORTSCROLL_WATSON, 0, plugin));
  }
}
