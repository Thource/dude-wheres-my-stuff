package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.MinigameStorage;
import dev.thource.runelite.dudewheresmystuff.MinigameStorageType;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class BarbarianAssault extends MinigameStorage {
    ItemStack attackerPoints = new ItemStack(ItemID.ATTACKER_ICON, "Attacker Points", 0, 0, 0, true);
    ItemStack collectorPoints = new ItemStack(ItemID.COLLECTOR_ICON, "Collector Points", 0, 0, 0, true);
    ItemStack defenderPoints = new ItemStack(ItemID.DEFENDER_ICON, "Defender Points", 0, 0, 0, true);
    ItemStack healerPoints = new ItemStack(ItemID.HEALER_ICON, "Healer Points", 0, 0, 0, true);

    Map<Integer, ItemStack> varbits = new HashMap<>();

    public BarbarianAssault(Client client, ItemManager itemManager) {
        super(MinigameStorageType.BARBARIAN_ASSAULT, client, itemManager);

        items.add(attackerPoints);
        items.add(collectorPoints);
        items.add(defenderPoints);
        items.add(healerPoints);

        varbits.put(4759, attackerPoints);
        varbits.put(4760, collectorPoints);
        varbits.put(4762, defenderPoints);
        varbits.put(4761, healerPoints);
    }

    @Override
    public boolean onVarbitChanged() {
        AtomicBoolean updated = new AtomicBoolean(false);

        varbits.forEach((varbit, itemStack) -> {
            int newPoints = client.getVarbitValue(varbit);
            if (newPoints == itemStack.getQuantity()) return;

            itemStack.setQuantity(newPoints);
            updated.set(true);
        });

        return updated.get();
    }
}
