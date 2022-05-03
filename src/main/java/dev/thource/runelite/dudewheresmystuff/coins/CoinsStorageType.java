package dev.thource.runelite.dudewheresmystuff.coins;

import dev.thource.runelite.dudewheresmystuff.StorageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.InventoryID;

@RequiredArgsConstructor
@Getter
public enum CoinsStorageType implements StorageType {
  NMZ("Nightmare Zone", 3948, -1, true, "nightmarezone", true, 1000),
  LMS("Last Man Standing", 5305, -1, true, "lastmanstanding", false, 1000),
  SERVANT_MONEYBAG("Servant's Moneybag", -1, -1, false, "servantsmoneybag", true),
  BF("Blast Furnace", 5357, -1, true, "blastfurnace", true),
  INVENTORY("Inventory", -1, InventoryID.INVENTORY.getId(), true, "inventory", false),
  LOOTING_BAG("Looting Bag", -1, 516, false, "lootingbag", true),
  GRAND_EXCHANGE("Grand Exchange", -1, -1, false, "grandexchange", false),
  SHILO_FURNACE("Shilo Furnace", -1, -1, false, "shilofurnace", true);

  private final String name;
  private final int varbitId;
  private final int itemContainerId;
  // Whether the storage can be updated with no action required by the player
  private final boolean automatic;
  private final String configKey;
  private final boolean membersOnly;
  private final int multiplier;

  CoinsStorageType(String name, int varbitId, int itemContainerId, boolean automatic,
      String configKey, boolean membersOnly) {
    this.name = name;
    this.varbitId = varbitId;
    this.itemContainerId = itemContainerId;
    this.automatic = automatic;
    this.configKey = configKey;
    this.membersOnly = membersOnly;
    this.multiplier = 1;
  }
}
