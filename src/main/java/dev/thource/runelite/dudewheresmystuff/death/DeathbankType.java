package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.Region;
import dev.thource.runelite.dudewheresmystuff.StorageType;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.vars.AccountType;

/**
 * DeathbankType is used to identify Deathbanks.
 */
@RequiredArgsConstructor
@Getter
public enum DeathbankType {
  UNKNOWN("Unknown Deathbank", -100, -100, null, null),
  HYDRA("Alchemical Hydra", 13, 14, Region.BOSS_HYDRA, "KaruulmSlayerDungeon"), // confirmed
  GUARDIANS("Grotesque Guardians", 3, 4, Region.BOSS_GROTESQUE_GUARDIANS, null), // confirmed
  SEPULCHRE("Hallowed Sepulchre", 29, 30, Region.MG_HALLOWED_SEPULCHRE, null), // confirmed
  HESPORI("Hespori", 15, 16, Region.BOSS_HESPORI, "FarmingGuild"), // confirmed
  MIMIC("The Mimic", 17, 18, null, null),
  NEX("Nex", 36, 37, Region.BOSS_NEX, null), // confirmed
  NIGHTMARE("The Nightmare", 25, 26, Region.BOSS_NIGHTMARE, "Shura"), // confirmed
  PHOSANI("Phosani's Nightmare", 27, 28, Region.BOSS_NIGHTMARE, "SisterSenga"), // confirmed
  TOB("Theatre of Blood", 11, 12, Region.RAIDS_THEATRE_OF_BLOOD, "TheatreofBlood"), // confirmed
  VOLCANIC_MINE("Volcanic Mine", 1, 2, Region.MG_VOLCANIC_MINE, "FossilIsland"), // confirmed
  VORKATH("Vorkath", 5, 6, Region.BOSS_VORKATH, "Rellekka"), // confirmed
  ZULRAH("Zulrah", 33, 0, Region.BOSS_ZULRAH, "Zul-Andra"), // confirmed
  QUEST_DS2("Dragon Slayer II", 7, 8, Region.REGION_GALVEK_SHIPWRECKS, null),
  QUEST_ATOH("A Taste of Hope", 9, 10, null, null),
  QUEST_SOTF("Sins of the Father", 23, 24, null,null),
  QUEST_SOTE("Song of the Elves", 21, 22, null, "elvenrebel"),
  QUEST_TFE("The Fremennik Exiles", 19, 20, null, null);

  private final String name;
  private final int deathBankLockedState;
  private final int deathBankUnlockedState;
  private final Region region;
  private final String deathWindowLocationText;
}
