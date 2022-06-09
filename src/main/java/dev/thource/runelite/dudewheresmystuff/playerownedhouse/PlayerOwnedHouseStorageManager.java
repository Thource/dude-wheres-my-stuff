package dev.thource.runelite.dudewheresmystuff.playerownedhouse;

import com.google.inject.Inject;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.Tab;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

/** PlayerOwnedHouseStorageManager is responsible for managing all PlayerOwnedHouseStorages. */
@Slf4j
public class PlayerOwnedHouseStorageManager
    extends StorageManager<PlayerOwnedHouseStorageType, PlayerOwnedHouseStorage> {

  @Inject
  private PlayerOwnedHouseStorageManager(
      Client client,
      ClientThread clientThread,
      ItemManager itemManager,
      ConfigManager configManager,
      DudeWheresMyStuffConfig config,
      Notifier notifier,
      DudeWheresMyStuffPlugin plugin) {
    super(client, itemManager, configManager, config, notifier, plugin);

    for (PlayerOwnedHouseStorageType type : PlayerOwnedHouseStorageType.values()) {
      storages.add(new PlayerOwnedHouseStorage(type, client, clientThread, itemManager));
    }
  }

  @Override
  public String getConfigKey() {
    return "poh";
  }

  @Override
  public Tab getTab() {
    return Tab.POH_STORAGE;
  }
}
