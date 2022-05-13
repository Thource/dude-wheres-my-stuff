package dev.thource.runelite.dudewheresmystuff.minigames;

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

/** MinigamesStorageManager is responsible for managing all MinigameStorages. */
@Slf4j
public class MinigamesStorageManager
    extends StorageManager<MinigamesStorageType, MinigamesStorage> {

  @Inject
  private MinigamesStorageManager(
      Client client,
      ClientThread clientThread,
      ItemManager itemManager,
      ConfigManager configManager,
      DudeWheresMyStuffConfig config,
      Notifier notifier,
      DudeWheresMyStuffPlugin plugin) {
    super(client, itemManager, configManager, config, notifier, plugin);

    storages.add(new MageTrainingArena(client, clientThread, itemManager));
    storages.add(new TitheFarm(client, clientThread, itemManager));
    storages.add(new LastManStanding(client, clientThread, itemManager));
    storages.add(new BarbarianAssault(client, clientThread, itemManager));
    storages.add(new NightmareZone(client, clientThread, itemManager));
    storages.add(new GuardiansOfTheRift(client, clientThread, itemManager));
  }

  @Override
  public String getConfigKey() {
    return "minigames";
  }

  @Override
  public Tab getTab() {
    return Tab.MINIGAMES;
  }
}
