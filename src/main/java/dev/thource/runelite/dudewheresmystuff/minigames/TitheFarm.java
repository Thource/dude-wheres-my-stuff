package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

/** TitheFarm is responsible for tracking the player's Tithe Farm points. */
@Getter
public class TitheFarm extends MinigamesStorage {

  private final ItemStack points = new ItemStack(ItemID.ZEAH_WATERINGCAN, "Points", 0, 0, 0, true);

  TitheFarm(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.TITHE_FARM, plugin);

    varbits = new int[]{VarbitID.HOSIDIUS_TITHE_REWARDPOINTS};

    items.add(points);
  }
}
