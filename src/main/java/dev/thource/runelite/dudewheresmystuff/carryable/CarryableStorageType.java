package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.StorageType;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.InventoryID;
import net.runelite.api.vars.AccountType;

/** CarryableStorageType is used to identify CarryableStorages. */
@RequiredArgsConstructor
@Getter
public enum CarryableStorageType implements StorageType {
  INVENTORY("Inventory", InventoryID.INVENTORY.getId(), true, "inventory", false),
  EQUIPMENT("Equipment", InventoryID.EQUIPMENT.getId(), true, "equipment", false),
  LOOTING_BAG("Looting Bag", 516, false, "lootingbag", true),
  SEED_BOX("Seed Box", 573, false, "seedbox", true),
  RUNE_POUCH("Rune Pouch", -1, true, "runepouch", true),
  BOTTOMLESS_BUCKET("Bottomless Compost Bucket", -1, false, "bottomlessbucket", true),
  PLANK_SACK("Plank Sack", -1, false, "planksack", true),
  BOLT_POUCH("Bolt Pouch", -1, true, "boltpouch", true),
  GNOMISH_FIRELIGHTER("Gnomish Firelighter", -1, false, "gnomishfirelighter", true);

  private final String name;
  private final int itemContainerId;
  // Whether the storage can be updated with no action required by the player
  private final boolean automatic;
  private final String configKey;
  private final boolean membersOnly;
  private final List<AccountType> accountTypeBlacklist = null;
}
