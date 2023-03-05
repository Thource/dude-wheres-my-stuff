package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;

/** NightmareZone is responsible for tracking the player's Nightmare Zone points. */
@Getter
public class NightmareZone extends MinigamesStorage {

  private final ItemStack points = new ItemStack(ItemID.DREAM_POTION, "Points", 0, 0, 0, true);

  NightmareZone(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.NIGHTMARE_ZONE, plugin);

    items.add(points);
  }

  @Override
  public boolean onVarbitChanged() {
    int newPoints =
        plugin.getClient().getVarbitValue(Varbits.NMZ_POINTS)
            + plugin.getClient().getVarpValue(VarPlayer.NMZ_REWARD_POINTS);
    if (newPoints == points.getQuantity()) {
      return false;
    }

    points.setQuantity(newPoints);
    return true;
  }
}
