package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum WorldStorageType implements StorageType {
  LEPRECHAUN("Tool Leprechaun", -1, true, "leprechaun", true);

  private final String name;
  private final int itemContainerId;
  // Whether the storage can be updated with no action required by the player
  private final boolean automatic;
  private final String configKey;
  private final boolean membersOnly;
}
