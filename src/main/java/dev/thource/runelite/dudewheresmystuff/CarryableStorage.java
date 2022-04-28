package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
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
            // move ammo into the correct place
            ItemStack ammo = items.remove(11);
            items.add(3, ammo);

            // pad it out to fit the 4 wide grid
            ItemStack empty = new ItemStack(-1, "empty", 1, 0 ,0, false);
            items.add(0, empty);
            items.add(2, empty);
            items.add(3, empty);
            items.add(7, empty);
            items.add(11, empty);
            items.add(15, empty);

            items.forEach(itemStack -> itemStack.id = itemManager.canonicalize(itemStack.id));
        }

        return updated;
    }
}
