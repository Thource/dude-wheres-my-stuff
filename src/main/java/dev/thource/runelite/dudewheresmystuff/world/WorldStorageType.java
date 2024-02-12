package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.StorageType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.InventoryID;

/** WorldStorageType is used to identify WorldStorages. */
@RequiredArgsConstructor
@Getter
public enum WorldStorageType implements StorageType {
  LEPRECHAUN("Tool Leprechaun", -1, true, "leprechaun", true, null),
  BANK(
      "Bank",
      InventoryID.BANK.getId(),
      false,
      "bank",
      false,
      Collections.singletonList(2)), // uim
  GROUP_STORAGE(
      "Group Storage",
      InventoryID.GROUP_STORAGE.getId(),
      false,
      "groupstorage",
      false,
      Arrays.asList(
          0, // normal
          1, // iron
          2, // uim
          3)), // hcim
  BLAST_FURNACE("Blast Furnace", -1, true, "blastfurnace", true, null),
  LOG_STORAGE("Log Storage", -1, false, "logstorage", true, null),
  FOSSIL_STORAGE("Fossil Storage", -1, true, "fossilstorage", true, null),
  VYRE_WELL("Vyre Well", -1, true, "vyrewell", true, null),
  SEED_VAULT(
      "Seed Vault",
      InventoryID.SEED_VAULT.getId(),
      false,
      "seedvault",
      true,
      Collections.singletonList(2)), // uim
  ANNETTE("Annette", -1, true, "annette", true, null),
  ELNOCK_INQUISITOR("Elnock Inquisitor", -1, true, "elnock", true, null),
  PICKAXE_STATUE("Pickaxe Statue", -1, true, "pickaxestatue", true, null),
  NULODION("Nulodion", -1, false, "nulodion", true, null),
  FORESTRY_SHOP("Forestry Shop", -1, false, "forestryshop", true,
      Arrays.asList(0, 1, 3, 4, 5, 6)); // uim only

  private final String name;
  private final int itemContainerId;
  // Whether the storage can be updated with no action required by the player
  private final boolean automatic;
  private final String configKey;
  private final boolean membersOnly;
  private final List<Integer> accountTypeBlacklist;
}
