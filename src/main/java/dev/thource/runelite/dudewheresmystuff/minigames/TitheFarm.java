package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.MinigameStorage;
import dev.thource.runelite.dudewheresmystuff.MinigameStorageType;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.client.game.ItemManager;

@Getter
public class TitheFarm extends MinigameStorage {
    ItemStack points = new ItemStack(ItemID.GRICOLLERS_CAN, "Points", 0, 0, 0, true);

    public TitheFarm(Client client, ItemManager itemManager) {
        super(MinigameStorageType.TITHE_FARM, client, itemManager);

        items.add(points);
    }

    @Override
    public boolean onVarbitChanged() {
        int newPoints = client.getVar(Varbits.TITHE_FARM_POINTS);
        if (newPoints == points.getQuantity()) return false;

        points.setQuantity(newPoints);
        return true;
    }
}
