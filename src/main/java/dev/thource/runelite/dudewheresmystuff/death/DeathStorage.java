package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.Storage;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.config.ConfigManager;

/**
 * DeathStorage is responsible for tracking death storages that hold the players items (deathpiles,
 * deathbanks).
 */
@Getter
public class DeathStorage extends Storage<DeathStorageType> {

  @Setter protected DeathWorldMapPoint worldMapPoint;

  protected DeathStorage(DeathStorageType type, DudeWheresMyStuffPlugin plugin) {
    super(type, plugin);
  }

  @Override
  public void save(ConfigManager configManager, String managerConfigKey) {
    // Death storage saving and loading is handled in DeathStorageManager
  }

  @Override
  public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
    // Death storage saving and loading is handled in DeathStorageManager
  }
}
