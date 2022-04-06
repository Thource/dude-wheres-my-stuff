package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.InventoryID;
import net.runelite.api.Varbits;

@RequiredArgsConstructor
@Getter
public enum CoinStorageType {
    NMZ("Nightmare Zone", 3948, -1, true, 1000),
    LMS("Last Man Standing", 5305, -1, true, 1000),
//    SERVANT_MONEYBAG("Servant's Moneybag", 5305, -1, 100),
    BF("Blast Furnace", Varbits.BLAST_FURNACE_COFFER.getId(), -1, true),
    INVENTORY("Inventory", -1, InventoryID.INVENTORY.getId(), true),
    LOOTING_BAG("Looting Bag", -1, 516, false);

    CoinStorageType(String name, int varbitId, int itemContainerId, boolean automatic) {
        this.name = name;
        this.varbitId = varbitId;
        this.itemContainerId = itemContainerId;
        this.automatic = automatic;
        this.multiplier = 1;
    }

    private final String name;
    private final int varbitId;
    private final int itemContainerId;
    // Whether the storage can be updated with no action required by the player
    private final boolean automatic;
    private final int multiplier;
}
