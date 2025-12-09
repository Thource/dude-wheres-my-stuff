package dev.thource.runelite.dudewheresmystuff.sailing;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStorage;
import lombok.Getter;

/** SailingStorage is the base class for all sailing storages. */
@Getter
public abstract class SailingStorage extends ItemStorage<SailingStorageType> {

  protected SailingStorage(SailingStorageType type, DudeWheresMyStuffPlugin plugin) {
    super(type, plugin);
  }
}
