package dev.thource.runelite.dudewheresmystuff;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
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

  @Getter protected final List<S> storages = new ArrayList<>();
  protected final DudeWheresMyStuffPlugin plugin;
  @Inject protected Client client;
  @Inject protected ItemManager itemManager;
  @Getter @Inject protected ConfigManager configManager;
  @Getter @Inject protected PluginManager pluginManager;
  @Getter @Inject protected ItemIdentificationPlugin itemIdentificationPlugin;
  @Getter @Inject protected ItemIdentificationConfig itemIdentificationConfig;
  @Getter @Inject protected ClientThread clientThread;
  protected boolean enabled = true;
  @Getter @Setter protected boolean isPreviewManager = false;
  @Getter @Setter protected StorageTabPanel<T, S, ? extends StorageManager<?, ?>> storageTabPanel;

  protected StorageManager(DudeWheresMyStuffPlugin plugin) {
    this.plugin = plugin;
  }

  public long getTotalValue() {
    return storages.stream().filter(Storage::isWithdrawable).mapToLong(Storage::getTotalValue)
        .sum();
  }

  protected void updateStorages(List<? extends S> storages) {
    if (!storages.isEmpty()) {
      storages.forEach(
          storage -> {
            SwingUtilities.invokeLater(storage.getStoragePanel()::update);
          });

      SwingUtilities.invokeLater(storageTabPanel::reorderStoragePanels);
    }
  }

  /** Pass onGameTick through to enabled storages. */
  public void onGameTick() {
    if (enabled) {
      updateStorages(
          storages.stream()
              .filter(storage -> storage.isEnabled() && storage.onGameTick())
              .collect(Collectors.toList()));
    }
  }

  /** Pass onWidgetLoaded through to enabled storages. */
  public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
    if (enabled) {
      updateStorages(
          storages.stream()
              .filter(storage -> storage.isEnabled() && storage.onWidgetLoaded(widgetLoaded))
              .collect(Collectors.toList()));
    }
  }

  /** Pass onWidgetClosed through to enabled storages. */
  public void onWidgetClosed(WidgetClosed widgetClosed) {
    if (enabled) {
      updateStorages(
          storages.stream()
              .filter(storage -> storage.isEnabled() && storage.onWidgetClosed(widgetClosed))
              .collect(Collectors.toList()));
    }
  }

  /** Pass onVarbitChanged through to enabled storages. */
  public void onVarbitChanged() {
    if (enabled) {
      updateStorages(
          storages.stream()
              .filter(storage -> storage.isEnabled() && storage.onVarbitChanged())
              .collect(Collectors.toList()));
    }
  }

  /** Pass onItemContainerChanged through to enabled storages. */
  public void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    if (enabled) {
      updateStorages(
          storages.stream()
              .filter(
                  storage ->
                      storage.isEnabled() && storage.onItemContainerChanged(itemContainerChanged))
              .collect(Collectors.toList()));
    }
  }

  public void onGameStateChanged(GameStateChanged gameStateChanged) {
  }

  public void onActorDeath(ActorDeath actorDeath) {
  }

  public void reset() {
    storages.forEach(Storage::reset);
    enable();
  }

  public abstract String getConfigKey();

  /**
   * Save all Storages.
   *
   * @param profileKey
   */
  public void save(String profileKey) {
    if (!enabled) {
      return;
    }

    storages.forEach(storage -> storage.save(configManager, profileKey, getConfigKey()));
  }

  public void load() {
    load(configManager.getRSProfileKey());
  }

  /** Load all Storages. */
  public void load(String profileKey) {
    if (!enabled || profileKey == null) {
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

  public void onItemDespawned(ItemDespawned itemDespawned) {
  }

  /** Pass onChatMessage through to enabled storages. */
  public void onChatMessage(ChatMessage chatMessage) {
    if (enabled) {
      updateStorages(
          storages.stream()
              .filter(storage -> storage.isEnabled() && storage.onChatMessage(chatMessage))
              .collect(Collectors.toList()));
    }
  }

  public void onMenuOptionClicked(MenuOptionClicked menuOption) {
    if (enabled) {
      updateStorages(
          storages.stream()
              .filter(storage -> storage.isEnabled() && storage.onMenuOptionClicked(menuOption))
              .collect(Collectors.toList()));
    }
  }
}
