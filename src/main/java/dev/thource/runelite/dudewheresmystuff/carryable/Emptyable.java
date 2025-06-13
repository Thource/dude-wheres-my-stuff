package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemStackUtils;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InterfaceID.Inventory;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;

interface Emptyable {

  List<ItemStack> getItems();

  void updateLastUpdated();

  DudeWheresMyStuffPlugin getPlugin();

  CarryableStorageType getType();

  boolean isHasStaticItems();

  EmptyingState getEmptyingState();

  void setEmptyingState(EmptyingState newState);

  default List<Integer> getItemWhitelist() {
    return List.of();
  }

  default boolean onGameTick() {
    if (getEmptyingState() == EmptyingState.NONE) {
      return false;
    }

    var itemWhitelist = getItemWhitelist();
    var watcher = (getEmptyingState() == EmptyingState.TO_INVENTORY_TICK_1
        || getEmptyingState() == EmptyingState.TO_INVENTORY_TICK_2)
        ? ItemContainerWatcher.getInventoryWatcher() : ItemContainerWatcher.getBankWatcher();
    var emptiedItems = watcher.getItemsAddedLastTick().stream()
        .filter(stack -> itemWhitelist.isEmpty() || itemWhitelist.contains(stack.getId()))
        .collect(Collectors.toList());

    if (getEmptyingState() == EmptyingState.TO_INVENTORY_TICK_1) {
      setEmptyingState(EmptyingState.TO_INVENTORY_TICK_2);
    } else if (getEmptyingState() == EmptyingState.TO_BANK_TICK_1) {
      setEmptyingState(EmptyingState.TO_BANK_TICK_2);
    } else {
      setEmptyingState(EmptyingState.NONE);
    }

    if (!emptiedItems.isEmpty()) {
      if (isHasStaticItems()) {
        for (ItemStack emptiedItem : emptiedItems) {
          getItems().stream().filter(i -> i.getId() == emptiedItem.getId()).findFirst()
              .ifPresent(i -> i.setQuantity(Math.max(0, i.getQuantity() - emptiedItem.getQuantity())));
        }
      } else {
        ItemStackUtils.removeItems(getItems(), emptiedItems);
      }
      updateLastUpdated();
      return true;
    }

    return false;
  }

  default boolean onMenuOptionClicked(MenuOptionClicked menuOption) {
    if (menuOption.getWidget() != null
        && getType().getContainerIds().contains(menuOption.getWidget().getItemId())
        && menuOption.getMenuOption().equals("Empty")) {
      var client = getPlugin().getClient();
      var depositBoxWidget = client.getWidget(InterfaceID.BankDepositbox.FRAME);
      var depositBoxOpen = depositBoxWidget != null && !depositBoxWidget.isHidden();
      var bankWidget = client.getWidget(InterfaceID.Bankmain.ITEMS);
      var bankOpen = bankWidget != null && !bankWidget.isHidden();

      setEmptyingState(
          depositBoxOpen || bankOpen ? EmptyingState.TO_BANK_TICK_1
              : EmptyingState.TO_INVENTORY_TICK_1);
    }

    return false;
  }
}
