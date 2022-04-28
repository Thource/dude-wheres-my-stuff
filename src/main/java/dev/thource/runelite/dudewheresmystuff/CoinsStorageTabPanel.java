package dev.thource.runelite.dudewheresmystuff;

import net.runelite.client.game.ItemManager;

class CoinsStorageTabPanel extends
    StorageTabPanel<CoinsStorageType, CoinsStorage, CoinsStorageManager> {

  CoinsStorageTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config,
      DudeWheresMyStuffPanel pluginPanel, CoinsStorageManager storageManager) {
    super(itemManager, config, pluginPanel, storageManager);
  }
}
