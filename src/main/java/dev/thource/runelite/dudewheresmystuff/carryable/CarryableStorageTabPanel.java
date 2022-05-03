package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPanel;
import dev.thource.runelite.dudewheresmystuff.StorageTabPanel;
import net.runelite.client.game.ItemManager;

public class CarryableStorageTabPanel extends
    StorageTabPanel<CarryableStorageType, CarryableStorage, CarryableStorageManager> {

  public CarryableStorageTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config,
      DudeWheresMyStuffPanel pluginPanel, CarryableStorageManager storageManager) {
    super(itemManager, config, storageManager);
  }
}
