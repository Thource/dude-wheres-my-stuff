package dev.thource.runelite.dudewheresmystuff.coins;

import com.google.inject.Inject;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import lombok.extern.slf4j.Slf4j;

/** CoinsStorageManager is responsible for managing all CoinsStorages. */
@Slf4j
public class CoinsStorageManager extends StorageManager<CoinsStorageType, CoinsStorage> {

  @Inject
  CoinsStorageManager(DudeWheresMyStuffPlugin plugin) {
    super(plugin);

    for (CoinsStorageType type : CoinsStorageType.values()) {
      if (type == CoinsStorageType.SERVANT_MONEYBAG
          || type == CoinsStorageType.SHILO_FURNACE
          || type == CoinsStorageType.GRAND_EXCHANGE
          || type == CoinsStorageType.LOOTING_BAG
          || type == CoinsStorageType.BOUNTY_HUNTER) {
        continue;
      }

      storages.add(new CoinsStorage(type, plugin));
    }

    storages.add(new ServantsMoneybag(plugin));
    storages.add(new ShiloFurnace(plugin));
    storages.add(new GrandExchange(plugin));
    storages.add(new LootingBag(plugin));
    storages.add(new BountyHunterCoffer(plugin));
  }

  @Override
  public String getConfigKey() {
    return "coins";
  }

}
