package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.Getter;
import net.runelite.api.ItemID;

/** TroubleBrewing is responsible for tracking the player's Trouble Brewing pieces of eight. */
@Getter
public class TroubleBrewing extends MinigamesStorage {

  private final ItemStack points = new ItemStack(ItemID.PIECES_OF_EIGHT, "Pieces of eight", 0, 0, 0, true);

  TroubleBrewing(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.TROUBLE_BREWING, plugin);

    items.add(points);
  }

  @Override
  public boolean onVarbitChanged() {
    int newPoints = plugin.getClient().getVarpValue(4218);
    if (newPoints != points.getQuantity()) {
      points.setQuantity(newPoints);
      return true;
    }

    return false;
  }
}
