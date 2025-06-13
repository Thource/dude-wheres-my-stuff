package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Var;
import lombok.Getter;
import net.runelite.api.EnumComposition;
import net.runelite.api.EnumID;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;

/**
 * RunePouch is responsible for tracking how many runes the player has stored in their rune pouch.
 */
@Getter
public class RunePouch extends CarryableStorage {

  private static final int[] RUNE_TYPE_VARBITS = {
    VarbitID.RUNE_POUCH_TYPE_1,
    VarbitID.RUNE_POUCH_TYPE_2,
    VarbitID.RUNE_POUCH_TYPE_3,
    VarbitID.RUNE_POUCH_TYPE_4,
  };
  private static final int[] RUNE_QUANTITY_VARBITS = {
    VarbitID.RUNE_POUCH_QUANTITY_1,
    VarbitID.RUNE_POUCH_QUANTITY_2,
    VarbitID.RUNE_POUCH_QUANTITY_3,
    VarbitID.RUNE_POUCH_QUANTITY_4,
  };
  private static final int MAX_RUNE_TYPES = RUNE_TYPE_VARBITS.length;

  RunePouch(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.RUNE_POUCH, plugin);

    resetItems();
  }

  @Override
  public boolean onVarbitChanged(VarbitChanged varbitChanged) {
    var updated = false;
    var client = plugin.getClient();

    for (int i = 0; i < MAX_RUNE_TYPES; i++) {
      var quantityBit = Var.bit(varbitChanged, RUNE_QUANTITY_VARBITS[i]);
      var typeBit = Var.bit(varbitChanged, RUNE_TYPE_VARBITS[i]);
      if (!quantityBit.wasChanged() && !typeBit.wasChanged()) {
        continue;
      }

      var runeStack = items.get(i);
      if (quantityBit.wasChanged()) {
        var newQuantity = quantityBit.getValue(client);
        if (newQuantity != runeStack.getQuantity()) {
          runeStack.setQuantity(newQuantity);
          updated = true;
        }
      }

      if (typeBit.wasChanged()) {
        EnumComposition runepouchEnum = plugin.getClient().getEnum(EnumID.RUNEPOUCH_RUNE);

        int runeId = runepouchEnum.getIntValue(typeBit.getValue(client));
        if (runeId != runeStack.getId()) {
          runeStack.setId(runeId, plugin);
          updated = true;
        }
      }
    }

    return updated;
  }

  @Override
  public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
    super.load(configManager, managerConfigKey, profileKey);

    while (items.size() < 4) {
      items.add(new ItemStack(-1, 0, plugin));
    }
  }

  @Override
  public void resetItems() {
    for (int i = 0; i < MAX_RUNE_TYPES; i++) {
      items.add(new ItemStack(-1, 0, plugin));
    }
  }
}
