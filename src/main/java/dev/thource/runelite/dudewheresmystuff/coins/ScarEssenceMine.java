package dev.thource.runelite.dudewheresmystuff.coins;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.Var;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarPlayerID;

/**
 * ScarEssenceMine is responsible for tracking how many coins the player has stored in the Scar
 * essence mine coffer.
 */
public class ScarEssenceMine extends CoinsStorage {

  ScarEssenceMine(DudeWheresMyStuffPlugin plugin) {
    super(CoinsStorageType.SCAR_ESSENCE_MINE, plugin);
  }

  @Override
  public boolean onVarbitChanged(VarbitChanged varbitChanged) {
    var client = plugin.getClient();

    var coinsVar = Var.player(varbitChanged, VarPlayerID.SCAR_ESSENCEMINE_COFFER);
    if (coinsVar.wasChanged()) {
      var newQuantity = coinsVar.getValue(client);
      if (newQuantity != coinStack.getQuantity()) {
        coinStack.setQuantity(newQuantity);
        return true;
      }
    }

    return false;
  }
}
