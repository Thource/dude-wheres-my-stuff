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

  private final int[] POTION_VARBITS = {
      3951,
      3952,
      3953,
      3954
  };

  private final ItemStack points = new ItemStack(ItemID.DREAM_POTION, "Points", 0, 0, 0, true);

  NightmareZone(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.NIGHTMARE_ZONE, plugin);

    items.add(points);
    items.add(new ItemStack(ItemID.SUPER_RANGING_1, 0, plugin));
    items.add(new ItemStack(ItemID.SUPER_MAGIC_POTION_1, 0, plugin));
    items.add(new ItemStack(ItemID.OVERLOAD_1, 0, plugin));
    items.add(new ItemStack(ItemID.ABSORPTION_1, 0, plugin));
    plugin.getClientThread()
        .invokeLater(() -> items.forEach(itemStack -> itemStack.setStackable(true)));
  }

  @Override
  public boolean onVarbitChanged() {
    boolean updated = false;

    int newPoints =
        plugin.getClient().getVarbitValue(Varbits.NMZ_POINTS)
            + plugin.getClient().getVarpValue(VarPlayer.NMZ_REWARD_POINTS);
    if (newPoints != points.getQuantity()) {
      points.setQuantity(newPoints);
      updated = true;
    }

    for (int i = 0; i < POTION_VARBITS.length; i++) {
      int varbit = POTION_VARBITS[i];
      int newValue = plugin.getClient().getVarbitValue(varbit);
      ItemStack itemStack = items.get(i + 1);

      if (itemStack.getQuantity() != newValue) {
        itemStack.setQuantity(newValue);
        updated = true;
      }
    }

    return updated;
  }
}
