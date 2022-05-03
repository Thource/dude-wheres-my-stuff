package dev.thource.runelite.dudewheresmystuff.coins;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPanel;
import dev.thource.runelite.dudewheresmystuff.StorageTabPanel;
import net.runelite.client.game.ItemManager;

public class CoinsStorageTabPanel extends
    StorageTabPanel<CoinsStorageType, CoinsStorage, CoinsStorageManager> {

  public CoinsStorageTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config,
      DudeWheresMyStuffPanel pluginPanel, CoinsStorageManager storageManager) {
    super(itemManager, config, storageManager);
  }
}
