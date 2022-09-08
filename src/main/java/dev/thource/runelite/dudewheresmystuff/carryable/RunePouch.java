package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.Getter;
import net.runelite.api.EnumComposition;
import net.runelite.api.EnumID;
import net.runelite.api.Varbits;

/**
 * RunePouch is responsible for tracking how many runes the player has stored in their rune pouch.
 */
@Getter
public class RunePouch extends CarryableStorage {

  private static final String EMPTY = "empty";

  private int rune1Type;
  private int rune2Type;
  private int rune3Type;

  private int rune1Quantity;
  private int rune2Quantity;
  private int rune3Quantity;

  RunePouch(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.RUNE_POUCH, plugin);
  }

  @Override
  public boolean onVarbitChanged() {
    int newRune1Type = plugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE1);
    int newRune2Type = plugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE2);
    int newRune3Type = plugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE3);
    int newRune1Quantity = plugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_AMOUNT1);
    int newRune2Quantity = plugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_AMOUNT2);
    int newRune3Quantity = plugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_AMOUNT3);

    if (newRune1Type == rune1Type
        && newRune2Type == rune2Type
        && newRune3Type == rune3Type
        && newRune1Quantity == rune1Quantity
        && newRune2Quantity == rune2Quantity
        && newRune3Quantity == rune3Quantity) {
      return false;
    }

    rune1Type = newRune1Type;
    rune2Type = newRune2Type;
    rune3Type = newRune3Type;

    rune1Quantity = newRune1Quantity;
    rune2Quantity = newRune2Quantity;
    rune3Quantity = newRune3Quantity;

    refreshItems();

    return true;
  }

  private void refreshItems() {
    items.clear();

    EnumComposition runepouchEnum = plugin.getClient().getEnum(EnumID.RUNEPOUCH_RUNE);
    int rune1Id = runepouchEnum.getIntValue(rune1Type);
    int rune2Id = runepouchEnum.getIntValue(rune2Type);
    int rune3Id = runepouchEnum.getIntValue(rune3Type);

    if (rune1Id != -1) {
      items.add(new ItemStack(rune1Id, rune1Quantity, plugin));
    } else {
      items.add(new ItemStack(-1, EMPTY, 1, 0, 0, false));
    }

    if (rune2Id != -1) {
      items.add(new ItemStack(rune2Id, rune2Quantity, plugin));
    } else {
      items.add(new ItemStack(-1, EMPTY, 1, 0, 0, false));
    }

    if (rune3Id != -1) {
      items.add(new ItemStack(rune3Id, rune3Quantity, plugin));
    } else {
      items.add(new ItemStack(-1, EMPTY, 1, 0, 0, false));
    }
  }

  @Override
  public void reset() {
    super.reset();

    rune1Type = 0;
    rune2Type = 0;
    rune3Type = 0;

    rune1Quantity = 0;
    rune2Quantity = 0;
    rune3Quantity = 0;
  }
}
