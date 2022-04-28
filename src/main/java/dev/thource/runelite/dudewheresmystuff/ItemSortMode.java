package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemSortMode {
  VALUE("Value"),
  UNSORTED("Unsorted");

  private final String name;

  @Override
  public String toString() {
    return name;
  }
}
