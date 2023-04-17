package dev.thource.runelite.dudewheresmystuff;

import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageManager;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageManager;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageType;
import dev.thource.runelite.dudewheresmystuff.death.DeathItems;
import dev.thource.runelite.dudewheresmystuff.death.DeathStorageManager;
import dev.thource.runelite.dudewheresmystuff.death.Deathbank;
import dev.thource.runelite.dudewheresmystuff.death.Deathpile;
import dev.thource.runelite.dudewheresmystuff.minigames.MinigamesStorageManager;
import dev.thource.runelite.dudewheresmystuff.playerownedhouse.PlayerOwnedHouseStorageManager;
import dev.thource.runelite.dudewheresmystuff.stash.StashStorageManager;
import dev.thource.runelite.dudewheresmystuff.world.WorldStorageManager;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.config.ConfigManager;

/** The manager of storage managers. */
@Slf4j
@Getter
public class StorageManagerManager {

  private final CarryableStorageManager carryableStorageManager;
  private final CoinsStorageManager coinsStorageManager;
  private final DeathStorageManager deathStorageManager;
  private final MinigamesStorageManager minigamesStorageManager;
  private final StashStorageManager stashStorageManager;
  private final PlayerOwnedHouseStorageManager playerOwnedHouseStorageManager;
  private final WorldStorageManager worldStorageManager;
  @Setter private String displayName;
  @Getter private final List<StorageManager<?, ?>> storageManagers;

  private final DudeWheresMyStuffPlugin plugin;
  private final ConfigManager configManager;

  @SuppressWarnings("java:S107")
  StorageManagerManager(
      DudeWheresMyStuffPlugin plugin,
      CarryableStorageManager carryableStorageManager,
      CoinsStorageManager coinsStorageManager,
      DeathStorageManager deathStorageManager,
      MinigamesStorageManager minigamesStorageManager,
      StashStorageManager stashStorageManager,
      PlayerOwnedHouseStorageManager playerOwnedHouseStorageManager,
      WorldStorageManager worldStorageManager) {
    this.plugin = plugin;
    this.configManager = carryableStorageManager.getConfigManager();
    this.carryableStorageManager = carryableStorageManager;
    this.coinsStorageManager = coinsStorageManager;
    this.deathStorageManager = deathStorageManager;
    this.minigamesStorageManager = minigamesStorageManager;
    this.stashStorageManager = stashStorageManager;
    this.playerOwnedHouseStorageManager = playerOwnedHouseStorageManager;
    this.worldStorageManager = worldStorageManager;
    this.displayName = "";

    storageManagers =
        Arrays.asList(
            carryableStorageManager,
            coinsStorageManager,
            deathStorageManager,
            minigamesStorageManager,
            stashStorageManager,
            playerOwnedHouseStorageManager,
            worldStorageManager);
  }

  void reset() {
    storageManagers.forEach(StorageManager::reset);
  }

  public void onActorDeath(ActorDeath actorDeath) {
    storageManagers.forEach(storageManager -> storageManager.onActorDeath(actorDeath));
  }

  public void onGameStateChanged(GameStateChanged gameStateChanged) {
    storageManagers.forEach(storageManager -> storageManager.onGameStateChanged(gameStateChanged));
  }

  /**
   * Loads the data for every storage.
   *
   * @param profileKey the profile key to load the data from
   */
  public void load(String profileKey) {
    for (StorageManager<?, ?> storageManager : storageManagers) {
      storageManager.load(profileKey);

      SwingUtilities.invokeLater(
          () -> {
            storageManager.getStorages().forEach(storage -> {
              if (storage.getStoragePanel() != null) {
                storage.getStoragePanel().update();
              }
            });
            storageManager.getStorageTabPanel().reorderStoragePanels();
          });
    }
  }

  /**
   * Saves the data for every storage.
   *
   * @param profileKey the profile key to save the data under
   */
  public void save(String profileKey) {
    for (StorageManager<?, ?> storageManager : storageManagers) {
      storageManager.save(profileKey);
    }
  }

  public void onGameTick() {
    storageManagers.forEach(StorageManager::onGameTick);
  }

  public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
    storageManagers.forEach(manager -> manager.onWidgetLoaded(widgetLoaded));
  }

  public void onWidgetClosed(WidgetClosed widgetClosed) {
    storageManagers.forEach(manager -> manager.onWidgetClosed(widgetClosed));
  }

  public void onVarbitChanged() {
    storageManagers.forEach(StorageManager::onVarbitChanged);
  }

  public void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    storageManagers.forEach(manager -> manager.onItemContainerChanged(itemContainerChanged));
  }

  public void onItemDespawned(ItemDespawned itemDespawned) {
    storageManagers.forEach(manager -> manager.onItemDespawned(itemDespawned));
  }

  public void onChatMessage(ChatMessage chatMessage) {
    storageManagers.forEach(manager -> manager.onChatMessage(chatMessage));
  }

  /**
   * Gets all the storages from each storage manager.
   *
   * @return a flat map of every storage from every storage manager (except for expired deathpiles,
   *     lost deathbanks and the inventory/looting bag coin storages.)
   */
  @SuppressWarnings("java:S1452")
  public Stream<? extends Storage<? extends Enum<? extends Enum<?>>>> getStorages() {
    return Stream.of(
            getDeathStorageManager().getStorages().stream()
                .filter(s -> !(s instanceof DeathItems))
                .filter(
                    s ->
                        (s instanceof Deathpile && !((Deathpile) s).hasExpired())
                            || (s instanceof Deathbank && ((Deathbank) s).isActive())),
            getCoinsStorageManager().getStorages().stream()
                .filter(
                    storage ->
                        storage.getType() != CoinsStorageType.INVENTORY
                            && storage.getType() != CoinsStorageType.LOOTING_BAG),
            getCarryableStorageManager().getStorages().stream(),
            getStashStorageManager().getStorages().stream(),
            getPlayerOwnedHouseStorageManager().getStorages().stream(),
            getWorldStorageManager().getStorages().stream())
        .flatMap(i -> i);
  }

  public List<ItemStack> getItems() {
    return getStorages().filter(Storage::isEnabled).map(Storage::getItems).flatMap(List::stream)
        .collect(Collectors.toList());
  }

  public void onMenuOptionClicked(MenuOptionClicked menuOption) {
    storageManagers.forEach(manager -> manager.onMenuOptionClicked(menuOption));
  }

  /** Creates a CSV file containing all the items in any exportable storage. */
  public void exportItems() {
    DataExporter exporter = new DataExporter(displayName, this);
    exporter.setMergeItems(plugin.getConfig().csvCombineItems());

    try {
      String filePath = exporter.export();
      plugin.getNotifier().notify("Items successfully exported to: " + filePath, MessageType.INFO);
    } catch (IOException | IllegalArgumentException e) {
      log.error("Unable to export: " + e.getMessage());
      plugin.getNotifier().notify("Item export failed.", MessageType.ERROR);
    }
  }
}
