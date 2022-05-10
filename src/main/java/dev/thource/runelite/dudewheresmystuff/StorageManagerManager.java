package dev.thource.runelite.dudewheresmystuff;

import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageManager;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageManager;
import dev.thource.runelite.dudewheresmystuff.death.DeathStorageManager;
import dev.thource.runelite.dudewheresmystuff.minigames.MinigamesStorageManager;
import dev.thource.runelite.dudewheresmystuff.world.WorldStorageManager;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;

@Getter
class StorageManagerManager {

  private final CarryableStorageManager carryableStorageManager;
  private final CoinsStorageManager coinsStorageManager;
  private final DeathStorageManager deathStorageManager;
  private final MinigamesStorageManager minigamesStorageManager;
  private final WorldStorageManager worldStorageManager;

  @Getter(AccessLevel.NONE)
  private final List<StorageManager<?, ?>> storageManagers;

  @Getter(AccessLevel.NONE)
  private final DudeWheresMyStuffPlugin plugin;

  StorageManagerManager(
      DudeWheresMyStuffPlugin plugin,
      CarryableStorageManager carryableStorageManager,
      CoinsStorageManager coinsStorageManager,
      DeathStorageManager deathStorageManager,
      MinigamesStorageManager minigamesStorageManager,
      WorldStorageManager worldStorageManager) {
    this.plugin = plugin;
    this.carryableStorageManager = carryableStorageManager;
    this.coinsStorageManager = coinsStorageManager;
    this.deathStorageManager = deathStorageManager;
    this.minigamesStorageManager = minigamesStorageManager;
    this.worldStorageManager = worldStorageManager;

    storageManagers =
        Arrays.asList(
            carryableStorageManager,
            coinsStorageManager,
            deathStorageManager,
            minigamesStorageManager,
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

  public void load() {
    storageManagers.forEach(StorageManager::load);
  }

  public void load(String profileKey) {
    storageManagers.forEach(storageManager -> storageManager.load(profileKey));
  }

  public void save() {
    storageManagers.forEach(StorageManager::save);
  }

  // Run the predicate against all StorageManagers, save if true, return if any true
  private boolean anyMatch(Predicate<? super StorageManager<?, ?>> predicate) {
    List<StorageManager<?, ?>> trueManagers =
        storageManagers.stream().filter(predicate).collect(Collectors.toList());

    if (plugin.getClientState() == ClientState.LOGGED_IN) {
      trueManagers.forEach(StorageManager::save);
    }

    return !trueManagers.isEmpty();
  }

  public boolean onGameTick() {
    return anyMatch(StorageManager::onGameTick);
  }

  public boolean onWidgetLoaded(WidgetLoaded widgetLoaded) {
    return anyMatch(storageManager -> storageManager.onWidgetLoaded(widgetLoaded));
  }

  public boolean onWidgetClosed(WidgetClosed widgetClosed) {
    return anyMatch(storageManager -> storageManager.onWidgetClosed(widgetClosed));
  }

  public boolean onVarbitChanged() {
    return anyMatch(StorageManager::onVarbitChanged);
  }

  public boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    return anyMatch(storageManager -> storageManager.onItemContainerChanged(itemContainerChanged));
  }

  public boolean onItemDespawned(ItemDespawned itemDespawned) {
    return anyMatch(storageManager -> storageManager.onItemDespawned(itemDespawned));
  }

  public boolean onChatMessage(ChatMessage chatMessage) {
    return anyMatch(storageManager -> storageManager.onChatMessage(chatMessage));
  }
}
