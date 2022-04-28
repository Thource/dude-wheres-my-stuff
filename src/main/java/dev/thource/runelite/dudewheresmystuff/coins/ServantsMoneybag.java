package dev.thource.runelite.dudewheresmystuff.coins;

import dev.thource.runelite.dudewheresmystuff.CoinsStorage;
import dev.thource.runelite.dudewheresmystuff.CoinsStorageType;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;
import org.apache.commons.lang3.math.NumberUtils;

public class ServantsMoneybag extends CoinsStorage {

  public ServantsMoneybag(Client client, ItemManager itemManager) {
    super(CoinsStorageType.SERVANT_MONEYBAG, client, itemManager);
  }

  @Override
  public boolean onGameTick() {
    Widget widget = client.getWidget(193, 2);
      if (widget == null) {
          return false;
      }
      if (!widget.getText().startsWith("The moneybag ")) {
          return false;
      }

    int parsedCoins = NumberUtils.toInt(widget.getText().replaceAll("\\D+", ""), 0);
    this.getCoinStack().setQuantity(parsedCoins);
    this.lastUpdated = System.currentTimeMillis();
    return true;
  }
}


