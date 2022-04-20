package dev.thource.runelite.dudewheresmystuff;

import net.runelite.client.game.ItemManager;

import java.util.Comparator;

class MinigamesStorageTabPanel extends StorageTabPanel<MinigamesStorageType, MinigamesStorage, MinigamesStorageManager> {
    MinigamesStorageTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config, DudeWheresMyStuffPanel pluginPanel, MinigamesStorageManager storageManager) {
        super(itemManager, config, pluginPanel, storageManager);
    }

    @Override
    protected Comparator<MinigamesStorage> getStorageSorter() {
        return Comparator.comparing(s -> s.getType().getName());
    }

    @Override
    protected boolean showPrice() {
        return false;
    }
}
