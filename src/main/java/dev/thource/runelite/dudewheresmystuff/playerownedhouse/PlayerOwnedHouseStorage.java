package dev.thource.runelite.dudewheresmystuff.playerownedhouse;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
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
  public boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    if (itemContainerChanged.getContainerId() != type.getItemContainerId()) {
      return false;
    }

    lastUpdated = System.currentTimeMillis();
    items.clear();
    for (Item item : itemContainerChanged.getItemContainer().getItems()) {
      if (type.getStorableItemIds() == null || type.getStorableItemIds().contains(item.getId())) {
        items.add(new ItemStack(item.getId(), item.getQuantity(), plugin));
      }
    }

    return true;
  }
}
