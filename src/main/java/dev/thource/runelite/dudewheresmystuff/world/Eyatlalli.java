package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Item;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.http.api.item.ItemPrice;

/** Eyatlalli is responsible for tracking the player's cold storage weapon. */
public class Eyatlalli extends WorldStorage {

  private static final Pattern LOGIN_MESSAGE_PATTERN = Pattern.compile(
      "Eyatlalli is holding onto your ([^.]+)\\.");

  protected Eyatlalli(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.EYATLALLI, plugin);
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return false;
    }

    String message = chatMessage.getMessage();
    // "<col=00e6e6>Your weapon freezes over. You're forced to let go!</col>"
    if (message.contains("Your weapon freezes over.")) {
      updateLastUpdated();
      resetItems();

      Item[] equippedItems = plugin.getClient().getItemContainer(InventoryID.WORN)
          .getItems();
      if (equippedItems.length > 3) {
        Item item = equippedItems[3];
        items.add(new ItemStack(item.getId(), item.getQuantity(), plugin));
      }

      return true;
    }

    // "Eyatlalli returns your lost weapon to you."
    // "Eyatlalli retrieves your weapon for you as you escape."
    // "You retrieve your weapon from within the cracked ice."
    // "As the icicle bursts, your weapon careens towards you, damaging you as you catch it."
    if (message.startsWith("Eyatlalli returns your lost weapon")
        || message.startsWith("Eyatlalli retrieves your weapon")
        || message.startsWith("You retrieve your weapon")
        || message.startsWith("As the icicle bursts, your weapon")) {
      updateLastUpdated();
      resetItems();

      return true;
    }

    // "Eyatlalli is holding onto your Elder maul. Speak to her to retrieve it."
    Matcher loginMessageMatcher = LOGIN_MESSAGE_PATTERN.matcher(message);
    if (loginMessageMatcher.find()) {
      updateLastUpdated();

      String itemName = loginMessageMatcher.group(1);
      Optional<ItemPrice> foundItem = plugin.getItemManager().search(itemName).stream()
          .filter(p -> p.getName().equals(itemName))
          .findFirst();
      if (!foundItem.isPresent() || items.isEmpty() || items.get(0).getId() != foundItem.get()
          .getId()) {
        resetItems();
        items.add(
            new ItemStack(foundItem.map(ItemPrice::getId).orElse(ItemID.MACRO_QUIZ_MYSTERY_BOX), 1, plugin));
      }

      return true;
    }

    return false;
  }
}
