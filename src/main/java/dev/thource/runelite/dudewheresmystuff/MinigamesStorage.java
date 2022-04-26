package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;

@Getter
public abstract class MinigamesStorage extends Storage<MinigamesStorageType> {
    protected MinigamesStorage(MinigamesStorageType type, Client client, ItemManager itemManager) {
        super(type, client, itemManager);
    }

    @Override
    public void reset() {
        items.forEach(item -> item.setQuantity(0));
        lastUpdated = -1L;
        enable();
    }
}
