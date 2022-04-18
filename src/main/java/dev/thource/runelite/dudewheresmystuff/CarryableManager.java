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
public class CarryableManager extends StorageManager<CarryableStorageType, CarryableStorage> {
    @Inject
    private CarryableManager(Client client, ItemManager itemManager, ConfigManager configManager, DudeWheresMyStuffConfig config, Notifier notifier) {
        super(client, itemManager, configManager, config, notifier);

        for (CarryableStorageType type : CarryableStorageType.values()) {
            if (type.getItemContainerId() != -1) {
                storages.add(new CarryableStorage(type, client, itemManager));
            }
        }

        storages.add(new RunePouch(client, itemManager));
    }

    @Override
    public String getConfigKey() {
        return "carryable";
    }
}
