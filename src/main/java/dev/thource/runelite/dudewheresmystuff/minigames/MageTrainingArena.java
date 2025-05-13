package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;
import org.apache.commons.lang3.math.NumberUtils;

/** MageTrainingArena is responsible for tracking the player's Mage Training Arena points. */
@Getter
public class MageTrainingArena extends MinigamesStorage {

  private final ItemStack telekineticPoints =
      new ItemStack(ItemID.LAWRUNE, "Telekinetic Points", 0, 0, 0, true);
  private final ItemStack graveyardPoints =
      new ItemStack(ItemID.PEACH, "Graveyard Points", 0, 0, 0, true);
  private final ItemStack enchantmentPoints =
      new ItemStack(ItemID.MAGICTRAINING_ENCHAN_CYLINDER, "Enchantment Points", 0, 0, 0, true);
  private final ItemStack alchemistPoints =
      new ItemStack(ItemID.FAKE_COINS, "Alchemist Points", 0, 0, 0, true);

  private final Map<ItemStack, MageTrainingArenaPoint> pointData = new HashMap<>();

  private Widget shopWidget = null;
  private boolean lobbyWidgetOpen;

  MageTrainingArena(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.MAGE_TRAINING_ARENA, plugin);

    items.add(telekineticPoints);
    items.add(graveyardPoints);
    items.add(enchantmentPoints);
    items.add(alchemistPoints);

    pointData.put(telekineticPoints, new MageTrainingArenaPoint(198, 261, 10));
    pointData.put(alchemistPoints, new MageTrainingArenaPoint(194, 262, 11));
    pointData.put(enchantmentPoints, new MageTrainingArenaPoint(195, 263, 12));
    pointData.put(graveyardPoints, new MageTrainingArenaPoint(196, 264, 13));
  }

  @Override
  public boolean onGameTick() {
    return updateFromWidgets();
  }

  @Override
  public boolean onWidgetLoaded(WidgetLoaded widgetLoaded) {
    if (widgetLoaded.getGroupId() == 197) {
      shopWidget = plugin.getClient().getWidget(197, 0);
    } else if (widgetLoaded.getGroupId() == 553) {
      lobbyWidgetOpen = true;
    } else {
      pointData.forEach(
          (itemStack, pointDatum) -> {
            if (widgetLoaded.getGroupId() != pointDatum.getWidgetId()) {
              return;
            }

            pointDatum.widget = plugin.getClient().getWidget(pointDatum.getWidgetId(), 6);
          });
    }

    return updateFromWidgets();
  }

  @Override
  public void onWidgetClosed(WidgetClosed widgetClosed) {
    if (widgetClosed.getGroupId() == 197) {
      shopWidget = null;
    } else if (widgetClosed.getGroupId() == 553) {
      lobbyWidgetOpen = false;
    } else {
      pointData.forEach(
          (itemStack, pointDatum) -> {
            if (widgetClosed.getGroupId() != pointDatum.getWidgetId()) {
              return;
            }

            pointDatum.widget = null;
          });
    }
  }

  boolean updateFromWidgets() {
    if (shopWidget != null) {
      updateLastUpdated();
      pointData.forEach(
          (itemStack, pointDatum) -> {
            int newPoints = plugin.getClient().getVarpValue(pointDatum.getVarpId());
            itemStack.setQuantity(newPoints);
          });

      return true;
    }

    if (lobbyWidgetOpen) {
      lastUpdated = System.currentTimeMillis();
      pointData.forEach(
          (itemStack, pointDatum) -> {
            Widget widget = plugin.getClient().getWidget(553, pointDatum.getLobbyWidgetId());
            if (widget == null) {
              return;
            }

            int newPoints = NumberUtils.toInt(widget.getText().replace(",", ""), 0);
            itemStack.setQuantity(newPoints);
          });

      return true;
    }

    for (Entry<ItemStack, MageTrainingArenaPoint> entry : pointData.entrySet()) {
      ItemStack itemStack = entry.getKey();
      MageTrainingArenaPoint pointDatum = entry.getValue();
      if (pointDatum.getWidget() == null) {
        continue;
      }

      int newPoints = NumberUtils.toInt(pointDatum.getWidget().getText().replace(",", ""), 0);
      itemStack.setQuantity(newPoints);
      updateLastUpdated();
      return true;
    }

    return false;
  }

  @Override
  public void reset() {
    super.reset();

    pointData.forEach((itemStack, mageTrainingArenaPoint) -> mageTrainingArenaPoint.reset());
  }
}
