package dev.thource.runelite.dudewheresmystuff.coins;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import net.runelite.api.widgets.Widget;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * ShiloFurnace is responsible for tracking how many coins the player has stored in the shilo
 * village furnace coffer.
 */
public class ShiloFurnace extends CoinsStorage {

  ShiloFurnace(DudeWheresMyStuffPlugin plugin) {
    super(CoinsStorageType.SHILO_FURNACE, plugin);
  }

  @Override
  public boolean onGameTick() {
    Widget widget = plugin.getClient().getWidget(219, 1);
    if (widget == null) {
      return false;
    }

    Widget textWidget = widget.getChild(0);
    if (textWidget == null) {
      return false;
    }
    if (!textWidget.getText().startsWith("Furnace coffer: ")) {
      return false;
    }

    int parsedCoins = NumberUtils.toInt(textWidget.getText().replaceAll("\\D+", ""), 0);
    this.getCoinStack().setQuantity(parsedCoins);
    this.lastUpdated = System.currentTimeMillis();
    return true;
  }
}
