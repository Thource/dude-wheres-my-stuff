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
public class CoinsManager extends StorageManager<CoinStorageType, CoinStorage> {
    @Inject
    CoinsManager(Client client, ItemManager itemManager, ConfigManager configManager, DudeWheresMyStuffConfig config, Notifier notifier) {
        super(client, itemManager, configManager, config, notifier);

        for (CoinStorageType type : CoinStorageType.values()) {
            storages.add(new CoinStorage(type, client, itemManager));
        }
    }
}
