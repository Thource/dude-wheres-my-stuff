package dev.thource.runelite.dudewheresmystuff;

import java.awt.Graphics2D;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.Keybind;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

public class ItemCountOverlay extends WidgetItemOverlay {

  private final Client client;
  private final DudeWheresMyStuffPlugin plugin;
  private final DudeWheresMyStuffConfig dudeWheresMyStuffConfig;
  private final TooltipManager tooltipManager;
  @Setter private boolean keybindPressed;

  @Inject
  ItemCountOverlay(Client client, DudeWheresMyStuffPlugin dudeWheresMyStuffPlugin,
      DudeWheresMyStuffConfig dudeWheresMyStuffConfig, TooltipManager tooltipManager) {
    this.client = client;
    this.plugin = dudeWheresMyStuffPlugin;
    this.dudeWheresMyStuffConfig = dudeWheresMyStuffConfig;
    this.tooltipManager = tooltipManager;
    showOnInventory();
    showOnBank();
  }

  @Override
  public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem) {
    if (dudeWheresMyStuffConfig.storedItemCountTooltip() == StoredItemCountTooltipMode.OFF) {
      return;
    }

    if (dudeWheresMyStuffConfig.storedItemCountTooltipKeybind().getKeyCode()
        != Keybind.NOT_SET.getKeyCode()
        || dudeWheresMyStuffConfig.storedItemCountTooltipKeybind().getModifiers()
        != Keybind.NOT_SET.getModifiers()
        && !keybindPressed) {
      return;
    }

    final Point mousePos = client.getMouseCanvasPosition();
    if (!widgetItem.getCanvasBounds().contains(mousePos.getX(), mousePos.getY())) {
      return;
    }

    if (dudeWheresMyStuffConfig.storedItemCountTooltip() == StoredItemCountTooltipMode.SIMPLE) {
      long count = plugin.getWithdrawableItemCount(widgetItem.getId());
      tooltipManager.add(new Tooltip("Stored: " + String.format("%,d", count)));
      return;
    }

    Map<Storage<?>, Long> detailedItemCountMap = plugin.getDetailedWithdrawableItemCount(
        widgetItem.getId());
    long total = detailedItemCountMap.values().stream().mapToLong(count -> count).sum();

    String detailedText = detailedItemCountMap.entrySet()
        .stream().sorted(Comparator.comparingLong(Entry<Storage<?>, Long>::getValue).reversed())
        .map(entry -> entry.getKey().getName() + ": " + String.format("%,d", entry.getValue()))
        .collect(Collectors.joining("</br>"));

    if (detailedItemCountMap.values().size() > 1) {
      detailedText += "</br></br>Total: " + String.format("%,d", total);
    }

    tooltipManager.add(new Tooltip(detailedText));
  }
}
