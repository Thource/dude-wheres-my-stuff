package dev.thource.runelite.dudewheresmystuff.world;

import com.google.inject.Inject;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.Tab;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** WorldStorageManager is responsible for managing all WorldStorages. */
@Slf4j
public class WorldStorageManager extends StorageManager<WorldStorageType, WorldStorage> {

  @Getter private final Leprechaun leprechaun;

  @Inject
  private WorldStorageManager(DudeWheresMyStuffPlugin plugin) {
    super(plugin);

    leprechaun = new Leprechaun(plugin);

    storages.add(leprechaun);
    storages.add(new WorldStorage(WorldStorageType.BANK, plugin));
    storages.add(new WorldStorage(WorldStorageType.GROUP_STORAGE, plugin));
    storages.add(new WorldStorage(WorldStorageType.SEED_VAULT, plugin));
    storages.add(new BlastFurnace(plugin));
    storages.add(new LogStorage(plugin));
    storages.add(new FossilStorage(plugin));
    storages.add(new VyreWell(plugin));
    storages.add(new Annette(plugin));
    storages.add(new ElnockInquisitor(plugin));
    storages.add(new PickaxeStatue(plugin));
    storages.add(new Nulodion(plugin));
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
