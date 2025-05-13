package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

/** Tempoross is responsible for tracking the player's Tempoross reward permits. */
@Getter
public class Tempoross extends MinigamesStorage {

  private final ItemStack points = new ItemStack(ItemID.TOME_OF_WATER, "Reward permits", 0, 0, 0, true);

  Tempoross(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.TEMPOROSS, plugin);

    varbits = new int[]{VarbitID.TEMPOROSS_REWARDPERMITS};

    items.add(points);
  }
}
