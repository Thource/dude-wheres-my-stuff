package dev.thource.runelite.dudewheresmystuff.minigames;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.widgets.Widget;

@RequiredArgsConstructor
@Getter
public class MageTrainingArenaPoint {

  final int widgetId;
  final int varpId;
  int lastWidgetValue = 0;
  int lastVarpValue = 0;
  Widget widget = null;

  void reset() {
    lastWidgetValue = 0;
    lastVarpValue = 0;
    widget = null;
  }
}
