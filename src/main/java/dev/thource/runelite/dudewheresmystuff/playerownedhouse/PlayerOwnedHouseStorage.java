package dev.thource.runelite.dudewheresmystuff.playerownedhouse;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemStorage;
import lombok.Getter;
import net.runelite.api.Item;
import net.runelite.api.events.ItemContainerChanged;

/** PlayerOwnedHouseStorage is responsible for tracking storages in the player's house. */
@Getter
public class PlayerOwnedHouseStorage extends ItemStorage<PlayerOwnedHouseStorageType> {

  protected PlayerOwnedHouseStorage(
      PlayerOwnedHouseStorageType type, DudeWheresMyStuffPlugin plugin) {
    super(type, plugin);
  }

  @Override
  protected ItemContainerWatcher loadItemContainerWatcher() {
    PlayerOwnedHouseStorageType.load(plugin.getGson());

    return super.loadItemContainerWatcher();
  }

  @Override
  public boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    var containerId = itemContainerChanged.getContainerId();
    if (containerId > 0x8000) {
      containerId -= 0x8000;
    }

    if (containerId != type.getItemContainerId()) {
      return false;
    }

    updateLastUpdated();
    items.clear();
    for (Item item : itemContainerChanged.getItemContainer().getItems()) {
      if (type.getStorableItemIds() == null || type.getStorableItemIds().contains(item.getId())) {
        items.add(new ItemStack(item.getId(), item.getQuantity(), plugin));
      }
    }

    return true;
  }
}
