package dev.thource.runelite.dudewheresmystuff.playerownedhouse;

import com.google.inject.Inject;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import lombok.extern.slf4j.Slf4j;

/** PlayerOwnedHouseStorageManager is responsible for managing all PlayerOwnedHouseStorages. */
@Slf4j
public class PlayerOwnedHouseStorageManager
    extends StorageManager<PlayerOwnedHouseStorageType, PlayerOwnedHouseStorage> {

  @Inject
  private PlayerOwnedHouseStorageManager(DudeWheresMyStuffPlugin plugin) {
    super(plugin);

    for (PlayerOwnedHouseStorageType type : PlayerOwnedHouseStorageType.values()) {
      if (type == PlayerOwnedHouseStorageType.MENAGERIE) {
        continue;
      }

      storages.add(new PlayerOwnedHouseStorage(type, plugin));
    }

    storages.add(new Menagerie(plugin));
  }

  @Override
  public String getConfigKey() {
    return "poh";
  }

}
