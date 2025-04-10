package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.Arrays;
import lombok.Getter;
import net.runelite.api.EnumComposition;
import net.runelite.api.EnumID;
import net.runelite.api.gameval.VarbitID;

/**
 * RunePouch is responsible for tracking how many runes the player has stored in their rune pouch.
 */
@Getter
public class RunePouch extends CarryableStorage {

  private static final String EMPTY = "empty";
  private static final int[] runeTypeVarbits = {
      VarbitID.RUNE_POUCH_TYPE_1,
      VarbitID.RUNE_POUCH_TYPE_2,
      VarbitID.RUNE_POUCH_TYPE_3,
      VarbitID.RUNE_POUCH_TYPE_4,
  };
  private static final int[] runeQuantityVarbits = {
      VarbitID.RUNE_POUCH_QUANTITY_1,
      VarbitID.RUNE_POUCH_QUANTITY_2,
      VarbitID.RUNE_POUCH_QUANTITY_3,
      VarbitID.RUNE_POUCH_QUANTITY_4,
  };
  private static final int MAX_RUNE_TYPES = runeTypeVarbits.length;
  private int[] runeTypes = new int[MAX_RUNE_TYPES];
  private int[] runeQuantities = new int[MAX_RUNE_TYPES];

  RunePouch(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.RUNE_POUCH, plugin);
  }

  @Override
  public boolean onVarbitChanged() {
    int[] newTypes = new int[MAX_RUNE_TYPES];
    int[] newQuantities = new int[MAX_RUNE_TYPES];
    for (int i = 0; i < MAX_RUNE_TYPES; i++) {
      newTypes[i] = plugin.getClient().getVarbitValue(runeTypeVarbits[i]);
    }
    for (int i = 0; i < MAX_RUNE_TYPES; i++) {
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

    for (int i = 0; i < MAX_RUNE_TYPES; i++) {
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

    for (int i = 0; i < MAX_RUNE_TYPES; i++) {
      runeTypes[i] = 0;
      runeQuantities[i] = 0;
    }
  }
}
