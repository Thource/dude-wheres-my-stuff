/*
 * Copyright (c) 2022, Thource <https://github.com/Thource>
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package dev.thource.runelite.dudewheresmystuff.data;

import static dev.thource.runelite.dudewheresmystuff.stash.ItemRequirementData.all;
import static dev.thource.runelite.dudewheresmystuff.stash.ItemRequirementData.any;
import static dev.thource.runelite.dudewheresmystuff.stash.ItemRequirementData.item;
import static dev.thource.runelite.dudewheresmystuff.stash.ItemRequirementData.range;

import dev.thource.runelite.dudewheresmystuff.EnumJsonGenerator;
import dev.thource.runelite.dudewheresmystuff.stash.ItemRequirementData;
import dev.thource.runelite.dudewheresmystuff.stash.StashUnit;
import java.util.Arrays;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.plugins.cluescrolls.clues.emote.STASHUnit;

// Latest update: https://oldschool.runescape.wiki/w/STASH?oldid=15102963

/**
 * Source of truth for the data behind {@link StashUnit}, which defines which items live at which
 * locations. See {@link EnumJsonGenerator}; the generated json is {@code
 * data/StashUnitData.json}.
 *
 * <p>{@code item}, {@code range}, {@code any} and {@code all} are static imports of {@link
 * ItemRequirementData}'s factories. They mirror RuneLite's {@code ItemRequirements} but build
 * serialisable data instead of live requirement objects.
 */
// Suppress duplicate string literal warning because this class is copied from runelite
@SuppressWarnings("java:S1192")
public enum StashUnitData implements EnumJsonGenerator.Exportable {
  NORTHEAST_CORNER_OF_THE_KHARAZI_JUNGLE(
      "Kharazi Jungle",
      "North-east corner of the Kharazi Jungle",
      STASHUnit.NORTHEAST_CORNER_OF_THE_KHARAZI_JUNGLE,
      new int[]{ItemID.TRAIL_GUTHIX_SCARF, ItemID.RUNE_HERALDIC_KITESHIELD1},
      any(
          "Any stole",
          item(ItemID.TRAIL_GUTHIX_SCARF),
          item(ItemID.TRAIL_SARADOMIN_SCARF),
          item(ItemID.TRAIL_ZAMORAK_SCARF),
          item(ItemID.TRAIL_ARMADYL_SCARF),
          item(ItemID.TRAIL_BANDOS_SCARF),
          item(ItemID.TRAIL_ANCIENT_SCARF)),
      any(
          "Any heraldic rune shield",
          item(ItemID.RUNE_HERALDIC_KITESHIELD1),
          item(ItemID.RUNE_HERALDIC_KITESHIELD2),
          item(ItemID.RUNE_HERALDIC_KITESHIELD3),
          item(ItemID.RUNE_HERALDIC_KITESHIELD4),
          item(ItemID.RUNE_HERALDIC_KITESHIELD5))),
  BARBARIAN_OUTPOST_OBSTACLE_COURSE(
      "Barbarian Outpost",
      "Barbarian Outpost obstacle course",
      STASHUnit.BARBARIAN_OUTPOST_OBSTACLE_COURSE,
      new int[]{ItemID.STEEL_PLATEBODY, ItemID.MAPLE_SHORTBOW, ItemID.WILDERNESS_CAPE_1},
      item(ItemID.STEEL_PLATEBODY),
      item(ItemID.MAPLE_SHORTBOW),
      any(
          "Any team cape",
          range(ItemID.WILDERNESS_CAPE_1, ItemID.WILDERNESS_CAPE_50),
          item(ItemID.WILDERNESS_CAPE_I),
          item(ItemID.WILDERNESS_CAPE_X),
          item(ItemID.WILDERNESS_CAPE_ZERO)
      )),
  SOUTHEAST_CORNER_OF_THE_MONASTERY(
      "Edgeville Monastery",
      "South-east corner of the Monastery",
      STASHUnit.SOUTHEAST_CORNER_OF_THE_MONASTERY,
      new int[]{ItemID.SARADOMINBOOK_COMPLETE},
      any(
          "Any god book",
          item(ItemID.SARADOMINBOOK_COMPLETE),
          item(ItemID.GUTHIXBOOK_COMPLETE),
          item(ItemID.ZAMORAKBOOK_COMPLETE),
          item(ItemID.ARMADYLBOOK_COMPLETE),
          item(ItemID.BANDOSBOOK_COMPLETE),
          item(ItemID.ZAROSBOOK_COMPLETE),
          item(ItemID.LEAGUE_3_BOOK_SARADOMIN),
          item(ItemID.LEAGUE_3_BOOK_GUTHIX),
          item(ItemID.LEAGUE_3_BOOK_ZAMORAK),
          item(ItemID.LEAGUE_3_BOOK_ARMADYL),
          item(ItemID.LEAGUE_3_BOOK_BANDOS),
          item(ItemID.LEAGUE_3_BOOK_ZAROS))),
  ENTRANCE_OF_THE_CAVE_OF_DAMIS(
      "Shadow dungeon",
      "Entrance of the cave of Damis",
      STASHUnit.ENTRANCE_OF_THE_CAVE_OF_DAMIS,
      new int[]{
          ItemID.TRAIL_GUTHIX_MITRE, ItemID.XBOWS_CROSSBOW_RUNITE, ItemID.DEATH_CLIMBINGBOOTS,
          ItemID.FD_RING_VISIBILITY
      },
      any(
          "Any mitre",
          item(ItemID.TRAIL_GUTHIX_MITRE),
          item(ItemID.TRAIL_SARADOMIN_MITRE),
          item(ItemID.TRAIL_ZAMORAK_MITRE),
          item(ItemID.TRAIL_ANCIENT_MITRE),
          item(ItemID.TRAIL_BANDOS_MITRE),
          item(ItemID.TRAIL_ARMADYL_MITRE)),
      any(
          "Rune crossbow",
          item(ItemID.XBOWS_CROSSBOW_RUNITE),
          item(ItemID.LEAGUE_3_RUNE_XBOW)),
      any(
          "Climbing boots",
          item(ItemID.DEATH_CLIMBINGBOOTS),
          item(ItemID.CLIMBING_BOOTS_G)),
      any(
          "Ring of visibility or ring of shadows",
          item(ItemID.FD_RING_VISIBILITY),
          item(ItemID.RING_OF_SHADOWS),
          item(ItemID.RING_OF_SHADOWS_UNCHARGED))),
  AGILITY_PYRAMID(
      "Agility Pyramid",
      "Agility Pyramid",
      STASHUnit.AGILITY_PYRAMID,
      new int[]{ItemID.MYSTIC_ROBE_TOP, ItemID.RUNE_HERALDIC_KITESHIELD1},
      item(ItemID.MYSTIC_ROBE_TOP),
      any(
          "Any rune heraldic shield",
          item(ItemID.RUNE_HERALDIC_KITESHIELD1),
          item(ItemID.RUNE_HERALDIC_KITESHIELD2),
          item(ItemID.RUNE_HERALDIC_KITESHIELD3),
          item(ItemID.RUNE_HERALDIC_KITESHIELD4),
          item(ItemID.RUNE_HERALDIC_KITESHIELD5))),
  WELL_OF_VOYAGE(
      "Iban's temple",
      "Well of Voyage",
      STASHUnit.WELL_OF_VOYAGE,
      new int[]{ItemID.IBANSTAFF, ItemID.MYSTIC_ROBE_TOP_DARK, ItemID.MYSTIC_ROBE_BOTTOM_DARK},
      any(
          "Any iban's staff",
          item(ItemID.IBANSTAFF),
          item(ItemID.IBANSTAFF_UPGRADED)),
      item(ItemID.MYSTIC_ROBE_TOP_DARK),
      item(ItemID.MYSTIC_ROBE_BOTTOM_DARK)),
  SOUTHEAST_CORNER_OF_THE_FISHING_PLATFORM(
      "Fishing Platform",
      "South-east corner of the Fishing Platform",
      STASHUnit.SOUTHEAST_CORNER_OF_THE_FISHING_PLATFORM,
      new int[]{ItemID.AMULET_OF_GLORY, ItemID.HUNDRED_GAUNTLETS_LEVEL_10, ItemID.DRAGON_MED_HELM},
      any(
          "Any amulet of glory",
          item(ItemID.AMULET_OF_GLORY),
          item(ItemID.AMULET_OF_GLORY_1),
          item(ItemID.AMULET_OF_GLORY_2),
          item(ItemID.AMULET_OF_GLORY_3),
          item(ItemID.AMULET_OF_GLORY_4),
          item(ItemID.AMULET_OF_GLORY_5),
          item(ItemID.AMULET_OF_GLORY_6),
          item(ItemID.AMULET_OF_GLORY_INF)),
      item(ItemID.HUNDRED_GAUNTLETS_LEVEL_10),
      any(
          "Any dragon med helm",
          item(ItemID.DRAGON_MED_HELM),
          item(ItemID.BH_DRAGON_MED_HELM_CORRUPTED))),
  DEATH_ALTAR(
      "Death altar",
      "Death Altar",
      STASHUnit.DEATH_ALTAR,
      new int[]{ItemID.RING_OF_WEALTH, ItemID.TIARA_DEATH, ItemID.CAPE_OF_LEGENDS},
      any(
          "Any ring of wealth",
          item(ItemID.RING_OF_WEALTH),
          item(ItemID.RING_OF_WEALTH_1),
          item(ItemID.RING_OF_WEALTH_2),
          item(ItemID.RING_OF_WEALTH_3),
          item(ItemID.RING_OF_WEALTH_4),
          item(ItemID.RING_OF_WEALTH_5),
          item(ItemID.RING_OF_WEALTH_I),
          item(ItemID.RING_OF_WEALTH_I1),
          item(ItemID.RING_OF_WEALTH_I2),
          item(ItemID.RING_OF_WEALTH_I3),
          item(ItemID.RING_OF_WEALTH_I4),
          item(ItemID.RING_OF_WEALTH_I5)),
      item(ItemID.TIARA_DEATH),
      item(ItemID.CAPE_OF_LEGENDS)),
  OUTSIDE_THE_BAR_BY_THE_FIGHT_ARENA(
      "Fight Arena pub",
      "Outside the bar by the Fight Arena",
      STASHUnit.OUTSIDE_THE_BAR_BY_THE_FIGHT_ARENA,
      new int[]{ItemID.PIRATE_BANDANNA, ItemID.DRAGONSTONE_NECKLACE, ItemID.MAGIC_LONGBOW},
      any(
          "Any pirate bandana",
          item(ItemID.PIRATE_BANDANNA),
          item(ItemID.PIRATE_BANDANA_RED),
          item(ItemID.PIRATE_BANDANA_BLUE),
          item(ItemID.PIRATE_BANDANA_BROWN)),
      item(ItemID.DRAGONSTONE_NECKLACE),
      item(ItemID.MAGIC_LONGBOW)),
  BARROWS_CHEST(
      "Barrows chest",
      "Barrows Chest",
      STASHUnit.BARROWS_CHEST,
      new int[]{
          ItemID.BARROWS_DHAROK_HEAD,
          ItemID.BARROWS_DHAROK_WEAPON,
          ItemID.BARROWS_DHAROK_BODY,
          ItemID.BARROWS_DHAROK_LEGS
      },
      any(
          "Any full barrows set",
          all(
              any(
                  "Ahrim's hood",
                  item(ItemID.BARROWS_AHRIM_HEAD),
                  range(ItemID.BARROWS_AHRIM_HEAD_100,
                      ItemID.BARROWS_AHRIM_HEAD_BROKEN)),
              any(
                  "Ahrim's staff",
                  item(ItemID.BARROWS_AHRIM_WEAPON),
                  range(ItemID.BARROWS_AHRIM_WEAPON_100,
                      ItemID.BARROWS_AHRIM_WEAPON_BROKEN)),
              any(
                  "Ahrim's robetop",
                  item(ItemID.BARROWS_AHRIM_BODY),
                  range(ItemID.BARROWS_AHRIM_BODY_100,
                      ItemID.BARROWS_AHRIM_BODY_BROKEN)),
              any(
                  "Ahrim's robeskirt",
                  item(ItemID.BARROWS_AHRIM_LEGS),
                  range(ItemID.BARROWS_AHRIM_LEGS_100,
                      ItemID.BARROWS_AHRIM_LEGS_BROKEN))),
          all(
              any(
                  "Dharok's helm",
                  item(ItemID.BARROWS_DHAROK_HEAD),
                  range(ItemID.BARROWS_DHAROK_HEAD_100,
                      ItemID.BARROWS_DHAROK_HEAD_BROKEN)),
              any(
                  "Dharok's greataxe",
                  item(ItemID.BARROWS_DHAROK_WEAPON),
                  range(ItemID.BARROWS_DHAROK_WEAPON_100,
                      ItemID.BARROWS_DHAROK_WEAPON_BROKEN)),
              any(
                  "Dharok's platebody",
                  item(ItemID.BARROWS_DHAROK_BODY),
                  range(ItemID.BARROWS_DHAROK_BODY_100,
                      ItemID.BARROWS_DHAROK_BODY_BROKEN)),
              any(
                  "Dharok's platelegs",
                  item(ItemID.BARROWS_DHAROK_LEGS),
                  range(
                      ItemID.BARROWS_DHAROK_LEGS_100, ItemID.BARROWS_DHAROK_LEGS_BROKEN))),
          all(
              any(
                  "Guthan's helm",
                  item(ItemID.BARROWS_GUTHAN_HEAD),
                  range(ItemID.BARROWS_GUTHAN_HEAD_100,
                      ItemID.BARROWS_GUTHAN_HEAD_BROKEN)),
              any(
                  "Guthan's warspear",
                  item(ItemID.BARROWS_GUTHAN_WEAPON),
                  range(ItemID.BARROWS_GUTHAN_WEAPON_100,
                      ItemID.BARROWS_GUTHAN_WEAPON_BROKEN)),
              any(
                  "Guthan's platebody",
                  item(ItemID.BARROWS_GUTHAN_BODY),
                  range(ItemID.BARROWS_GUTHAN_BODY_100,
                      ItemID.BARROWS_GUTHAN_BODY_BROKEN)),
              any(
                  "Guthan's chainskirt",
                  item(ItemID.BARROWS_GUTHAN_LEGS),
                  range(
                      ItemID.BARROWS_GUTHAN_LEGS_100, ItemID.BARROWS_GUTHAN_LEGS_BROKEN))),
          all(
              any(
                  "Karil's coif",
                  item(ItemID.BARROWS_KARIL_HEAD),
                  range(ItemID.BARROWS_KARIL_HEAD_100,
                      ItemID.BARROWS_KARIL_HEAD_BROKEN)),
              any(
                  "Karil's crossbow",
                  item(ItemID.BARROWS_KARIL_WEAPON),
                  range(ItemID.BARROWS_KARIL_WEAPON_100,
                      ItemID.BARROWS_KARIL_WEAPON_BROKEN)),
              any(
                  "Karil's leathertop",
                  item(ItemID.BARROWS_KARIL_BODY),
                  range(ItemID.BARROWS_KARIL_BODY_100,
                      ItemID.BARROWS_KARIL_BODY_BROKEN)),
              any(
                  "Karil's leatherskirt",
                  item(ItemID.BARROWS_KARIL_LEGS),
                  range(
                      ItemID.BARROWS_KARIL_LEGS_100, ItemID.BARROWS_KARIL_LEGS_BROKEN))),
          all(
              any(
                  "Torag's helm",
                  item(ItemID.BARROWS_TORAG_HEAD),
                  range(ItemID.BARROWS_TORAG_HEAD_100,
                      ItemID.BARROWS_TORAG_HEAD_BROKEN)),
              any(
                  "Torag's hammers",
                  item(ItemID.BARROWS_TORAG_WEAPON),
                  range(ItemID.BARROWS_TORAG_WEAPON_100,
                      ItemID.BARROWS_TORAG_WEAPON_BROKEN)),
              any(
                  "Torag's platebody",
                  item(ItemID.BARROWS_TORAG_BODY),
                  range(ItemID.BARROWS_TORAG_BODY_100,
                      ItemID.BARROWS_TORAG_BODY_BROKEN)),
              any(
                  "Torag's platelegs",
                  item(ItemID.BARROWS_TORAG_LEGS),
                  range(ItemID.BARROWS_TORAG_LEGS_100,
                      ItemID.BARROWS_TORAG_LEGS_BROKEN))),
          all(
              any(
                  "Verac's helm",
                  item(ItemID.BARROWS_VERAC_HEAD),
                  range(ItemID.BARROWS_VERAC_HEAD_100,
                      ItemID.BARROWS_VERAC_HEAD_BROKEN)),
              any(
                  "Verac's flail",
                  item(ItemID.BARROWS_VERAC_WEAPON),
                  range(ItemID.BARROWS_VERAC_WEAPON_100,
                      ItemID.BARROWS_VERAC_WEAPON_BROKEN)),
              any(
                  "Verac's brassard",
                  item(ItemID.BARROWS_VERAC_BODY),
                  range(ItemID.BARROWS_VERAC_BODY_100,
                      ItemID.BARROWS_VERAC_BODY_BROKEN)),
              any(
                  "Verac's plateskirt",
                  item(ItemID.BARROWS_VERAC_LEGS),
                  range(
                      ItemID.BARROWS_VERAC_LEGS_100, ItemID.BARROWS_VERAC_LEGS_BROKEN))))),
  IN_THE_MIDDLE_OF_JIGGIG(
      "Jiggig",
      "In the middle of Jiggig",
      STASHUnit.IN_THE_MIDDLE_OF_JIGGIG,
      new int[]{ItemID.TRAIL_HERALDIC_HELM_1_RUNE, ItemID.RUNE_SPEAR, ItemID.RUNE_PLATELEGS},
      range(ItemID.TRAIL_HERALDIC_HELM_1_RUNE, ItemID.TRAIL_HERALDIC_HELM_5_RUNE),
      item(ItemID.RUNE_SPEAR),
      item(ItemID.RUNE_PLATELEGS)),
  BY_THE_BEAR_CAGE_IN_VARROCK_PALACE_GARDENS(
      "Varrock Castle",
      "By the bear cage in Varrock Palace gardens",
      STASHUnit.BY_THE_BEAR_CAGE_IN_VARROCK_PALACE_GARDENS,
      new int[]{ItemID.ZGS},
      any(
          "Zamorak godsword",
          item(ItemID.ZGS),
          item(ItemID.ZGSG))),
  BEHIND_MISS_SCHISM_IN_DRAYNOR_VILLAGE(
      "Draynor Village",
      "Behind Miss Schism in Draynor Village",
      STASHUnit.BEHIND_MISS_SCHISM_IN_DRAYNOR_VILLAGE,
      new int[]{ItemID.ABYSSAL_WHIP, ItemID.CAPE_OF_LEGENDS, ItemID.DAGGANOTH_RANGED_LEGS},
      any(
          "Abyssal whip",
          item(ItemID.ABYSSAL_WHIP),
          item(ItemID.ABYSSAL_WHIP_LAVA),
          item(ItemID.ABYSSAL_WHIP_ICE),
          item(ItemID.LEAGUE_3_WHIP),
          item(ItemID.ABYSSAL_TENTACLE),
          item(ItemID.LEAGUE_3_WHIP_TENTACLE)),
      item(ItemID.CAPE_OF_LEGENDS),
      item(ItemID.DAGGANOTH_RANGED_LEGS)),
  CRYSTALLINE_MAPLE_TREES(
      "North of Prifddinas",
      "North of Prifddinas by several maple trees",
      STASHUnit.CRYSTALLINE_MAPLE_TREES,
      new int[]{ItemID.NATURE_STAFF_UNCHARGED, ItemID.TIARA_NATURE},
      range(ItemID.NATURE_STAFF_UNCHARGED, ItemID.NATURE_STAFF_CHARGED),
      item(ItemID.TIARA_NATURE)),
  DIGSITE(
      "Digsite",
      "Digsite",
      STASHUnit.DIGSITE,
      new int[]{ItemID.GNOME_HAT_GREEN, ItemID.SNAKESKIN_BOOTS, ItemID.IRON_PICKAXE},
      item(ItemID.GNOME_HAT_GREEN),
      item(ItemID.SNAKESKIN_BOOTS),
      item(ItemID.IRON_PICKAXE)),
  SOUTH_OF_THE_SHRINE_IN_TAI_BWO_WANNAI_VILLAGE(
      "Tai Bwo Wannai",
      "South of the shrine in Tai Bwo Wannai Village",
      STASHUnit.SOUTH_OF_THE_SHRINE_IN_TAI_BWO_WANNAI_VILLAGE,
      new int[]{ItemID.DRAGONHIDE_CHAPS, ItemID.RING_OF_DUELING_1, ItemID.MITHRIL_MED_HELM},
      item(ItemID.DRAGONHIDE_CHAPS),
      any(
          "Ring of dueling",
          item(ItemID.RING_OF_DUELING_1),
          item(ItemID.RING_OF_DUELING_2),
          item(ItemID.RING_OF_DUELING_3),
          item(ItemID.RING_OF_DUELING_4),
          item(ItemID.RING_OF_DUELING_5),
          item(ItemID.RING_OF_DUELING_6),
          item(ItemID.RING_OF_DUELING_7),
          item(ItemID.RING_OF_DUELING_8)),
      item(ItemID.MITHRIL_MED_HELM)),
  WEST_OF_THE_SHAYZIEN_COMBAT_RING(
      "Shayzien Combat Ring",
      "North of the Shayzien combat ring",
      STASHUnit.WEST_OF_THE_SHAYZIEN_COMBAT_RING,
      new int[]{ItemID.ADAMANT_PLATELEGS, ItemID.ADAMANT_PLATEBODY, ItemID.ADAMANT_FULL_HELM},
      item(ItemID.ADAMANT_PLATELEGS),
      item(ItemID.ADAMANT_PLATEBODY),
      item(ItemID.ADAMANT_FULL_HELM)),
  TENT_IN_LORD_IORWERTHS_ENCAMPMENT(
      "Lord Iorwerth's camp",
      "Tent in Lord Iorwerth's encampment",
      STASHUnit.TENT_IN_LORD_IORWERTHS_ENCAMPMENT,
      new int[]{ItemID.CRYSTAL_BOW},
      any(
          "Crystal Bow",
          item(ItemID.CRYSTAL_BOW),
          item(ItemID.CRYSTAL_BOW_2500),
          item(ItemID.BOW_OF_FAERDHINEN),
          item(ItemID.BOW_OF_FAERDHINEN_INFINITE),
          item(ItemID.BOW_OF_FAERDHINEN_INFINITE_DUMMY),
          item(ItemID.BOW_OF_FAERDHINEN_INFINITE_ITHELL),
          item(ItemID.BOW_OF_FAERDHINEN_INFINITE_IORWERTH),
          item(ItemID.BOW_OF_FAERDHINEN_INFINITE_TRAHAEARN),
          item(ItemID.BOW_OF_FAERDHINEN_INFINITE_CADARN),
          item(ItemID.BOW_OF_FAERDHINEN_INFINITE_CRWYS),
          item(ItemID.BOW_OF_FAERDHINEN_INFINITE_MEILYR),
          item(ItemID.BOW_OF_FAERDHINEN_INFINITE_AMLODD),
          item(ItemID.BOW_OF_FAERDHINEN_INFINITE_DEADMAN),
          item(ItemID.BOW_OF_FAERDHINEN_INACTIVE))),
  OUTSIDE_THE_LEGENDS_GUILD_GATES(
      "Legend's Guild",
      "Outside the Legends' Guild gates",
      STASHUnit.OUTSIDE_THE_LEGENDS_GUILD_GATES,
      new int[]{ItemID.IRON_PLATELEGS, ItemID.OAK_LONGBOW, ItemID.STRUNG_EMERALD_AMULET},
      item(ItemID.IRON_PLATELEGS),
      item(ItemID.OAK_LONGBOW),
      item(ItemID.STRUNG_EMERALD_AMULET)),
  OUTSIDE_THE_LEGENDS_GUILD_DOOR(
      "Legend's Guild",
      "Outside the Legends' Guild door",
      STASHUnit.OUTSIDE_THE_LEGENDS_GUILD_DOOR,
      new int[]{ItemID.CAPE_OF_LEGENDS, ItemID.DRAGON_BATTLEAXE, ItemID.AMULET_OF_GLORY},
      item(ItemID.CAPE_OF_LEGENDS),
      any(
          "Any dragon battleaxe",
          item(ItemID.DRAGON_BATTLEAXE),
          item(ItemID.BH_DRAGON_BATTLEAXE_CORRUPTED)),
      any(
          "Any amulet of glory",
          item(ItemID.AMULET_OF_GLORY),
          item(ItemID.AMULET_OF_GLORY_1),
          item(ItemID.AMULET_OF_GLORY_2),
          item(ItemID.AMULET_OF_GLORY_3),
          item(ItemID.AMULET_OF_GLORY_4),
          item(ItemID.AMULET_OF_GLORY_5),
          item(ItemID.AMULET_OF_GLORY_6),
          item(ItemID.AMULET_OF_GLORY_INF))),
  MUBARIZS_ROOM_AT_THE_DUEL_ARENA(
      "Emir's Arena",
      "Mubariz's room at the Emir's Arena",
      STASHUnit.EMIRS_ARENA_TICKET_OFFICE,
      new int[]{ItemID.IRON_CHAINBODY, ItemID.LEATHER_CHAPS, ItemID.COIF},
      item(ItemID.IRON_CHAINBODY),
      item(ItemID.LEATHER_CHAPS),
      item(ItemID.COIF)),
  TOP_FLOOR_OF_THE_LIGHTHOUSE(
      "Lighthouse",
      "Top floor of the Lighthouse",
      STASHUnit.TOP_FLOOR_OF_THE_LIGHTHOUSE,
      new int[]{ItemID.BLUE_DRAGONHIDE_BODY, ItemID.BLUE_DRAGON_VAMBRACES},
      item(ItemID.BLUE_DRAGONHIDE_BODY),
      item(ItemID.BLUE_DRAGON_VAMBRACES)),
  SHILO_VILLAGE_BANK(
      "Shilo Village",
      "Shilo Village bank",
      STASHUnit.SHILO_VILLAGE_BANK,
      new int[]{ItemID.MYSTIC_HAT, ItemID.CAVE_GOBLIN_BONE_SPEAR, ItemID.RUNE_PLATEBODY},
      item(ItemID.MYSTIC_HAT),
      item(ItemID.CAVE_GOBLIN_BONE_SPEAR),
      item(ItemID.RUNE_PLATEBODY)),
  NEAR_A_LADDER_IN_THE_WILDERNESS_LAVA_MAZE(
      "Lava maze",
      "Near a ladder in the Wilderness Lava Maze",
      STASHUnit.NEAR_A_LADDER_IN_THE_WILDERNESS_LAVA_MAZE,
      new int[]{ItemID.BLACK_DRAGONHIDE_CHAPS, ItemID.HUNTING_LIGHT_CAPE,
          ItemID.HUNDRED_ROLLINGPIN},
      item(ItemID.BLACK_DRAGONHIDE_CHAPS),
      any(
          "Spotted cape",
          item(ItemID.HUNTING_LIGHT_CAPE),
          item(ItemID.HUNTING_LIGHT_CAPE_WORN)),
      item(ItemID.HUNDRED_ROLLINGPIN)),
  OUTSIDE_KRIL_TSUTSAROTHS_ROOM(
      "K'ril's chamber",
      "Outside K'ril Tsutsaroth's room",
      STASHUnit.OUTSIDE_KRIL_TSUTSAROTHS_ROOM,
      new int[]{ItemID.RUNE_FULL_HELM_ZAMORAK, ItemID.SHADOW_MAJ_SHADOW_SWORD},
      item(ItemID.RUNE_FULL_HELM_ZAMORAK),
      item(ItemID.SHADOW_MAJ_SHADOW_SWORD)),
  TAVERLEY_STONE_CIRCLE(
      "Taverley stone circle",
      "Taverley Stone Circle",
      STASHUnit.TAVERLEY_STONE_CIRCLE,
      new int[]{ItemID.BLUEWIZHAT, ItemID.BRONZE_2H_SWORD, ItemID.HAM_BOOTS},
      item(ItemID.BLUEWIZHAT),
      item(ItemID.BRONZE_2H_SWORD),
      item(ItemID.HAM_BOOTS)),
  NORTH_OF_EVIL_DAVES_HOUSE_IN_EDGEVILLE(
      "Edgeville",
      "North of Evil Dave's house in Edgeville",
      STASHUnit.NORTH_OF_EVIL_DAVES_HOUSE_IN_EDGEVILLE,
      new int[]{ItemID.BROWN_APRON, ItemID.LEATHER_BOOTS, ItemID.LEATHER_GLOVES},
      item(ItemID.BROWN_APRON),
      item(ItemID.LEATHER_BOOTS),
      item(ItemID.LEATHER_GLOVES)),
  OGRE_CAGE_IN_KING_LATHAS_TRAINING_CAMP(
      "King Lathas' camp",
      "Ogre cage in the Ardougne Training Camp",
      STASHUnit.OGRE_CAGE_IN_KING_LATHAS_TRAINING_CAMP,
      new int[]{ItemID.DRAGONHIDE_BODY, ItemID.DRAGONHIDE_CHAPS, ItemID.STEEL_SQ_SHIELD},
      item(ItemID.DRAGONHIDE_BODY),
      item(ItemID.DRAGONHIDE_CHAPS),
      item(ItemID.STEEL_SQ_SHIELD)),
  ENTRANA_CHAPEL(
      "Entrana church",
      "Entrana Chapel",
      STASHUnit.ENTRANA_CHAPEL,
      new int[]{ItemID.BLACK_DRAGON_VAMBRACES, ItemID.BLACK_DRAGONHIDE_CHAPS,
          ItemID.BLACK_DRAGONHIDE_BODY},
      item(ItemID.BLACK_DRAGON_VAMBRACES),
      item(ItemID.BLACK_DRAGONHIDE_CHAPS),
      item(ItemID.BLACK_DRAGONHIDE_BODY)),
  NEAR_THE_ENTRANA_FERRY_IN_PORT_SARIM(
      "Port Sarim",
      "Near the Entrana ferry in Port Sarim",
      STASHUnit.NEAR_THE_ENTRANA_FERRY_IN_PORT_SARIM,
      new int[]{ItemID.COIF, ItemID.STEEL_PLATESKIRT, ItemID.SAPPHIRE_NECKLACE},
      item(ItemID.COIF),
      item(ItemID.STEEL_PLATESKIRT),
      item(ItemID.SAPPHIRE_NECKLACE)),
  OUTSIDE_THE_DIGSITE_EXAM_CENTRE(
      "Exam Centre",
      "Outside the Digsite Exam Centre",
      STASHUnit.OUTSIDE_THE_DIGSITE_EXAM_CENTRE,
      new int[]{ItemID.WHITE_APRON, ItemID.GNOME_BOOTS_GREEN, ItemID.LEATHER_GLOVES},
      item(ItemID.WHITE_APRON),
      item(ItemID.GNOME_BOOTS_GREEN),
      item(ItemID.LEATHER_GLOVES)),
  ON_THE_BRIDGE_TO_THE_MISTHALIN_WIZARDS_TOWER(
      "Wizards' Tower",
      "On the bridge to the Misthalin Wizards' Tower",
      STASHUnit.ON_THE_BRIDGE_TO_THE_MISTHALIN_WIZARDS_TOWER,
      new int[]{ItemID.IRON_MED_HELM, ItemID.EMERALD_RING, ItemID.WHITE_APRON},
      item(ItemID.IRON_MED_HELM),
      item(ItemID.EMERALD_RING),
      item(ItemID.WHITE_APRON)),
  UPSTAIRS_IN_THE_ARDOUGNE_WINDMILL(
      "East Ardougne",
      "Upstairs in the Ardougne windmill",
      STASHUnit.UPSTAIRS_IN_THE_ARDOUGNE_WINDMILL,
      new int[]{ItemID.GNOME_ROBETOP_BLUE, ItemID.HAM_ROBE, ItemID.TIARA},
      item(ItemID.GNOME_ROBETOP_BLUE),
      item(ItemID.HAM_ROBE),
      item(ItemID.TIARA)),
  OUTSIDE_THE_SEERS_VILLAGE_COURTHOUSE(
      "Seers Village",
      "Outside the Seers' Village courthouse",
      STASHUnit.OUTSIDE_THE_SEERS_VILLAGE_COURTHOUSE,
      new int[]{ItemID.ADAMANT_HALBERD, ItemID.MYSTIC_ROBE_BOTTOM, ItemID.DIAMOND_RING},
      item(ItemID.ADAMANT_HALBERD),
      item(ItemID.MYSTIC_ROBE_BOTTOM),
      item(ItemID.DIAMOND_RING)),
  OUTSIDE_THE_WILDERNESS_AXE_HUT(
      "Magic axe hut",
      "Outside the Wilderness axe hut",
      STASHUnit.OUTSIDE_THE_WILDERNESS_AXE_HUT,
      new int[]{ItemID.TRAIL_FLARED_PANTS, ItemID.LOCKPICK},
      item(ItemID.TRAIL_FLARED_PANTS),
      item(ItemID.LOCKPICK)),
  NORTH_OF_MOUNT_KARUULM(
      "Mount Karuulm",
      "North of Mount Karuulm",
      STASHUnit.NORTH_OF_MOUNT_KARUULM,
      new int[]{ItemID.ADAMNT_WARHAMMER, ItemID.RING_OF_LIFE, ItemID.MITHRIL_ARMOURED_BOOTS},
      item(ItemID.ADAMNT_WARHAMMER),
      item(ItemID.RING_OF_LIFE),
      item(ItemID.MITHRIL_ARMOURED_BOOTS)),
  HICKTONS_ARCHERY_EMPORIUM(
      "Catherby",
      "Hickton's Archery Emporium",
      STASHUnit.HICKTONS_ARCHERY_EMPORIUM,
      new int[]{ItemID.GNOME_BOOTS_BLUE, ItemID.HARDLEATHER_BODY, ItemID.SILVER_SICKLE},
      item(ItemID.GNOME_BOOTS_BLUE),
      item(ItemID.HARDLEATHER_BODY),
      item(ItemID.SILVER_SICKLE)),
  OUTSIDE_HARRYS_FISHING_SHOP_IN_CATHERBY(
      "Catherby",
      "Outside Harry's Fishing Shop in Catherby",
      STASHUnit.OUTSIDE_HARRYS_FISHING_SHOP_IN_CATHERBY,
      new int[]{ItemID.ADAMANT_SQ_SHIELD, ItemID.DTTD_BONE_DAGGER, ItemID.MITHRIL_PLATEBODY},
      item(ItemID.ADAMANT_SQ_SHIELD),
      item(ItemID.DTTD_BONE_DAGGER),
      item(ItemID.MITHRIL_PLATEBODY)),
  GNOME_STRONGHOLD_BALANCING_ROPE(
      "Gnome Stronghold",
      "Gnome Stronghold balancing rope",
      STASHUnit.GNOME_STRONGHOLD_BALANCING_ROPE,
      new int[]{ItemID.STEEL_KITESHIELD, ItemID.RING_OF_FORGING, ItemID.DRAGONHIDE_CHAPS},
      item(ItemID.STEEL_KITESHIELD),
      item(ItemID.RING_OF_FORGING),
      item(ItemID.DRAGONHIDE_CHAPS)),
  TZHAAR_GEM_STORE(
      "Tzhaar gem store",
      "TzHaar gem store",
      STASHUnit.TZHAAR_GEM_STORE,
      new int[]{ItemID.TZHAAR_CAPE_FIRE, ItemID.TZHAAR_THROWINGRING},
      any(
          "Fire cape",
          item(ItemID.TZHAAR_CAPE_FIRE),
          item(ItemID.TZHAAR_CAPE_FIRE_TROUVER),
          item(ItemID.SKILLCAPE_MAX_FIRECAPE),
          item(ItemID.SKILLCAPE_MAX_FIRECAPE_TROUVER),
          item(ItemID.INFERNAL_CAPE),
          item(ItemID.INFERNAL_CAPE_TROUVER),
          item(ItemID.SKILLCAPE_MAX_INFERNALCAPE),
          item(ItemID.SKILLCAPE_MAX_INFERNALCAPE_TROUVER)),
      item(ItemID.TZHAAR_THROWINGRING)),
  OUTSIDE_DRAYNOR_VILLAGE_JAIL(
      "Draynor Village jail",
      "Outside Draynor Village jail",
      STASHUnit.OUTSIDE_DRAYNOR_VILLAGE_JAIL,
      new int[]{ItemID.ADAMANT_SWORD, ItemID.STRUNG_SAPPHIRE_AMULET, ItemID.ADAMANT_PLATESKIRT},
      item(ItemID.ADAMANT_SWORD),
      item(ItemID.STRUNG_SAPPHIRE_AMULET),
      item(ItemID.ADAMANT_PLATESKIRT)),
  CROSSROADS_NORTH_OF_DRAYNOR_VILLAGE(
      "Draynor Village",
      "Crossroads north of Draynor Village",
      STASHUnit.CROSSROADS_NORTH_OF_DRAYNOR_VILLAGE,
      new int[]{ItemID.IRON_CHAINBODY, ItemID.SAPPHIRE_RING, ItemID.LONGBOW},
      item(ItemID.IRON_CHAINBODY),
      item(ItemID.SAPPHIRE_RING),
      item(ItemID.LONGBOW)),
  OUTSIDE_THE_FALADOR_PARTY_ROOM(
      "Falador Party Room",
      "Outside the Falador Party Room",
      STASHUnit.OUTSIDE_THE_FALADOR_PARTY_ROOM,
      new int[]{ItemID.STEEL_FULL_HELM, ItemID.STEEL_PLATEBODY, ItemID.IRON_PLATESKIRT},
      item(ItemID.STEEL_FULL_HELM),
      item(ItemID.STEEL_PLATEBODY),
      item(ItemID.IRON_PLATESKIRT)),
  NEAR_A_SHED_IN_LUMBRIDGE_SWAMP(
      "Lumbridge swamp",
      "Near a shed in Lumbridge Swamp",
      STASHUnit.NEAR_A_SHED_IN_LUMBRIDGE_SWAMP,
      new int[]{ItemID.BRONZE_DAGGER, ItemID.IRON_FULL_HELM, ItemID.GOLD_RING},
      item(ItemID.BRONZE_DAGGER),
      item(ItemID.IRON_FULL_HELM),
      item(ItemID.GOLD_RING)),
  LUMBRIDGE_SWAMP_CAVES(
      "Lumbridge swamp caves",
      "Lumbridge Swamp caves",
      STASHUnit.LUMBRIDGE_SWAMP_CAVES,
      new int[]{ItemID.STAFF_OF_AIR, ItemID.BRONZE_FULL_HELM, ItemID.AMULET_OF_POWER},
      item(ItemID.STAFF_OF_AIR),
      item(ItemID.BRONZE_FULL_HELM),
      item(ItemID.AMULET_OF_POWER)),
  OUTSIDE_THE_GREAT_PYRAMID_OF_SOPHANEM(
      "Pyramid Of Sophanem",
      "Outside the great pyramid of Sophanem",
      STASHUnit.OUTSIDE_THE_GREAT_PYRAMID_OF_SOPHANEM,
      new int[]{ItemID.RING_OF_LIFE, ItemID.AMULET_OF_GLORY, ItemID.ADAMANT_2H_SWORD},
      item(ItemID.RING_OF_LIFE),
      item(ItemID.AMULET_OF_GLORY),
      item(ItemID.ADAMANT_2H_SWORD)),
  CENTRE_OF_CANIFIS(
      "Canifis",
      "Centre of Canifis",
      STASHUnit.CENTRE_OF_CANIFIS,
      new int[]{ItemID.GNOME_ROBETOP_GREEN, ItemID.MITHRIL_PLATELEGS, ItemID.IRON_2H_SWORD},
      item(ItemID.GNOME_ROBETOP_GREEN),
      item(ItemID.MITHRIL_PLATELEGS),
      item(ItemID.IRON_2H_SWORD)),
  KING_BLACK_DRAGONS_LAIR(
      "King black dragon's lair",
      "King Black Dragon's lair",
      STASHUnit.KING_BLACK_DRAGONS_LAIR,
      new int[]{ItemID.BLACK_DRAGONHIDE_BODY, ItemID.BLACK_DRAGON_VAMBRACES,
          ItemID.DRAGONMASK_BLACK},
      item(ItemID.BLACK_DRAGONHIDE_BODY),
      item(ItemID.BLACK_DRAGON_VAMBRACES),
      item(ItemID.DRAGONMASK_BLACK)),
  SOUTH_OF_THE_GRAND_EXCHANGE(
      "Grand Exchange",
      "South of the Grand Exchange",
      STASHUnit.SOUTH_OF_THE_GRAND_EXCHANGE,
      new int[]{ItemID.PINK_SKIRT, ItemID.GNOME_ROBETOP_PINK, ItemID.TIARA_BODY},
      item(ItemID.PINK_SKIRT),
      item(ItemID.GNOME_ROBETOP_PINK),
      item(ItemID.TIARA_BODY)),
  OUTSIDE_MUDKNUCKLES_HUT(
      "Goblin Village",
      "Outside Mudknuckles' hut",
      STASHUnit.OUTSIDE_MUDKNUCKLES_HUT,
      new int[]{ItemID.RUNE_PLATEBODY_BANDOS, ItemID.TRAIL_BANDOS_CLOAK, ItemID.BGS},
      item(ItemID.RUNE_PLATEBODY_BANDOS),
      item(ItemID.TRAIL_BANDOS_CLOAK),
      any(
          "Bandos godsword",
          item(ItemID.BGS),
          item(ItemID.BGSG))),
  AL_KHARID_SCORPION_MINE(
      "Al Kharid mine",
      "Al Kharid scorpion mine",
      STASHUnit.AL_KHARID_SCORPION_MINE,
      new int[]{ItemID.DESERT_SHIRT, ItemID.LEATHER_GLOVES, ItemID.LEATHER_BOOTS},
      item(ItemID.DESERT_SHIRT),
      item(ItemID.LEATHER_GLOVES),
      item(ItemID.LEATHER_BOOTS)),
  INSIDE_THE_DIGSITE_EXAM_CENTRE(
      "Exam Centre",
      "Inside the Digsite Exam Centre",
      STASHUnit.INSIDE_THE_DIGSITE_EXAM_CENTRE,
      new int[]{ItemID.MYSTIC_FIRE_STAFF, ItemID.JEWL_DIAMOND_BRACELET, ItemID.RUNE_ARMOURED_BOOTS},
      item(ItemID.MYSTIC_FIRE_STAFF),
      item(ItemID.JEWL_DIAMOND_BRACELET),
      item(ItemID.RUNE_ARMOURED_BOOTS)),
  OUTSIDE_THE_SLAYER_TOWER_GARGOYLE_ROOM(
      "Slayer Tower",
      "Outside the Slayer Tower gargoyle room",
      STASHUnit.OUTSIDE_THE_SLAYER_TOWER_GARGOYLE_ROOM,
      new int[]{ItemID.DAGANOTH_CAVE_MAGIC_SHORTBOW, ItemID.JEWL_BRACELET_OF_COMBAT_4,
          ItemID.FRIS_KINGLY_HELM},
      item(ItemID.DAGANOTH_CAVE_MAGIC_SHORTBOW),
      any(
          "Combat bracelet",
          range(ItemID.JEWL_BRACELET_OF_COMBAT_4, ItemID.JEWL_BRACELET_OF_COMBAT),
          item(ItemID.JEWL_BRACELET_OF_COMBAT_5),
          item(ItemID.JEWL_BRACELET_OF_COMBAT_6)),
      any(
          "Helm of neitiznot",
          item(ItemID.FRIS_KINGLY_HELM),
          item(ItemID.BH_FRIS_KINGLY_HELM_CORRUPTED)
      )
  ),
  OUTSIDE_THE_FISHING_GUILD(
      "Fishing Guild",
      "Outside the Fishing Guild",
      STASHUnit.OUTSIDE_THE_FISHING_GUILD,
      new int[]{ItemID.EMERALD_RING, ItemID.STRUNG_SAPPHIRE_AMULET, ItemID.BRONZE_CHAINBODY},
      item(ItemID.EMERALD_RING),
      item(ItemID.STRUNG_SAPPHIRE_AMULET),
      item(ItemID.BRONZE_CHAINBODY)),
  SHANTAY_PASS(
      "Shantay Pass",
      "Shantay Pass",
      STASHUnit.SHANTAY_PASS,
      new int[]{ItemID.SNELM_POINT_BLUE, ItemID.STAFF_OF_AIR, ItemID.BRONZE_SQ_SHIELD},
      any(
          "Bruise blue snelm (pointed)", item(ItemID.SNELM_POINT_BLUE)),
      item(ItemID.STAFF_OF_AIR),
      item(ItemID.BRONZE_SQ_SHIELD)),
  AUBURYS_SHOP_IN_VARROCK(
      "Varrock rune store",
      "Aubury's shop in Varrock",
      STASHUnit.AUBURYS_SHOP_IN_VARROCK,
      new int[]{ItemID.TIARA_AIR, ItemID.STAFF_OF_WATER},
      item(ItemID.TIARA_AIR),
      item(ItemID.STAFF_OF_WATER)),
  CATHERBY_BEEHIVE_FIELD(
      "Catherby",
      "Catherby beehive field",
      STASHUnit.CATHERBY_BEEHIVE_FIELD,
      new int[]{ItemID.DESERT_SHIRT, ItemID.GNOME_ROBEBOTTOMS_GREEN, ItemID.STEEL_AXE},
      item(ItemID.DESERT_SHIRT),
      item(ItemID.GNOME_ROBEBOTTOMS_GREEN),
      item(ItemID.STEEL_AXE)),
  OUTSIDE_YANILLE_BANK(
      "Yanille",
      "Outside Yanille bank",
      STASHUnit.OUTSIDE_YANILLE_BANK,
      new int[]{ItemID.BROWN_APRON, ItemID.ADAMANT_MED_HELM, ItemID.SNAKESKIN_CHAPS},
      item(ItemID.BROWN_APRON),
      item(ItemID.ADAMANT_MED_HELM),
      item(ItemID.SNAKESKIN_CHAPS)),
  TZHAAR_WEAPONS_STORE(
      "Tzhaar weapon store",
      "TzHaar weapons store",
      STASHUnit.TZHAAR_WEAPONS_STORE,
      new int[]{ItemID.STEEL_LONGSWORD, ItemID.BLUE_DRAGONHIDE_BODY, ItemID.MYSTIC_GLOVES},
      item(ItemID.STEEL_LONGSWORD),
      item(ItemID.BLUE_DRAGONHIDE_BODY),
      item(ItemID.MYSTIC_GLOVES)),
  ENTRANCE_OF_THE_CAVERN_UNDER_THE_WHIRLPOOL(
      "Ancient cavern",
      "Entrance of the cavern under the whirlpool",
      STASHUnit.ENTRANCE_OF_THE_CAVERN_UNDER_THE_WHIRLPOOL,
      new int[]{ItemID.GRANITE_SHIELD, ItemID.SPLITBARK_BODY, ItemID.TRAIL_HERALDIC_HELM_1_RUNE},
      item(ItemID.GRANITE_SHIELD),
      item(ItemID.SPLITBARK_BODY),
      range(ItemID.TRAIL_HERALDIC_HELM_1_RUNE, ItemID.TRAIL_HERALDIC_HELM_5_RUNE)),
  NEAR_A_RUNITE_ROCK_IN_THE_FREMENNIK_ISLES(
      "Fremennik Isles",
      "Near a runite rock in the Fremennik Isles",
      STASHUnit.NEAR_A_RUNITE_ROCK_IN_THE_FREMENNIK_ISLES,
      new int[]{ItemID.RUNE_ARMOURED_BOOTS, ItemID.BASIC_TK_RANK2_BODY, ItemID.DRAGONSTONE_RING},
      item(ItemID.RUNE_ARMOURED_BOOTS),
      item(ItemID.BASIC_TK_RANK2_BODY),
      item(ItemID.DRAGONSTONE_RING)),
  NEAR_THE_PIER_IN_ZULANDRA(
      "Zul-Andra",
      "Near the pier in Zul-Andra",
      STASHUnit.NEAR_THE_PIER_IN_ZULANDRA,
      new int[]{ItemID.DRAGON_2H_SWORD, ItemID.BANDOS_BOOTS, ItemID.TZHAAR_CAPE_OBSIDIAN},
      any(
          "Any dragon 2h sword",
          item(ItemID.DRAGON_2H_SWORD),
          item(ItemID.BH_DRAGON_2H_SWORD_CORRUPTED)),
      any(
          "Bandos boots",
          item(ItemID.BANDOS_BOOTS),
          item(ItemID.GUARDIAN_BOOTS),
          item(ItemID.ECHO_BOOTS)),
      item(ItemID.TZHAAR_CAPE_OBSIDIAN)),
  FOUNTAIN_OF_HEROES(
      "Fountain of heroes",
      "Fountain of Heroes",
      STASHUnit.FOUNTAIN_OF_HEROES,
      new int[]{ItemID.SPLITBARK_LEGS, ItemID.DRAGON_BOOTS, ItemID.RUNE_LONGSWORD},
      item(ItemID.SPLITBARK_LEGS),
      any(
          "Dragon boots",
          item(ItemID.DRAGON_BOOTS),
          item(ItemID.DRAGON_BOOTS_GOLD),
          item(ItemID.BH_DRAGON_BOOTS_CORRUPTED),
          item(ItemID.PRIMORDIAL_BOOTS),
          item(ItemID.AVERNIC_TREADS_MELEE),
          item(ItemID.AVERNIC_TREADS_MELEE_RANGED),
          item(ItemID.AVERNIC_TREADS_MELEE_MAGIC),
          item(ItemID.AVERNIC_TREADS_MAX)),
      item(ItemID.RUNE_LONGSWORD)),
  MOUNTAIN_CAMP_GOAT_ENCLOSURE(
      "Mountain Camp",
      "Mountain Camp goat enclosure",
      STASHUnit.MOUNTAIN_CAMP_GOAT_ENCLOSURE,
      new int[]{ItemID.RUNE_FULL_HELM, ItemID.BLUE_DRAGONHIDE_CHAPS, ItemID.FIRE_BATTLESTAFF},
      item(ItemID.RUNE_FULL_HELM),
      item(ItemID.BLUE_DRAGONHIDE_CHAPS),
      item(ItemID.FIRE_BATTLESTAFF)),
  ROAD_JUNCTION_SOUTH_OF_SINCLAIR_MANSION(
      "Sinclair Mansion",
      "Road junction south of Sinclair Mansion",
      STASHUnit.ROAD_JUNCTION_SOUTH_OF_SINCLAIR_MANSION,
      new int[]{ItemID.LEATHER_COWL, ItemID.WIZARDS_ROBE, ItemID.IRON_SCIMITAR},
      item(ItemID.LEATHER_COWL),
      item(ItemID.WIZARDS_ROBE),
      item(ItemID.IRON_SCIMITAR)),
  NEAR_THE_GEM_STALL_IN_ARDOUGNE_MARKET(
      "Ardougne",
      "Near the gem stall in Ardougne market",
      STASHUnit.NEAR_THE_GEM_STALL_IN_ARDOUGNE_MARKET,
      new int[]{ItemID.JEWL_CASTLEWARS_BRACELET3, ItemID.STRUNG_DRAGONSTONE_AMULET,
          ItemID.RING_OF_FORGING},
      any(
          "Castle wars bracelet",
          range(ItemID.JEWL_CASTLEWARS_BRACELET3,
              ItemID.JEWL_CASTLEWARS_BRACELET)),
      item(ItemID.STRUNG_DRAGONSTONE_AMULET),
      item(ItemID.RING_OF_FORGING)),
  LIMESTONE_MINE(
      "Limestone Mine",
      "Limestone mine",
      STASHUnit.LIMESTONE_MINE,
      new int[]{ItemID.BRONZE_PLATELEGS, ItemID.STEEL_PICKAXE, ItemID.STEEL_MED_HELM},
      item(ItemID.BRONZE_PLATELEGS),
      item(ItemID.STEEL_PICKAXE),
      item(ItemID.STEEL_MED_HELM)),
  MAUSOLEUM_OFF_THE_MORYTANIA_COAST(
      "Morytania mausoleum",
      "Mausoleum off the Morytania coast",
      STASHUnit.MAUSOLEUM_OFF_THE_MORYTANIA_COAST,
      new int[]{ItemID.MITHRIL_PLATESKIRT, ItemID.MAPLE_LONGBOW},
      item(ItemID.MITHRIL_PLATESKIRT),
      item(ItemID.MAPLE_LONGBOW)),
  VOLCANO_IN_THE_NORTHEASTERN_WILDERNESS(
      "Blighted volcano",
      "Volcano in the north-eastern Wilderness",
      STASHUnit.VOLCANO_IN_THE_NORTHEASTERN_WILDERNESS,
      new int[]{ItemID.HEADBAND_RED, ItemID.TRAIL_ANCIENT_STAFF},
      any(
          "Any headband",
          range(ItemID.HEADBAND_RED, ItemID.HEADBAND_BROWN),
          range(ItemID.HEADBAND_WHITE, ItemID.HEADBAND_GREEN)),
      any(
          "Any crozier",
          item(ItemID.TRAIL_ANCIENT_STAFF),
          item(ItemID.TRAIL_ARMADYL_STAFF),
          item(ItemID.TRAIL_BANDOS_STAFF),
          range(ItemID.TRAIL_SARADOMIN_STAFF, ItemID.TRAIL_ZAMORAK_STAFF))),
  GNOME_GLIDER_ON_WHITE_WOLF_MOUNTAIN(
      "White Wolf Mountain",
      "Gnome Glider on White Wolf Mountain",
      STASHUnit.GNOME_GLIDER_ON_WHITE_WOLF_MOUNTAIN,
      new int[]{ItemID.MITHRIL_PLATELEGS, ItemID.RING_OF_LIFE, ItemID.RUNE_AXE},
      item(ItemID.MITHRIL_PLATELEGS),
      item(ItemID.RING_OF_LIFE),
      item(ItemID.RUNE_AXE)),
  SOUTHEAST_CORNER_OF_LAVA_DRAGON_ISLE(
      "Lava dragon isle",
      "South-east corner of Lava Dragon Isle",
      STASHUnit.SOUTHEAST_CORNER_OF_LAVA_DRAGON_ISLE,
      new int[]{
          ItemID.DRAGON_MED_HELM, ItemID.TZHAAR_SPIKESHIELD, ItemID.OLAF2_BRINE_SABRE,
          ItemID.RUNE_PLATEBODY,
          ItemID.AMULET_OF_GLORY
      },
      any(
          "Any dragon med helm",
          item(ItemID.DRAGON_MED_HELM),
          item(ItemID.BH_DRAGON_MED_HELM_CORRUPTED)),
      item(ItemID.TZHAAR_SPIKESHIELD),
      item(ItemID.OLAF2_BRINE_SABRE),
      item(ItemID.RUNE_PLATEBODY),
      any(
          "Uncharged Amulet of glory", item(ItemID.AMULET_OF_GLORY))),
  HALFWAY_DOWN_TROLLWEISS_MOUNTAIN(
      "Trollweiss mountain",
      "Half-way down Trollweiss Mountain",
      STASHUnit.HALFWAY_DOWN_TROLLWEISS_MOUNTAIN,
      new int[]{
          ItemID.BLUE_DRAGON_VAMBRACES, ItemID.DRAGON_SPEAR, ItemID.RUNE_PLATESKIRT,
          ItemID.TROLLROMANCE_TOBOGGON_WAXED
      },
      item(ItemID.BLUE_DRAGON_VAMBRACES),
      any(
          "Any dragon spear",
          item(ItemID.DRAGON_SPEAR),
          item(ItemID.DRAGON_SPEAR_P),
          item(ItemID.DRAGON_SPEAR_P_),
          item(ItemID.DRAGON_SPEAR_P__),
          item(ItemID.BH_DRAGON_SPEAR_CORRUPTED),
          item(ItemID.BH_DRAGON_SPEAR_P_CORRUPTED),
          item(ItemID.BH_DRAGON_SPEAR_P__CORRUPTED),
          item(ItemID.BH_DRAGON_SPEAR_P___CORRUPTED)),
      item(ItemID.RUNE_PLATESKIRT),
      item(ItemID.TROLLROMANCE_TOBOGGON_WAXED)),
  WARRIORS_GUILD_BANK_29047(
      "Warriors' guild",
      "Warriors' Guild bank (master)",
      STASHUnit.WARRIORS_GUILD_BANK_29047,
      new int[]{ItemID.DRAGON_BATTLEAXE, ItemID.DRAGON_PARRYINGDAGGER, ItemID.SLAYER_HELM},
      any(
          "Any dragon battleaxe",
          item(ItemID.DRAGON_BATTLEAXE),
          item(ItemID.BH_DRAGON_BATTLEAXE_CORRUPTED)),
      any(
          "Dragon defender or Avernic defender",
          item(ItemID.DRAGON_PARRYINGDAGGER),
          item(ItemID.DRAGON_PARRYINGDAGGER_T),
          item(ItemID.DRAGON_PARRYINGDAGGER_TROUVER),
          item(ItemID.INFERNAL_DEFENDER),
          item(ItemID.INFERNAL_DEFENDER_TROUVER),
          item(ItemID.INFERNAL_DEFENDER_GHOMMAL_5),
          item(ItemID.INFERNAL_DEFENDER_GHOMMAL_5_TROUVER),
          item(ItemID.INFERNAL_DEFENDER_GHOMMAL_6),
          item(ItemID.INFERNAL_DEFENDER_GHOMMAL_6_TROUVER)),
      any(
          "Any slayer helmet",
          item(ItemID.SLAYER_HELM),
          item(ItemID.SLAYER_HELM_I),
          item(ItemID.SW_SLAYER_HELM_I),
          item(ItemID.PVPA_SLAYER_HELM_I),
          item(ItemID.SLAYER_HELM_ARAXYTE),
          item(ItemID.SLAYER_HELM_I_ARAXYTE),
          item(ItemID.SW_SLAYER_HELM_I_ARAXYTE),
          item(ItemID.PVPA_SLAYER_HELM_I_ARAXYTE),
          item(ItemID.SLAYER_HELM_BLACK),
          item(ItemID.SLAYER_HELM_I_BLACK),
          item(ItemID.SW_SLAYER_HELM_I_BLACK),
          item(ItemID.PVPA_SLAYER_HELM_I_BLACK),
          item(ItemID.SLAYER_HELM_GREEN),
          item(ItemID.SLAYER_HELM_I_GREEN),
          item(ItemID.SW_SLAYER_HELM_I_GREEN),
          item(ItemID.PVPA_SLAYER_HELM_I_GREEN),
          item(ItemID.SLAYER_HELM_RED),
          item(ItemID.SLAYER_HELM_I_RED),
          item(ItemID.SW_SLAYER_HELM_I_RED),
          item(ItemID.PVPA_SLAYER_HELM_I_RED),
          item(ItemID.SLAYER_HELM_PURPLE),
          item(ItemID.SLAYER_HELM_I_PURPLE),
          item(ItemID.SW_SLAYER_HELM_I_PURPLE),
          item(ItemID.PVPA_SLAYER_HELM_I_PURPLE),
          item(ItemID.SLAYER_HELM_TURQUOISE),
          item(ItemID.SLAYER_HELM_I_TURQUOISE),
          item(ItemID.SW_SLAYER_HELM_I_TURQUOISE),
          item(ItemID.PVPA_SLAYER_HELM_I_TURQUOISE),
          item(ItemID.SLAYER_HELM_HYDRA),
          item(ItemID.SLAYER_HELM_I_HYDRA),
          item(ItemID.SW_SLAYER_HELM_I_HYDRA),
          item(ItemID.PVPA_SLAYER_HELM_I_HYDRA),
          item(ItemID.SLAYER_HELM_TWISTED),
          item(ItemID.SLAYER_HELM_I_TWISTED),
          item(ItemID.SW_SLAYER_HELM_I_TWISTED),
          item(ItemID.PVPA_SLAYER_HELM_I_TWISTED),
          item(ItemID.SLAYER_HELM_JAD),
          item(ItemID.SLAYER_HELM_I_JAD),
          item(ItemID.SW_SLAYER_HELM_I_JAD),
          item(ItemID.PVPA_SLAYER_HELM_I_JAD),
          item(ItemID.SLAYER_HELM_VERZIK),
          item(ItemID.SLAYER_HELM_I_VERZIK),
          item(ItemID.SW_SLAYER_HELM_I_VERZIK),
          item(ItemID.PVPA_SLAYER_HELM_I_VERZIK),
          item(ItemID.SLAYER_HELM_ZUK),
          item(ItemID.SLAYER_HELM_I_ZUK),
          item(ItemID.SW_SLAYER_HELM_I_ZUK),
          item(ItemID.PVPA_SLAYER_HELM_I_ZUK))),
  NEAR_THE_PARROTS_IN_ARDOUGNE_ZOO(
      "Ardougne Zoo",
      "Near the parrots in Ardougne Zoo",
      STASHUnit.NEAR_THE_PARROTS_IN_ARDOUGNE_ZOO,
      new int[]{ItemID.STUDDED_BODY, ItemID.BRONZE_PLATELEGS, ItemID.PLAINSTAFF},
      item(ItemID.STUDDED_BODY),
      item(ItemID.BRONZE_PLATELEGS),
      item(ItemID.PLAINSTAFF)),
  OUTSIDE_KEEP_LE_FAYE(
      "Keep Le Faye",
      "Outside Keep Le Faye",
      STASHUnit.OUTSIDE_KEEP_LE_FAYE,
      new int[]{ItemID.COIF, ItemID.IRON_PLATEBODY, ItemID.LEATHER_GLOVES},
      item(ItemID.COIF),
      item(ItemID.IRON_PLATEBODY),
      item(ItemID.LEATHER_GLOVES)),
  FISHING_GUILD_BANK(
      "Fishing Guild",
      "Fishing Guild bank",
      STASHUnit.FISHING_GUILD_BANK,
      new int[]{ItemID.ELEMENTAL_SHIELD, ItemID.BLUE_DRAGONHIDE_CHAPS, ItemID.RUNE_WARHAMMER},
      item(ItemID.ELEMENTAL_SHIELD),
      item(ItemID.BLUE_DRAGONHIDE_CHAPS),
      item(ItemID.RUNE_WARHAMMER)),
  WEST_SIDE_OF_THE_KARAMJA_BANANA_PLANTATION(
      "Karamja",
      "West side of the Karamja banana plantation",
      STASHUnit.WEST_SIDE_OF_THE_KARAMJA_BANANA_PLANTATION,
      new int[]{ItemID.DIAMOND_RING, ItemID.AMULET_OF_POWER},
      item(ItemID.DIAMOND_RING),
      item(ItemID.AMULET_OF_POWER)),
  WARRIORS_GUILD_BANK(
      "Warriors' guild",
      "Warriors' Guild bank",
      STASHUnit.WARRIORS_GUILD_BANK,
      new int[]{ItemID.BLACK_SALAMANDER},
      item(ItemID.BLACK_SALAMANDER)),
  HOSIDIUS_MESS(
      "Hosidius mess hall",
      "Hosidius Mess",
      STASHUnit.HOSIDIUS_MESS,
      new int[]{ItemID.RUNE_HALBERD, ItemID.RUNE_PLATEBODY, ItemID.AMULET_OF_STRENGTH},
      item(ItemID.RUNE_HALBERD),
      item(ItemID.RUNE_PLATEBODY),
      item(ItemID.AMULET_OF_STRENGTH)),
  RIMMINGTON_MINE(
      "Rimmington mine",
      "Rimmington mine",
      STASHUnit.RIMMINGTON_MINE,
      new int[]{ItemID.GOLD_NECKLACE, ItemID.GOLD_RING, ItemID.BRONZE_SPEAR},
      item(ItemID.GOLD_NECKLACE),
      item(ItemID.GOLD_RING),
      item(ItemID.BRONZE_SPEAR)),
  OUTSIDE_CATHERBY_BANK(
      "Catherby",
      "Outside Catherby bank",
      STASHUnit.OUTSIDE_CATHERBY_BANK,
      new int[]{ItemID.MAPLE_LONGBOW, ItemID.DRAGONHIDE_CHAPS, ItemID.IRON_MED_HELM},
      item(ItemID.MAPLE_LONGBOW),
      item(ItemID.DRAGONHIDE_CHAPS),
      item(ItemID.IRON_MED_HELM)),
  CHAOS_TEMPLE_IN_THE_SOUTHEASTERN_WILDERNESS(
      "East of the Level 19 Wilderness Obelisk",
      "Chaos Temple in the south-eastern Wilderness",
      STASHUnit.EAST_OF_THE_LEVEL_19_WILDERNESS_OBELISK,
      new int[]{ItemID.RUNE_PLATELEGS, ItemID.IRON_PLATEBODY, ItemID.BLUE_DRAGON_VAMBRACES},
      item(ItemID.RUNE_PLATELEGS),
      item(ItemID.IRON_PLATEBODY),
      item(ItemID.BLUE_DRAGON_VAMBRACES)),
  SHAYZIEN_WAR_TENT(
      "Shayzien war tent",
      "Shayzien War Tent",
      STASHUnit.SHAYZIEN_WAR_TENT,
      new int[]{ItemID.MYSTIC_ROBE_BOTTOM, ItemID.RUNE_KITESHIELD, ItemID.TRAIL_BOB_SHIRT_RED},
      item(ItemID.MYSTIC_ROBE_BOTTOM),
      item(ItemID.RUNE_KITESHIELD),
      range(ItemID.TRAIL_BOB_SHIRT_RED, ItemID.TRAIL_BOB_SHIRT_PURPLE)),
  CENTRE_OF_THE_CATACOMBS_OF_KOUREND(
      "Kourend catacombs",
      "Centre of the Catacombs of Kourend",
      STASHUnit.CENTRE_OF_THE_CATACOMBS_OF_KOUREND,
      new int[]{ItemID.ARCLIGHT, ItemID.DAMNED_AMULET_DEGRADED},
      any("Arclight or Emberlight", item(ItemID.ARCLIGHT),
          item(ItemID.EMBERLIGHT)),
      any(
          "Amulet of the damned",
          item(ItemID.DAMNED_AMULET_DEGRADED),
          item(ItemID.DAMNED_AMULET))),
  ROAD_JUNCTION_NORTH_OF_RIMMINGTON(
      "Rimmington",
      "Road junction north of Rimmington",
      STASHUnit.ROAD_JUNCTION_NORTH_OF_RIMMINGTON,
      new int[]{ItemID.GNOME_HAT_GREEN, ItemID.GNOME_ROBETOP_CREAM, ItemID.LEATHER_CHAPS},
      item(ItemID.GNOME_HAT_GREEN),
      item(ItemID.GNOME_ROBETOP_CREAM),
      item(ItemID.LEATHER_CHAPS)),
  DRAYNOR_MANOR_BY_THE_FOUNTAIN(
      "Draynor Manor",
      "Draynor Manor by the fountain",
      STASHUnit.DRAYNOR_MANOR_BY_THE_FOUNTAIN,
      new int[]{ItemID.IRON_PLATEBODY, ItemID.STUDDED_CHAPS, ItemID.BRONZE_FULL_HELM},
      item(ItemID.IRON_PLATEBODY),
      item(ItemID.STUDDED_CHAPS),
      item(ItemID.BRONZE_FULL_HELM)),
  SOUL_ALTAR(
      "Soul altar",
      "Soul Altar",
      STASHUnit.SOUL_ALTAR,
      new int[]{ItemID.DRAGON_PICKAXE, ItemID.FRIS_KINGLY_HELM, ItemID.RUNE_ARMOURED_BOOTS},
      any(
          "Dragon or Crystal pickaxe",
          item(ItemID.DRAGON_PICKAXE),
          item(ItemID.DRAGON_PICKAXE_PRETTY),
          item(ItemID.INFERNAL_PICKAXE),
          item(ItemID.INFERNAL_PICKAXE_EMPTY),
          item(ItemID.ZALCANO_PICKAXE),
          item(ItemID.TRAILBLAZER_PICKAXE_NO_INFERNAL),
          item(ItemID.CRYSTAL_PICKAXE),
          item(ItemID.CRYSTAL_PICKAXE_INACTIVE),
          item(ItemID.TRAILBLAZER_PICKAXE),
          item(ItemID.TRAILBLAZER_PICKAXE_EMPTY)),
      item(ItemID.FRIS_KINGLY_HELM),
      item(ItemID.RUNE_ARMOURED_BOOTS)),
  OUTSIDE_VARROCK_PALACE_COURTYARD(
      "Varrock Castle",
      "Outside Varrock Palace courtyard",
      STASHUnit.OUTSIDE_VARROCK_PALACE_COURTYARD,
      new int[]{ItemID.BLACK_AXE, ItemID.COIF, ItemID.RUBY_RING},
      item(ItemID.BLACK_AXE),
      item(ItemID.COIF),
      item(ItemID.RUBY_RING)),
  CHAPEL_IN_WEST_ARDOUGNE(
      "West Ardougne Church",
      "Chapel in West Ardougne",
      STASHUnit.CHAPEL_IN_WEST_ARDOUGNE,
      new int[]{ItemID.DRAGON_SPEAR, ItemID.RED_DRAGONHIDE_CHAPS},
      any(
          "Any dragon spear",
          item(ItemID.DRAGON_SPEAR),
          item(ItemID.DRAGON_SPEAR_P),
          item(ItemID.DRAGON_SPEAR_P_),
          item(ItemID.DRAGON_SPEAR_P__),
          item(ItemID.BH_DRAGON_SPEAR_CORRUPTED),
          item(ItemID.BH_DRAGON_SPEAR_P_CORRUPTED),
          item(ItemID.BH_DRAGON_SPEAR_P__CORRUPTED),
          item(ItemID.BH_DRAGON_SPEAR_P___CORRUPTED)),
      item(ItemID.RED_DRAGONHIDE_CHAPS)),
  EAST_OF_THE_BARBARIAN_VILLAGE_BRIDGE(
      "Barbarian Village",
      "East of the Barbarian Village bridge",
      STASHUnit.EAST_OF_THE_BARBARIAN_VILLAGE_BRIDGE,
      new int[]{ItemID.WOLFENGLOVES_PURPLE, ItemID.STEEL_KITESHIELD, ItemID.MITHRIL_FULL_HELM},
      item(ItemID.WOLFENGLOVES_PURPLE),
      item(ItemID.STEEL_KITESHIELD),
      item(ItemID.MITHRIL_FULL_HELM)),
  NORTHWESTERN_CORNER_OF_THE_ENCHANTED_VALLEY(
      "Enchanted Valley (BKQ)",
      "North-western corner of the Enchanted Valley",
      STASHUnit.NORTHWESTERN_CORNER_OF_THE_ENCHANTED_VALLEY,
      new int[]{ItemID.DRAGON_AXE},
      any(
          "Dragon or Crystal axe",
          item(ItemID.DRAGON_AXE),
          item(ItemID.TRAILBLAZER_AXE_NO_INFERNAL),
          item(ItemID.DRAGON_AXE_2H),
          item(ItemID.CRYSTAL_AXE),
          item(ItemID.CRYSTAL_AXE_INACTIVE),
          item(ItemID.CRYSTAL_AXE_2H),
          item(ItemID.CRYSTAL_AXE_2H_INACTIVE),
          item(ItemID.INFERNAL_AXE),
          item(ItemID.INFERNAL_AXE_EMPTY),
          item(ItemID.TRAILBLAZER_AXE),
          item(ItemID.TRAILBLAZER_AXE_EMPTY))),
  WHEAT_FIELD_NEAR_THE_LUMBRIDGE_WINDMILL(
      "Lumbridge mill",
      "Wheat field near the Lumbridge windmill",
      STASHUnit.WHEAT_FIELD_NEAR_THE_LUMBRIDGE_WINDMILL,
      new int[]{ItemID.GNOME_ROBETOP_BLUE, ItemID.GNOME_ROBEBOTTOMS_TURQUOISE, ItemID.OAK_SHORTBOW},
      item(ItemID.GNOME_ROBETOP_BLUE),
      item(ItemID.GNOME_ROBEBOTTOMS_TURQUOISE),
      item(ItemID.OAK_SHORTBOW)),
  OBSERVATORY(
      "Observatory",
      "Observatory",
      STASHUnit.OBSERVATORY,
      new int[]{ItemID.MITHRIL_CHAINBODY, ItemID.DRAGONHIDE_CHAPS, ItemID.STRUNG_RUBY_AMULET},
      item(ItemID.MITHRIL_CHAINBODY),
      item(ItemID.DRAGONHIDE_CHAPS),
      item(ItemID.STRUNG_RUBY_AMULET)),
  NEAR_THE_SAWMILL_OPERATORS_BOOTH(
      "Lumber Yard",
      "Near the Sawmill Operator's booth",
      STASHUnit.NEAR_THE_SAWMILL_OPERATORS_BOOTH,
      new int[]{ItemID.HARDLEATHER_BODY, ItemID.LEATHER_CHAPS, ItemID.BRONZE_AXE},
      item(ItemID.HARDLEATHER_BODY),
      item(ItemID.LEATHER_CHAPS),
      item(ItemID.BRONZE_AXE)),
  NEAR_HERQUINS_SHOP_IN_FALADOR(
      "Falador",
      "Near Herquin's shop in Falador",
      STASHUnit.NEAR_HERQUINS_SHOP_IN_FALADOR,
      new int[]{ItemID.MITHRIL_PICKAXE, ItemID.BLACK_PLATEBODY, ItemID.IRON_KITESHIELD},
      item(ItemID.MITHRIL_PICKAXE),
      item(ItemID.BLACK_PLATEBODY),
      item(ItemID.IRON_KITESHIELD)),
  MUDSKIPPER_POINT(
      "Mudskipper Point (AIQ)",
      "Mudskipper Point",
      STASHUnit.MUDSKIPPER_POINT,
      new int[]{ItemID.BLACK_CAPE, ItemID.LEATHER_CHAPS, ItemID.STEEL_MACE},
      item(ItemID.BLACK_CAPE),
      item(ItemID.LEATHER_CHAPS),
      item(ItemID.STEEL_MACE)),
  NORTHERN_WALL_OF_CASTLE_DRAKAN(
      "Castle Drakan",
      "Northern wall of Castle Drakan",
      STASHUnit.NORTHERN_WALL_OF_CASTLE_DRAKAN,
      new int[]{ItemID.DRAGON_SQ_SHIELD, ItemID.SPLITBARK_BODY, ItemID.STRAWBOATER_RED},
      any(
          "Dragon sq shield",
          item(ItemID.DRAGON_SQ_SHIELD),
          item(ItemID.DRAGON_SQ_SHIELD_GOLD),
          item(ItemID.BH_DRAGON_SQ_SHIELD_CORRUPTED)),
      item(ItemID.SPLITBARK_BODY),
      any(
          "Any boater",
          item(ItemID.STRAWBOATER_RED),
          item(ItemID.STRAWBOATER_ORANGE),
          item(ItemID.STRAWBOATER_GREEN),
          item(ItemID.STRAWBOATER_BLUE),
          item(ItemID.STRAWBOATER_BLACK),
          item(ItemID.STRAWBOATER_PINK),
          item(ItemID.STRAWBOATER_PURPLE),
          item(ItemID.STRAWBOATER_WHITE))),
  SEVENTH_CHAMBER_OF_JALSAVRAH(
      "Pyramid Plunder",
      "7th Chamber of Jalsavrah",
      STASHUnit._7TH_CHAMBER_OF_JALSAVRAH,
      new int[]{
          ItemID.NTK_JEWELLED_SCEPTRE_3,
          ItemID.ROGUETRADER_MENAPHITE_HAT,
          ItemID.ROGUETRADER_MENAPHITE_TOP,
          ItemID.ROGUETRADER_MENAPHITE_LEGS
      },
      any(
          "Pharaoh's sceptre",
          item(ItemID.NTK_JEWELLED_SCEPTRE_3),
          item(ItemID.NTK_JEWELLED_SCEPTRE_3),
          item(ItemID.CERT_NTK_JEWELLED_SCEPTRE_3),
          item(ItemID.NTK_JEWELLED_SCEPTRE_2),
          item(ItemID.CERT_NTK_JEWELLED_SCEPTRE_2),
          item(ItemID.NTK_JEWELLED_SCEPTRE_1),
          item(ItemID.CERT_NTK_JEWELLED_SCEPTRE_1),
          item(ItemID.NTK_JEWELLED_SCEPTRE_0),
          item(ItemID.CERT_NTK_JEWELLED_SCEPTRE_0),
          item(ItemID.NTK_JEWELLED_SCEPTRE_8),
          item(ItemID.NTK_JEWELLED_SCEPTRE_7),
          item(ItemID.NTK_JEWELLED_SCEPTRE_5),
          item(ItemID.NTK_JEWELLED_SCEPTRE_4),
          item(ItemID.PLACEHOLDER_NTK_JEWELLED_SCEPTRE_8),
          item(ItemID.PLACEHOLDER_NTK_JEWELLED_SCEPTRE_1),
          item(ItemID.PLACEHOLDER_NTK_JEWELLED_SCEPTRE_0),
          item(ItemID.PHARAOHS_SCEPTRE_CHARGED),
          item(ItemID.PHARAOHS_SCEPTRE_CHARGED_INITIAL)),
      any(
          "Full set of menaphite robes",
          all(
              item(ItemID.ROGUETRADER_MENAPHITE_HAT),
              item(ItemID.ROGUETRADER_MENAPHITE_TOP),
              range(ItemID.ROGUETRADER_MENAPHITE_LEGS,
                  ItemID.ROGUETRADER_MENAPHITE_LEGS2)),
          all(
              item(ItemID.ROGUETRADER_MENAPHITE_HAT_RED),
              item(ItemID.ROGUETRADER_MENAPHITE_TOP_RED),
              range(ItemID.ROGUETRADER_MENAPHITE_LEGS_RED,
                  ItemID.ROGUETRADER_MENAPHITE_LEGS_RED2)))),
  VARROCK_PALACE_LIBRARY(
      "Varrock Castle",
      "Varrock Palace Library",
      STASHUnit.VARROCK_PALACE_LIBRARY,
      new int[]{ItemID.GNOME_ROBETOP_GREEN, ItemID.HAM_ROBE, ItemID.IRON_WARHAMMER},
      item(ItemID.GNOME_ROBETOP_GREEN),
      item(ItemID.HAM_ROBE),
      item(ItemID.IRON_WARHAMMER)),
  DRAYNOR_VILLAGE_MARKET(
      "Draynor",
      "Draynor Village market",
      STASHUnit.DRAYNOR_VILLAGE_MARKET,
      new int[]{ItemID.STUDDED_CHAPS, ItemID.IRON_KITESHIELD, ItemID.STEEL_LONGSWORD},
      item(ItemID.STUDDED_CHAPS),
      item(ItemID.IRON_KITESHIELD),
      item(ItemID.STEEL_LONGSWORD)),
  CASTLE_WARS_BANK(
      "Castle Wars",
      "Castle Wars bank",
      STASHUnit.CASTLE_WARS_BANK,
      new int[]{ItemID.STRUNG_RUBY_AMULET, ItemID.MITHRIL_SCIMITAR, ItemID.WILDERNESS_CAPE_1},
      item(ItemID.STRUNG_RUBY_AMULET),
      item(ItemID.MITHRIL_SCIMITAR),
      range(ItemID.WILDERNESS_CAPE_1, ItemID.WILDERNESS_CAPE_50)),
  NOTERAZZOS_SHOP_IN_THE_WILDERNESS(
      "Rogues general store",
      "Noterazzo's shop in the Wilderness",
      STASHUnit.NOTERAZZOS_SHOP_IN_THE_WILDERNESS,
      new int[]{ItemID.ADAMANT_SQ_SHIELD, ItemID.BLUE_DRAGON_VAMBRACES, ItemID.RUNE_PICKAXE},
      item(ItemID.ADAMANT_SQ_SHIELD),
      item(ItemID.BLUE_DRAGON_VAMBRACES),
      item(ItemID.RUNE_PICKAXE)),
  ON_TOP_OF_TROLLHEIM_MOUNTAIN(
      "Trollheim Mountain",
      "On top of Trollheim Mountain",
      STASHUnit.ON_TOP_OF_TROLLHEIM_MOUNTAIN,
      new int[]{ItemID.LAVA_BATTLESTAFF, ItemID.BLACK_DRAGON_VAMBRACES,
          ItemID.ELEMENTAL_MIND_SHIELD},
      any(
          "Lava battlestaff",
          item(ItemID.LAVA_BATTLESTAFF),
          item(ItemID.LAVA_BATTLESTAFF_PRETTY)),
      item(ItemID.BLACK_DRAGON_VAMBRACES),
      item(ItemID.ELEMENTAL_MIND_SHIELD)),
  ENTRANCE_OF_THE_ARCEUUS_LIBRARY(
      "Arceuus library",
      "Entrance of the Arceuus library",
      STASHUnit.ENTRANCE_OF_THE_ARCEUUS_LIBRARY,
      new int[]{ItemID.BLUE_DRAGON_VAMBRACES, ItemID.ADAMANT_ARMOURED_BOOTS, ItemID.ADAMANT_DAGGER},
      item(ItemID.BLUE_DRAGON_VAMBRACES),
      item(ItemID.ADAMANT_ARMOURED_BOOTS),
      item(ItemID.ADAMANT_DAGGER)),
  TOP_FLOOR_OF_THE_YANILLE_WATCHTOWER(
      "Yanille Watchtower",
      "Top floor of the Yanille Watchtower",
      STASHUnit.TOP_FLOOR_OF_THE_YANILLE_WATCHTOWER,
      new int[]{
          ItemID.DRAGON_PLATESKIRT,
          ItemID.DEATH_CLIMBINGBOOTS,
          ItemID.DRAGON_CHAINBODY,
          ItemID.BULLROARER
      },
      any(
          "Dragon plateskirt",
          item(ItemID.DRAGON_PLATESKIRT),
          item(ItemID.DRAGON_PLATESKIRT_GOLD),
          item(ItemID.BH_DRAGON_PLATESKIRT_CORRUPTED)),
      any(
          "Climbing boots",
          item(ItemID.DEATH_CLIMBINGBOOTS),
          item(ItemID.CLIMBING_BOOTS_G)),
      any(
          "Dragon chainbody",
          item(ItemID.DRAGON_CHAINBODY),
          item(ItemID.DRAGON_CHAINBODY_GOLD),
          item(ItemID.BH_DRAGON_CHAINBODY_CORRUPTED)),
      item(ItemID.BULLROARER)),
  GYPSY_TENT_ENTRANCE(
      "Varrock",
      "Aris's tent",
      STASHUnit.GYPSY_TENT_ENTRANCE,
      new int[]{ItemID.GOLD_RING, ItemID.GOLD_NECKLACE},
      item(ItemID.GOLD_RING),
      item(ItemID.GOLD_NECKLACE)),
  FINE_CLOTHES_ENTRANCE(
      "Varrock",
      "Iffie Nitter in Varrock",
      STASHUnit.FINE_CLOTHES_ENTRANCE,
      new int[]{ItemID.CHEFS_HAT, ItemID.RED_CAPE},
      item(ItemID.CHEFS_HAT),
      item(ItemID.RED_CAPE)),
  BOB_AXES_ENTRANCE(
      "Lumbridge",
      "Bob's Brilliant Axes in Lumbridge",
      STASHUnit.BOB_AXES_ENTRANCE,
      new int[]{ItemID.BRONZE_AXE, ItemID.LEATHER_BOOTS},
      item(ItemID.BRONZE_AXE),
      item(ItemID.LEATHER_BOOTS)),
  CHARCOAL_BURNERS(
      "Charcoal Burners",
      "Near the Charcoal Burners",
      STASHUnit.CHARCOAL_BURNERS,
      new int[]{ItemID.TITHE_REWARD_HAT_MALE, ItemID.SHAYZIEN_BODY_5, ItemID.PYROMANCER_BOTTOM},
      any(
          "Farmer's strawhat",
          item(ItemID.TITHE_REWARD_HAT_MALE),
          item(ItemID.TITHE_REWARD_HAT_FEMALE)),
      item(ItemID.SHAYZIEN_BODY_5),
      item(ItemID.PYROMANCER_BOTTOM)),
  FORTIS_GRAND_MUSEUM(
      "Fortis Grand Museum",
      "Near the entrance of the Civitas illa Fortis Grand Museum",
      STASHUnit.FORTIS_GRAND_MUSEUM,
      new int[]{ItemID.EMERALD_NECKLACE, ItemID.BLUE_SKIRT, ItemID.GNOME_ROBETOP_TURQUOISE},
      item(ItemID.EMERALD_NECKLACE),
      item(ItemID.BLUE_SKIRT),
      item(ItemID.GNOME_ROBETOP_TURQUOISE)),
  CAM_TORUM_ENTRANCE(
      "Cam Torum",
      "South of the gates to Cam Torum",
      STASHUnit.CAM_TORUM_ENTRANCE,
      new int[]{ItemID.FROST_MOON_HELM, ItemID.FROST_MOON_CHESTPLATE, ItemID.FROST_MOON_TASSETS,
          ItemID.FROSTMOON_SPEAR},
      any(
          "Blue moon helm",
          item(ItemID.FROST_MOON_HELM),
          item(ItemID.FROST_MOON_HELM_DEGRADED)),
      any("Blue moon chestplate",
          item(ItemID.FROST_MOON_CHESTPLATE),
          item(ItemID.FROST_MOON_CHESTPLATE_DEGRADED)),
      any("Blue moon tassets",
          item(ItemID.FROST_MOON_TASSETS),
          item(ItemID.FROST_MOON_TASSETS_DEGRADED)),
      item(ItemID.FROSTMOON_SPEAR)
  ),
  TEMPLE_SOUTHEAST_OF_THE_BAZAAR(
      "Civitas illa Fortis",
      "Outside the temple in Civitas illa Fortis",
      STASHUnit.TEMPLE_SOUTHEAST_OF_THE_BAZAAR,
      new int[]{ItemID.SUNFIRE_HELM, ItemID.SUNFIRE_BODY,
          ItemID.SUNFIRE_LEGS},
      any(
          "Any piece of Sunfire Fanatic armour",
          item(ItemID.SUNFIRE_HELM),
          item(ItemID.SUNFIRE_BODY),
          item(ItemID.SUNFIRE_LEGS))),
  TWILIGHT_TEMPLE_MINE(
      "Twilight Temple mine",
      "North of the Twilight Temple",
      STASHUnit.TWILIGHT_TEMPLE_MINE,
      new int[]{ItemID.MAPLE_LONGBOW, ItemID.STRUNG_RUBY_AMULET, ItemID.STEEL_PLATELEGS},
      item(ItemID.MAPLE_LONGBOW),
      item(ItemID.STRUNG_RUBY_AMULET),
      item(ItemID.STEEL_PLATELEGS)),
  ORTUS_MEETS_PROUDSPIRE(
      "East of Proudspire",
      "Where the River Ortus meets the Proudspire",
      STASHUnit.ORTUS_MEETS_PROUDSPIRE,
      new int[]{ItemID.BLUEWIZHAT, ItemID.WIZARDS_ROBE},
      item(ItemID.BLUEWIZHAT),
      item(ItemID.WIZARDS_ROBE)),
  OUTSIDE_TWILIGHT_TEMPLE(
      "Twilight Temple",
      "Twilight Temple",
      STASHUnit.OUTSIDE_TWILIGHT_TEMPLE,
      new int[]{ItemID.RUNE_LONGSWORD, ItemID.RUNE_PLATEBODY, ItemID.RUNE_PLATESKIRT},
      item(ItemID.RUNE_LONGSWORD),
      item(ItemID.RUNE_PLATEBODY),
      item(ItemID.RUNE_PLATESKIRT)),
  WESTERN_SALVAGER_OVERLOOK(
      "Western Salvager Overlook",
      "West side of Salvager Overlook",
      STASHUnit.WESTERN_SALVAGER_OVERLOOK,
      new int[]{ItemID.HUEY_COIF, ItemID.HUEY_VAMBRACES},
      item(ItemID.HUEY_COIF),
      item(ItemID.HUEY_VAMBRACES)),
  PANDEMONIUM_BAR(
      "Pandemonium Bar",
      "The bar on the Pandemonium",
      STASHUnit.PANDEMONIUM_BAR,
      new int[]{ItemID.EYE_PATCH, ItemID.BRONZE_SCIMITAR},
      item(ItemID.EYE_PATCH),
      item(ItemID.BRONZE_SCIMITAR)),
  WINTUMBER_ISLAND(
      "Wintumber Island",
      "On Wintumber Island",
      STASHUnit.WINTUMBER_ISLAND,
      new int[]{ItemID.HUNDRED_PIRATE_CRAB_SHELL_HELM, ItemID.HUNDRED_PIRATE_CRAB_SHELL_GAUNTLET},
      item(ItemID.HUNDRED_PIRATE_CRAB_SHELL_HELM),
      item(ItemID.HUNDRED_PIRATE_CRAB_SHELL_GAUNTLET)),
  BRITTLE_ISLE(
      "Brittle Isle",
      "On Brittle Isle",
      STASHUnit.BRITTLE_ISLE,
      new int[]{ItemID.MEDALLION_OF_THE_DEEP, ItemID.ROSEWOOD_BLOWPIPE},
      item(ItemID.MEDALLION_OF_THE_DEEP),
      item(ItemID.ROSEWOOD_BLOWPIPE));

  private final String locationName;
  private final String chartText;
  private final STASHUnit stashUnitData;
  private final int[] defaultItemIds;
  private final ItemRequirementData[] itemRequirements;

  StashUnitData(
      String locationName,
      String chartText,
      STASHUnit stashUnitData,
      int[] defaultItemIds,
      ItemRequirementData... itemRequirements) {
    this.locationName = locationName;
    this.chartText = chartText;
    this.stashUnitData = stashUnitData;
    this.defaultItemIds = defaultItemIds;
    this.itemRequirements = itemRequirements;
  }

  @Override
  public String jsonKey() {
    return name();
  }

  /** Uses the same class the plugin deserialises into, so the field names can't drift. */
  @Override
  public Object toJson() {
    return new StashUnit.Data(
        locationName, chartText, stashUnitData, defaultItemIds, Arrays.asList(itemRequirements));
  }
}