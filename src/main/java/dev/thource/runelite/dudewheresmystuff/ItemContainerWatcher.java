package dev.thource.runelite.dudewheresmystuff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

/** ItemContainerWatcher makes it easy to detect changes to ItemContainers. */
public class ItemContainerWatcher {

  @Getter
  static ItemContainerWatcher inventoryWatcher =
      new ItemContainerWatcher(InventoryID.INVENTORY.getId());

  @Getter
  static ItemContainerWatcher equipmentWatcher =
      new ItemContainerWatcher(InventoryID.EQUIPMENT.getId());

  @Getter static ItemContainerWatcher lootingBagWatcher = new ItemContainerWatcher(516);
  @Getter static ItemContainerWatcher seedBoxWatcher = new ItemContainerWatcher(573);
  @Getter static ItemContainerWatcher deathbankWatcher = new ItemContainerWatcher(525);

  private static final ItemContainerWatcher[] all =
      new ItemContainerWatcher[] {
        inventoryWatcher, equipmentWatcher, lootingBagWatcher, seedBoxWatcher, deathbankWatcher
      };

  private static Client client;
  private static ClientThread clientThread;
  private static ItemManager itemManager;

  private final int itemContainerId;
  private final List<ItemStack> itemsLastTick = new ArrayList<>();
  @Getter private final List<ItemStack> items = new ArrayList<>();
  private boolean justUpdated = false;

  ItemContainerWatcher(int itemContainerId) {
    this.itemContainerId = itemContainerId;
  }

  static void init(Client client, ClientThread clientThread, ItemManager itemManager) {
    ItemContainerWatcher.client = client;
    ItemContainerWatcher.clientThread = clientThread;
    ItemContainerWatcher.itemManager = itemManager;
  }

  static void reset() {
    for (ItemContainerWatcher itemContainerWatcher : all) {
      itemContainerWatcher.itemsLastTick.clear();
      itemContainerWatcher.items.clear();
      itemContainerWatcher.justUpdated = false;
    }
  }

  static void onGameTick() {
    for (ItemContainerWatcher itemContainerWatcher : all) {
      itemContainerWatcher.gameTick();
    }
  }

  public boolean wasJustUpdated() {
    return justUpdated;
  }

  private void gameTick() {
    justUpdated = false;
    itemsLastTick.clear();
    itemsLastTick.addAll(items);

    ItemContainer itemContainer = client.getItemContainer(itemContainerId);
    if (itemContainer == null) {
      return;
    }

    justUpdated = true;
    items.clear();
    items.addAll(
        Arrays.stream(itemContainer.getItems())
            .map(
                item ->
                    new ItemStack(
                        item.getId(), item.getQuantity(), client, clientThread, itemManager))
            .collect(Collectors.toList()));
  }

  /**
   * returns a List of just added ItemStacks.
   *
   * @return ItemStack List
   */
  public List<ItemStack> getItemsAddedLastTick() {
    List<ItemStack> itemsAddedLastTick =
        items.stream()
            .filter(i -> i.getId() != -1)
            .map(ItemStack::new)
            .collect(Collectors.toList());

    for (ItemStack itemStack : itemsLastTick) {
      ItemStackUtils.removeItemStack(itemsAddedLastTick, itemStack, false);
    }

    return itemsAddedLastTick;
  }

  /**
   * returns a List of just removed ItemStacks.
   *
   * @return ItemStack List
   */
  public List<ItemStack> getItemsRemovedLastTick() {
    List<ItemStack> itemsRemovedLastTick =
        itemsLastTick.stream()
            .filter(i -> i.getId() != -1)
            .map(ItemStack::new)
            .collect(Collectors.toList());

    for (ItemStack itemStack : items) {
      ItemStackUtils.removeItemStack(itemsRemovedLastTick, itemStack, false);
    }

    return itemsRemovedLastTick;
  }
}
