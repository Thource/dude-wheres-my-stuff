package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.game.ItemManager;

@RequiredArgsConstructor
@Getter
public class CoinStorage {
    CoinStorageType type;
    protected Client client;
    protected ItemManager itemManager;

    int coins;

    CoinStorage(CoinStorageType type, Client client, ItemManager itemManager) {
        this.type = type;
        this.client = client;
        this.itemManager = itemManager;
    }

    boolean updateVarbits() {
        if (type.getVarbitId() == -1) return false;

        int coins = client.getVarbitValue(type.getVarbitId()) * type.getMultiplier();
        if (this.coins == coins) return false;

        this.coins = coins;
        return true;
    }

    boolean updateItemContainer(ItemContainerChanged itemContainerChanged) {
        if (type.getItemContainerId() == -1 || type.getItemContainerId() != itemContainerChanged.getContainerId())
            return false;

        ItemContainer itemContainer = client.getItemContainer(type.getItemContainerId());
        if (itemContainer == null) return false;

        int coins = itemContainer.count(995);
        if (this.coins == coins) return false;

        this.coins = coins;
        return true;
    }
}
