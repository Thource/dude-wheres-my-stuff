package dev.thource.runelite.dudewheresmystuff.coins;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.StorageTabPanel;
import net.runelite.client.game.ItemManager;

/** CoinsStorageTabPanel is responsible for displaying coins storage data to the player. */
public class CoinsStorageTabPanel
    extends StorageTabPanel<CoinsStorageType, CoinsStorage, CoinsStorageManager> {

  public CoinsStorageTabPanel(
      ItemManager itemManager, DudeWheresMyStuffConfig config, CoinsStorageManager storageManager) {
    super(itemManager, config, storageManager);
  }
}
