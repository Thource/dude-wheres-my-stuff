package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;

/** TroubleBrewing is responsible for tracking the player's Trouble Brewing pieces of eight. */
@Getter
public class TroubleBrewing extends MinigamesStorage {

  private final ItemStack points = new ItemStack(ItemID.BREW_PIECE_OF_EIGHT, "Pieces of eight", 0, 0, 0, true);

  TroubleBrewing(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.TROUBLE_BREWING, plugin);

    items.add(points);
  }

  @Override
  public boolean onVarbitChanged() {
    int newPoints = plugin.getClient().getVarpValue(VarPlayerID.BREW_PIECES);
    if (newPoints != points.getQuantity()) {
      points.setQuantity(newPoints);
      return true;
    }

    return false;
  }
}
