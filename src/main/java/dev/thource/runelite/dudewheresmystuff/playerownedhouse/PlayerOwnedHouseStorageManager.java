package dev.thource.runelite.dudewheresmystuff.playerownedhouse;

import com.google.inject.Inject;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.Region;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;

/** PlayerOwnedHouseStorageManager is responsible for managing all PlayerOwnedHouseStorages. */
@Slf4j
public class PlayerOwnedHouseStorageManager
    extends StorageManager<PlayerOwnedHouseStorageType, PlayerOwnedHouseStorage> {

  @Inject
  private PlayerOwnedHouseStorageManager(DudeWheresMyStuffPlugin plugin) {
    super(plugin);

    for (PlayerOwnedHouseStorageType type : PlayerOwnedHouseStorageType.values()) {
      if (type == PlayerOwnedHouseStorageType.MENAGERIE
          || type == PlayerOwnedHouseStorageType.CAPE_HANGER) {
        continue;
      }

      storages.add(new PlayerOwnedHouseStorage(type, plugin));
    }

    storages.add(new CapeHanger(plugin));
    storages.add(new Menagerie(plugin));
  }

  private boolean notInHouse() {
    return client.getLocalPlayer() != null
        && Region.get(client.getLocalPlayer().getWorldLocation().getRegionID())
        != Region.REGION_POH;
  }

  @Override
  public void onChatMessage(ChatMessage chatMessage) {
    if (notInHouse()) {
      return;
    }

    super.onChatMessage(chatMessage);
  }

  @Override
  public void onActorDeath(ActorDeath actorDeath) {
    if (notInHouse()) {
      return;
    }

    super.onActorDeath(actorDeath);
  }

  @Override
  public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned) {
    if (notInHouse()) {
      return;
    }

    super.onGameObjectSpawned(gameObjectSpawned);
  }

  @Override
  public void onGameStateChanged(GameStateChanged gameStateChanged) {
    if (notInHouse()) {
      return;
    }

    super.onGameStateChanged(gameStateChanged);
  }

  @Override
  public void onGameTick() {
    if (notInHouse()) {
      return;
    }

    super.onGameTick();
  }

  @Override
  public void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    if (notInHouse()) {
      return;
    }

    super.onItemContainerChanged(itemContainerChanged);
  }

  @Override
  public void onItemDespawned(ItemDespawned itemDespawned) {
    if (notInHouse()) {
      return;
    }

    super.onItemDespawned(itemDespawned);
  }

  @Override
  public void onMenuOptionClicked(MenuOptionClicked menuOption) {
    if (notInHouse()) {
      return;
    }

    super.onMenuOptionClicked(menuOption);
  }

  @Override
  public void onVarbitChanged() {
    if (notInHouse()) {
      return;
    }

    super.onVarbitChanged();
  }

  @Override
  public void onWidgetClosed(WidgetClosed widgetClosed) {
    if (notInHouse()) {
      return;
    }

    super.onWidgetClosed(widgetClosed);
  }

  @Override
  public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
    if (notInHouse()) {
      return;
    }

    super.onWidgetLoaded(widgetLoaded);
  }

  @Override
  public String getConfigKey() {
    return "poh";
  }

}
