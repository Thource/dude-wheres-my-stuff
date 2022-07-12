package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import org.apache.commons.lang3.math.NumberUtils;

public class LogStorage extends WorldStorage {

  private final Pattern checkPattern =
      Pattern.compile(
          "This crate currently contains (\\d+) logs, (\\d+) oak logs, (\\d+) willow logs, (\\d+) yew logs and (\\d+) magic logs.");

  private final Pattern depositPattern =
      Pattern.compile("You put the (.*) in the crate. You now have (\\d+) stored.");

  private final Pattern chatPattern =
      Pattern.compile("You have (\\d+) sets of (.*) left in storage.");

  protected LogStorage(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.LOG_STORAGE, plugin);

    ItemStack logs = new ItemStack(ItemID.LOGS, plugin);
    logs.stripPrices();
    ItemStack oakLogs = new ItemStack(ItemID.OAK_LOGS, plugin);
    ItemStack willowLogs = new ItemStack(ItemID.WILLOW_LOGS, plugin);
    ItemStack yewLogs = new ItemStack(ItemID.YEW_LOGS, plugin);
    ItemStack magicLogs = new ItemStack(ItemID.MAGIC_LOGS, plugin);

    items.add(logs);
    items.add(oakLogs);
    items.add(willowLogs);
    items.add(yewLogs);
    items.add(magicLogs);

    plugin.getClientThread().invoke(() -> items.forEach(ItemStack::stripPrices));
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return false;
    }

    Matcher matcher = chatPattern.matcher(chatMessage.getMessage());
    if (!matcher.find()) {
      return false;
    }

    Optional<ItemStack> itemStack =
        items.stream().filter(i -> Objects.equals(i.getName(), matcher.group(2))).findFirst();
    if (!itemStack.isPresent()) {
      return false;
    }

    int quantity = NumberUtils.toInt(matcher.group(1), 0);
    if (itemStack.get().getQuantity() == quantity) {
      return false;
    }

    itemStack.get().setQuantity(quantity);
    this.lastUpdated = System.currentTimeMillis();
    return true;
  }

  @Override
  public boolean onGameTick() {
    return checkForCheck() || checkForDeposit();
  }

  private boolean checkForCheck() {
    Widget widget = plugin.getClient().getWidget(229, 1);
    if (widget == null) {
      return false;
    }

    Matcher matcher = checkPattern.matcher(widget.getText().replace("<br>", " "));
    if (!matcher.find()) {
      return false;
    }

    boolean updated = false;

    for (int i = 0; i < items.size(); i++) {
      ItemStack itemStack = items.get(i);
      int quantity = NumberUtils.toInt(matcher.group(i + 1), 0);

      if (itemStack.getQuantity() != quantity) {
        itemStack.setQuantity(quantity);
        updated = true;
      }
    }

    if (updated) {
      this.lastUpdated = System.currentTimeMillis();
    }

    return updated;
  }

  private boolean checkForDeposit() {
    Widget widget = plugin.getClient().getWidget(193, 2);
    if (widget == null) {
      return false;
    }

    Matcher matcher = depositPattern.matcher(widget.getText().replace("<br>", " "));
    if (!matcher.find()) {
      return false;
    }

    Optional<ItemStack> itemStack =
        items.stream().filter(i -> Objects.equals(i.getName(), matcher.group(1))).findFirst();
    if (!itemStack.isPresent()) {
      return false;
    }

    int quantity = NumberUtils.toInt(matcher.group(2), 0);
    if (itemStack.get().getQuantity() == quantity) {
      return false;
    }

    itemStack.get().setQuantity(quantity);
    this.lastUpdated = System.currentTimeMillis();
    return true;
  }

  @Override
  public void save(ConfigManager configManager, String managerConfigKey) {
    String data =
        lastUpdated
            + ";"
            + items.stream().map(item -> "" + item.getQuantity()).collect(Collectors.joining("="));

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
    if (dataSplit.length != 2) {
      return;
    }

    lastUpdated = NumberUtils.toLong(dataSplit[0], -1);

    String[] itemSplit = dataSplit[1].split("=");
    if (itemSplit.length != items.size()) {
      return;
    }

    for (int i = 0; i < items.size(); i++) {
      items.get(i).setQuantity(NumberUtils.toInt(itemSplit[i]));
    }
  }

  @Override
  public void reset() {
    for (ItemStack item : items) {
      item.setQuantity(0);
    }
    lastUpdated = -1;
    enable();
  }
}
