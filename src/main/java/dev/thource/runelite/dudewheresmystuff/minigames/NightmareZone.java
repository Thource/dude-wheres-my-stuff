package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.MinigameStorage;
import dev.thource.runelite.dudewheresmystuff.MinigameStorageType;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.client.game.ItemManager;

@Getter
public class NightmareZone extends MinigameStorage {
    ItemStack points = new ItemStack(ItemID.DREAM_POTION, "Points", 0, 0, 0, true);

    public NightmareZone(Client client, ItemManager itemManager) {
        super(MinigameStorageType.NIGHTMARE_ZONE, client, itemManager);

        items.add(points);
    }

    @Override
    public boolean onVarbitChanged() {
        int newPoints = client.getVar(Varbits.NMZ_POINTS) + client.getVar(VarPlayer.NMZ_REWARD_POINTS);
        if (newPoints == points.getQuantity()) return false;

        points.setQuantity(newPoints);
        return true;
    }
}
