package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import java.util.Objects;
import lombok.Getter;
import net.runelite.api.widgets.Widget;

/** LootingBag is responsible for tracking the player's items in their looting bag. */
@Getter
public class LootingBag extends CarryableStorage {

  public LootingBag(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.LOOTING_BAG, plugin);
  }

  @Override
  public boolean onGameTick() {
    boolean didUpdate = super.onGameTick();

    Widget lootingBagWidget = plugin.getClient().getWidget(81, 5);
    if (lootingBagWidget == null) {
      return didUpdate;
    }

    Widget emptyText = lootingBagWidget.getChild(28);
    if (emptyText == null || !Objects.equals(emptyText.getText(), "The bag is empty.")) {
      return didUpdate;
    }

    items.clear();
    lastUpdated = System.currentTimeMillis();
    return true;
  }
}
