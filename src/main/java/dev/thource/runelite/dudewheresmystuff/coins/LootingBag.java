package dev.thource.runelite.dudewheresmystuff.coins;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import java.util.Objects;
import lombok.Getter;
import net.runelite.api.widgets.Widget;

/** LootingBag is responsible for tracking the player's coins in their looting bag. */
@Getter
public class LootingBag extends CoinsStorage {

  LootingBag(DudeWheresMyStuffPlugin plugin) {
    super(CoinsStorageType.LOOTING_BAG, plugin);
  }

  @Override
  public boolean onGameTick() {
    Widget lootingBagWidget = plugin.getClient().getWidget(81, 5);
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
