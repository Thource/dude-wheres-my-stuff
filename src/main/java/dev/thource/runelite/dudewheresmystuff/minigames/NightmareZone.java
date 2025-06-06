package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Var;
import lombok.Getter;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;

/** NightmareZone is responsible for tracking the player's Nightmare Zone points. */
@Getter
public class NightmareZone extends MinigamesStorage {

  private final ItemStack points = new ItemStack(ItemID.DREAM_VIAL_FULL, "Points", 0, 0, 0, true);

  NightmareZone(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.NIGHTMARE_ZONE, plugin);

    varbits =
        new int[] {
          VarbitID.NZONE_POTION_1,
          VarbitID.NZONE_POTION_2,
          VarbitID.NZONE_POTION_3,
          VarbitID.NZONE_POTION_4
        };
    varbitItemOffset = 1;

    items.add(points);
    items.add(new ItemStack(ItemID.NZONE1DOSE2RANGERSPOTION, 0, plugin));
    items.add(new ItemStack(ItemID.NZONE1DOSE2MAGICPOTION, 0, plugin));
    items.add(new ItemStack(ItemID.NZONE1DOSEOVERLOADPOTION, 0, plugin));
    items.add(new ItemStack(ItemID.NZONE1DOSEABSORPTIONPOTION, 0, plugin));
    plugin
        .getClientThread()
        .invokeLater(() -> items.forEach(itemStack -> itemStack.setStackable(true)));
  }

  @Override
  public boolean onVarbitChanged(VarbitChanged varbitChanged) {
    var updated = super.onVarbitChanged(varbitChanged);

    var currentPointsVar = Var.bit(varbitChanged, VarbitID.NZONE_CURRENTPOINTS);
    var rewardPointsVar = Var.player(varbitChanged, VarPlayerID.NZONE_REWARDPOINTS);
    if (!currentPointsVar.wasChanged() && !rewardPointsVar.wasChanged()) {
      return updated;
    }

    var client = plugin.getClient();
    int newPoints = currentPointsVar.getValue(client) + rewardPointsVar.getValue(client);
    if (newPoints != points.getQuantity()) {
      points.setQuantity(newPoints);
      updated = true;
    }

    return updated;
  }
}
