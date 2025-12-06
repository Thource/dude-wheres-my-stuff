package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

/**
 * PlankSack is responsible for tracking which planks the player has stored in their plank sack.
 */
@Getter
public class PlankSack extends CarryableStorage {
  private final ItemStack plankStack;
  private final ItemStack oakPlankStack;
  private final ItemStack teakPlankStack;
  private final ItemStack mahoganyPlankStack;
  private final ItemStack camphorPlankStack;
  private final ItemStack ironwoodPlankStack;
  private final ItemStack rosewoodPlankStack;

  PlankSack(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.PLANK_SACK, plugin);

    hasStaticItems = true;

    plankStack = new ItemStack(ItemID.WOODPLANK, plugin);
    oakPlankStack = new ItemStack(ItemID.PLANK_OAK, plugin);
    teakPlankStack = new ItemStack(ItemID.PLANK_TEAK, plugin);
    mahoganyPlankStack = new ItemStack(ItemID.PLANK_MAHOGANY, plugin);
    camphorPlankStack = new ItemStack(ItemID.PLANK_CAMPHOR, plugin);
    ironwoodPlankStack = new ItemStack(ItemID.PLANK_IRONWOOD, plugin);
    rosewoodPlankStack = new ItemStack(ItemID.PLANK_ROSEWOOD, plugin);

    items.add(plankStack);
    items.add(oakPlankStack);
    items.add(teakPlankStack);
    items.add(mahoganyPlankStack);
    items.add(camphorPlankStack);
    items.add(ironwoodPlankStack);
    items.add(rosewoodPlankStack);

    varbits = new int[] {
        VarbitID.PLANK_SACK_OAK, VarbitID.PLANK_SACK_TEAK, VarbitID.PLANK_SACK_MAHOGANY,
        VarbitID.PLANK_SACK_CAMPHOR, VarbitID.PLANK_SACK_IRONWOOD, VarbitID.PLANK_SACK_ROSEWOOD
    };
  }
}
