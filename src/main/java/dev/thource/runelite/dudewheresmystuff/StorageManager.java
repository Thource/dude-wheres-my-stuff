package dev.thource.runelite.dudewheresmystuff;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

abstract class StorageManager<ST extends StorageType, S extends Storage<ST>> {

  protected transient final Client client;
  protected transient final ItemManager itemManager;
  protected transient final ConfigManager configManager;
  protected transient final DudeWheresMyStuffConfig config;
  protected transient final Notifier notifier;

  protected final List<S> storages = new ArrayList<>();

  protected boolean enabled = true;
  protected DudeWheresMyStuffPlugin plugin;

  StorageManager(Client client, ItemManager itemManager, ConfigManager configManager,
      DudeWheresMyStuffConfig config, Notifier notifier, DudeWheresMyStuffPlugin plugin) {
    this.client = client;
    this.itemManager = itemManager;
    this.configManager = configManager;
    this.config = config;
    this.notifier = notifier;
    this.plugin = plugin;
  }

  long getTotalValue() {
    return storages.stream().mapToLong(Storage::getTotalValue).sum();
  }

  boolean onGameTick() {
      if (!enabled) {
          return false;
      }

    boolean updated = false;

    for (S storage : storages) {
        if (!storage.isEnabled()) {
            continue;
        }

        if (storage.onGameTick()) {
            updated = true;
        }
    }

    return updated;
  }

  boolean onWidgetLoaded(WidgetLoaded widgetLoaded) {
      if (!enabled) {
          return false;
      }

    boolean updated = false;

    for (S storage : storages) {
        if (!storage.isEnabled()) {
            continue;
        }

        if (storage.onWidgetLoaded(widgetLoaded)) {
            updated = true;
        }
    }

    return updated;
  }

  boolean onWidgetClosed(WidgetClosed widgetClosed) {
      if (!enabled) {
          return false;
      }

    boolean updated = false;

    for (S storage : storages) {
        if (!storage.isEnabled()) {
            continue;
        }

        if (storage.onWidgetClosed(widgetClosed)) {
            updated = true;
        }
    }

    return updated;
  }

  boolean onVarbitChanged() {
      if (!enabled) {
          return false;
      }

    boolean updated = false;

    for (S storage : storages) {
        if (!storage.isEnabled()) {
            continue;
        }

        if (storage.onVarbitChanged()) {
            updated = true;
        }
    }

    return updated;
  }

  boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
      if (!enabled) {
          return false;
      }

    boolean updated = false;

    for (S storage : storages) {
        if (!storage.isEnabled()) {
            continue;
        }

        if (storage.onItemContainerChanged(itemContainerChanged)) {
            updated = true;
        }
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
      if (!enabled) {
          return;
      }

    storages.forEach(storage -> storage.save(configManager, getConfigKey()));
  }

  public void load() {
    load(configManager.getRSProfileKey());
  }

  public void load(String profileKey) {
      if (!enabled) {
          return;
      }

    storages.forEach(storage -> storage.load(configManager, getConfigKey(), profileKey));
  }

  boolean isMembersOnly() {
    for (Storage<?> storage : storages) {
        if (!storage.getType().isMembersOnly()) {
            return false;
        }
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
