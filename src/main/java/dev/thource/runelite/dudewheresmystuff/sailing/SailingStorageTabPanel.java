package dev.thource.runelite.dudewheresmystuff.sailing;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StorageTabPanel;
import java.util.Comparator;

/** SailingStorageTabPanel is responsible for displaying sailing storage data to the player. */
public class SailingStorageTabPanel
    extends StorageTabPanel<SailingStorageType, SailingStorage, SailingStorageManager> {

  public SailingStorageTabPanel(
      DudeWheresMyStuffPlugin plugin, SailingStorageManager storageManager) {
    super(plugin, storageManager);
  }

  @Override
  protected Comparator<SailingStorage> getStorageSorter() {
    return Comparator.comparingLong((SailingStorage s) -> {
      if (s instanceof LostBoatStorage) {
        var storage = (LostBoatStorage) s;

        // Move lost boats to the bottom of the list and sort them newest first
        return Long.MAX_VALUE - storage.getLastUpdated();
      }

      return 0;
    }).thenComparing(super.getStorageSorter());
  }
}
