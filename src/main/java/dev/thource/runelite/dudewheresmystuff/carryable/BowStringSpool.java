package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

/**
 * BowStringSpool is responsible for tracking how many bow strings the player has on their bow
 * string spool.
 */
@Getter
public class BowStringSpool extends CarryableStorage {
  BowStringSpool(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.BOW_STRING_SPOOL, plugin);

    hasStaticItems = true;

    items.add(new ItemStack(ItemID.BOW_STRING, plugin));

    varbits = new int[] {
        VarbitID.BOWSTRING_SPOOL_CHARGES
    };
  }
}
