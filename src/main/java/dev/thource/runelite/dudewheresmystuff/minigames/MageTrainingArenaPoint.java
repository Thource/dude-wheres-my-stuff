package dev.thource.runelite.dudewheresmystuff.minigames;

import java.io.Serializable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.widgets.Widget;

@RequiredArgsConstructor
@Getter
public class MageTrainingArenaPoint implements Serializable {

  final int widgetId;
  final int varpId;
  transient int lastWidgetValue = 0;
  transient int lastVarpValue = 0;
  transient Widget widget = null;

  void reset() {
    lastWidgetValue = 0;
    lastVarpValue = 0;
    widget = null;
  }
}
