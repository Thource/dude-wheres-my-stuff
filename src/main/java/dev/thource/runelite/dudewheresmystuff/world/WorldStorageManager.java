package dev.thource.runelite.dudewheresmystuff.world;

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

@Slf4j
public class WorldStorageManager extends StorageManager<WorldStorageType, WorldStorage> {

  @Inject
  private WorldStorageManager(Client client, ItemManager itemManager, ConfigManager configManager,
      DudeWheresMyStuffConfig config, Notifier notifier, ClientThread clientThread,
      DudeWheresMyStuffPlugin plugin) {
    super(client, itemManager, configManager, config, notifier, plugin);

    storages.add(new Leprechaun(client, clientThread, itemManager));
  }

  @Override
  public String getConfigKey() {
    return "world";
  }

  @Override
  public Tab getTab() {
    return Tab.WORLD;
  }
}
