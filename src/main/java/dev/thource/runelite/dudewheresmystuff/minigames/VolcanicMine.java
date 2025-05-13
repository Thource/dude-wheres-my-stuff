package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.util.Text;

/** VolcanicMine is responsible for tracking the player's Volcanic Mine points. */
@Getter
public class VolcanicMine extends MinigamesStorage {

  private static final Pattern SHOP_PATTERN = Pattern.compile("Points: (\\d+)");

  private final ItemStack points = new ItemStack(ItemID.FOSSIL_VOLCANIC_ASH, "Points", 0, 0, 0, true);

  VolcanicMine(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.VOLCANIC_MINE, plugin);

    items.add(points);
  }

  @Override
  public boolean onGameTick() {
    return updateFromWidgets();
  }

  boolean updateFromWidgets() {
    Widget widget = plugin.getClient().getWidget(612, 5);
    if (widget == null) {
      return false;
    }

    String widgetText = Text.removeTags(widget.getText()).replace(",", "");
    Matcher matcher = SHOP_PATTERN.matcher(widgetText);
    if (!matcher.find()) {
      return false;
    }

    updateLastUpdated();
    int newPoints = Integer.parseInt(matcher.group(1));
    if (newPoints != points.getQuantity()) {
      points.setQuantity(newPoints);
    }
    return true;
  }
}
