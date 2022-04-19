package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.game.ItemManager;

@Getter
public abstract class MinigameStorage extends Storage<MinigameStorageType> {
    protected MinigameStorage(MinigameStorageType type, Client client, ItemManager itemManager) {
        super(type, client, itemManager);
    }

    @Override
    public void reset() {
        for (ItemStack item : items) {
            item.setQuantity(0);
        }
        lastUpdated = null;
    }
}
