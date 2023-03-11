package dev.thource.runelite.dudewheresmystuff.carryable;

import com.google.inject.Inject;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.Tab;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** CarryableStorageManager is responsible for managing all CarryableStorages. */
@Slf4j
public class CarryableStorageManager
    extends StorageManager<CarryableStorageType, CarryableStorage> {

  @Getter private final BottomlessBucket bottomlessBucket;

  @Inject
  private CarryableStorageManager(DudeWheresMyStuffPlugin plugin) {
    super(plugin);

    bottomlessBucket = new BottomlessBucket(plugin);

    storages.add(new CarryableStorage(CarryableStorageType.EQUIPMENT, plugin));
    storages.add(new CarryableStorage(CarryableStorageType.INVENTORY, plugin));
    storages.add(new LootingBag(plugin));
    storages.add(new SeedBox(plugin));
    storages.add(new RunePouch(plugin));
    storages.add(bottomlessBucket);
    storages.add(new PlankSack(plugin));
    storages.add(new BoltPouch(plugin));
    storages.add(new GnomishFirelighter(plugin));
    storages.add(new MasterScrollBook(plugin));
  }

  @Override
  public String getConfigKey() {
    return "carryable";
  }

  @Override
  public Tab getTab() {
    return Tab.CARRYABLE_STORAGE;
  }
}
