package dev.thource.runelite.dudewheresmystuff;

import net.runelite.api.Client;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

abstract class StorageManager<ST extends StorageType, S extends Storage<ST>> {
    protected transient final Client client;
    protected transient final ItemManager itemManager;
    protected transient final ConfigManager configManager;
    protected transient final DudeWheresMyStuffConfig config;
    protected transient final Notifier notifier;

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

    boolean onGameTick() {
        boolean updated = false;

        for (S storage : storages) {
            if (storage.onGameTick()) updated = true;
        }

        return updated;
    }

    boolean onWidgetLoaded(WidgetLoaded widgetLoaded) {
        boolean updated = false;

        for (S storage : storages) {
            if (storage.onWidgetLoaded(widgetLoaded)) updated = true;
        }

        return updated;
    }

    boolean onWidgetClosed(WidgetClosed widgetClosed) {
        boolean updated = false;

        for (S storage : storages) {
            if (storage.onWidgetClosed(widgetClosed)) updated = true;
        }

        return updated;
    }

    boolean onVarbitChanged() {
        boolean updated = false;

        for (S storage : storages) {
            if (storage.onVarbitChanged()) updated = true;
        }

        return updated;
    }

    boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
        boolean updated = false;

        for (S storage : storages) {
            if (storage.onItemContainerChanged(itemContainerChanged)) updated = true;
        }

        return updated;
    }

    void reset() {
        storages.forEach(Storage::reset);
    }

    public abstract String getConfigKey();

    public void save() {
        storages.forEach(storage -> storage.save(configManager, getConfigKey()));
    }

    public void load() {
        storages.forEach(storage -> storage.load(configManager, getConfigKey()));
    }
}
