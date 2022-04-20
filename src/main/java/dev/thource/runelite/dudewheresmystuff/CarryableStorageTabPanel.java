package dev.thource.runelite.dudewheresmystuff;

import net.runelite.client.game.ItemManager;

class CarryableStorageTabPanel extends StorageTabPanel<CarryableStorageType, CarryableStorage, CarryableStorageManager> {
    CarryableStorageTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config, DudeWheresMyStuffPanel pluginPanel, CarryableStorageManager storageManager) {
        super(itemManager, config, pluginPanel, storageManager);
    }
}
