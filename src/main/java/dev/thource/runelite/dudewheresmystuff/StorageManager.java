package dev.thource.runelite.dudewheresmystuff;

import net.runelite.api.Client;
import net.runelite.api.events.*;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

import java.util.ArrayList;
import java.util.List;

abstract class StorageManager<ST extends StorageType, S extends Storage<ST>> {
    protected transient final Client client;
    protected transient final ItemManager itemManager;
    protected transient final ConfigManager configManager;
    protected transient final DudeWheresMyStuffConfig config;
    protected transient final Notifier notifier;

    protected final List<S> storages = new ArrayList<>();

    protected boolean enabled = true;

    StorageManager(Client client, ItemManager itemManager, ConfigManager configManager, DudeWheresMyStuffConfig config, Notifier notifier) {
        this.client = client;
        this.itemManager = itemManager;
        this.configManager = configManager;
        this.config = config;
        this.notifier = notifier;
    }

    long getTotalValue() {
        return storages.stream().mapToLong(Storage::getTotalValue).sum();
    }

    boolean onGameTick(boolean isMember) {
        if (!enabled) return false;

        boolean updated = false;

        for (S storage : storages) {
            if (storage.getType().isMembersOnly() && !isMember) continue;

            if (storage.onGameTick()) updated = true;
        }

        return updated;
    }

    boolean onWidgetLoaded(WidgetLoaded widgetLoaded, boolean isMember) {
        if (!enabled) return false;

        boolean updated = false;

        for (S storage : storages) {
            if (storage.getType().isMembersOnly() && !isMember) continue;

            if (storage.onWidgetLoaded(widgetLoaded)) updated = true;
        }

        return updated;
    }

    boolean onWidgetClosed(WidgetClosed widgetClosed, boolean isMember) {
        if (!enabled) return false;

        boolean updated = false;

        for (S storage : storages) {
            if (storage.getType().isMembersOnly() && !isMember) continue;

            if (storage.onWidgetClosed(widgetClosed)) updated = true;
        }

        return updated;
    }

    boolean onVarbitChanged(boolean isMember) {
        if (!enabled) return false;

        boolean updated = false;

        for (S storage : storages) {
            if (storage.getType().isMembersOnly() && !isMember) continue;

            if (storage.onVarbitChanged()) updated = true;
        }

        return updated;
    }

    boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged, boolean isMember) {
        if (!enabled) return false;

        boolean updated = false;

        for (S storage : storages) {
            if (storage.getType().isMembersOnly() && !isMember) continue;

            if (storage.onItemContainerChanged(itemContainerChanged)) updated = true;
        }

        return updated;
    }

    public void onGameStateChanged(GameStateChanged gameStateChanged) {
    }

    public void onActorDeath(ActorDeath actorDeath) {
    }

    void reset() {
        storages.forEach(Storage::reset);
        enable();
    }

    public abstract String getConfigKey();

    public void save() {
        if (!enabled) return;

        storages.forEach(storage -> storage.save(configManager, getConfigKey()));
    }

    public void load() {
        if (!enabled) return;

        storages.forEach(storage -> storage.load(configManager, getConfigKey()));
    }

    boolean isMembersOnly() {
        for (Storage<?> storage : storages) {
            if (!storage.getType().isMembersOnly()) return false;
        }

        return true;
    }

    public void disable() {
        enabled = false;
    }

    public void enable() {
        enabled = true;
    }

    public abstract Tab getTab();

    public boolean onItemDespawned(ItemDespawned itemDespawned) {
        return false;
    }
}
