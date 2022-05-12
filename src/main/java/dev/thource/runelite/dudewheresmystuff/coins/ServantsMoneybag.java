package dev.thource.runelite.dudewheresmystuff.coins;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * ServantsMoneybag is responsible for tracking how many coins the player has stored in their
 * servant's moneybag.
 */
public class ServantsMoneybag extends CoinsStorage {

  ServantsMoneybag(Client client, ClientThread clientThread, ItemManager itemManager) {
    super(CoinsStorageType.SERVANT_MONEYBAG, client, clientThread, itemManager);
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
