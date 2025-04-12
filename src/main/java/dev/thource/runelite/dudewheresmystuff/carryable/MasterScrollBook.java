package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

/**
 * MasterScrollBook is responsible for tracking which scrolls the player has stored in their master
 * scroll book.
 */
@Slf4j
public class MasterScrollBook extends CarryableStorage {

  MasterScrollBook(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.MASTER_SCROLL_BOOK, plugin);

    hasStaticItems = true;

    varbits =
        new int[] {
          VarbitID.BOOKOFSCROLLS_NARDAH,
          VarbitID.BOOKOFSCROLLS_DIGSITE,
          VarbitID.BOOKOFSCROLLS_FELDIP,
          VarbitID.BOOKOFSCROLLS_LUNARISLE,
          VarbitID.BOOKOFSCROLLS_MORTTON,
          VarbitID.BOOKOFSCROLLS_PESTCONTROL,
          VarbitID.BOOKOFSCROLLS_PISCATORIS,
          VarbitID.BOOKOFSCROLLS_TAIBWO,
          VarbitID.BOOKOFSCROLLS_ELF,
          VarbitID.BOOKOFSCROLLS_MOSLES,
          VarbitID.BOOKOFSCROLLS_LUMBERYARD,
          VarbitID.BOOKOFSCROLLS_ZULANDRA,
          VarbitID.BOOKOFSCROLLS_CERBERUS,
          VarbitID.BOOKOFSCROLLS_REVENANTS,
          VarbitID.BOOKOFSCROLLS_WATSON_LOWBITS,
          VarbitID.BOOKOFSCROLLS_GUTHIXIAN_TEMPLE,
          VarbitID.BOOKOFSCROLLS_SPIDERCAVE,
          VarbitID.BOOKOFSCROLLS_COLOSSAL_WYRM
        };

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
    items.add(new ItemStack(ItemID.TELEPORTSCROLL_GUTHIXIAN_TEMPLE, 0, plugin));
    items.add(new ItemStack(ItemID.TELEPORTSCROLL_SPIDERCAVE, 0, plugin));
    items.add(new ItemStack(ItemID.TELEPORTSCROLL_COLOSSAL_WYRM, 0, plugin));
  }
}
