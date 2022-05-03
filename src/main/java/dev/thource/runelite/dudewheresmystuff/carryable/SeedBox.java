package dev.thource.runelite.dudewheresmystuff.carryable;

import java.util.Arrays;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;

@Getter
public class SeedBox extends CarryableStorage {

  public SeedBox(Client client, ItemManager itemManager) {
    super(CarryableStorageType.SEED_BOX, client, itemManager);
  }

  @Override
  public boolean onGameTick() {
    Widget seedBoxWidget = client.getWidget(128, 11);
    if (seedBoxWidget == null) {
      return false;
    }

    Widget[] seedBoxItems = seedBoxWidget.getChildren();
    if (seedBoxItems == null || Arrays.stream(seedBoxWidget.getChildren())
        .anyMatch(w -> w.getItemId() != -1)) {
      return false;
    }

    items.clear();
    lastUpdated = System.currentTimeMillis();
    return true;
  }
}
