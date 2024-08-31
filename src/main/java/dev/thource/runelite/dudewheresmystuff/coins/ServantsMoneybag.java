package dev.thource.runelite.dudewheresmystuff.coins;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import net.runelite.api.widgets.Widget;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * ServantsMoneybag is responsible for tracking how many coins the player has stored in their
 * servant's moneybag.
 */
public class ServantsMoneybag extends CoinsStorage {

  ServantsMoneybag(DudeWheresMyStuffPlugin plugin) {
    super(CoinsStorageType.SERVANT_MONEYBAG, plugin);
  }

  @Override
  public boolean onGameTick() {
    Widget widget = plugin.getClient().getWidget(193, 2);
    if (widget == null) {
      return false;
    }
    if (!widget.getText().startsWith("The money bag ")) {
      return false;
    }

    int parsedCoins = NumberUtils.toInt(widget.getText().replaceAll("\\D+", ""), 0);
    this.getCoinStack().setQuantity(parsedCoins);
    this.lastUpdated = System.currentTimeMillis();
    return true;
  }
}
