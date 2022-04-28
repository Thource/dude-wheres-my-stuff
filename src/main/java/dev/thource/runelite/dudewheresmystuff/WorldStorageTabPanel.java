package dev.thource.runelite.dudewheresmystuff;

import net.runelite.client.game.ItemManager;

class WorldStorageTabPanel extends
    StorageTabPanel<WorldStorageType, WorldStorage, WorldStorageManager> {

  WorldStorageTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config,
      DudeWheresMyStuffPanel pluginPanel, WorldStorageManager storageManager) {
    super(itemManager, config, pluginPanel, storageManager);
  }
}
