package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StorageTabPanel;

/** WorldStorageTabPanel is responsible for displaying world storage data to the player. */
public class WorldStorageTabPanel
    extends StorageTabPanel<WorldStorageType, WorldStorage, WorldStorageManager> {

  public WorldStorageTabPanel(DudeWheresMyStuffPlugin plugin, WorldStorageManager storageManager) {
    super(plugin, storageManager);
  }
}
