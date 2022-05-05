package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.Storage;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

/**
 * DeathStorage is responsible for tracking death storages that hold the players items (deathpiles,
 * deathbanks).
 */
@Getter
public class DeathStorage extends Storage<DeathStorageType> {

  @Setter protected DeathWorldMapPoint worldMapPoint;

  protected DeathStorage(DeathStorageType type, Client client, ItemManager itemManager) {
    super(type, client, itemManager);
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
