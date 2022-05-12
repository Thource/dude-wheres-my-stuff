package dev.thource.runelite.dudewheresmystuff.coins;

import java.util.Objects;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

/** LootingBag is responsible for tracking the player's coins in their looting bag. */
@Getter
public class LootingBag extends CoinsStorage {

  LootingBag(Client client, ClientThread clientThread, ItemManager itemManager) {
    super(CoinsStorageType.LOOTING_BAG, client, clientThread, itemManager);
  }

  @Override
  public boolean onGameTick() {
    Widget lootingBagWidget = client.getWidget(81, 5);
    if (lootingBagWidget == null) {
      return false;
    }

    Widget emptyText = lootingBagWidget.getChild(28);
    if (emptyText == null || !Objects.equals(emptyText.getText(), "The bag is empty.")) {
      return false;
    }

    coinStack.setQuantity(0);
    lastUpdated = System.currentTimeMillis();
    return true;
  }
}
