package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemStackUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;

/** LootingBag is responsible for tracking the player's items in their looting bag. */
@Getter
public class LootingBag extends CarryableStorage implements UseDepositable {

  private final List<SuspendedItem> itemsUsed = new ArrayList<>();

  public LootingBag(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.LOOTING_BAG, plugin);
  }

  @Override
  public boolean onGameTick() {
    boolean didUpdate = super.onGameTick();

    if (UseDepositable.super.onGameTick()) {
      didUpdate = true;
    }

    Widget lootingBagWidget = plugin.getClient().getWidget(81, 5);
    if (lootingBagWidget != null) {
      Widget emptyText = lootingBagWidget.getChild(28);
      if (emptyText != null && Objects.equals(emptyText.getText(), "The bag is empty.")) {
        items.clear();
        updateLastUpdated();

        return true;
      }

      if (checkForDeposit()) {
        didUpdate = true;
      }
    }

    return didUpdate;
  }

  @Override
  public List<SuspendedItem> getItemsUsed() {
    return itemsUsed;
  }

  @Override
  public boolean isDepositDialogOpen() {
    var depositDialog = plugin.getClient().getWidget(219, 1);
    if (depositDialog != null) {
      var titleWidget = depositDialog.getChild(0);

      return
          titleWidget != null && titleWidget.getText().equals("How many do you want to deposit?");
    }

    var depositXDialog = plugin.getClient().getWidget(InterfaceID.Chatbox.MES_TEXT);
    return depositXDialog != null && !depositXDialog.isHidden() && depositXDialog.getText()
        .equals("Enter amount:");
  }

  private boolean checkForDeposit() {
    Widget widgetTitle = plugin.getClient().getWidget(81, 1);
    if (widgetTitle != null && widgetTitle.getText().equals("Add to bag")) {
      boolean updated = false;
      for (ItemStack itemStack : ItemContainerWatcher.getInventoryWatcher()
          .getItemsRemovedLastTick()) {
        ItemStackUtils.addItemStack(items, itemStack);
        updated = true;
      }

      if (updated) {
        updateLastUpdated();

        return true;
      }
    }

    return false;
  }

  @Override
  public boolean onMenuOptionClicked(MenuOptionClicked menuOption) {
    return UseDepositable.super.onMenuOptionClicked(menuOption);
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return false;
    }

    if (chatMessage.getMessage().startsWith("You can't put items in the looting bag")) {
      itemsUsed.clear();
    }

    return false;
  }
}
