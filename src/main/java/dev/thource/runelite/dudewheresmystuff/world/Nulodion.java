package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.util.Text;

/** Nulodion is responsible for tracking when the player can claim a new cannon from Nulodion. */
public class Nulodion extends WorldStorage {
  protected Nulodion(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.NULODION, plugin);

    hasStaticItems = true;

    items.add(new ItemStack(ItemID.CANNON_STAND, plugin));
    items.add(new ItemStack(ItemID.CANNON_BASE, plugin));
    items.add(new ItemStack(ItemID.CANNON_FURNACE, plugin));
    items.add(new ItemStack(ItemID.CANNON_BARREL, plugin));
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE) {
      String message = Text.removeTags(chatMessage.getMessage());
      if (message.startsWith("Your cannon has decayed")) {
        items.forEach(itemStack -> itemStack.setQuantity(1));
        lastUpdated = System.currentTimeMillis();
        return true;
      }

      if (message.startsWith("The dwarf gives you a new cannon")) {
        items.forEach(itemStack -> itemStack.setQuantity(0));
        lastUpdated = System.currentTimeMillis();
        return true;
      }
    }

    return false;
  }
}
