package dev.thource.runelite.dudewheresmystuff;

import net.runelite.client.game.ItemManager;

import java.util.Comparator;

class MinigamesTabPanel extends StorageTabPanel<MinigameStorageType, MinigameStorage, MinigamesManager> {
    MinigamesTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config, DudeWheresMyStuffPanel pluginPanel, MinigamesManager storageManager) {
        super(itemManager, config, pluginPanel, storageManager);
    }

    @Override
    protected Comparator<MinigameStorage> getStorageSorter() {
        return Comparator.comparing(s -> s.getType().getName());
    }

    @Override
    protected boolean showPrice() {
        return false;
    }
}
