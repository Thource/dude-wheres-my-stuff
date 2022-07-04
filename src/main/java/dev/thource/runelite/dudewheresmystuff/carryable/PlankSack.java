package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
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

    plankStack = new ItemStack(ItemID.PLANK, plugin);
    oakPlankStack = new ItemStack(ItemID.OAK_PLANK, plugin);
    teakPlankStack = new ItemStack(ItemID.TEAK_PLANK, plugin);
    mahoganyPlankStack = new ItemStack(ItemID.MAHOGANY_PLANK, plugin);

    items.add(plankStack);
    items.add(oakPlankStack);
    items.add(teakPlankStack);
    items.add(mahoganyPlankStack);
  }

  @Override
  public void reset() {
    plankStack.setQuantity(0);
    lastUpdated = -1;
    enable();
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
      lastUpdated = System.currentTimeMillis();
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
    lastUpdated = System.currentTimeMillis();
    return true;
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
    if (dataSplit.length != 5) {
      return;
    }

    lastUpdated = NumberUtils.toLong(dataSplit[0], -1);
    plankStack.setQuantity(NumberUtils.toLong(dataSplit[1], 0));
    oakPlankStack.setQuantity(NumberUtils.toLong(dataSplit[2], 0));
    teakPlankStack.setQuantity(NumberUtils.toLong(dataSplit[3], 0));
    mahoganyPlankStack.setQuantity(NumberUtils.toLong(dataSplit[4], 0));
  }
}
