package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * BottomlessBucket is responsible for tracking how many charges of which type of compost the player
 * has stored in their bottomless compost bucket.
 */
@Slf4j
public class BottomlessBucket extends CarryableStorage {

  private static final Pattern chargesPattern = Pattern.compile("(\\d+) uses");

  private final ItemStack compostStack;
  private final ItemStack supercompostStack;
  private final ItemStack ultracompostStack;

  BottomlessBucket(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.BOTTOMLESS_BUCKET, plugin);

    hasStaticItems = true;

    compostStack = new ItemStack(ItemID.BUCKET_COMPOST, plugin);
    supercompostStack = new ItemStack(ItemID.BUCKET_SUPERCOMPOST, plugin);
    ultracompostStack = new ItemStack(ItemID.BUCKET_ULTRACOMPOST, plugin);

    items.add(compostStack);
    items.add(supercompostStack);
    items.add(ultracompostStack);

    plugin.getClientThread().invokeLater(() -> items.forEach(ItemStack::stripPrices));
  }

  @Override
  public boolean onGameTick() {
    Widget widget = plugin.getClient().getWidget(193, 2);
    if (widget == null) {
      return false;
    }

    String widgetText = widget.getText().replace("<br>", " ").replace(",", "");
    if (!widgetText.contains("compost bucket")) {
      return false;
    }

    if (widgetText.contains("currently empty") || widgetText.startsWith("You discard")) {
      compostStack.setQuantity(0);
      supercompostStack.setQuantity(0);
      ultracompostStack.setQuantity(0);
      updateLastUpdated();
      return true;
    }

    Matcher matcher = chargesPattern.matcher(widgetText);
    int charges = 1;
    if (matcher.find()) {
      charges = NumberUtils.toInt(matcher.group(1));
    } else if (!widgetText.contains("one use")) {
      return false;
    }

    compostStack.setQuantity(0);
    supercompostStack.setQuantity(0);
    ultracompostStack.setQuantity(0);
    updateLastUpdated();

    if (widgetText.contains("ultracompost")) {
      ultracompostStack.setQuantity(charges);
    } else if (widgetText.contains("supercompost")) {
      supercompostStack.setQuantity(charges);
    } else {
      compostStack.setQuantity(charges);
    }

    return true;
  }

  /**
   * Updates the compost count, for use by Leprechaun.
   *
   * @param type    0 = missing, 1 = empty, 2 = compost, 3 = supercompost, 4 = ultracompost
   * @param charges the amount of charges
   */
  public void updateCompost(int type, int charges) {
    // Don't update on type 0, bucket has been withdrawn so charges are 0
    if (type == 0) {
      return;
    }

    compostStack.setQuantity(0);
    supercompostStack.setQuantity(0);
    ultracompostStack.setQuantity(0);
    updateLastUpdated();

    if (type == 2) {
      compostStack.setQuantity(charges);
    } else if (type == 3) {
      supercompostStack.setQuantity(charges);
    } else if (type == 4) {
      ultracompostStack.setQuantity(charges);
    }
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return false;
    }

    if (!chatMessage.getMessage().startsWith("Your bottomless compost bucket has")) {
      return false;
    }

    if (chatMessage.getMessage().contains("run out")) {
      compostStack.setQuantity(0);
      supercompostStack.setQuantity(0);
      ultracompostStack.setQuantity(0);
      updateLastUpdated();
      return true;
    }

    Matcher matcher = chargesPattern.matcher(chatMessage.getMessage().replace(",", ""));
    int charges = 1;
    if (matcher.find()) {
      charges = NumberUtils.toInt(matcher.group(1));
    } else if (!chatMessage.getMessage().contains("single use")) {
      return false;
    }

    compostStack.setQuantity(0);
    supercompostStack.setQuantity(0);
    ultracompostStack.setQuantity(0);
    updateLastUpdated();

    if (chatMessage.getMessage().contains("ultracompost")) {
      ultracompostStack.setQuantity(charges);
    } else if (chatMessage.getMessage().contains("supercompost")) {
      supercompostStack.setQuantity(charges);
    } else {
      compostStack.setQuantity(charges);
    }

    return true;
  }

  @Override
  public boolean isWithdrawable() {
    return false;
  }
}
