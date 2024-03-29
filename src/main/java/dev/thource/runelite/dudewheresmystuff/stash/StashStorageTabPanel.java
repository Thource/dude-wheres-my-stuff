package dev.thource.runelite.dudewheresmystuff.stash;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StorageTabPanel;

/** StashStorageTabPanel is responsible for displaying STASH unit data to the player. */
public class StashStorageTabPanel
    extends StorageTabPanel<StashStorageType, StashStorage, StashStorageManager> {

  public StashStorageTabPanel(DudeWheresMyStuffPlugin plugin, StashStorageManager storageManager) {
    super(plugin, storageManager);
  }
}
