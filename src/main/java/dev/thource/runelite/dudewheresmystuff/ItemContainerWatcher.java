package dev.thource.runelite.dudewheresmystuff;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InventoryID;

/** ItemContainerWatcher makes it easy to detect changes to ItemContainers. */
public class ItemContainerWatcher {

  @Getter static final ItemContainerWatcher inventoryWatcher =
      new ItemContainerWatcher(InventoryID.INV);
  @Getter static final ItemContainerWatcher lootingBagWatcher = new ItemContainerWatcher(516);
  @Getter static final ItemContainerWatcher seedBoxWatcher = new ItemContainerWatcher(573);
  @Getter static final ItemContainerWatcher deathsOfficeWatcher = new ItemContainerWatcher(636);
  private static final Map<Integer, ItemContainerWatcher> watcherMap;
  private static final ItemContainerWatcher[] all =
      new ItemContainerWatcher[]{inventoryWatcher, lootingBagWatcher, seedBoxWatcher,
          deathsOfficeWatcher};
  private static Client client;

  static {
    ImmutableMap.Builder<Integer, ItemContainerWatcher> mapBuilder = new ImmutableMap.Builder<>();
    for (ItemContainerWatcher itemContainerWatcher : all) {
      mapBuilder.put(itemContainerWatcher.itemContainerId, itemContainerWatcher);
    }
    watcherMap = mapBuilder.build();
  }

  private final int itemContainerId;
  private final List<ItemStack> itemsLastTick = new ArrayList<>();
  @Getter private final List<ItemStack> items = new ArrayList<>();
  private boolean justUpdated = false;

  ItemContainerWatcher(int itemContainerId) {
    this.itemContainerId = itemContainerId;
  }

  static void init(Client client) {
    ItemContainerWatcher.client = client;
  }

  static void reset() {
    for (ItemContainerWatcher itemContainerWatcher : all) {
      itemContainerWatcher.itemsLastTick.clear();
      itemContainerWatcher.items.clear();
      itemContainerWatcher.justUpdated = false;
    }
  }

  static void onGameTick(DudeWheresMyStuffPlugin plugin) {
    for (ItemContainerWatcher itemContainerWatcher : all) {
      itemContainerWatcher.gameTick(plugin);
    }
  }

  public static ItemContainerWatcher getWatcher(int itemContainerId) {
    return watcherMap.get(itemContainerId);
  }

  public boolean wasJustUpdated() {
    return justUpdated;
  }

  private void gameTick(DudeWheresMyStuffPlugin plugin) {
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
                item -> {
                  if (item.getId() == -1) {
                    return new ItemStack(-1, "empty", 1, 0, 0, false);
                  }

                  Optional<ItemStack> oldItem =
                      itemsLastTick.stream().filter(o -> o.getId() == item.getId()).findFirst();
                  if (oldItem.isPresent()) {
                    ItemStack newItem = new ItemStack(oldItem.get());
                    newItem.setQuantity(item.getQuantity());

                    return newItem;
                  }

                  return new ItemStack(item.getId(), item.getQuantity(), plugin);
                })
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

    ItemStackUtils.removeItems(itemsAddedLastTick, itemsLastTick);

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

    ItemStackUtils.removeItems(itemsRemovedLastTick, items);

    return itemsRemovedLastTick;
  }
}
