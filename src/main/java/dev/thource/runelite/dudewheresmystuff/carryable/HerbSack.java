package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.regex.Pattern;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.gameval.ItemID;

/** HerbSack is responsible for tracking the player's items in their herb sack. */
public class HerbSack extends CarryableStorage {

  private static final Pattern CHECK_PATTERN = Pattern.compile("(\\d+) x (.*)");
  private static final Pattern PICK_UP_PATTERN =
      Pattern.compile("You put the (.*) herb into your herb sack");
  private static final Pattern USE_PATTERN = Pattern.compile("You add the (.*) to your sack");

  private boolean checkingSack;
  private boolean addingToSack;
  private boolean removingFromSack;

  public HerbSack(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.HERB_SACK, plugin);

    hasStaticItems = true;

    items.add(new ItemStack(ItemID.UNIDENTIFIED_GUAM, 0, plugin));
    items.add(new ItemStack(ItemID.UNIDENTIFIED_MARENTILL, 0, plugin));
    items.add(new ItemStack(ItemID.UNIDENTIFIED_TARROMIN, 0, plugin));
    items.add(new ItemStack(ItemID.UNIDENTIFIED_HARRALANDER, 0, plugin));
    items.add(new ItemStack(ItemID.UNIDENTIFIED_RANARR, 0, plugin));
    items.add(new ItemStack(ItemID.UNIDENTIFIED_TOADFLAX, 0, plugin));
    items.add(new ItemStack(ItemID.UNIDENTIFIED_IRIT, 0, plugin));
    items.add(new ItemStack(ItemID.UNIDENTIFIED_AVANTOE, 0, plugin));
    items.add(new ItemStack(ItemID.UNIDENTIFIED_KWUARM, 0, plugin));
    items.add(new ItemStack(ItemID.UNIDENTIFIED_HUASCA, 0, plugin));
    items.add(new ItemStack(ItemID.UNIDENTIFIED_SNAPDRAGON, 0, plugin));
    items.add(new ItemStack(ItemID.UNIDENTIFIED_CADANTINE, 0, plugin));
    items.add(new ItemStack(ItemID.UNIDENTIFIED_LANTADYME, 0, plugin));
    items.add(new ItemStack(ItemID.UNIDENTIFIED_DWARF_WEED, 0, plugin));
    items.add(new ItemStack(ItemID.UNIDENTIFIED_TORSTOL, 0, plugin));
  }

  private void addQuantityByName(String name, int quantity) {
    items.stream()
        .filter(itemStack -> itemStack.getName().equals(name))
        .findFirst()
        .ifPresent(itemStack -> itemStack.setQuantity(itemStack.getQuantity() + quantity));
  }

  private void setQuantityByName(String name, int quantity) {
    items.stream()
        .filter(itemStack -> itemStack.getName().equals(name))
        .findFirst()
        .ifPresent(itemStack -> itemStack.setQuantity(quantity));
  }

  @Override
  public boolean onGameTick() {
    boolean didUpdate = super.onGameTick();

    if (checkingSack) {
      checkingSack = false;
      updateLastUpdated();
      didUpdate = true;
    }

    if (addingToSack) {
      ItemContainerWatcher.getInventoryWatcher()
          .getItemsRemovedLastTick()
          .forEach(itemStack -> addQuantityByName(itemStack.getName(), 1));

      addingToSack = false;
      updateLastUpdated();
      didUpdate = true;
    }

    if (removingFromSack) {
      ItemContainerWatcher.getInventoryWatcher()
          .getItemsAddedLastTick()
          .forEach(itemStack -> addQuantityByName(itemStack.getName(), -1));

      removingFromSack = false;
      updateLastUpdated();
      didUpdate = true;
    }

    return didUpdate;
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return false;
    }

    if (checkingSack) {
      var matcher = CHECK_PATTERN.matcher(chatMessage.getMessage());
      if (matcher.find()) {
        var quantity = Integer.parseInt(matcher.group(1));
        var name = matcher.group(2);

        setQuantityByName(name, quantity);

        return true;
      }

      return false;
    }

    var pickUpMatcher = PICK_UP_PATTERN.matcher(chatMessage.getMessage());
    if (pickUpMatcher.find()) {
      var name = pickUpMatcher.group(1);
      addQuantityByName(name, 1);

      updateLastUpdated();
      return true;
    }

    if (USE_PATTERN.matcher(chatMessage.getMessage()).find()) {
      addingToSack = true;
    } else if (chatMessage.getMessage().startsWith("You look in your herb sack")) {
      checkingSack = true;
    } else if (chatMessage.getMessage().startsWith("The herb sack is empty")) {
      resetItems();
      updateLastUpdated();
    } else if (chatMessage.getMessage().startsWith("You add the herbs to your sack")) {
      addingToSack = true;
    } else if (chatMessage
        .getMessage()
        .startsWith("You rummage around to see if you can extract any herbs from your herb sack")) {
      removingFromSack = true;
    }

    return false;
  }
}
