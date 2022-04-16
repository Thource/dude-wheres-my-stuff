package dev.thource.runelite.dudewheresmystuff;

import net.runelite.api.Client;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

import java.util.ArrayList;
import java.util.List;

abstract class StorageManager<ST extends StorageType, S extends Storage<ST>> {
    protected final Client client;
    protected final ItemManager itemManager;
    protected final ConfigManager configManager;
    protected final DudeWheresMyStuffConfig config;
    protected final Notifier notifier;

    protected final List<S> storages = new ArrayList<>();

    StorageManager(Client client, ItemManager itemManager, ConfigManager configManager, DudeWheresMyStuffConfig config, Notifier notifier) {
        this.client = client;
        this.itemManager = itemManager;
        this.configManager = configManager;
        this.config = config;
        this.notifier = notifier;

        reset();
    }

    long getTotalValue() {
        return storages.stream().mapToLong(Storage::getTotalValue).sum();
    }

    boolean updateVarbits() {
        boolean updated = false;

        for (S storage : storages) {
            if (storage.updateVarbits()) updated = true;
        }

        return updated;
    }

    boolean updateItemContainer(ItemContainerChanged itemContainerChanged) {
        boolean updated = false;

        for (S storage : storages) {
            if (storage.updateItemContainer(itemContainerChanged)) updated = true;
        }

        return updated;
    }

    void reset() {
        storages.forEach(Storage::reset);
    }
}
