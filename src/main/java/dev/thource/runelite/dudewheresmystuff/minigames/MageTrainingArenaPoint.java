package dev.thource.runelite.dudewheresmystuff.minigames;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.widgets.Widget;

@RequiredArgsConstructor
@Getter
class MageTrainingArenaPoint {

  final int widgetId;
  final int varpId;
  final int lobbyWidgetId;
  Widget widget = null;

  void reset() {
    widget = null;
  }
}
