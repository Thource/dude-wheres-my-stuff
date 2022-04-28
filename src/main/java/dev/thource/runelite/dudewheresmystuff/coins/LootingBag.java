package dev.thource.runelite.dudewheresmystuff.coins;

import dev.thource.runelite.dudewheresmystuff.CoinsStorage;
import dev.thource.runelite.dudewheresmystuff.CoinsStorageType;
import java.util.Objects;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;

@Getter
public class LootingBag extends CoinsStorage {

  public LootingBag(Client client, ItemManager itemManager) {
    super(CoinsStorageType.LOOTING_BAG, client, itemManager);
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
