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
public class TackleBox extends CarryableStorage implements UseDepositable {

  private final List<SuspendedItem> itemsUsed = new ArrayList<>();

  public TackleBox(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.TACKLE_BOX, plugin);
  }

  @Override
  public boolean onGameTick() {
    boolean didUpdate = super.onGameTick();

    if (UseDepositable.super.onGameTick()) {
      didUpdate = true;
    }

    return didUpdate;
  }

  @Override
  public boolean isDepositDialogOpen() {
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

    if (chatMessage.getMessage().startsWith("The tackle box is now empty")) {
      resetItems();
      updateLastUpdated();
      return true;
    }

    return false;
  }
}
