package dev.thource.runelite.dudewheresmystuff.stash;

import dev.thource.runelite.dudewheresmystuff.StorageType;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.vars.AccountType;

/** StashStorageType is used to identify StashStorages. */
@RequiredArgsConstructor
@Getter
public enum StashStorageType implements StorageType {
  STASH("Stash", -1, false, "stash", true);

  private final String name;
  private final int itemContainerId;
  // Whether the storage can be updated with no action required by the player
  private final boolean automatic;
  private final String configKey;
  private final boolean membersOnly;
  private final List<AccountType> accountTypeBlacklist = null;
}
