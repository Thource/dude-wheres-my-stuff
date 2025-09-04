package dev.thource.runelite.dudewheresmystuff.coins;

import dev.thource.runelite.dudewheresmystuff.StorageType;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarbitID;

/** CoinsStorageType is used to identify CoinsStorages. */
@RequiredArgsConstructor
@Getter
public enum CoinsStorageType implements StorageType {
  BANK("Bank", -1, InventoryID.BANK, false, "bank", false),
  NMZ("Nightmare Zone", VarbitID.NZONE_CASH, -1, true, "nightmarezone", true, 1000),
  LMS("Last Man Standing", VarbitID.BR_COFFER, -1, true, "lastmanstanding", false, 1000),
  SERVANT_MONEYBAG("Servant's Moneybag", -1, -1, false, "servantsmoneybag", true),
  BF("Blast Furnace", VarbitID.BLAST_FURNACE_COFFER, -1, true, "blastfurnace", true),
  INVENTORY("Inventory", -1, InventoryID.INV, true, "inventory", false),
  LOOTING_BAG("Looting Bag", -1, InventoryID.LOOTING_BAG, false, "lootingbag", true),
  GRAND_EXCHANGE("Grand Exchange", -1, -1, false, "grandexchange", false),
  SHILO_FURNACE("Shilo Furnace", -1, -1, false, "shilofurnace", true),
  BOUNTY_HUNTER("Bounty Hunter", -1, -1, false, "bountyhunter", true),
  MANAGING_MISCELLANIA("Managing Miscellania", VarbitID.MISC_COFFERS, -1, true, "managingMiscellania", true),
  SCAR_ESSENCE_MINE("Scar Essence Mine", -1, -1, true, "scarEssenceMine", true);

  private final String name;
  private final int varbitId;
  private final int itemContainerId;
  // Whether the storage can be updated with no action required by the player
  private final boolean automatic;
  private final String configKey;
  private final boolean membersOnly;
  private final int multiplier;
  private final List<Integer> accountTypeBlacklist = null;

  CoinsStorageType(
      String name,
      int varbitId,
      int itemContainerId,
      boolean automatic,
      String configKey,
      boolean membersOnly) {
    this.name = name;
    this.varbitId = varbitId;
    this.itemContainerId = itemContainerId;
    this.automatic = automatic;
    this.configKey = configKey;
    this.membersOnly = membersOnly;
    this.multiplier = 1;
  }
}
