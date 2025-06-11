package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOptionClicked;

/** TackleBox is responsible for tracking the player's items in their tackle box. */
@Getter
public class GemBag extends CarryableStorage implements DepositOnUse {

  private final List<SuspendedItem> itemsUsed = new ArrayList<>();

  public GemBag(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.GEM_BAG, plugin);
  }

  @Override
  public boolean onGameTick() {
    boolean didUpdate = super.onGameTick();

    if (checkUsedItems()) {
      didUpdate = true;
    }

    return didUpdate;
  }

  @Override
  public List<SuspendedItem> getItemsUsed() {
    return itemsUsed;
  }

  @Override
  public boolean isDepositDialogOpen() {
    return false;
  }

  @Override
  public boolean onMenuOptionClicked(MenuOptionClicked menuOption) {
    return DepositOnUse.super.onMenuOptionClicked(menuOption);
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return false;
    }

    if (chatMessage.getMessage().startsWith("The tackle box is now empty")) {
      resetItems();
      updateLastUpdated();
    }

    return false;
  }
}
