package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStorage;
import dev.thource.runelite.dudewheresmystuff.Storage;
import lombok.Getter;

/**
 * WorldStorage is responsible for tracking storages in the world that hold the players items
 * (leprechaun, fossil storage, stash units, etc).
 */
@Getter
public class WorldStorage extends ItemStorage<WorldStorageType> {

  protected WorldStorage(WorldStorageType type, DudeWheresMyStuffPlugin plugin) {
    super(type, plugin);
  }
}
