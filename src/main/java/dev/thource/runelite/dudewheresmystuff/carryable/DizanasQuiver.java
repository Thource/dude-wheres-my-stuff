package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Var;
import lombok.Getter;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarPlayerID;

/**
 * RunePouch is responsible for tracking how many runes the player has stored in their rune pouch.
 */
@Getter
public class DizanasQuiver extends CarryableStorage {

  private ItemStack ammo;

  DizanasQuiver(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.DIZANAS_QUIVER, plugin);

    resetItems();
  }

  @Override
  public boolean onVarbitChanged(VarbitChanged varbitChanged) {
    var client = plugin.getClient();

    var typeVar = Var.player(varbitChanged, VarPlayerID.DIZANAS_QUIVER_TEMP_AMMO);
    if (typeVar.wasChanged()) {
      var newType = typeVar.getValue(client);
      if (newType != ammo.getId()) {
        ammo.setId(newType, plugin);
        return true;
      }

      return false;
    }

    var quantityVar = Var.player(varbitChanged, VarPlayerID.DIZANAS_QUIVER_TEMP_AMMO_AMOUNT);
    if (quantityVar.wasChanged()) {
      var newQuantity = quantityVar.getValue(client);
      if (newQuantity != ammo.getQuantity()) {
        ammo.setQuantity(newQuantity);
        return true;
      }

      return false;
    }

    return false;
  }

  @Override
  protected void resetItems() {
    items.clear();
    ammo = new ItemStack(-1, 0, plugin);
    items.add(ammo);
  }
}
