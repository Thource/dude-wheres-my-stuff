package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStorage;
import dev.thource.runelite.dudewheresmystuff.SaveFieldFormatter;
import dev.thource.runelite.dudewheresmystuff.SaveFieldLoader;
import java.util.ArrayList;
import java.util.UUID;
import lombok.Getter;

/**
 * DeathStorage is responsible for tracking death storages that hold the players items (deathpiles,
 * deathbanks).
 */
@Getter
public class DeathStorage extends ItemStorage<DeathStorageType> {

  protected DeathStorage(DeathStorageType type, DudeWheresMyStuffPlugin plugin) {
    super(type, plugin);
  }
}
