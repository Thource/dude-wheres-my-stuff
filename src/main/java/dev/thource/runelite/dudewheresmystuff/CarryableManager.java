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
public class CarryableManager {
    private final Client client;
    private final ItemManager itemManager;
    private final ConfigManager configManager;
    private final DudeWheresMyStuffConfig config;
    private final Notifier notifier;

    final List<CarryableStorage> storages = new ArrayList<>();

    @Inject
    private CarryableManager(Client client, ItemManager itemManager, ConfigManager configManager, DudeWheresMyStuffConfig config, Notifier notifier) {
        this.client = client;
        this.itemManager = itemManager;
        this.configManager = configManager;
        this.config = config;
        this.notifier = notifier;

        reset();
    }

    public long getTotalValue() {
        return storages.stream().mapToLong(CarryableStorage::getTotalValue).sum();
    }

    boolean updateVarbits() {
        boolean updated = false;

        for (CarryableStorage storage : storages) {
            if (storage.updateVarbits()) updated = true;
        }

        return updated;
    }

    public boolean updateItemContainer(ItemContainerChanged itemContainerChanged) {
        boolean updated = false;

        for (CarryableStorage storage : storages) {
            if (storage.updateItemContainer(itemContainerChanged)) updated = true;
        }

        return updated;
    }

    void reset() {
        storages.clear();

        for (CarryableStorageType type : CarryableStorageType.values()) {
            if (type.getItemContainerId() != -1) {
                storages.add(new CarryableStorage(type, client, itemManager));
            }
        }

        storages.add(new RunePouch(CarryableStorageType.RUNE_POUCH, client, itemManager));
    }
}
