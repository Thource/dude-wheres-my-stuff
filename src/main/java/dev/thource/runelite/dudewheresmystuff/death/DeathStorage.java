package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStorage;
import dev.thource.runelite.dudewheresmystuff.Saved;
import dev.thource.runelite.dudewheresmystuff.Storage;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.config.ConfigManager;

/**
 * DeathStorage is responsible for tracking death storages that hold the players items (deathpiles,
 * deathbanks).
 */
@Getter
public class DeathStorage extends ItemStorage<DeathStorageType> {

  @Saved(index = 2) public UUID uuid = UUID.randomUUID();

  protected DeathStorage(DeathStorageType type, DudeWheresMyStuffPlugin plugin) {
    super(type, plugin);
  }

  @Override
  protected String getConfigKey(String managerConfigKey) {
    return super.getConfigKey(managerConfigKey) + "." + uuid;
  }
}
