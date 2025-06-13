package dev.thource.runelite.dudewheresmystuff.carryable;

import java.util.regex.Pattern;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;

interface PatternEmptyable {

  void resetItems();

  void updateLastUpdated();

  Pattern getEmptyPattern();

  void setEmptyingState(EmptyingState newState);

  default boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return false;
    }

    if (getEmptyPattern().matcher(chatMessage.getMessage()).matches()) {
      // Stop Emptyable from removing stuff
      setEmptyingState(EmptyingState.NONE);

      resetItems();
      updateLastUpdated();
      return true;
    }

    return false;
  }
}
