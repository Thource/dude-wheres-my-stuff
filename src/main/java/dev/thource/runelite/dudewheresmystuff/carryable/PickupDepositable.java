package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemStackUtils;
import dev.thource.runelite.dudewheresmystuff.death.SuspendedGroundItem;
import java.util.List;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InventoryID;

interface PickupDepositable {

  List<SuspendedGroundItem> getItemsPickedUp();

  List<ItemStack> getItems();

  void updateLastUpdated();

  DudeWheresMyStuffPlugin getPlugin();

  CarryableStorageType getType();

  boolean isHasStaticItems();

  int getOpenContainerId();

  default List<Integer> getItemWhitelist() {
    return List.of();
  }

  default boolean onGameTick() {
    var listIterator = getItemsPickedUp().listIterator();
    while (listIterator.hasNext()) {
      var item = listIterator.next();

      if (item.getTicksLeft() <= 1) {
        listIterator.remove();
      } else {
        item.setTicksLeft(item.getTicksLeft() - 1);
      }
    }

    return false;
  }

  default boolean onMenuOptionClicked(MenuOptionClicked menuOption) {
    // check if the player has the open item in inventory

    if (menuOption.getMenuAction() != MenuAction.GROUND_ITEM_THIRD_OPTION
        && menuOption.getMenuAction() != MenuAction.WIDGET_TARGET_ON_GROUND_ITEM) {
      return false;
    }

    var client = getPlugin().getClient();
    var inv = client.getItemContainer(InventoryID.INV);
    if (inv == null || !inv.contains(getOpenContainerId())) {
      return false;
    }

    var itemId = menuOption.getId();
    var itemWhitelist = getItemWhitelist();
    if (!itemWhitelist.isEmpty() && !itemWhitelist.contains(itemId)) {
      return false;
    }

    var worldView = getPlugin().getClient().getTopLevelWorldView();
    if (worldView == null) {
      return false;
    }

    var worldPoint =
        WorldPoint.fromScene(
            worldView, menuOption.getParam0(), menuOption.getParam1(), worldView.getPlane());
    var suspendedGroundItem = new SuspendedGroundItem(menuOption.getId(), worldPoint);
    suspendedGroundItem.setTicksLeft(100);

    var itemsPickedUp = getItemsPickedUp();
    for (var i : itemsPickedUp) {
      if (i.getTicksLeft() > 2) {
        i.setTicksLeft(2);
      }
    }

    itemsPickedUp.add(suspendedGroundItem);

    return false;
  }

  default boolean onItemDespawned(ItemDespawned itemDespawned) {
    var worldPoint = itemDespawned.getTile().getWorldLocation();
    var despawnedItem = itemDespawned.getItem();

    var itemsPickedUp = getItemsPickedUp();
    if (itemsPickedUp.stream()
        .noneMatch(
            i -> i.getWorldPoint().equals(worldPoint) && i.getId() == despawnedItem.getId())) {
      return false;
    }

    itemsPickedUp.stream()
        .filter(i -> i.getId() == despawnedItem.getId())
        .forEach(i -> {
          if (i.getTicksLeft() > 2) {
            i.setTicksLeft(2);
          }
        });

    ItemStackUtils.addItemStack(getItems(),
        new ItemStack(despawnedItem.getId(), despawnedItem.getQuantity(), getPlugin()),
        isHasStaticItems());
    updateLastUpdated();

    return true;
  }
}
