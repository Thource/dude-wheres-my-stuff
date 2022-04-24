package dev.thource.runelite.dudewheresmystuff.coins;

import dev.thource.runelite.dudewheresmystuff.CoinsStorage;
import dev.thource.runelite.dudewheresmystuff.CoinsStorageType;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Objects;

public class GrandExchange extends CoinsStorage {
    public GrandExchange(Client client, ItemManager itemManager) {
        super(CoinsStorageType.GRAND_EXCHANGE, client, itemManager);
    }

    @Override
    public boolean onGameTick() {
        return updateFromGEWindow() || updateFromCollectWindow();
    }

    private boolean updateFromCollectWindow() {
        if (client.getWidget(402, 1) == null) return false;

        long oldValue = coinStack.getQuantity();
        coinStack.setQuantity(0L);
        for (int i = 0; i < 8; i++) {
            Widget slotWidget = client.getWidget(402, 5 + i);
            if (slotWidget == null) continue;

            Widget itemWidget = slotWidget.getChild(3);
            if (itemWidget == null) continue;
            if (itemWidget.getItemId() != 995) continue;
            if (itemWidget.isHidden()) continue;

            coinStack.setQuantity(coinStack.getQuantity() + itemWidget.getItemQuantity());
        }
        lastUpdated = System.currentTimeMillis();

        return oldValue != coinStack.getQuantity();
    }

    private boolean updateFromGEWindow() {
        if (client.getWidget(465, 1) == null) return false;

        long oldValue = coinStack.getQuantity();
        coinStack.setQuantity(0L);
        for (int i = 0; i < 8; i++) {
            Widget slotWidget = client.getWidget(465, 7 + i);
            if (slotWidget == null) continue;

            Widget offerType = slotWidget.getChild(16);
            if (offerType == null) continue;
            if (!Objects.equals(offerType.getText(), "Buy")) continue;

            Widget offerBar = slotWidget.getChild(22);
            if (offerBar == null) continue;
            if (!Objects.equals(offerBar.getTextColor(), 0x8f0000)) continue;

            Widget offerCoins = slotWidget.getChild(25);
            if (offerCoins == null) continue;

            coinStack.setQuantity(coinStack.getQuantity() + NumberUtils.toInt(offerCoins.getText().replaceAll("\\D+", ""), 0));
        }

        lastUpdated = System.currentTimeMillis();

        return oldValue != coinStack.getQuantity();
    }
}


