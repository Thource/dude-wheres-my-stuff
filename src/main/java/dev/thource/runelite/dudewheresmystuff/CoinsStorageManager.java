package dev.thource.runelite.dudewheresmystuff;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

@Slf4j
@Singleton
public class CoinsStorageManager extends StorageManager<CoinsStorageType, CoinsStorage> {
    @Inject
    CoinsStorageManager(Client client, ItemManager itemManager, ConfigManager configManager, DudeWheresMyStuffConfig config, Notifier notifier) {
        super(client, itemManager, configManager, config, notifier);

        for (CoinsStorageType type : CoinsStorageType.values()) {
            storages.add(new CoinsStorage(type, client, itemManager));
        }
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
