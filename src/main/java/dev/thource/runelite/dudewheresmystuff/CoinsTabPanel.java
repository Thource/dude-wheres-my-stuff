package dev.thource.runelite.dudewheresmystuff;

import net.runelite.client.game.ItemManager;

class CoinsTabPanel extends StorageTabPanel<CoinStorageType, CoinStorage, CoinsManager> {
    CoinsTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config, DudeWheresMyStuffPanel pluginPanel, CoinsManager storageManager) {
        super(itemManager, config, pluginPanel, storageManager);
    }
}
