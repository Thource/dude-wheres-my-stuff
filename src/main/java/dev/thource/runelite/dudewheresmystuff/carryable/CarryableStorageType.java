package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.StorageType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemID;

/** CarryableStorageType is used to identify CarryableStorages. */
@RequiredArgsConstructor
@Getter
public enum CarryableStorageType implements StorageType {
  INVENTORY("Inventory", InventoryID.INVENTORY.getId(), true, "inventory", false, new ArrayList<>(),
      -1),
  EQUIPMENT("Equipment", InventoryID.EQUIPMENT.getId(), true, "equipment", false, new ArrayList<>(),
      -1),
  LOOTING_BAG("Looting Bag", 516, false, "lootingbag", true,
      Arrays.asList(ItemID.LOOTING_BAG, ItemID.LOOTING_BAG_22586), -1),
  SEED_BOX("Seed Box", 573, false, "seedbox", true,
      Arrays.asList(ItemID.SEED_BOX, ItemID.OPEN_SEED_BOX), 15314),
  RUNE_POUCH("Rune Pouch", -1, true, "runepouch", true,
      Arrays.asList(ItemID.RUNE_POUCH, ItemID.RUNE_POUCH_L, ItemID.DIVINE_RUNE_POUCH,
          ItemID.DIVINE_RUNE_POUCH_L), 15311),
  BOTTOMLESS_BUCKET("Bottomless Compost Bucket", -1, false, "bottomlessbucket", true,
      new ArrayList<>(), -1),
  PLANK_SACK("Plank Sack", -1, false, "planksack", true, new ArrayList<>(), -1),
  BOLT_POUCH("Bolt Pouch", -1, true, "boltpouch", true,
      Collections.singletonList(ItemID.BOLT_POUCH), 15313),
  GNOMISH_FIRELIGHTER("Gnomish Firelighter", -1, false, "gnomishfirelighter", true,
      Collections.singletonList(ItemID.GNOMISH_FIRELIGHTER_20278), -1),
  MASTER_SCROLL_BOOK("Master Scroll Book", -1, true, "masterscrollbook", true, new ArrayList<>(),
      -1);

  private final String name;
  private final int itemContainerId;
  // Whether the storage can be updated with no action required by the player
  private final boolean automatic;
  private final String configKey;
  private final boolean membersOnly;
  private final List<Integer> accountTypeBlacklist = null;
  // ids of container items (the id of the rune pouch item, for example)
  private final List<Integer> containerIds;
  private final int emptyOnDeathVarbit;
}
