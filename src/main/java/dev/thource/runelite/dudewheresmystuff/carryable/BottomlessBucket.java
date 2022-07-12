package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
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

    compostStack = new ItemStack(ItemID.COMPOST, plugin);
    supercompostStack = new ItemStack(ItemID.SUPERCOMPOST, plugin);
    ultracompostStack = new ItemStack(ItemID.ULTRACOMPOST, plugin);

    items.add(compostStack);
    items.add(supercompostStack);
    items.add(ultracompostStack);

    plugin.getClientThread().invoke(() -> items.forEach(ItemStack::stripPrices));
  }

  @Override
  public boolean onGameTick() {
    Widget widget = plugin.getClient().getWidget(193, 2);
    if (widget == null) {
      return false;
    }

    String widgetText = widget.getText().replace("<br>", " ");
    if (!widgetText.contains("compost bucket")) {
      return false;
    }

    if (widgetText.contains("currently empty") || widgetText.startsWith("You discard")) {
      compostStack.setQuantity(0);
      supercompostStack.setQuantity(0);
      ultracompostStack.setQuantity(0);
      this.lastUpdated = System.currentTimeMillis();
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
    this.lastUpdated = System.currentTimeMillis();

    if (widgetText.contains("ultracompost")) {
      ultracompostStack.setQuantity(charges);
    } else if (widgetText.contains("supercompost")) {
      supercompostStack.setQuantity(charges);
    } else {
      compostStack.setQuantity(charges);
    }

    return true;
  }

  @Override
  public void reset() {
    compostStack.setQuantity(0);
    supercompostStack.setQuantity(0);
    ultracompostStack.setQuantity(0);
    lastUpdated = -1;
    enable();
  }

  @Override
  public void save(ConfigManager configManager, String managerConfigKey) {
    String data =
        getLastUpdated()
            + ";"
            + items.stream().map(item -> "" + item.getQuantity()).collect(Collectors.joining(";"));

    configManager.setRSProfileConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, managerConfigKey + "." + type.getConfigKey(), data);
  }

  @Override
  public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
    String data =
        configManager.getConfiguration(
            DudeWheresMyStuffConfig.CONFIG_GROUP,
            profileKey,
            managerConfigKey + "." + type.getConfigKey(),
            String.class);
    if (data == null) {
      return;
    }

    String[] dataSplit = data.split(";");
    if (dataSplit.length != 4) {
      return;
    }

    lastUpdated = NumberUtils.toLong(dataSplit[0], -1);
    compostStack.setQuantity(NumberUtils.toLong(dataSplit[1], 0));
    supercompostStack.setQuantity(NumberUtils.toLong(dataSplit[2], 0));
    ultracompostStack.setQuantity(NumberUtils.toLong(dataSplit[3], 0));
  }

  /**
   * Updates the compost count, for use by Leprechaun.
   *
   * @param type 0 = missing, 1 = empty, 2 = compost, 3 = supercompost, 4 = ultracompost
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
    this.lastUpdated = System.currentTimeMillis();

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
      this.lastUpdated = System.currentTimeMillis();
      return true;
    }

    Matcher matcher = chargesPattern.matcher(chatMessage.getMessage());
    int charges = 1;
    if (matcher.find()) {
      charges = NumberUtils.toInt(matcher.group(1));
    } else if (!chatMessage.getMessage().contains("single use")) {
      return false;
    }

    compostStack.setQuantity(0);
    supercompostStack.setQuantity(0);
    ultracompostStack.setQuantity(0);
    this.lastUpdated = System.currentTimeMillis();

    if (chatMessage.getMessage().contains("ultracompost")) {
      ultracompostStack.setQuantity(charges);
    } else if (chatMessage.getMessage().contains("supercompost")) {
      supercompostStack.setQuantity(charges);
    } else {
      compostStack.setQuantity(charges);
    }

    return true;
  }
}
