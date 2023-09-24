package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.StorageType;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** DeathStorageType is used to identify DeathStorages. */
@RequiredArgsConstructor
@Getter
public enum DeathStorageType implements StorageType {
  DEATH_ITEMS("Death Items", -1, true, "", false),
  DEATHPILE("Deathpile", -1, true, "deathpile", false),
  DEATHBANK("Deathbank", -1, false, "deathbank", true);

  private final String name;
  private final int itemContainerId;
  // Whether the storage can be updated with no action required by the player
  private final boolean automatic;
  private final String configKey;
  private final boolean membersOnly;
  private final List<Integer> accountTypeBlacklist = null;
}
