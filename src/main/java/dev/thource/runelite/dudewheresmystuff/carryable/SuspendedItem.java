package dev.thource.runelite.dudewheresmystuff.carryable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
class SuspendedItem {
  private final int inventorySlot;
  private final int id;
  private final int quantity;
  @Setter private int ticksLeft = 3;
}
