package dev.thource.runelite.dudewheresmystuff.sailing;

import dev.thource.runelite.dudewheresmystuff.StorageType;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.InventoryID;

/** SailingStorageType is used to identify SailingStorages. */
@RequiredArgsConstructor
@Getter
public enum SailingStorageType implements StorageType {
  BOAT_1("Boat 1", InventoryID.SAILING_BOAT_1_CARGOHOLD, "boat1"),
  BOAT_2("Boat 2", InventoryID.SAILING_BOAT_2_CARGOHOLD, "boat2"),
  BOAT_3("Boat 3", InventoryID.SAILING_BOAT_3_CARGOHOLD, "boat3"),
  BOAT_4("Boat 4", InventoryID.SAILING_BOAT_4_CARGOHOLD, "boat4"),
  BOAT_5("Boat 5", InventoryID.SAILING_BOAT_5_CARGOHOLD, "boat5"),
  LOST_BOAT("Lost boat", -1, "lostBoat");

  private final String name;
  private final int itemContainerId;
  // Whether the storage can be updated with no action required by the player
  private final boolean automatic = false;
  private final String configKey;
  private final boolean membersOnly = true;
  private final List<Integer> accountTypeBlacklist = null;
}
