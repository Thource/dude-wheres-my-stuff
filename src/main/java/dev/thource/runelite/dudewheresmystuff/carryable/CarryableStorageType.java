package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.StorageType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

/** CarryableStorageType is used to identify CarryableStorages. */
@RequiredArgsConstructor
@Getter
public enum CarryableStorageType implements StorageType {
  INVENTORY("Inventory", InventoryID.INV, true, "inventory", false, new ArrayList<>(), -1),
  EQUIPMENT("Equipment", InventoryID.WORN, true, "equipment", false, new ArrayList<>(), -1),
  LOOTING_BAG(
      "Looting Bag",
      InventoryID.LOOTING_BAG,
      false,
      "lootingbag",
      true,
      Arrays.asList(ItemID.LOOTING_BAG, ItemID.LOOTING_BAG_OPEN),
      -1),
  SEED_BOX(
      "Seed Box",
      InventoryID.SEED_BOX,
      false,
      "seedbox",
      true,
      Arrays.asList(ItemID.SEED_BOX, ItemID.SEED_BOX_OPEN),
      VarbitID.EMPTYONDEATH_SEEDBOX),
  RUNE_POUCH(
      "Rune Pouch",
      -1,
      true,
      "runepouch",
      true,
      Arrays.asList(
          ItemID.BH_RUNE_POUCH,
          ItemID.BH_RUNE_POUCH_TROUVER,
          ItemID.DIVINE_RUNE_POUCH,
          ItemID.DIVINE_RUNE_POUCH_TROUVER),
      VarbitID.EMPTYONDEATH_RUNEPOUCH),
  BOTTOMLESS_BUCKET(
      "Bottomless Compost Bucket", -1, false, "bottomlessbucket", true, new ArrayList<>(), -1),
  PLANK_SACK("Plank Sack", -1, false, "planksack", true, new ArrayList<>(), -1),
  BOLT_POUCH(
      "Bolt Pouch",
      -1,
      true,
      "boltpouch",
      true,
      Collections.singletonList(ItemID.XBOWS_BOLT_POUCH),
      VarbitID.EMPTYONDEATH_BOLTPOUCH),
  GNOMISH_FIRELIGHTER(
      "Gnomish Firelighter",
      -1,
      false,
      "gnomishfirelighter",
      true,
      Collections.singletonList(ItemID.GNOMISH_FIRELIGHTER_CHARGED),
      -1),
  MASTER_SCROLL_BOOK(
      "Master Scroll Book", -1, true, "masterscrollbook", true, new ArrayList<>(), -1),
  HUNTSMANS_KIT(
      "Huntsman's Kit",
      InventoryID.HUNTSMANS_KIT,
      false,
      "huntsmanskit",
      true,
      Collections.singletonList(ItemID.HUNTSMANS_KIT),
      -1),
  FORESTRY_KIT(
      "Forestry Kit",
      InventoryID.FORESTRY_KIT,
      false,
      "forestrykit",
      true,
      Arrays.asList(
          ItemID.FORESTRY_KIT, ItemID.FORESTRY_BASKET_CLOSED, ItemID.FORESTRY_BASKET_OPEN),
      -1),
  TACKLE_BOX(
      "Tackle Box",
      InventoryID.TACKLE_BOX,
      false,
      "tackleBox",
      true,
      Collections.singletonList(ItemID.TACKLE_BOX),
      VarbitID.EMPTYONDEATH_TACKLEBOX),
  HERB_SACK(
      "Herb Sack",
      -1,
      false,
      "herbSack",
      true,
      List.of(ItemID.SLAYER_HERB_SACK, ItemID.SLAYER_HERB_SACK_OPEN),
      VarbitID.EMPTYONDEATH_HERBSACK),
  CHUGGING_BARREL(
      "Chugging Barrel",
      InventoryID.PREPOT_DEVICE_INV,
      false,
      "chuggingBarrel",
      true,
      Collections.singletonList(ItemID.MM_PREPOT_DEVICE),
      -1),
  GEM_BAG(
      "Gem Bag",
      -1,
      false,
      "gemBag",
      true,
      List.of(ItemID.GEM_BAG, ItemID.GEM_BAG_OPEN),
      -1)
  ;

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
