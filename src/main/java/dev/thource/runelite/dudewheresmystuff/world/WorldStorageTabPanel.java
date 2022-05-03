package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.StorageTabPanel;
import net.runelite.client.game.ItemManager;

public class WorldStorageTabPanel extends
    StorageTabPanel<WorldStorageType, WorldStorage, WorldStorageManager> {

  public WorldStorageTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config,
      WorldStorageManager storageManager) {
    super(itemManager, config, storageManager);
  }
}
