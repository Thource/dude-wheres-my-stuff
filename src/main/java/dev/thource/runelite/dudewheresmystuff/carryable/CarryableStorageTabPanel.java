package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.StorageTabPanel;
import net.runelite.client.game.ItemManager;

/** CarryableStorageTabPanel is responsible for displaying carryable storage data to the player. */
public class CarryableStorageTabPanel
    extends StorageTabPanel<CarryableStorageType, CarryableStorage, CarryableStorageManager> {

  public CarryableStorageTabPanel(
      ItemManager itemManager,
      DudeWheresMyStuffConfig config,
      CarryableStorageManager storageManager) {
    super(itemManager, config, storageManager);
  }
}
