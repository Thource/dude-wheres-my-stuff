package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.widgets.Widget;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * GnomishFirelighter is responsible for tracking how many charges of which type of firelighter the
 * player has stored in their gnomish firelighter.
 */
@Slf4j
public class GnomishFirelighter extends CarryableStorage {

  private static final Pattern chargesPattern = Pattern.compile(
      "(\\d+) (\\w+) firelighter charges");

  GnomishFirelighter(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.GNOMISH_FIRELIGHTER, plugin);

    hasStaticItems = true;

    items.add(new ItemStack(ItemID.RED_FIRELIGHTER, 0, plugin));
    items.add(new ItemStack(ItemID.GREEN_FIRELIGHTER, 0, plugin));
    items.add(new ItemStack(ItemID.BLUE_FIRELIGHTER, 0, plugin));
    items.add(new ItemStack(ItemID.PURPLE_FIRELIGHTER, 0, plugin));
    items.add(new ItemStack(ItemID.WHITE_FIRELIGHTER, 0, plugin));
  }

  @Override
  public boolean onGameTick() {
    Widget widget = plugin.getClient().getWidget(193, 2);
    if (widget == null) {
      return false;
    }

    String widgetText = widget.getText().replace("<br>", " ");
    if (!widgetText.contains("gnomish firelighter")) {
      return false;
    }

    if (widgetText.contains("is empty")) {
      items.forEach(itemStack -> itemStack.setQuantity(0));
      this.lastUpdated = System.currentTimeMillis();
      return true;
    }

    Matcher matcher = chargesPattern.matcher(widgetText);
    int charges = 0;
    Optional<ItemStack> itemStack = Optional.empty();
    if (matcher.find()) {
      charges = NumberUtils.toInt(matcher.group(1));
      itemStack = items.stream().filter(i -> i.getName().contains(matcher.group(2))).findFirst();
    }

    if (!itemStack.isPresent()) {
      return false;
    }

    itemStack.get().setQuantity(charges);
    lastUpdated = System.currentTimeMillis();

    return true;
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return false;
    }

    if (!chatMessage.getMessage().startsWith("You uncharge the gnomish firelighter")) {
      return false;
    }

    items.forEach(itemStack -> itemStack.setQuantity(0));
    lastUpdated = System.currentTimeMillis();
    return true;
  }
}
