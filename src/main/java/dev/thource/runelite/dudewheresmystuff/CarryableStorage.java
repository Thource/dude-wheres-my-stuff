package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;

@Getter
public class CarryableStorage extends Storage<CarryableStorageType> {
    CarryableStorage(CarryableStorageType type, Client client, ItemManager itemManager) {
        super(type, client, itemManager);
    }
}
