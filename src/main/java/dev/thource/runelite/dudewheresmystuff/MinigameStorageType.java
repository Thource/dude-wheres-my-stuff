package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MinigameStorageType implements StorageType {
    MAGE_TRAINING_ARENA("Mage Training Arena", -1, false),
    TITHE_FARM("Tithe Farm", -1, true),
    LAST_MAN_STANDING("Last Man Standing", -1, false),
    NIGHTMARE_ZONE("Nightmare Zone", -1, true),
    PEST_CONTROL("Pest Control", -1, true),
    BARBARIAN_ASSAULT("Barbarian Assault", -1, true),
    GUARDIANS_OF_THE_RIFT("Guardians of the Rift", -1, true),
    TEMPOROSS("Tempoross", -1, true),
    SLAYER("Slayer", -1, true),
    SOUL_WARS("Soul Wars", -1, true),
    MAHOGANY_HOMES("Mahogany Homes", -1, true);

    private final String name;
    private final int itemContainerId;
    // Whether the storage can be updated with no action required by the player
    private final boolean automatic;
}
