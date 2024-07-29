package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemStackUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.InventoryID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;

/** LootingBag is responsible for tracking the player's items in their looting bag. */
@Getter
public class LootingBag extends CarryableStorage {

  private final List<SuspendedItem> itemsUsedOnBag = new ArrayList<>();

  public LootingBag(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.LOOTING_BAG, plugin);
  }

  @Override
  public boolean onGameTick() {
    boolean didUpdate = super.onGameTick();

    if (checkUsedItems()) {
      didUpdate = true;
    }

    Widget lootingBagWidget = plugin.getClient().getWidget(81, 5);
    if (lootingBagWidget != null) {
      Widget emptyText = lootingBagWidget.getChild(28);
      if (emptyText != null && Objects.equals(emptyText.getText(), "The bag is empty.")) {
        items.clear();
        lastUpdated = System.currentTimeMillis();

        return true;
      }

      if (checkForDeposit()) {
        didUpdate = true;
      }
    }

    return didUpdate;
  }

  private boolean checkUsedItems() {
    boolean updated = false;

    Widget depositDialog = plugin.getClient().getWidget(219, 1);
    boolean depositDialogOpen = false;
    if (depositDialog != null) {
      Widget titleWidget = depositDialog.getChild(0);
      depositDialogOpen =
          titleWidget != null && titleWidget.getText().equals("How many do you want to deposit?");
    }

    for (ItemStack itemStack :
        ItemContainerWatcher.getInventoryWatcher().getItemsRemovedLastTick()) {
      if (itemsUsedOnBag.stream().anyMatch(i -> i.getId() == itemStack.getId())) {
        ItemStackUtils.addItemStack(items, itemStack);
        updated = true;
      }
    }

    ListIterator<SuspendedItem> listIterator = itemsUsedOnBag.listIterator();
    while (listIterator.hasNext()) {
      SuspendedItem item = listIterator.next();

      if (item.getQuantity() == 1 || !depositDialogOpen) {
        if (item.getTicksLeft() <= 1) {
          listIterator.remove();
        } else {
          item.setTicksLeft(item.getTicksLeft() - 1);
        }
      }
    }

    if (updated) {
      lastUpdated = System.currentTimeMillis();
    }

    return updated;
  }

  private boolean checkForDeposit() {
    Widget widgetTitle = plugin.getClient().getWidget(81, 1);
    if (widgetTitle != null && widgetTitle.getText().equals("Add to bag")) {
      boolean updated = false;
      for (ItemStack itemStack :
          ItemContainerWatcher.getInventoryWatcher().getItemsRemovedLastTick()) {
        ItemStackUtils.addItemStack(items, itemStack);
        updated = true;
      }

      if (updated) {
        lastUpdated = System.currentTimeMillis();

        return true;
      }
    }

    return false;
  }

  @Override
  public boolean onMenuOptionClicked(MenuOptionClicked menuOption) {
    Widget item1Widget = plugin.getClient().getSelectedWidget();

    if (menuOption.getWidget() == null
        || menuOption.getWidget().getParentId() != ComponentID.INVENTORY_CONTAINER) {
      return false;
    }

    Widget item2Widget = menuOption.getWidget();
    if (item1Widget == null) {
      if (!menuOption.getMenuOption().equals("Use")
          && !menuOption.getMenuOption().equals("Examine")) {
        itemsUsedOnBag.removeIf(item -> item.getInventorySlot() == item2Widget.getIndex());
      }

      return false;
    }

    // one of the widgets is not an item
    if (item1Widget.getItemId() == -1 || item2Widget.getItemId() == -1) {
      return false;
    }

    boolean item1IsLootingBag = type.getContainerIds().contains(item1Widget.getItemId());
    // Item wasn't used on a looting bag
    if (!item1IsLootingBag && !type.getContainerIds().contains(item2Widget.getItemId())) {
      return false;
    }

    Widget itemWidget = (item1IsLootingBag ? item2Widget : item1Widget);
    itemsUsedOnBag.add(
        new SuspendedItem(
            itemWidget.getIndex(),
            itemWidget.getItemId(),
            Objects.requireNonNull(plugin.getClient().getItemContainer(InventoryID.INVENTORY))
                .count(itemWidget.getItemId())));

    return false;
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return false;
    }

    if (chatMessage.getMessage().startsWith("You can't put items in the looting bag")) {
      itemsUsedOnBag.clear();
    }

    return false;
  }
}
