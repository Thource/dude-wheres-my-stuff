package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemStackUtils;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.events.MenuOptionClicked;

interface Fillable {

  List<ItemStack> getItems();

  void updateLastUpdated();

  DudeWheresMyStuffPlugin getPlugin();

  CarryableStorageType getType();

  boolean isHasStaticItems();

  default List<Integer> getItemWhitelist() {
    return List.of();
  }

  FillingState getFillingState();

  void setFillingState(FillingState newValue);

  default boolean onGameTick() {
    if (getFillingState() == FillingState.NONE) {
      return false;
    }

    var itemWhitelist = getItemWhitelist();
    var filledItems = ItemContainerWatcher.getInventoryWatcher().getItemsRemovedLastTick().stream()
        .filter(stack -> itemWhitelist.isEmpty() || itemWhitelist.contains(stack.getId()))
        .collect(Collectors.toList());

    if (getFillingState() == FillingState.TICK_1) {
      setFillingState(FillingState.TICK_2);
    } else {
      setFillingState(FillingState.NONE);
    }

    if (!filledItems.isEmpty()) {
      for (ItemStack item : filledItems) {
        ItemStackUtils.addItemStack(getItems(), item, isHasStaticItems());
      }
      updateLastUpdated();
      return true;
    }

    return false;
  }

  default boolean onMenuOptionClicked(MenuOptionClicked menuOption) {
    if (menuOption.getWidget() != null
        && getType().getContainerIds().contains(menuOption.getWidget().getItemId())
        && menuOption.getMenuOption().equals("Fill")) {
      setFillingState(FillingState.TICK_1);
    }

    return false;
  }
}
