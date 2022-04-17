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

import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class LastManStanding extends MinigameStorage {
    ItemStack points = new ItemStack(ItemID.SKULL, "Points", 0, 0, 0, true);

    Widget shopWidget = null;

    public LastManStanding(Client client, ItemManager itemManager) {
        super(MinigameStorageType.LAST_MAN_STANDING, client, itemManager);

        items.add(points);
    }

    @Override
    public boolean onWidgetLoaded(WidgetLoaded widgetLoaded) {
        if (client.getLocalPlayer() == null) return false;

        if (widgetLoaded.getGroupId() == 645) {
            shopWidget = client.getWidget(645, 0);
        }

        return updateFromWidgets();
    }

    @Override
    public boolean onWidgetClosed(WidgetClosed widgetClosed) {
        if (client.getLocalPlayer() == null) return false;

        if (widgetClosed.getGroupId() == 645) {
            shopWidget = null;
        }

        return false;
    }

    boolean updateFromWidgets() {
        AtomicBoolean updated = new AtomicBoolean(false);

        if (shopWidget != null) {
            int newPoints = client.getVarpValue(261);
            if (newPoints == points.getQuantity()) return false;

            points.setQuantity(newPoints);
            updated.set(true);
        }

        return updated.get();
    }
}
