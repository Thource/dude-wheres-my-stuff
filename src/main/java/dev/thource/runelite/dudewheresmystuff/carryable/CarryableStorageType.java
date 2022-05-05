package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.StorageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.InventoryID;

/** CarryableStorageType is used to identify CarryableStorages. */
@RequiredArgsConstructor
@Getter
public enum CarryableStorageType implements StorageType {
  INVENTORY("Inventory", InventoryID.INVENTORY.getId(), true, "inventory", false),
  EQUIPMENT("Equipment", InventoryID.EQUIPMENT.getId(), true, "equipment", false),
  LOOTING_BAG("Looting Bag", 516, false, "lootingbag", true),
  SEED_BOX("Seed Box", 573, false, "seedbox", true),
  RUNE_POUCH("Rune Pouch", -1, true, "runepouch", true);

  private final String name;
  private final int itemContainerId;
  // Whether the storage can be updated with no action required by the player
  private final boolean automatic;
  private final String configKey;
  private final boolean membersOnly;
}
