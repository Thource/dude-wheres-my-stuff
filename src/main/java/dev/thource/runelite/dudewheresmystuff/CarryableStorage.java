package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.game.ItemManager;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CarryableStorage {
    CarryableStorageType type;
    List<ItemStack> items = new ArrayList<>();
    protected Client client;
    protected ItemManager itemManager;

    CarryableStorage(CarryableStorageType type, Client client, ItemManager itemManager) {
        this.type = type;
        this.client = client;
        this.itemManager = itemManager;
    }

    long getTotalValue() {
        return items.stream().mapToLong(ItemStack::getTotalGePrice).sum();
    }

    boolean updateVarbits() {
        return false;
    }

    public boolean updateItemContainer(ItemContainerChanged itemContainerChanged) {
        if (type.getItemContainerId() == -1 || type.getItemContainerId() != itemContainerChanged.getContainerId())
            return false;

        ItemContainer itemContainer = client.getItemContainer(type.getItemContainerId());
        if (itemContainer == null) return false;

        items.clear();
        for (Item item : itemContainer.getItems()) {
            if (item.getId() == -1) continue;

            ItemStack itemStack = items.stream().filter(i -> i.getId() == item.getId()).findFirst().orElse(null);
            if (itemStack != null) {
                itemStack.setQuantity(itemStack.getQuantity() + item.getQuantity());
                continue;
            }

            ItemComposition itemComposition = itemManager.getItemComposition(item.getId());
            items.add(new ItemStack(item.getId(), itemComposition.getName(), item.getQuantity(), itemManager.getItemPrice(item.getId()), itemComposition.getHaPrice(), itemComposition.isStackable()));
        }

        return true;
    }
}
