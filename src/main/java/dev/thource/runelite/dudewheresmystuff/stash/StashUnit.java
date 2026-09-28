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

package dev.thource.runelite.dudewheresmystuff.stash;

import com.google.gson.Gson;
import dev.thource.runelite.dudewheresmystuff.JsonEnumData;
import java.util.List;
import lombok.Getter;
import net.runelite.client.plugins.cluescrolls.clues.emote.STASHUnit;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;

/**
 * StashUnit is used to define which items live at which locations.
 *
 * <p>The data for each unit is loaded from {@code data/StashUnitData.json}, which is generated from
 * {@code StashUnitData} in the test source set by {@code EnumJsonGenerator}. Do not edit the JSON
 * by hand.
 *
 * <p>Call {@link #load(Gson)} with the plugin's injected Gson before using any unit.
 */
public enum StashUnit {
  NORTHEAST_CORNER_OF_THE_KHARAZI_JUNGLE,
  BARBARIAN_OUTPOST_OBSTACLE_COURSE,
  SOUTHEAST_CORNER_OF_THE_MONASTERY,
  ENTRANCE_OF_THE_CAVE_OF_DAMIS,
  AGILITY_PYRAMID,
  WELL_OF_VOYAGE,
  SOUTHEAST_CORNER_OF_THE_FISHING_PLATFORM,
  DEATH_ALTAR,
  OUTSIDE_THE_BAR_BY_THE_FIGHT_ARENA,
  BARROWS_CHEST,
  IN_THE_MIDDLE_OF_JIGGIG,
  BY_THE_BEAR_CAGE_IN_VARROCK_PALACE_GARDENS,
  BEHIND_MISS_SCHISM_IN_DRAYNOR_VILLAGE,
  CRYSTALLINE_MAPLE_TREES,
  DIGSITE,
  SOUTH_OF_THE_SHRINE_IN_TAI_BWO_WANNAI_VILLAGE,
  WEST_OF_THE_SHAYZIEN_COMBAT_RING,
  TENT_IN_LORD_IORWERTHS_ENCAMPMENT,
  OUTSIDE_THE_LEGENDS_GUILD_GATES,
  OUTSIDE_THE_LEGENDS_GUILD_DOOR,
  MUBARIZS_ROOM_AT_THE_DUEL_ARENA,
  TOP_FLOOR_OF_THE_LIGHTHOUSE,
  SHILO_VILLAGE_BANK,
  NEAR_A_LADDER_IN_THE_WILDERNESS_LAVA_MAZE,
  OUTSIDE_KRIL_TSUTSAROTHS_ROOM,
  TAVERLEY_STONE_CIRCLE,
  NORTH_OF_EVIL_DAVES_HOUSE_IN_EDGEVILLE,
  OGRE_CAGE_IN_KING_LATHAS_TRAINING_CAMP,
  ENTRANA_CHAPEL,
  NEAR_THE_ENTRANA_FERRY_IN_PORT_SARIM,
  OUTSIDE_THE_DIGSITE_EXAM_CENTRE,
  ON_THE_BRIDGE_TO_THE_MISTHALIN_WIZARDS_TOWER,
  UPSTAIRS_IN_THE_ARDOUGNE_WINDMILL,
  OUTSIDE_THE_SEERS_VILLAGE_COURTHOUSE,
  OUTSIDE_THE_WILDERNESS_AXE_HUT,
  NORTH_OF_MOUNT_KARUULM,
  HICKTONS_ARCHERY_EMPORIUM,
  OUTSIDE_HARRYS_FISHING_SHOP_IN_CATHERBY,
  GNOME_STRONGHOLD_BALANCING_ROPE,
  TZHAAR_GEM_STORE,
  OUTSIDE_DRAYNOR_VILLAGE_JAIL,
  CROSSROADS_NORTH_OF_DRAYNOR_VILLAGE,
  OUTSIDE_THE_FALADOR_PARTY_ROOM,
  NEAR_A_SHED_IN_LUMBRIDGE_SWAMP,
  LUMBRIDGE_SWAMP_CAVES,
  OUTSIDE_THE_GREAT_PYRAMID_OF_SOPHANEM,
  CENTRE_OF_CANIFIS,
  KING_BLACK_DRAGONS_LAIR,
  SOUTH_OF_THE_GRAND_EXCHANGE,
  OUTSIDE_MUDKNUCKLES_HUT,
  AL_KHARID_SCORPION_MINE,
  INSIDE_THE_DIGSITE_EXAM_CENTRE,
  OUTSIDE_THE_SLAYER_TOWER_GARGOYLE_ROOM,
  OUTSIDE_THE_FISHING_GUILD,
  SHANTAY_PASS,
  AUBURYS_SHOP_IN_VARROCK,
  CATHERBY_BEEHIVE_FIELD,
  OUTSIDE_YANILLE_BANK,
  TZHAAR_WEAPONS_STORE,
  ENTRANCE_OF_THE_CAVERN_UNDER_THE_WHIRLPOOL,
  NEAR_A_RUNITE_ROCK_IN_THE_FREMENNIK_ISLES,
  NEAR_THE_PIER_IN_ZULANDRA,
  FOUNTAIN_OF_HEROES,
  MOUNTAIN_CAMP_GOAT_ENCLOSURE,
  ROAD_JUNCTION_SOUTH_OF_SINCLAIR_MANSION,
  NEAR_THE_GEM_STALL_IN_ARDOUGNE_MARKET,
  LIMESTONE_MINE,
  MAUSOLEUM_OFF_THE_MORYTANIA_COAST,
  VOLCANO_IN_THE_NORTHEASTERN_WILDERNESS,
  GNOME_GLIDER_ON_WHITE_WOLF_MOUNTAIN,
  SOUTHEAST_CORNER_OF_LAVA_DRAGON_ISLE,
  HALFWAY_DOWN_TROLLWEISS_MOUNTAIN,
  WARRIORS_GUILD_BANK_29047,
  NEAR_THE_PARROTS_IN_ARDOUGNE_ZOO,
  OUTSIDE_KEEP_LE_FAYE,
  FISHING_GUILD_BANK,
  WEST_SIDE_OF_THE_KARAMJA_BANANA_PLANTATION,
  WARRIORS_GUILD_BANK,
  HOSIDIUS_MESS,
  RIMMINGTON_MINE,
  OUTSIDE_CATHERBY_BANK,
  CHAOS_TEMPLE_IN_THE_SOUTHEASTERN_WILDERNESS,
  SHAYZIEN_WAR_TENT,
  CENTRE_OF_THE_CATACOMBS_OF_KOUREND,
  ROAD_JUNCTION_NORTH_OF_RIMMINGTON,
  DRAYNOR_MANOR_BY_THE_FOUNTAIN,
  SOUL_ALTAR,
  OUTSIDE_VARROCK_PALACE_COURTYARD,
  CHAPEL_IN_WEST_ARDOUGNE,
  EAST_OF_THE_BARBARIAN_VILLAGE_BRIDGE,
  NORTHWESTERN_CORNER_OF_THE_ENCHANTED_VALLEY,
  WHEAT_FIELD_NEAR_THE_LUMBRIDGE_WINDMILL,
  OBSERVATORY,
  NEAR_THE_SAWMILL_OPERATORS_BOOTH,
  NEAR_HERQUINS_SHOP_IN_FALADOR,
  MUDSKIPPER_POINT,
  NORTHERN_WALL_OF_CASTLE_DRAKAN,
  SEVENTH_CHAMBER_OF_JALSAVRAH,
  VARROCK_PALACE_LIBRARY,
  DRAYNOR_VILLAGE_MARKET,
  CASTLE_WARS_BANK,
  NOTERAZZOS_SHOP_IN_THE_WILDERNESS,
  ON_TOP_OF_TROLLHEIM_MOUNTAIN,
  ENTRANCE_OF_THE_ARCEUUS_LIBRARY,
  TOP_FLOOR_OF_THE_YANILLE_WATCHTOWER,
  GYPSY_TENT_ENTRANCE,
  FINE_CLOTHES_ENTRANCE,
  BOB_AXES_ENTRANCE,
  CHARCOAL_BURNERS,
  FORTIS_GRAND_MUSEUM,
  CAM_TORUM_ENTRANCE,
  TEMPLE_SOUTHEAST_OF_THE_BAZAAR,
  TWILIGHT_TEMPLE_MINE,
  ORTUS_MEETS_PROUDSPIRE,
  OUTSIDE_TWILIGHT_TEMPLE,
  WESTERN_SALVAGER_OVERLOOK,
  PANDEMONIUM_BAR,
  WINTUMBER_ISLAND,
  BRITTLE_ISLE;

  private static final JsonEnumData<StashUnit, Data> DATA =
      new JsonEnumData<>(values(), Data.class, "StashUnitData");

  /** One entry of the json. The RuneLite objects are resolved lazily and cached. */
  public static class Data {
    @Getter private final String locationName;
    @Getter private final String chartText;
    // name of the RuneLite STASHUnit constant
    private final String stashUnit;
    @Getter private final int[] defaultItemIds;
    private final List<ItemRequirementData> itemRequirements;

    private transient volatile STASHUnit resolvedStashUnit;
    private transient volatile ItemRequirement[] resolvedItemRequirements;

    public Data(
        String locationName,
        String chartText,
        STASHUnit stashUnit,
        int[] defaultItemIds,
        List<ItemRequirementData> itemRequirements) {
      this.locationName = locationName;
      this.chartText = chartText;
      this.stashUnit = stashUnit.name();
      this.defaultItemIds = defaultItemIds;
      this.itemRequirements = itemRequirements;
    }

    public STASHUnit getStashUnit() {
      STASHUnit resolved = resolvedStashUnit;
      if (resolved == null) {
        // Looked up by name over values() rather than valueOf, which would use reflection.
        for (STASHUnit candidate : STASHUnit.values()) {
          if (candidate.name().equals(stashUnit)) {
            resolved = candidate;
            break;
          }
        }

        if (resolved == null) {
          throw new IllegalStateException("Unknown STASHUnit " + stashUnit);
        }

        resolvedStashUnit = resolved;
      }

      return resolved;
    }

    public ItemRequirement[] getItemRequirements() {
      ItemRequirement[] resolved = resolvedItemRequirements;
      if (resolved == null) {
        resolved = new ItemRequirement[itemRequirements.size()];
        for (int i = 0; i < resolved.length; i++) {
          resolved[i] = itemRequirements.get(i).toItemRequirement();
        }

        // benign race: worst case two equivalent arrays get built
        resolvedItemRequirements = resolved;
      }

      return resolved;
    }
  }

  public static void load(Gson gson) {
    DATA.load(gson);
  }

  public Data getData() {
    return DATA.get(this);
  }

  public String getLocationName() {
    return getData().getLocationName();
  }

  public String getChartText() {
    return getData().getChartText();
  }

  public STASHUnit getStashUnitData() {
    return getData().getStashUnit();
  }

  public int[] getDefaultItemIds() {
    return getData().getDefaultItemIds();
  }

  public ItemRequirement[] getItemRequirements() {
    return getData().getItemRequirements();
  }
}