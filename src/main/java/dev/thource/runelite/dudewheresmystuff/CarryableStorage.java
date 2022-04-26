package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.game.ItemManager;

import java.util.stream.Collectors;

@Getter
public class CarryableStorage extends Storage<CarryableStorageType> {
    protected CarryableStorage(CarryableStorageType type, Client client, ItemManager itemManager) {
        super(type, client, itemManager);
    }

    @Override
    public boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
        boolean updated = super.onItemContainerChanged(itemContainerChanged);

        if (type == CarryableStorageType.EQUIPMENT && updated) {
            items = items.stream()
                    .filter(itemStack -> itemStack.getId() != -1)
                    .collect(Collectors.toList());

            items.forEach(itemStack -> itemStack.id = itemManager.canonicalize(itemStack.id));
        }

        return updated;
    }
}
