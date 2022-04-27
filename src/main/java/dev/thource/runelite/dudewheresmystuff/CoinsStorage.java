package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

@Getter
public class CoinsStorage extends Storage<CoinsStorageType> {
    protected ItemStack coinStack = new ItemStack(995, "Coins", 0, 1, 0, true);

    protected CoinsStorage(CoinsStorageType type, Client client, ItemManager itemManager) {
        super(type, client, itemManager);

        items.add(coinStack);
    }

    @Override
    public boolean onVarbitChanged() {
        if (type.getVarbitId() == -1) return false;

        int coins = client.getVarbitValue(type.getVarbitId()) * type.getMultiplier();
        if (coinStack.getQuantity() == coins) return false;

        coinStack.setQuantity(coins);
        return true;
    }

    @Override
    public boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
        if (type.getItemContainerId() == -1 || type.getItemContainerId() != itemContainerChanged.getContainerId())
            return false;

        ItemContainer itemContainer = client.getItemContainer(type.getItemContainerId());
        if (itemContainer == null) return false;

        lastUpdated = System.currentTimeMillis();
        int coins = itemContainer.count(995);
        if (coinStack.getQuantity() == coins) return !this.getType().isAutomatic();

        coinStack.setQuantity(coins);
        return true;
    }

    @Override
    public void reset() {
        coinStack.setQuantity(0);
        lastUpdated = -1;
        enable();
    }

    @Override
    public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
        super.load(configManager, managerConfigKey, profileKey);

        if (!items.isEmpty()) this.coinStack = items.get(0);
    }
}
