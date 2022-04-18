package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemID;

@RequiredArgsConstructor
@Getter
public enum CarryableStorageType implements StorageType {
    INVENTORY("Inventory", InventoryID.INVENTORY.getId(), true, "inventory"),
    EQUIPMENT("Equipment", InventoryID.EQUIPMENT.getId(), true, "equipment"),
    LOOTING_BAG("Looting Bag", 516, false, "lootingbag"),
    SEED_BOX("Seed Box", 573, false, "seedbox"),
    RUNE_POUCH("Rune Pouch", -1, true, "runepouch");

    private final String name;
    private final int itemContainerId;
    // Whether the storage can be updated with no action required by the player
    private final boolean automatic;
    private final String configKey;
}
