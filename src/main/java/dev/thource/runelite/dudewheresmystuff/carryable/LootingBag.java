package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.CarryableStorage;
import dev.thource.runelite.dudewheresmystuff.CarryableStorageType;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;

import java.util.Objects;

@Getter
public class LootingBag extends CarryableStorage {
    public LootingBag(Client client, ItemManager itemManager) {
        super(CarryableStorageType.LOOTING_BAG, client, itemManager);
    }

    @Override
    public boolean onGameTick() {
        Widget lootingBagWidget = client.getWidget(81, 5);
        if (lootingBagWidget == null) return false;

        Widget emptyText = lootingBagWidget.getChild(28);
        if (emptyText == null || !Objects.equals(emptyText.getText(), "The bag is empty.")) return false;

        items.clear();
        lastUpdated = System.currentTimeMillis();
        return true;
    }
}
