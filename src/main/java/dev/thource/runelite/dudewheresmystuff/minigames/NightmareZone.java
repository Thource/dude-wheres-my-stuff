package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;

/** NightmareZone is responsible for tracking the player's Nightmare Zone points. */
@Getter
public class NightmareZone extends MinigamesStorage {

  private static final int[] POTION_VARBITS = {
      3951,
      3952,
      3953,
      3954
  };

  private final ItemStack points = new ItemStack(ItemID.DREAM_VIAL_FULL, "Points", 0, 0, 0, true);

  NightmareZone(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.NIGHTMARE_ZONE, plugin);

    varbits = POTION_VARBITS;
    varbitItemOffset = 1;

    items.add(points);
    items.add(new ItemStack(ItemID.NZONE1DOSE2RANGERSPOTION, 0, plugin));
    items.add(new ItemStack(ItemID.NZONE1DOSE2MAGICPOTION, 0, plugin));
    items.add(new ItemStack(ItemID.NZONE1DOSEOVERLOADPOTION, 0, plugin));
    items.add(new ItemStack(ItemID.NZONE1DOSEABSORPTIONPOTION, 0, plugin));
    plugin.getClientThread()
        .invokeLater(() -> items.forEach(itemStack -> itemStack.setStackable(true)));
  }

  @Override
  public boolean onVarbitChanged() {
    boolean updated = super.onVarbitChanged();

    int newPoints =
        plugin.getClient().getVarbitValue(VarbitID.NZONE_CURRENTPOINTS)
            + plugin.getClient().getVarpValue(VarPlayerID.NZONE_REWARDPOINTS);
    if (newPoints != points.getQuantity()) {
      points.setQuantity(newPoints);
      updated = true;
    }

    return updated;
  }
}
