package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemStackUtils;
import dev.thource.runelite.dudewheresmystuff.Seed;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.widgets.Widget;
import org.apache.commons.lang3.math.NumberUtils;

/** SeedBox is responsible for tracking how many seeds the player has stored in their seed box. */
@Getter
public class SeedBox extends CarryableStorage {

  private static final Pattern[] additionPatterns = {
      Pattern.compile("Stored (?<count>\\d+) x (?<seed>.+) in your seed box\\."),
      Pattern.compile("You put (?<count>\\d+) x (?<seed>.+) straight into your open seed box\\."),
      Pattern.compile(
          "The following stolen loot gets added to your seed box: (?<seed>.+) x (?<count>\\d+)\\."),
      Pattern.compile("You put the stolen (?<seed>.+) into your seed box\\."),
  };
  private static final Pattern[] removalPatterns = {
      Pattern.compile("Emptied (?<count>\\d+) x (?<seed>.+) to your inventory\\."),
  };

  SeedBox(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.SEED_BOX, plugin);
  }

  @Override
  public boolean onGameTick() {
    boolean didUpdate = super.onGameTick();

    Widget seedBoxWidget = plugin.getClient().getWidget(128, 11);
    if (seedBoxWidget == null) {
      return didUpdate;
    }

    Widget[] seedBoxItems = seedBoxWidget.getChildren();
    if (seedBoxItems == null
        || Arrays.stream(seedBoxWidget.getChildren()).anyMatch(w -> w.getItemId() != -1)) {
      return didUpdate;
    }

    items.clear();
    updateLastUpdated();
    return true;
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return false;
    }

    return checkForAdditions(chatMessage.getMessage())
        || checkForRemovals(chatMessage.getMessage());
  }

  private boolean checkForAdditions(String chatMessage) {
    for (Pattern pattern : additionPatterns) {
      Matcher matcher = pattern.matcher(chatMessage);
      if (!matcher.matches()) {
        continue;
      }

      int quantity = 1;
      try {
        quantity = NumberUtils.toInt(matcher.group("count"));
      } catch (IllegalArgumentException e) {
        // Do nothing, count is an optional group
      }

      Optional<Seed> optionalSeed = Seed.findByName(matcher.group("seed"));
      if (!optionalSeed.isPresent()) {
        return false;
      }

      ItemStackUtils.addItemStack(
          items, new ItemStack(optionalSeed.get().getItemId(), quantity, plugin));
      updateLastUpdated();
      return true;
    }

    return false;
  }

  private boolean checkForRemovals(String chatMessage) {
    for (Pattern pattern : removalPatterns) {
      Matcher matcher = pattern.matcher(chatMessage);
      if (!matcher.matches()) {
        continue;
      }

      int quantity = NumberUtils.toInt(matcher.group("count"), 1);
      Optional<Seed> optionalSeed = Seed.findByName(matcher.group("seed"));
      if (!optionalSeed.isPresent()) {
        return false;
      }

      ItemStackUtils.removeItemStack(
          items, new ItemStack(optionalSeed.get().getItemId(), quantity, plugin));
      updateLastUpdated();
      return true;
    }

    return false;
  }
}
