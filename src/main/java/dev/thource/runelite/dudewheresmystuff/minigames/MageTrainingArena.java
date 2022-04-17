package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.MinigameStorage;
import dev.thource.runelite.dudewheresmystuff.MinigameStorageType;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class MageTrainingArena extends MinigameStorage {
    ItemStack telekineticPoints = new ItemStack(ItemID.LAW_RUNE, "Telekinetic Points", 0, 0, 0, true);
    ItemStack graveyardPoints = new ItemStack(ItemID.PEACH, "Graveyard Points", 0, 0, 0, true);
    ItemStack enchantmentPoints = new ItemStack(ItemID.CYLINDER, "Enchantment Points", 0, 0, 0, true);
    ItemStack alchemistPoints = new ItemStack(ItemID.COINS, "Alchemist Points", 0, 0, 0, true);

    Map<ItemStack, MageTrainingArenaPoint> pointData = new HashMap<>();

    Widget shopWidget = null;

    public MageTrainingArena(Client client, ItemManager itemManager) {
        super(MinigameStorageType.MAGE_TRAINING_ARENA, client, itemManager);

        items.add(telekineticPoints);
        items.add(graveyardPoints);
        items.add(enchantmentPoints);
        items.add(alchemistPoints);

        pointData.put(telekineticPoints, new MageTrainingArenaPoint(198, 261));
        pointData.put(alchemistPoints, new MageTrainingArenaPoint(194, 262));
        pointData.put(enchantmentPoints, new MageTrainingArenaPoint(195, 263));
        pointData.put(graveyardPoints, new MageTrainingArenaPoint(196, 264));
    }

    @Override
    public boolean onGameTick() {
        return updateFromWidgets();
    }

    @Override
    public boolean onWidgetLoaded(WidgetLoaded widgetLoaded) {
        if (client.getLocalPlayer() == null) return false;

        if (widgetLoaded.getGroupId() == 197) {
            shopWidget = client.getWidget(197, 0);
        } else {
            pointData.forEach((itemStack, pointData) -> {
                if (widgetLoaded.getGroupId() != pointData.getWidgetId()) return;

                pointData.widget = client.getWidget(pointData.getWidgetId(), 6);
            });
        }

        return updateFromWidgets();
    }

    @Override
    public boolean onWidgetClosed(WidgetClosed widgetClosed) {
        if (client.getLocalPlayer() == null) return false;

        if (widgetClosed.getGroupId() == 197) {
            shopWidget = null;
        } else {
            pointData.forEach((itemStack, pointData) -> {
                if (widgetClosed.getGroupId() != pointData.getWidgetId()) return;

                pointData.widget = null;
            });
        }

        return false;
    }

    boolean updateFromWidgets() {
        AtomicBoolean updated = new AtomicBoolean(false);

        if (shopWidget != null) {
            lastUpdated = Instant.now();
            pointData.forEach((itemStack, pointData) -> {
                int newPoints = client.getVarpValue(pointData.getVarpId());
                if (newPoints == pointData.getLastVarpValue()) return;

                itemStack.setQuantity(newPoints);
                pointData.lastVarpValue = newPoints;
                updated.set(true);
            });

            return updated.get();
        }

        pointData.forEach((itemStack, pointData) -> {
            if (pointData.getWidget() == null) return;

            lastUpdated = Instant.now();
            int newPoints = NumberUtils.toInt(pointData.getWidget().getText(), 0);
            if (!type.isAutomatic()) updated.set(true);
            if (newPoints == pointData.getLastWidgetValue()) return;

            itemStack.setQuantity(newPoints);
            pointData.lastWidgetValue = newPoints;
            updated.set(true);
        });

        return updated.get();
    }

    @Override
    public void reset() {
        super.reset();

        pointData.forEach((itemStack, mageTrainingArenaPoint) -> mageTrainingArenaPoint.reset());
    }
}
