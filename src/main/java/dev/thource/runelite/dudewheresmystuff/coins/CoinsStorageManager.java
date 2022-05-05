package dev.thource.runelite.dudewheresmystuff.coins;

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

/** CoinsStorageManager is responsible for managing all CoinsStorages. */
@Slf4j
public class CoinsStorageManager extends StorageManager<CoinsStorageType, CoinsStorage> {

  @Inject
  CoinsStorageManager(
      Client client,
      ItemManager itemManager,
      ConfigManager configManager,
      DudeWheresMyStuffConfig config,
      Notifier notifier,
      DudeWheresMyStuffPlugin plugin) {
    super(client, itemManager, configManager, config, notifier, plugin);

    for (CoinsStorageType type : CoinsStorageType.values()) {
      if (type == CoinsStorageType.SERVANT_MONEYBAG
          || type == CoinsStorageType.SHILO_FURNACE
          || type == CoinsStorageType.GRAND_EXCHANGE
          || type == CoinsStorageType.LOOTING_BAG) {
        continue;
      }

      storages.add(new CoinsStorage(type, client, itemManager));
    }

    storages.add(new ServantsMoneybag(client, itemManager));
    storages.add(new ShiloFurnace(client, itemManager));
    storages.add(new GrandExchange(client, itemManager));
    storages.add(new LootingBag(client, itemManager));
  }

  @Override
  public String getConfigKey() {
    return "coins";
  }

  @Override
  public Tab getTab() {
    return Tab.COINS;
  }
}
