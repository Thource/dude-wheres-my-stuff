package dev.thource.runelite.dudewheresmystuff.death;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.runelite.api.coords.WorldPoint;

@RequiredArgsConstructor
@Getter
public class SuspendedGroundItem {
  private final int id;
  private final WorldPoint worldPoint;
  @Setter private int ticksLeft;
}
