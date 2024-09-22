package dev.thource.runelite.dudewheresmystuff.coins;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import java.util.Objects;
import java.util.stream.IntStream;
import net.runelite.api.widgets.Widget;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * GrandExchange is responsible for tracking how many coins the player has stored in the Grand
 * Exchange.
 */
public class GrandExchange extends CoinsStorage {

  GrandExchange(DudeWheresMyStuffPlugin plugin) {
    super(CoinsStorageType.GRAND_EXCHANGE, plugin);
  }

  @Override
  public boolean onGameTick() {
    return updateFromGrandExchangeWindow() || updateFromCollectWindow();
  }

  private boolean updateFromCollectWindow() {
    if (plugin.getClient().getWidget(402, 1) == null) {
      return false;
    }

    long oldValue = coinStack.getQuantity();
    coinStack.setQuantity(IntStream.range(0, 8).mapToLong(this::getCoinsInCollectSlot).sum());
    updateLastUpdated();

    return oldValue != coinStack.getQuantity();
  }

  private int getCoinsInCollectSlot(int slot) {
    Widget slotWidget = plugin.getClient().getWidget(402, 5 + slot);
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
    if (plugin.getClient().getWidget(465, 1) == null) {
      return false;
    }

    long oldValue = coinStack.getQuantity();
    coinStack.setQuantity(IntStream.range(0, 8).mapToLong(this::getCoinsInGrandExchangeSlot).sum());
    updateLastUpdated();

    return oldValue != coinStack.getQuantity();
  }

  private int getCoinsInGrandExchangeSlot(int slot) {
    Widget slotWidget = plugin.getClient().getWidget(465, 7 + slot);
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
