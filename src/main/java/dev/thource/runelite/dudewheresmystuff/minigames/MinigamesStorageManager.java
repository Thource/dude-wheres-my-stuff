package dev.thource.runelite.dudewheresmystuff.minigames;

import com.google.inject.Inject;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.Tab;
import lombok.extern.slf4j.Slf4j;

/** MinigamesStorageManager is responsible for managing all MinigameStorages. */
@Slf4j
public class MinigamesStorageManager
    extends StorageManager<MinigamesStorageType, MinigamesStorage> {

  @Inject
  private MinigamesStorageManager(DudeWheresMyStuffPlugin plugin) {
    super(plugin);

    storages.add(new MageTrainingArena(plugin));
    storages.add(new TitheFarm(plugin));
    storages.add(new LastManStanding(plugin));
    storages.add(new BarbarianAssault(plugin));
    storages.add(new NightmareZone(plugin));
    storages.add(new GuardiansOfTheRift(plugin));
    storages.add(new MahoganyHomes(plugin));
    storages.add(new Slayer(plugin));
    storages.add(new PestControl(plugin));
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
