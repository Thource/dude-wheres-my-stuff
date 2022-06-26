package dev.thource.runelite.dudewheresmystuff.coins;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StorageTabPanel;

/** CoinsStorageTabPanel is responsible for displaying coins storage data to the player. */
public class CoinsStorageTabPanel
    extends StorageTabPanel<CoinsStorageType, CoinsStorage, CoinsStorageManager> {

  public CoinsStorageTabPanel(DudeWheresMyStuffPlugin plugin, CoinsStorageManager storageManager) {
    super(plugin, storageManager);
  }
}
