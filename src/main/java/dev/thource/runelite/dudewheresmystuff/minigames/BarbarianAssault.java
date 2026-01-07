package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Var;
import lombok.Getter;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

/** BarbarianAssault is responsible for tracking the player's Barbarian Assault points. */
@Getter
public class BarbarianAssault extends MinigamesStorage {

  private final ItemStack attackerPoints =
      new ItemStack(ItemID.BARBASSAULT_PLAYERICON_ATTACKER, "Attacker Points", 0, 0, 0, true);
  private final ItemStack collectorPoints =
      new ItemStack(ItemID.BARBASSAULT_PLAYERICON_COLLECTOR, "Collector Points", 0, 0, 0, true);
  private final ItemStack defenderPoints =
      new ItemStack(ItemID.BARBASSAULT_PLAYERICON_DEFENDER, "Defender Points", 0, 0, 0, true);
  private final ItemStack healerPoints =
      new ItemStack(ItemID.BARBASSAULT_PLAYERICON_HEALER, "Healer Points", 0, 0, 0, true);
  private final ItemStack queenKills =
      new ItemStack(ItemID.PENANCEPET, "Queen Kills", 0, 0, 0, true);

  BarbarianAssault(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.BARBARIAN_ASSAULT, plugin);

    varbits =
        new int[] {
          VarbitID.BARBASSAULT_POINTS_ATTACKER_BASE,
          VarbitID.BARBASSAULT_POINTS_COLLECTOR_BASE,
          VarbitID.BARBASSAULT_POINTS_DEFENDER_BASE,
          VarbitID.BARBASSAULT_POINTS_HEALER_BASE
        };

    items.add(attackerPoints);
    items.add(collectorPoints);
    items.add(defenderPoints);
    items.add(healerPoints);
    items.add(queenKills);
  }

  @Override
  public boolean onVarbitChanged(VarbitChanged varbitChanged) {
    if (varbits == null) {
      return false;
    }

    var updated = false;

    for (int i = 0; i < varbits.length; i++) {
      var var = Var.bit(varbitChanged, varbits[i]);
      if (!var.wasChanged()) {
        continue;
      }

      var itemStack = items.get(i);
      var client = plugin.getClient();
      var newPoints = var.getValue(client) + (client.getVarbitValue(varbits[i] + 4) * 512);
      if (newPoints == itemStack.getQuantity()) {
        continue;
      }

      itemStack.setQuantity(newPoints);
      updated = true;
    }

    var queenKillsVar1 = Var.bit(varbitChanged, VarbitID.BARBASSAULT_QUEENKILLS_EXTRA);
    var queenKillsVar2 = Var.bit(varbitChanged, VarbitID.BARBASSAULT_QUEENKILLS_EXTRA_2);
    if (queenKillsVar1.wasChanged() || queenKillsVar2.wasChanged()) {
      var newQueenKills =
          queenKillsVar1.getValue(plugin.getClient()) * 2
              + queenKillsVar2.getValue(plugin.getClient()) * 16;
      queenKills.setQuantity(newQueenKills);
      updated = true;
    }

    return updated;
  }
}
