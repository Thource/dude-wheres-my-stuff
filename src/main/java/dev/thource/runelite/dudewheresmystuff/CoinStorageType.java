package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.InventoryID;
import net.runelite.api.Varbits;

@RequiredArgsConstructor
@Getter
public enum CoinStorageType implements StorageType {
    NMZ("Nightmare Zone", 3948, -1, true, "nightmarezone", 1000),
    LMS("Last Man Standing", 5305, -1, true, "lastmanstanding", 1000),
    //    SERVANT_MONEYBAG("Servant's Moneybag", 5305, -1, 100),
    BF("Blast Furnace", Varbits.BLAST_FURNACE_COFFER.getId(), -1, true, "blastfurnace"),
    INVENTORY("Inventory", -1, InventoryID.INVENTORY.getId(), true, "inventory"),
    LOOTING_BAG("Looting Bag", -1, 516, false, "lootingbag");

    CoinStorageType(String name, int varbitId, int itemContainerId, boolean automatic, String configKey) {
        this.name = name;
        this.varbitId = varbitId;
        this.itemContainerId = itemContainerId;
        this.automatic = automatic;
        this.configKey = configKey;
        this.multiplier = 1;
    }

    private final String name;
    private final int varbitId;
    private final int itemContainerId;
    // Whether the storage can be updated with no action required by the player
    private final boolean automatic;
    private final String configKey;
    private final int multiplier;
}
