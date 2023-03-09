package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.widgets.Widget;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.math.NumberUtils;

/** MahoganyHomes is responsible for tracking the player's Mahogany Homes points. */
@Getter
public class MahoganyHomes extends MinigamesStorage {

  private final Pattern chatMessagePattern =
      Pattern.compile(
          "You have completed <col=ef1020>\\d+</col> contracts with a total of "
              + "<col=ef1020>(\\d+)</col> points\\.");

  private final ItemStack points = new ItemStack(ItemID.SAW, "Points", 0, 0, 0, true);

  MahoganyHomes(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.MAHOGANY_HOMES, plugin);

    items.add(points);
  }

  @Override
  public boolean onGameTick() {
    Widget widget = plugin.getClient().getWidget(673, 8);
    if (widget == null) {
      return false;
    }

    points.setQuantity(
        NumberUtils.toInt(Text.removeTags(widget.getText()).replaceAll("\\D+", ""), 0));
    lastUpdated = System.currentTimeMillis();
    return true;
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return false;
    }

    Matcher matcher = chatMessagePattern.matcher(chatMessage.getMessage().replace(",", ""));
    if (!matcher.matches()) {
      return false;
    }

    points.setQuantity(NumberUtils.toInt(matcher.group(1), 0));
    lastUpdated = System.currentTimeMillis();

    return true;
  }
}
