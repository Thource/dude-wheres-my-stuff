package dev.thource.runelite.dudewheresmystuff.carryable;

import com.google.inject.Inject;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.Tab;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

/** CarryableStorageManager is responsible for managing all CarryableStorages. */
@Slf4j
public class CarryableStorageManager
    extends StorageManager<CarryableStorageType, CarryableStorage> {

  @Getter private final BottomlessBucket bottomlessBucket;

  @Inject
  private CarryableStorageManager(
      Client client,
      ClientThread clientThread,
      ItemManager itemManager,
      ConfigManager configManager,
      DudeWheresMyStuffConfig config,
      Notifier notifier,
      DudeWheresMyStuffPlugin plugin) {
    super(client, itemManager, configManager, config, notifier, plugin);

    for (CarryableStorageType type : CarryableStorageType.values()) {
      if (type == CarryableStorageType.RUNE_POUCH
          || type == CarryableStorageType.LOOTING_BAG
          || type == CarryableStorageType.SEED_BOX
          || type == CarryableStorageType.BOTTOMLESS_BUCKET) {
        continue;
      }

      storages.add(new CarryableStorage(type, client, clientThread, itemManager));
    }

    bottomlessBucket = new BottomlessBucket(client, clientThread, itemManager);

    storages.add(new RunePouch(client, clientThread, itemManager));
    storages.add(new LootingBag(client, clientThread, itemManager));
    storages.add(new SeedBox(client, clientThread, itemManager));
    storages.add(bottomlessBucket);
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
