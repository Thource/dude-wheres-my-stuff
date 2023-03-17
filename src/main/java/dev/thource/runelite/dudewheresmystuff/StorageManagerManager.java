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
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import lombok.Getter;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.config.ConfigManager;

@Getter
public class StorageManagerManager {

  private final CarryableStorageManager carryableStorageManager;
  private final CoinsStorageManager coinsStorageManager;
  private final DeathStorageManager deathStorageManager;
  private final MinigamesStorageManager minigamesStorageManager;
  private final StashStorageManager stashStorageManager;
  private final PlayerOwnedHouseStorageManager playerOwnedHouseStorageManager;
  private final WorldStorageManager worldStorageManager;

  @Getter() private final List<StorageManager<?, ?>> storageManagers;

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

  public void load(String profileKey) {
    for (StorageManager<?, ?> storageManager : storageManagers) {
      storageManager.load(profileKey);

      SwingUtilities.invokeLater(
          () -> {
            storageManager.getStorages().forEach(storage -> storage.getStoragePanel().update());
            storageManager.getStorageTabPanel().reorderStoragePanels();
          });
    }
  }

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

  @SuppressWarnings("java:S1452")
  public Stream<? extends Storage<? extends Enum<? extends Enum<?>>>> getStorages() {
    return Stream.of(
            getDeathStorageManager().storages.stream()
                .filter(s -> !(s instanceof DeathItems))
                .filter(
                    s ->
                        (s instanceof Deathpile && !((Deathpile) s).hasExpired())
                            || (s instanceof Deathbank && ((Deathbank) s).getLostAt() == -1L)),
            getCoinsStorageManager().storages.stream()
                .filter(
                    storage ->
                        storage.getType() != CoinsStorageType.INVENTORY
                            && storage.getType() != CoinsStorageType.LOOTING_BAG),
            getCarryableStorageManager().storages.stream(),
            getStashStorageManager().storages.stream(),
            getPlayerOwnedHouseStorageManager().storages.stream(),
            getWorldStorageManager().storages.stream())
        .flatMap(i -> i);
  }

  public List<ItemStack> getItems() {
    return getStorages().filter(Storage::isEnabled).map(Storage::getItems).flatMap(List::stream).collect(Collectors.toList());
  }

  public void setItemSortMode(ItemSortMode itemSortMode) {
    storageManagers.forEach(
        storageManager -> {
          storageManager.getStorages().stream()
              .map(Storage::getStoragePanel)
              .forEach(storagePanel -> storagePanel.setSortMode(itemSortMode));

          JComboBox<ItemSortMode> sortDropdown =
              storageManager.getStorageTabPanel().getSortItemsDropdown();

          final ItemListener[] itemListeners = sortDropdown.getItemListeners();

          // We need to remove and re-add the item listeners to avoid recursion
          Arrays.stream(itemListeners).forEach(sortDropdown::removeItemListener);
          sortDropdown.setSelectedItem(itemSortMode);
          Arrays.stream(itemListeners).forEach(sortDropdown::addItemListener);
        });
  }

  public void onMenuOptionClicked(MenuOptionClicked menuOption) {
    storageManagers.forEach(manager -> manager.onMenuOptionClicked(menuOption));
  }
}
