package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.Getter;
import net.runelite.api.ItemID;

/** BarbarianAssault is responsible for tracking the player's Barbarian Assault points. */
@Getter
public class BarbarianAssault extends MinigamesStorage {

  private static final int[] VARBITS = {4759, 4760, 4762, 4761};

  private final ItemStack attackerPoints =
      new ItemStack(ItemID.ATTACKER_ICON, "Attacker Points", 0, 0, 0, true);
  private final ItemStack collectorPoints =
      new ItemStack(ItemID.COLLECTOR_ICON, "Collector Points", 0, 0, 0, true);
  private final ItemStack defenderPoints =
      new ItemStack(ItemID.DEFENDER_ICON, "Defender Points", 0, 0, 0, true);
  private final ItemStack healerPoints =
      new ItemStack(ItemID.HEALER_ICON, "Healer Points", 0, 0, 0, true);

  BarbarianAssault(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.BARBARIAN_ASSAULT, plugin);

    varbits = VARBITS;

    items.add(attackerPoints);
    items.add(collectorPoints);
    items.add(defenderPoints);
    items.add(healerPoints);
  }
}
