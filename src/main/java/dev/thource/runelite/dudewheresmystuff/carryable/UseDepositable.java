package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemStackUtils;
import java.util.List;
import java.util.Objects;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InterfaceID.Inventory;
import net.runelite.api.gameval.InventoryID;

interface UseDepositable {

  List<SuspendedItem> getItemsUsed();

  List<ItemStack> getItems();

  void updateLastUpdated();

  DudeWheresMyStuffPlugin getPlugin();

  CarryableStorageType getType();

  default boolean isDepositDialogOpen() {
    return false;
  }

  boolean isHasStaticItems();

  default List<Integer> getItemWhitelist() {
    return List.of();
  }

  default boolean wasItemUsed(int itemId) {
    return getItemsUsed().stream().anyMatch(i -> i.getId() == itemId);
  }

  default boolean onGameTick() {
    var updated = false;
    var itemsUsed = getItemsUsed();

    for (var itemStack :
        ItemContainerWatcher.getInventoryWatcher().getItemsRemovedLastTick()) {
      if (wasItemUsed(itemStack.getId())) {
        ItemStackUtils.addItemStack(getItems(), itemStack, isHasStaticItems());
        updated = true;
      }
    }

    var listIterator = itemsUsed.listIterator();
    while (listIterator.hasNext()) {
      var item = listIterator.next();

      if (item.getQuantity() == 1 || !isDepositDialogOpen()) {
        if (item.getTicksLeft() <= 1) {
          listIterator.remove();
        } else {
          item.setTicksLeft(item.getTicksLeft() - 1);
        }
      }
    }

    if (updated) {
      updateLastUpdated();
    }

    return updated;
  }

  default boolean onMenuOptionClicked(MenuOptionClicked menuOption) {
    var item1Widget = getPlugin().getClient().getSelectedWidget();

    if (menuOption.getWidget() == null || menuOption.getWidget().getParentId() != Inventory.ITEMS) {
      return false;
    }

    var itemsUsed = getItemsUsed();
    var item2Widget = menuOption.getWidget();
    if (item1Widget == null) {
      if (!menuOption.getMenuOption().equals("Use")
          && !menuOption.getMenuOption().equals("Examine")) {
        itemsUsed.removeIf(item -> item.getInventorySlot() == item2Widget.getIndex());
      }

      return false;
    }

    // one of the widgets is not an item
    if (item1Widget.getItemId() == -1 || item2Widget.getItemId() == -1) {
      return false;
    }

    var item1IsThis = getType().getContainerIds().contains(item1Widget.getItemId());
    // Item wasn't used on this item
    if (!item1IsThis && !getType().getContainerIds().contains(item2Widget.getItemId())) {
      return false;
    }

    var itemWidget = (item1IsThis ? item2Widget : item1Widget);
    var itemWhitelist = getItemWhitelist();
    if (!itemWhitelist.isEmpty() && !itemWhitelist.contains(itemWidget.getItemId())) {
      return false;
    }

    itemsUsed.add(
        new SuspendedItem(
            itemWidget.getIndex(),
            itemWidget.getItemId(),
            Objects.requireNonNull(getPlugin().getClient().getItemContainer(InventoryID.INV))
                .count(itemWidget.getItemId())));

    return false;
  }
}
