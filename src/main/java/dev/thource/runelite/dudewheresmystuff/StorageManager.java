package dev.thource.runelite.dudewheresmystuff;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.itemidentification.ItemIdentificationConfig;
import net.runelite.client.plugins.itemidentification.ItemIdentificationPlugin;

/**
 * StorageManager manages Storages that are assigned to it, it passes on RuneLite events so that the
 * Storages can do their jobs.
 */
public abstract class StorageManager<T extends StorageType, S extends Storage<T>> {

  protected final Client client;
  protected final ItemManager itemManager;
  @Getter protected final ConfigManager configManager;
  protected final DudeWheresMyStuffConfig config;
  protected final Notifier notifier;

  @Getter protected final List<S> storages = new ArrayList<>();

  protected boolean enabled = true;
  @Setter protected boolean isPreviewManager = false;
  protected DudeWheresMyStuffPlugin plugin;

  @Getter @Inject protected PluginManager pluginManager;
  @Getter @Inject protected ItemIdentificationPlugin itemIdentificationPlugin;
  @Getter @Inject protected ItemIdentificationConfig itemIdentificationConfig;
  @Getter @Inject protected ClientThread clientThread;

  protected StorageManager(
      Client client,
      ItemManager itemManager,
      ConfigManager configManager,
      DudeWheresMyStuffConfig config,
      Notifier notifier,
      DudeWheresMyStuffPlugin plugin) {
    this.client = client;
    this.itemManager = itemManager;
    this.configManager = configManager;
    this.config = config;
    this.notifier = notifier;
    this.plugin = plugin;
  }

  public long getTotalValue() {
    return storages.stream().mapToLong(Storage::getTotalValue).sum();
  }

  /**
   * Pass onGameTick through to enabled storages.
   *
   * @return true if any Storage's data was updated
   */
  public boolean onGameTick() {
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

  /**
   * Pass onWidgetLoaded through to enabled storages.
   *
   * @return true if any Storage's data was updated
   */
  public boolean onWidgetLoaded(WidgetLoaded widgetLoaded) {
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

  /**
   * Pass onWidgetClosed through to enabled storages.
   *
   * @return true if any Storage's data was updated
   */
  public boolean onWidgetClosed(WidgetClosed widgetClosed) {
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

  /**
   * Pass onVarbitChanged through to enabled storages.
   *
   * @return true if any Storage's data was updated
   */
  public boolean onVarbitChanged() {
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

  /**
   * Pass onItemContainerChanged through to enabled storages.
   *
   * @return true if any Storage's data was updated
   */
  public boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
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

  public void onGameStateChanged(GameStateChanged gameStateChanged) {}

  public void onActorDeath(ActorDeath actorDeath) {}

  public void reset() {
    storages.forEach(Storage::reset);
    enable();
  }

  public abstract String getConfigKey();

  /** Save all Storages. */
  public void save() {
    if (!enabled) {
      return;
    }

    storages.forEach(storage -> storage.save(configManager, getConfigKey()));
  }

  public void load() {
    load(configManager.getRSProfileKey());
  }

  /** Load all Storages. */
  public void load(String profileKey) {
    if (!enabled) {
      return;
    }

    storages.forEach(storage -> storage.load(configManager, getConfigKey(), profileKey));
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

  /**
   * Pass onChatMessage through to enabled storages.
   *
   * @return true if any Storage's data was updated
   */
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (!enabled) {
      return false;
    }

    boolean updated = false;

    for (S storage : storages) {
      if (!storage.isEnabled()) {
        continue;
      }

      if (storage.onChatMessage(chatMessage)) {
        updated = true;
      }
    }

    return updated;
  }
}
