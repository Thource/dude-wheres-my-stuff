package dev.thource.runelite.dudewheresmystuff.carryable;

import java.util.Objects;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

/** LootingBag is responsible for tracking the player's items in their looting bag. */
@Getter
public class LootingBag extends CarryableStorage {

  public LootingBag(Client client, ClientThread clientThread, ItemManager itemManager) {
    super(CarryableStorageType.LOOTING_BAG, client, clientThread, itemManager);
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

    items.clear();
    lastUpdated = System.currentTimeMillis();
    return true;
  }
}
