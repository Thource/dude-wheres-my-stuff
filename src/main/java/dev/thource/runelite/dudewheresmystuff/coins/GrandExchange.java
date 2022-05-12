package dev.thource.runelite.dudewheresmystuff.coins;

import java.util.Objects;
import java.util.stream.IntStream;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * GrandExchange is responsible for tracking how many coins the player has stored in the Grand
 * Exchange.
 */
public class GrandExchange extends CoinsStorage {

  GrandExchange(Client client, ClientThread clientThread, ItemManager itemManager) {
    super(CoinsStorageType.GRAND_EXCHANGE, client, clientThread, itemManager);
  }

  @Override
  public boolean onGameTick() {
    return updateFromGrandExchangeWindow() || updateFromCollectWindow();
  }

  private boolean updateFromCollectWindow() {
    if (client.getWidget(402, 1) == null) {
      return false;
    }

    long oldValue = coinStack.getQuantity();
    coinStack.setQuantity(IntStream.range(0, 8).mapToLong(this::getCoinsInCollectSlot).sum());
    lastUpdated = System.currentTimeMillis();

    return oldValue != coinStack.getQuantity();
  }

  private int getCoinsInCollectSlot(int slot) {
    Widget slotWidget = client.getWidget(402, 5 + slot);
    if (slotWidget == null) {
      return 0;
    }

    Widget itemWidget = slotWidget.getChild(3);
    if (itemWidget == null || itemWidget.getItemId() != 995 || itemWidget.isHidden()) {
      return 0;
    }

    return itemWidget.getItemQuantity();
  }

  private boolean updateFromGrandExchangeWindow() {
    if (client.getWidget(465, 1) == null) {
      return false;
    }

    long oldValue = coinStack.getQuantity();
    coinStack.setQuantity(IntStream.range(0, 8).mapToLong(this::getCoinsInGrandExchangeSlot).sum());
    lastUpdated = System.currentTimeMillis();

    return oldValue != coinStack.getQuantity();
  }

  private int getCoinsInGrandExchangeSlot(int slot) {
    Widget slotWidget = client.getWidget(465, 7 + slot);
    if (slotWidget == null) {
      return 0;
    }

    Widget offerType = slotWidget.getChild(16);
    if (offerType == null || !Objects.equals(offerType.getText(), "Buy")) {
      return 0;
    }

    Widget offerBar = slotWidget.getChild(22);
    if (offerBar == null || !Objects.equals(offerBar.getTextColor(), 0x8f0000)) {
      return 0;
    }

    Widget offerCoins = slotWidget.getChild(25);
    if (offerCoins == null) {
      return 0;
    }

    return NumberUtils.toInt(offerCoins.getText().replaceAll("\\D+", ""), 0);
  }
}
