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
      new ItemContainerWatcher(InventoryID.INV, true);
  @Getter static final ItemContainerWatcher wornWatcher =
      new ItemContainerWatcher(InventoryID.WORN, true);
  @Getter static final ItemContainerWatcher lootingBagWatcher = new ItemContainerWatcher(
      InventoryID.LOOTING_BAG);
  @Getter static final ItemContainerWatcher bankWatcher = new ItemContainerWatcher(
      InventoryID.BANK);
  @Getter static final ItemContainerWatcher seedBoxWatcher = new ItemContainerWatcher(
      InventoryID.SEED_BOX);
  @Getter static final ItemContainerWatcher deathsOfficeWatcher = new ItemContainerWatcher(
      InventoryID.DEATH_PERMANENT);
  private static final Map<Integer, ItemContainerWatcher> watcherMap;
  private static final ItemContainerWatcher[] all =
      new ItemContainerWatcher[]{inventoryWatcher, wornWatcher, lootingBagWatcher, bankWatcher,
          seedBoxWatcher, deathsOfficeWatcher};
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
  private final boolean requiresInitialUpdate;
  private boolean initialUpdateOccurred = false;

  ItemContainerWatcher(int itemContainerId) {
    this.itemContainerId = itemContainerId;
    this.requiresInitialUpdate = false;
  }

  ItemContainerWatcher(int itemContainerId, boolean requiresInitialUpdate) {
    this.itemContainerId = itemContainerId;
    this.requiresInitialUpdate = requiresInitialUpdate;
  }

  static void init(Client client) {
    ItemContainerWatcher.client = client;
  }

  static void reset() {
    for (ItemContainerWatcher itemContainerWatcher : all) {
      itemContainerWatcher.itemsLastTick.clear();
      itemContainerWatcher.items.clear();
      itemContainerWatcher.justUpdated = false;
      if (itemContainerWatcher.requiresInitialUpdate) {
        itemContainerWatcher.initialUpdateOccurred = false;
      }
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

    if (requiresInitialUpdate && !initialUpdateOccurred) {
      initialUpdateOccurred = true;

      itemsLastTick.clear();
      itemsLastTick.addAll(items);
    }
  }

  /**
   * returns a List of just added ItemStacks.
   *
   * @return ItemStack List
   */
  public List<ItemStack> getItemsAddedLastTick() {
    if (!justUpdated || (requiresInitialUpdate && !initialUpdateOccurred)) {
      return List.of();
    }

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
    if (!justUpdated || (requiresInitialUpdate && !initialUpdateOccurred)) {
      return List.of();
    }

    List<ItemStack> itemsRemovedLastTick =
        itemsLastTick.stream()
            .filter(i -> i.getId() != -1)
            .map(ItemStack::new)
            .collect(Collectors.toList());

    ItemStackUtils.removeItems(itemsRemovedLastTick, items);

    return itemsRemovedLastTick;
  }
}
