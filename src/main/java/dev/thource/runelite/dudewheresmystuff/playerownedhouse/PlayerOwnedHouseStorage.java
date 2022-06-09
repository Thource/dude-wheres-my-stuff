package dev.thource.runelite.dudewheresmystuff.playerownedhouse;

import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Storage;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

/** PlayerOwnedHouseStorage is responsible for tracking storages in the player's house. */
@Getter
public class PlayerOwnedHouseStorage extends Storage<PlayerOwnedHouseStorageType> {

  protected PlayerOwnedHouseStorage(
      PlayerOwnedHouseStorageType type,
      Client client,
      ClientThread clientThread,
      ItemManager itemManager) {
    super(type, client, clientThread, itemManager);
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
        items.add(new ItemStack(item.getId(), 1, client, clientThread, itemManager));
      }
    }

    return true;
  }
}
