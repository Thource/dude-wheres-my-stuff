package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.Region;
import dev.thource.runelite.dudewheresmystuff.StorageType;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.vars.AccountType;

/** DeathStorageType is used to identify DeathStorages. */
@RequiredArgsConstructor
@Getter
public enum DeathStorageType implements StorageType {
  DEATH_ITEMS("Death Items", -1, true, "", false, -100, -100, null, null),
  DEATHPILE("Deathpile", -1, true, "deathpiles", false, -100, -100, null, null),
  UNKNOWN_DEATHBANK("Unknown Deathbank", -1, false, "unknown", true, -100, -100, null, null),
  HYDRA(
      "Alchemical Hydra",
      -1,
      false,
      "hydra",
      true,
      13,
      14,
      Region.BOSS_HYDRA,
      "KaruulmSlayerDungeon"), // confirmed
  GUARDIANS(
      "Grotesque Guardians",
      -1,
      false,
      "guardians",
      true,
      3,
      4,
      Region.BOSS_GROTESQUE_GUARDIANS,
      null), // confirmed
  SEPULCHRE(
      "Hallowed Sepulchre",
      -1,
      false,
      "sepulchre",
      true,
      29,
      30,
      Region.MG_HALLOWED_SEPULCHRE,
      null), // confirmed
  HESPORI(
      "Hespori",
      -1,
      false,
      "hespori",
      true,
      15,
      16,
      Region.BOSS_HESPORI,
      "FarmingGuild"), // confirmed
  MIMIC("The Mimic", -1, false, "mimic", true, 17, 18, null, null),
  NEX("Nex", -1, false, "nex", true, 36, 37, Region.BOSS_NEX, null), // confirmed
  NIGHTMARE(
      "The Nightmare",
      -1,
      false,
      "nightmare",
      true,
      25,
      26,
      Region.BOSS_NIGHTMARE,
      "Shura"), // confirmed
  PHOSANI(
      "Phosani's Nightmare",
      -1,
      false,
      "phosani",
      true,
      27,
      28,
      Region.BOSS_NIGHTMARE,
      "SisterSenga"), // confirmed
  TOB(
      "Theatre of Blood",
      -1,
      false,
      "tob",
      true,
      11,
      12,
      Region.RAIDS_THEATRE_OF_BLOOD,
      "TheatreofBlood"), // confirmed
  VOLCANIC_MINE(
      "Volcanic Mine",
      -1,
      false,
      "volcanicmine",
      true,
      1,
      2,
      Region.MG_VOLCANIC_MINE,
      "FossilIsland"), // confirmed
  VORKATH(
      "Vorkath", -1, false, "vorkath", true, 5, 6, Region.BOSS_VORKATH, "Rellekka"), // confirmed
  ZULRAH("Zulrah", -1, false, "zulrah", true, 33, 0, Region.BOSS_ZULRAH, "Zul-Andra"), // confirmed
  QUEST_DS2(
      "Dragon Slayer II", -1, false, "questds2", true, 7, 8, Region.REGION_GALVEK_SHIPWRECKS, null),
  QUEST_ATOH("A Taste of Hope", -1, false, "questatoh", true, 9, 10, null, null),
  QUEST_SOTF("Sins of the Father", -1, false, "questsotf", true, 23, 24, null, null),
  QUEST_SOTE("Song of the Elves", -1, false, "questsote", true, 19, 20, null, null),
  QUEST_TFE("The Fremennik Exiles", -1, false, "questtfe", true, 21, 22, null, null);

  private final String name;
  private final int itemContainerId;
  // Whether the storage can be updated with no action required by the player
  private final boolean automatic;
  private final String configKey;
  private final boolean membersOnly;
  private final int deathBankLockedState;
  private final int deathBankUnlockedState;
  private final Region region;
  private final String deathWindowLocationText;
  private final List<AccountType> accountTypeBlacklist = null;
}
