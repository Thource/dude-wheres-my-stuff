package dev.thource.runelite.dudewheresmystuff.coins;

import dev.thource.runelite.dudewheresmystuff.CoinsStorage;
import dev.thource.runelite.dudewheresmystuff.CoinsStorageType;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;
import org.apache.commons.lang3.math.NumberUtils;

public class ShiloFurnace extends CoinsStorage {

  public ShiloFurnace(Client client, ItemManager itemManager) {
    super(CoinsStorageType.SHILO_FURNACE, client, itemManager);
  }

  @Override
  public boolean onGameTick() {
    Widget widget = client.getWidget(219, 1);
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


