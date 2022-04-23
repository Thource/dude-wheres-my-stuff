package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DeathStorageType implements StorageType {
    DEATHPILE("Deathpile", -1, true, "deathpiles", false),
    HYDRA("Alchemical Hydra", -1, false, "hydra", true),
    GUARDIANS("Grotesque Guardians", -1, false, "guardians", true),
    SEPULCHRE("Halloewd Sepulchre", -1, false, "sepulchre", true),
    HESPORI("Hespori", -1, false, "hespori", true),
    MIMIC("The Mimic", -1, false, "mimic", true),
    NEX("Nex", -1, false, "nex", true),
    NIGHTMARE("The Nightmare", -1, false, "nightmare", true),
    PHOSANI("Phosani's Nightmare", -1, false, "phosani", true),
    TOB("Theatre of Blood", -1, false, "tob", true),
    VOLCANIC_MINE("Volcanic Mine", -1, false, "volcanicmine", true),
    VORKATH("Vorkath", -1, false, "vorkath", true),
    ZULRAH("Zulrah", -1, false, "zulrah", true),
    QUEST_DS2("Dragon Slayer II", -1, false, "questds2", true),
    QUEST_ATOH("A Taste of Hope", -1, false, "questatoh", true),
    QUEST_SOTF("Sins of the Father", -1, false, "questsotf", true),
    QUEST_SOTE("Song of the Elves", -1, false, "questsote", true),
    QUEST_TFE("The Fremennik Exiles", -1, false, "questtfe", true),
    ;

    private final String name;
    private final int itemContainerId;
    // Whether the storage can be updated with no action required by the player
    private final boolean automatic;
    private final String configKey;
    private final boolean membersOnly;
}
