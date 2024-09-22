package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Region;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.widgets.Widget;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * GuardiansOfTheRift is responsible for tracking the player's Guardians of the Rift reward points.
 */
@Getter
public class GuardiansOfTheRift extends MinigamesStorage {

  private final ItemStack elementalEnergy =
      new ItemStack(ItemID.AIR_RUNE, "Elemental Energy", 0, 0, 0, true);
  private final ItemStack catalyticEnergy =
      new ItemStack(ItemID.COSMIC_RUNE, "Catalytic Energy", 0, 0, 0, true);

  private final Pattern chatPointsPattern =
      Pattern.compile(
          "Total elemental energy: <col=ef1020>(\\d+)</col>\\. "
              + "Total catalytic energy: {2}<col=ef1020>(\\d+)</col>\\.");

  private final Pattern widgetPattern =
      Pattern.compile("You have (\\d+) catalytic energy and (\\d+) elemental energy\\.");

  GuardiansOfTheRift(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.GUARDIANS_OF_THE_RIFT, plugin);

    items.add(elementalEnergy);
    items.add(catalyticEnergy);
  }

  @Override
  public boolean onGameTick() {
    Widget widget = plugin.getClient().getWidget(229, 1);
    if (widget == null) {
      return false;
    }

    Matcher matcher = widgetPattern.matcher(widget.getText().replace(",", ""));
    if (!matcher.find()) {
      return false;
    }

    final long lastElementalEnergy = elementalEnergy.getQuantity();
    final long lastCatalyticEnergy = catalyticEnergy.getQuantity();
    elementalEnergy.setQuantity(NumberUtils.toInt(matcher.group(2), 0));
    catalyticEnergy.setQuantity(NumberUtils.toInt(matcher.group(1), 0));
    updateLastUpdated();

    return lastElementalEnergy != elementalEnergy.getQuantity()
        || lastCatalyticEnergy != catalyticEnergy.getQuantity();
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return false;
    }

    if (chatMessage.getMessage().startsWith("You found some loot:")) {
      if (Region.get(
          WorldPoint.fromLocalInstance(
                  plugin.getClient(),
                  Objects.requireNonNull(plugin.getClient().getLocalPlayer())
                      .getLocalLocation())
              .getRegionID())
          != Region.MG_GUARDIANS_OF_THE_RIFT) {
        return false;
      }

      elementalEnergy.setQuantity(elementalEnergy.getQuantity() - 1);
      catalyticEnergy.setQuantity(catalyticEnergy.getQuantity() - 1);
      updateLastUpdated();

      return true;
    }

    Matcher matcher = chatPointsPattern.matcher(chatMessage.getMessage().replace(",", ""));
    if (!matcher.matches()) {
      return false;
    }

    final long lastElementalEnergy = elementalEnergy.getQuantity();
    final long lastCatalyticEnergy = catalyticEnergy.getQuantity();
    elementalEnergy.setQuantity(NumberUtils.toInt(matcher.group(1), 0));
    catalyticEnergy.setQuantity(NumberUtils.toInt(matcher.group(2), 0));
    updateLastUpdated();

    return lastElementalEnergy != elementalEnergy.getQuantity()
        || lastCatalyticEnergy != catalyticEnergy.getQuantity();
  }
}
