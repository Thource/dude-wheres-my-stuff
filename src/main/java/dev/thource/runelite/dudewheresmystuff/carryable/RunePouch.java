package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.Getter;
import net.runelite.api.EnumComposition;
import net.runelite.api.EnumID;
import net.runelite.api.Varbits;
import java.util.Arrays;

/**
 * RunePouch is responsible for tracking how many runes the player has stored in their rune pouch.
 */
@Getter
public class RunePouch extends CarryableStorage {

  private static final String EMPTY = "empty";

  private static int maxRuneTypes = 4;
  private static final int[] runeTypeVarbits = {
          Varbits.RUNE_POUCH_RUNE1,
          Varbits.RUNE_POUCH_RUNE2,
          Varbits.RUNE_POUCH_RUNE3,
          Varbits.RUNE_POUCH_RUNE4,
  };

  private static final int[] runeQuantityVarbits = {
          Varbits.RUNE_POUCH_AMOUNT1,
          Varbits.RUNE_POUCH_AMOUNT2,
          Varbits.RUNE_POUCH_AMOUNT3,
          Varbits.RUNE_POUCH_AMOUNT4,
  };

  private int runeTypes[] = new int[maxRuneTypes];
  private int runeQuantities[] = new int[maxRuneTypes];

  RunePouch(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.RUNE_POUCH, plugin);
  }

  @Override
  public boolean onVarbitChanged() {
    int[] newTypes = new int[maxRuneTypes];
    int[] newQuantities = new int[maxRuneTypes];
    for (int i=0; i<maxRuneTypes; i++) {
      newTypes[i] = plugin.getClient().getVarbitValue(runeTypeVarbits[i]);
    }
    for (int i=0; i<maxRuneTypes; i++) {
      newQuantities[i] = plugin.getClient().getVarbitValue(runeQuantityVarbits[i]);
    }
    if (Arrays.equals(newQuantities, runeQuantities) && Arrays.equals(newTypes, runeTypes)) {
      return false;
    }

    runeQuantities = newQuantities;
    runeTypes = newTypes;

    refreshItems();

    return true;
  }

  private void refreshItems() {
    items.clear();

    EnumComposition runepouchEnum = plugin.getClient().getEnum(EnumID.RUNEPOUCH_RUNE);

    for (int i=0; i<maxRuneTypes; i++) {
      int runeId = runepouchEnum.getIntValue(runeTypes[i]);
      if (runeId != -1) {
        items.add(new ItemStack(runeId, runeQuantities[i], plugin));
      } else {
        items.add(new ItemStack(-1, EMPTY, 1, 0, 0, false));
      }
    }
  }

  @Override
  public void reset() {
    super.reset();

    for (int i=0; i<maxRuneTypes; i++) {
      runeTypes[i] = 0;
      runeQuantities[i] = 0;
    }
  }
}
