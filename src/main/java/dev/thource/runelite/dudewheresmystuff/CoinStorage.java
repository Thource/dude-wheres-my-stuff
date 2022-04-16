package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.game.ItemManager;

@Getter
public class CoinStorage extends Storage<CoinStorageType> {
    private final ItemStack coinStack = new ItemStack(995, "Coins", 0, 1, 0, true);

    CoinStorage(CoinStorageType type, Client client, ItemManager itemManager) {
        super(type, client, itemManager);

        items.add(coinStack);
    }

    @Override
    boolean updateVarbits() {
        if (type.getVarbitId() == -1) return false;

        int coins = client.getVarbitValue(type.getVarbitId()) * type.getMultiplier();
        if (coinStack.getQuantity() == coins) return false;

        coinStack.setQuantity(coins);
        return true;
    }

    @Override
    boolean updateItemContainer(ItemContainerChanged itemContainerChanged) {
        if (type.getItemContainerId() == -1 || type.getItemContainerId() != itemContainerChanged.getContainerId())
            return false;

        ItemContainer itemContainer = client.getItemContainer(type.getItemContainerId());
        if (itemContainer == null) return false;

        int coins = itemContainer.count(995);
        if (coinStack.getQuantity() == coins) return false;

        coinStack.setQuantity(coins);
        return true;
    }

    @Override
    void reset() {
        coinStack.setQuantity(0);
    }
}
