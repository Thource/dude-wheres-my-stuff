package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.List;
import java.util.regex.Pattern;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;

interface PatternCheckable {

  List<ItemStack> getItems();

  void updateLastUpdated();

  Pattern getCheckPattern();

  void setEmptyingState(EmptyingState newState);

  default boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return false;
    }

    var matcher = getCheckPattern().matcher(chatMessage.getMessage());
    if (matcher.matches()) {
      // Stop Emptyable from removing stuff after we have a check
      setEmptyingState(EmptyingState.NONE);

      var items = getItems();
      for (int i = 1; i <= matcher.groupCount(); i++) {
        var quantity = Integer.parseInt(matcher.group(i));
        items.get(i - 1).setQuantity(quantity);
      }

      updateLastUpdated();
      return true;
    }

    return false;
  }
}
