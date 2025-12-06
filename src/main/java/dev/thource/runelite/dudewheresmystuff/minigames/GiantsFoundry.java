package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;
import org.apache.commons.lang3.math.NumberUtils;

/** GiantsFoundry is responsible for tracking the player's Giants' Foundry points. */
@Getter
public class GiantsFoundry extends MinigamesStorage {

  private static final Pattern HAND_IN_PATTERN = Pattern.compile("at quality: (\\d+)");

  private final ItemStack points =
      new ItemStack(ItemID.GIANTS_FOUNDRY_COLOSSAL_BLADE, "Points", 0, 0, 0, true);
  private boolean didJustHandIn = false;

  GiantsFoundry(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.GIANTS_FOUNDRY, plugin);

    items.add(points);
  }

  @Override
  public boolean onGameTick() {
    Widget shopWidget = plugin.getClient().getWidget(753, 13);
    if (shopWidget != null) {
      points.setQuantity(Integer.parseInt(shopWidget.getText()));
      updateLastUpdated();
      return true;
    }

    Widget chatWidget = plugin.getClient().getWidget(InterfaceID.Messagebox.TEXT);
    if (chatWidget != null) {
      if (!didJustHandIn) {
        Matcher matcher = HAND_IN_PATTERN.matcher(chatWidget.getText());
        if (matcher.find()) {
          points.setQuantity(points.getQuantity() + NumberUtils.toInt(matcher.group(1)));
          updateLastUpdated();
          didJustHandIn = true;
          return true;
        }
      }
    } else {
      didJustHandIn = false;
    }

    return false;
  }
}
