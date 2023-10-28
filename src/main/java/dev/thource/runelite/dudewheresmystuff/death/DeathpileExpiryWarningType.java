package dev.thource.runelite.dudewheresmystuff.death;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** DeathpileExpiryWarningType is used to decide how expiring deathpiles will be shown. */
@Getter
@RequiredArgsConstructor
public enum DeathpileExpiryWarningType {
  OFF("Off"),
  TEXT("Screen text"),
  INFOBOX("Infobox flashing"),
  BOTH("BOTH");

  private final String name;

  @Override
  public String toString() {
    return name;
  }
}
