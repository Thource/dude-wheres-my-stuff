package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.StorageType;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.InventoryID;

/** DeathStorageType is used to identify DeathStorages. */
@RequiredArgsConstructor
@Getter
public enum DeathStorageType implements StorageType {
  DEATH_ITEMS("Death Items", -1, true, "", false, null),
  DEATHPILE("Deathpile", -1, true, "deathpile", false, null),
  GRAVE("Grave", -1, true, "grave", false, null),
  DEATHBANK("Deathbank", -1, false, "deathbank", true, null),
  DEATHS_OFFICE("Death's Office", InventoryID.DEATH_PERMANENT, false, "deathsoffice", false, Collections.singletonList(2));

  private final String name;
  private final int itemContainerId;
  // Whether the storage can be updated with no action required by the player
  private final boolean automatic;
  private final String configKey;
  private final boolean membersOnly;
  private final List<Integer> accountTypeBlacklist;
}
