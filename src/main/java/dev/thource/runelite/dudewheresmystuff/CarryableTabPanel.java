package dev.thource.runelite.dudewheresmystuff;

import net.runelite.client.game.ItemManager;

class CarryableTabPanel extends StorageTabPanel<CarryableStorageType, CarryableStorage, CarryableManager> {
    CarryableTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config, DudeWheresMyStuffPanel pluginPanel, CarryableManager storageManager) {
        super(itemManager, config, pluginPanel, storageManager);
    }
}
