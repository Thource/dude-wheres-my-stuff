package dev.thource.runelite.dudewheresmystuff.minigames;

import com.google.inject.Inject;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.Tab;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

@Slf4j
public class MinigamesStorageManager extends
    StorageManager<MinigamesStorageType, MinigamesStorage> {

  @Inject
  private MinigamesStorageManager(Client client, ItemManager itemManager,
      ConfigManager configManager, DudeWheresMyStuffConfig config, Notifier notifier,
      DudeWheresMyStuffPlugin plugin) {
    super(client, itemManager, configManager, config, notifier, plugin);

    storages.add(new MageTrainingArena(client, itemManager));
    storages.add(new TitheFarm(client, itemManager));
    storages.add(new LastManStanding(client, itemManager));
    storages.add(new BarbarianAssault(client, itemManager));
    storages.add(new NightmareZone(client, itemManager));
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
