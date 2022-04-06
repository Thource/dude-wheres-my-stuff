package dev.thource.runelite.dudewheresmystuff;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Singleton
public class CoinsManager {
    private final Client client;
    private final ItemManager itemManager;
    private final ConfigManager configManager;
    private final DudeWheresMyStuffConfig config;
    private final Notifier notifier;

    final List<CoinStorage> storages = new ArrayList<>();

    @Inject
    private CoinsManager(Client client, ItemManager itemManager, ConfigManager configManager, DudeWheresMyStuffConfig config, Notifier notifier) {
        this.client = client;
        this.itemManager = itemManager;
        this.configManager = configManager;
        this.config = config;
        this.notifier = notifier;

        reset();
    }

    public long getTotalValue() {
        return storages.stream().mapToLong(CoinStorage::getCoins).sum();
    }

    boolean updateVarbits() {
        boolean updated = false;

        for (CoinStorage coinStorage : storages) {
            if (coinStorage.updateVarbits()) updated = true;
        }

        return updated;
    }

    boolean updateItemContainer(ItemContainerChanged itemContainerChanged) {
        boolean updated = false;

        for (CoinStorage coinStorage : storages) {
            if (coinStorage.updateItemContainer(itemContainerChanged)) updated = true;
        }

        return updated;
    }

    void reset() {
        storages.clear();

        for (CoinStorageType type : CoinStorageType.values()) {
            storages.add(new CoinStorage(type, client, itemManager));
        }
    }
}
