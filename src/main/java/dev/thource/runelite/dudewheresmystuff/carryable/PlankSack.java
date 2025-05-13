package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * PlankSack is responsible for tracking how many planks the player has stored in their plank sack.
 */
@Getter
public class PlankSack extends CarryableStorage {

  // Thanks for using \u00A0, Jagex...
  private static final Pattern chatPattern =
      Pattern.compile(
          "Basic\\u00A0planks:\\u00A0(\\d+), Oak\\u00A0planks:\\u00A0(\\d+), "
              + "Teak\\u00A0planks:\\u00A0(\\d+), Mahogany\\u00A0planks:\\u00A0(\\d+)");
  private static final Pattern usePattern = Pattern.compile(".*[Pp]lank -> Plank sack");

  private final ItemStack plankStack;
  private final ItemStack oakPlankStack;
  private final ItemStack teakPlankStack;
  private final ItemStack mahoganyPlankStack;

  PlankSack(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.PLANK_SACK, plugin);

    hasStaticItems = true;

    plankStack = new ItemStack(ItemID.WOODPLANK, plugin);
    oakPlankStack = new ItemStack(ItemID.PLANK_OAK, plugin);
    teakPlankStack = new ItemStack(ItemID.PLANK_TEAK, plugin);
    mahoganyPlankStack = new ItemStack(ItemID.PLANK_MAHOGANY, plugin);

    items.add(plankStack);
    items.add(oakPlankStack);
    items.add(teakPlankStack);
    items.add(mahoganyPlankStack);
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return false;
    }

    if (chatMessage.getMessage().equals("Your sack is empty.")) {
      plankStack.setQuantity(0);
      oakPlankStack.setQuantity(0);
      teakPlankStack.setQuantity(0);
      mahoganyPlankStack.setQuantity(0);
      updateLastUpdated();
      return true;
    }

    Matcher matcher = chatPattern.matcher(Text.removeTags(chatMessage.getMessage()));
    if (!matcher.matches()) {
      return false;
    }

    plankStack.setQuantity(NumberUtils.toInt(matcher.group(1)));
    oakPlankStack.setQuantity(NumberUtils.toInt(matcher.group(2)));
    teakPlankStack.setQuantity(NumberUtils.toInt(matcher.group(3)));
    mahoganyPlankStack.setQuantity(NumberUtils.toInt(matcher.group(4)));
    updateLastUpdated();
    return true;
  }
}
