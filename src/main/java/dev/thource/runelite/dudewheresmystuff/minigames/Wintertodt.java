package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

/** Wintertodt is responsible for tracking the player's Wintertodt reward permits. */
@Getter
public class Wintertodt extends MinigamesStorage {

  private final ItemStack points =
      new ItemStack(ItemID.TOME_OF_FIRE, "Reward permits", 0, 0, 0, true);

  Wintertodt(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.WINTERTODT, plugin);

    varbits = new int[] {VarbitID.WINT_REWARD_POOL};

    items.add(points);
  }
}
