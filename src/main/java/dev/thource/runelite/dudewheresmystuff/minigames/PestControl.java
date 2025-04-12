package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.widgets.Widget;
import org.apache.commons.lang3.math.NumberUtils;

/** PestControl is responsible for tracking the player's Pest Control points. */
@Getter
public class PestControl extends MinigamesStorage {

  private static final Pattern afterGamePattern1 = Pattern.compile(
      "awarded you (\\d+) Void Knight");
  private static final Pattern afterGamePattern2 = Pattern.compile(
      "now have <col=800000>(\\d+)<col=000080> Void Knight");
  private static final Pattern afterPurchasePattern = Pattern.compile(
      "Remaining Void Knight Commendation Points: (\\d+)");

  private final ItemStack points =
      new ItemStack(ItemID.PEST_SEAL_1, "Points", 0, 0, 0, true);

  private Widget shopWidget = null;

  PestControl(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.PEST_CONTROL, plugin);

    items.add(points);
  }

  @Override
  public boolean onGameTick() {
    // This can't go in onWidgetLoaded because for some reason the text isn't populated at that
    // point
    Widget widget = plugin.getClient().getWidget(229, 1);
    if (widget != null) {
      String widgetText = widget.getText().replace("<br>", " ").replace(",", "");
      Matcher matcher = afterGamePattern2.matcher(widgetText);
      if (matcher.find()) {
        points.setQuantity(NumberUtils.toInt(matcher.group(1)));
        updateLastUpdated();
        return true;
      }

      matcher = afterPurchasePattern.matcher(widgetText);
      if (matcher.find()) {
        points.setQuantity(NumberUtils.toInt(matcher.group(1)));
        updateLastUpdated();
        return true;
      }
    }

    return updateFromWidget();
  }

  @Override
  public boolean onWidgetLoaded(WidgetLoaded widgetLoaded) {
    if (widgetLoaded.getGroupId() == 243) {
      shopWidget = plugin.getClient().getWidget(243, 0);
      return updateFromWidget();
    } else if (widgetLoaded.getGroupId() == 231) {
      Widget widget = plugin.getClient().getWidget(231, 6);
      if (widget != null) {
        Matcher matcher = afterGamePattern1.matcher(widget.getText().replace("<br>", " "));
        if (matcher.find()) {
          points.setQuantity(points.getQuantity() + NumberUtils.toInt(matcher.group(1)));
          updateLastUpdated();
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public void onWidgetClosed(WidgetClosed widgetClosed) {
    if (widgetClosed.getGroupId() == 243) {
      shopWidget = null;
    }
  }

  boolean updateFromWidget() {
    if (shopWidget == null) {
      return false;
    }

    updateLastUpdated();
    points.setQuantity(plugin.getClient().getVarpValue(VarPlayerID.IF1));
    return true;
  }
}
