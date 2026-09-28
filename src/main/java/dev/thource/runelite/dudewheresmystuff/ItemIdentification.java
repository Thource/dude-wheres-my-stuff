/*
 * Copyright (c) 2019, Hydrox6 <ikada@protonmail.ch>
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

package dev.thource.runelite.dudewheresmystuff;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import java.util.Map;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ItemIdentification defines the short names shown on identified items.
 *
 * <p>The data for each constant is loaded from {@code data/ItemIdentificationData.json}, which is
 * generated from {@code ItemIdentificationData} in the test source set by {@code
 * EnumJsonGenerator}. Do not edit the JSON by hand.
 *
 * <p>Call {@link #load(Gson)} with the plugin's injected Gson before using {@link #get(int)} or any
 * of the getters.
 */
public enum ItemIdentification {
  GUAM_SEED,
  MARRENTILL_SEED,
  TARROMIN_SEED,
  HARRALANDER_SEED,
  RANARR_SEED,
  TOADFLAX_SEED,
  IRIT_SEED,
  AVANTOE_SEED,
  KWUARM_SEED,
  SNAPDRAGON_SEED,
  CADANTINE_SEED,
  LANTADYME_SEED,
  DWARF_WEED_SEED,
  TORSTOL_SEED,
  HUASCA_SEED,
  REDBERRY_SEED,
  CADAVABERRY_SEED,
  DWELLBERRY_SEED,
  JANGERBERRY_SEED,
  WHITEBERRY_SEED,
  POISON_IVY_SEED,
  GRAPE_SEED,
  MUSHROOM_SPORE,
  BELLADONNA_SEED,
  SEAWEED_SPORE,
  HESPORI_SEED,
  KRONOS_SEED,
  IASOR_SEED,
  ATTAS_SEED,
  CACTUS_SEED,
  POTATO_CACTUS_SEED,
  ACORN,
  WILLOW_SEED,
  MAPLE_SEED,
  YEW_SEED,
  MAGIC_SEED,
  REDWOOD_SEED,
  TEAK_SEED,
  MAHOGANY_SEED,
  CRYSTAL_ACORN,
  CELASTRUS_SEED,
  SPIRIT_SEED,
  CALQUAT_SEED,
  APPLE_TREE_SEED,
  BANANA_TREE_SEED,
  ORANGE_TREE_SEED,
  CURRY_TREE_SEED,
  PINEAPPLE_SEED,
  PAPAYA_TREE_SEED,
  PALM_TREE_SEED,
  DRAGONFRIUT_TREE_SEED,
  POTATO_SEED,
  ONION_SEED,
  CABBAGE_SEED,
  TOMATO_SEED,
  SWEETCORN_SEED,
  STRAWBERRY_SEED,
  WATERMELON_SEED,
  SNAPE_GRASS_SEED,
  MARIGOLD_SEED,
  ROSEMARY_SEED,
  NASTURTIUM_SEED,
  WOAD_SEED,
  LIMPWURT_SEED,
  WHITE_LILY_SEED,
  BARLEY_SEED,
  HAMMERSTONE_SEED,
  ASGARNIAN_SEED,
  JUTE_SEED,
  YANILLIAN_SEED,
  KRANDORIAN_SEED,
  WILDBLOOD_SEED,
  SACK,
  CABBAGE_SACK,
  ONION_SACK,
  POTATO_SACK,
  GUAM,
  MARRENTILL,
  TARROMIN,
  HARRALANDER,
  RANARR,
  TOADFLAX,
  IRIT,
  AVANTOE,
  KWUARM,
  SNAPDRAGON,
  CADANTINE,
  LANTADYME,
  DWARF_WEED,
  TORSTOL,
  HUASCA,
  ARDRIGAL,
  ROGUES_PURSE,
  SITO_FOIL,
  SNAKE_WEED,
  VOLENCIA_MOSS,
  RED_LOGS,
  GREEN_LOGS,
  BLUE_LOGS,
  WHITE_LOGS,
  PURPLE_LOGS,
  SCRAPEY_TREE_LOGS,
  LOG,
  ACHEY_TREE_LOG,
  OAK_LOG,
  WILLOW_LOG,
  TEAK_LOG,
  JUNIPER_LOG,
  MAPLE_LOG,
  MAHOGANY_LOG,
  ARCTIC_PINE_LOG,
  YEW_LOG,
  BLISTERWOOD_LOG,
  MAGIC_LOG,
  REDWOOD_LOG,
  PYRE_LOGS,
  ARCTIC_PYRE_LOGS,
  OAK_PYRE_LOGS,
  WILLOW_PYRE_LOGS,
  TEAK_PYRE_LOGS,
  MAPLE_PYRE_LOGS,
  MAHOGANY_PYRE_LOGS,
  YEW_PYRE_LOGS,
  MAGIC_PYRE_LOGS,
  REDWOOD_PYRE_LOGS,
  PLANK,
  OAK_PLANK,
  TEAK_PLANK,
  MAHOGANY_PLANK,
  WAXWOOD_PLANK,
  MALLIGNUM_ROOT_PLANK,
  OAK_SAPLING,
  WILLOW_SAPLING,
  MAPLE_SAPLING,
  YEW_SAPLING,
  MAGIC_SAPLING,
  REDWOOD_SAPLING,
  SPIRIT_SAPLING,
  CRYSTAL_SAPLING,
  APPLE_SAPLING,
  BANANA_SAPLING,
  ORANGE_SAPLING,
  CURRY_SAPLING,
  PINEAPPLE_SAPLING,
  PAPAYA_SAPLING,
  PALM_SAPLING,
  DRAGONFRUIT_SAPLING,
  TEAK_SAPLING,
  MAHOGANY_SAPLING,
  CALQUAT_SAPLING,
  CELASTRUS_SAPLING,
  COMPOST,
  SUPERCOMPOST,
  ULTRACOMPOST,
  COPPER_ORE,
  TIN_ORE,
  IRON_ORE,
  SILVER_ORE,
  COAL_ORE,
  GOLD_ORE,
  MITHRIL_ORE,
  ADAMANTITE_ORE,
  RUNITE_ORE,
  RUNE_ESSENCE,
  PURE_ESSENCE,
  PAYDIRT,
  AMETHYST,
  LOVAKITE_ORE,
  BLURITE_ORE,
  ELEMENTAL_ORE,
  DAEYALT_ORE,
  LUNAR_ORE,
  BRONZE_BAR,
  IRON_BAR,
  SILVER_BAR,
  STEEL_BAR,
  GOLD_BAR,
  MITHRIL_BAR,
  ADAMANTITE_BAR,
  RUNITE_BAR,
  SAPPHIRE,
  EMERALD,
  RUBY,
  DIAMOND,
  OPAL,
  JADE,
  RED_TOPAZ,
  DRAGONSTONE,
  ONYX,
  ZENYTE,
  ATTACK,
  STRENGTH,
  DEFENCE,
  COMBAT,
  MAGIC,
  RANGING,
  BASTION,
  BATTLEMAGE,
  SUPER_ATTACK,
  SUPER_STRENGTH,
  SUPER_DEFENCE,
  SUPER_COMBAT,
  SUPER_RANGING,
  SUPER_MAGIC,
  DIVINE_SUPER_ATTACK,
  DIVINE_SUPER_DEFENCE,
  DIVINE_SUPER_STRENGTH,
  DIVINE_SUPER_COMBAT,
  DIVINE_RANGING,
  DIVINE_MAGIC,
  DIVINE_BASTION,
  DIVINE_BATTLEMAGE,
  RESTORE,
  GUTHIX_BALANCE,
  SUPER_RESTORE,
  PRAYER,
  ENERGY,
  SUPER_ENERGY,
  STAMINA,
  OVERLOAD,
  ABSORPTION,
  ZAMORAK_BREW,
  SARADOMIN_BREW,
  ANCIENT_BREW,
  ANTIPOISON,
  SUPERANTIPOISON,
  ANTIDOTE_P,
  ANTIDOTE_PP,
  ANTIVENOM,
  ANTIVENOM_P,
  RELICYMS_BALM,
  SANFEW_SERUM,
  ANTIFIRE,
  EXTENDED_ANTIFIRE,
  SUPER_ANTIFIRE,
  EXTENDED_SUPER_ANTIFIRE,
  SERUM_207,
  SERUM_208,
  COMPOST_POTION,
  AGILITY,
  FISHING,
  HUNTER,
  GOBLIN,
  MAGIC_ESS,
  REJUVENATION,
  GUAM_POTION,
  MARRENTILL_POTION,
  TARROMIN_POTION,
  HARRALANDER_POTION,
  RANARR_POTION,
  TOADFLAX_POTION,
  IRIT_POTION,
  AVANTOE_POTION,
  KWUARM_POTION,
  SNAPDRAGON_POTION,
  CADANTINE_POTION,
  LANTADYME_POTION,
  DWARF_WEED_POTION,
  TORSTOL_POTION,
  HUASCA_POTION,
  BABY_IMPLING,
  YOUNG_IMPLING,
  GOURMET_IMPLING,
  EARTH_IMPLING,
  ESSENCE_IMPLING,
  ECLECTIC_IMPLING,
  NATURE_IMPLING,
  MAGPIE_IMPLING,
  NINJA_IMPLING,
  CRYSTAL_IMPLING,
  DRAGON_IMPLING,
  LUCKY_IMPLING,
  VARROCK_TELEPORT,
  LUMBRIDGE_TELEPORT,
  FALADOR_TELEPORT,
  CAMELOT_TELEPORT,
  ARDOUGNE_TELEPORT,
  WATCHTOWER_TELEPORT,
  TELEPORT_TO_HOUSE,
  ENCHANT_SAPPHIRE_OR_OPAL,
  ENCHANT_EMERALD_OR_JADE,
  ENCHANT_RUBY_OR_TOPAZ,
  ENCHANT_DIAMOND,
  ENCHANT_DRAGONSTONE,
  ENCHANT_ONYX,
  TELEKINETIC_GRAB,
  BONES_TO_PEACHES,
  BONES_TO_BANANAS,
  RIMMINGTON_TELEPORT,
  TAVERLEY_TELEPORT,
  POLLNIVNEACH_TELEPORT,
  RELLEKKA_TELEPORT,
  BRIMHAVEN_TELEPORT,
  YANILLE_TELEPORT,
  TROLLHEIM_TELEPORT,
  PRIFDDINAS_TELEPORT,
  HOSIDIUS_TELEPORT,
  ANNAKARL_TELEPORT,
  CARRALLANGER_TELEPORT,
  DAREEYAK_TELEPORT,
  GHORROCK_TELEPORT,
  KHARYRLL_TELEPORT,
  LASSAR_TELEPORT,
  PADDEWWA_TELEPORT,
  SENNTISTEN_TELEPORT,
  ARCEUUS_LIBRARY_TELEPORT,
  DRAYNOR_MANOR_TELEPORT,
  MIND_ALTAR_TELEPORT,
  SALVE_GRAVEYARD_TELEPORT,
  FENKENSTRAINS_CASTLE_TELEPORT,
  WEST_ARDOUGNE_TELEPORT,
  HARMONY_ISLAND_TELEPORT,
  CEMETERY_TELEPORT,
  BARROWS_TELEPORT,
  APE_ATOLL_TELEPORT,
  BATTLEFRONT_TELEPORT,
  MOONCLAN_TELEPORT,
  OURANIA_TELEPORT,
  WATERBIRTH_TELEPORT,
  BARBARIAN_TELEPORT,
  KHAZARD_TELEPORT,
  FISHING_GUILD_TELEPORT,
  CATHERBY_TELEPORT,
  ICE_PLATEAU,
  TARGET_TELEPORT,
  VOLCANIC_MINE_TELEPORT,
  WILDERNESS_CRABS_TELEPORT,
  NARDAH_TELEPORT,
  DIGSITE_TELEPORT,
  FELDIP_HILLS_TELEPORT,
  LUNAR_ISLE_TELEPORT,
  MORTTON_TELEPORT,
  PEST_CONTROL_TELEPORT,
  PISCATORIS_TELEPORT,
  TAI_BWO_WANNAI_TELEPORT,
  IORWERTH_CAMP_TELEPORT,
  MOS_LEHARMLESS_TELEPORT,
  LUMBERYARD_TELEPORT,
  ZUL_ANDRA_TELEPORT,
  KEY_MASTER_TELEPORT,
  REVENANT_CAVE_TELEPORT,
  WATSON_TELEPORT,
  OPAL_JEWELLERY,
  JADE_JEWELLERY,
  TOPAZ_JEWELLERY,
  GOLD_JEWELLERY,
  SAPPHIRE_JEWELLERY,
  EMERALD_JEWELLERY,
  RUBY_JEWELLERY,
  DIAMOND_JEWELLERY,
  DRAGONSTONE_JEWELLERY,
  ONYX_JEWELLERY,
  ZENYTE_JEWELLERY,
  RING_OF_PURSUIT,
  DODGY_NECKLACE,
  EXPEDITIOUS_BRACELET,
  AMULET_OF_BOUNTY,
  RING_OF_RETURNING,
  NECKLACE_OF_PASSAGE,
  FLAMTAER_BRACELET,
  AMULET_OF_CHEMISTRY,
  EFARITAYS_AID,
  NECKLACE_OF_FAITH,
  BRACELET_OF_SLAUGHTER,
  BURNING_AMULET,
  RING_OF_RECOIL,
  GAMES_NECKLACE,
  BRACELET_OF_CLAY,
  AMULET_OF_MAGIC,
  RING_OF_DUELING,
  BINDING_NECKLACE,
  CASTLE_WARS_BRACELET,
  AMULET_OF_DEFENCE,
  AMULET_OF_NATURE,
  RING_OF_FORGING,
  DIGSITE_PENDANT,
  INOCULATION_BRACELET,
  AMULET_OF_STRENGTH,
  RING_OF_LIFE,
  PHOENIX_NECKLACE,
  ABYSSAL_BRACELET,
  AMULET_OF_POWER,
  RING_OF_WEALTH,
  SKILLS_NECKLACE,
  COMBAT_BRACELET,
  AMULET_OF_GLORY,
  RING_OF_STONE,
  BERSERKER_NECKLACE,
  REGEN_BRACELET,
  AMULET_OF_FURY,
  RING_OF_SUFFERING,
  NECKLACE_OF_ANGUISH,
  TORMENTED_BRACELET,
  AMULET_OF_TORTURE,
  OCCULT_NECKLACE,
  DRAGONBONE_NECKLACE,
  SLAYER_RING;

  private static final JsonEnumData<ItemIdentification, Data> DATA =
      new JsonEnumData<>(values(), Data.class, "ItemIdentificationData");

  // Built by load(); null until then.
  private static volatile Map<Integer, ItemIdentification> itemIdentifications;

  /** One entry of the json. */
  @Getter
  @AllArgsConstructor
  public static class Data {
    private final Type type;
    private final String medName;
    private final String shortName;
    private final int[] itemIds;
  }

  /** Loads the data and builds the item id lookup. Safe to call more than once. */
  public static synchronized void load(Gson gson) {
    DATA.load(gson);

    if (itemIdentifications != null) {
      return;
    }

    ImmutableMap.Builder<Integer, ItemIdentification> builder = new ImmutableMap.Builder<>();

    for (ItemIdentification i : values()) {
      for (int id : i.getItemIds()) {
        builder.put(id, i);
      }
    }

    itemIdentifications = builder.build();
  }

  static ItemIdentification get(int id) {
    Map<Integer, ItemIdentification> lookup = itemIdentifications;
    if (lookup == null) {
      throw new IllegalStateException(
          "ItemIdentification isn't loaded; call ItemIdentification.load(Gson) first");
    }

    return lookup.get(id);
  }

  Data getData() {
    return DATA.get(this);
  }

  Type getType() {
    return getData().getType();
  }

  String getMedName() {
    return getData().getMedName();
  }

  String getShortName() {
    return getData().getShortName();
  }

  int[] getItemIds() {
    return getData().getItemIds();
  }

  @AllArgsConstructor
  public enum Type {
    SEED_HERB(ItemIdentificationConfig::showHerbSeeds),
    SEED_BERRY(ItemIdentificationConfig::showBerrySeeds),
    SEED_ALLOTMENT(ItemIdentificationConfig::showAllotmentSeeds),
    SEED_SPECIAL(ItemIdentificationConfig::showSpecialSeeds),
    SEED_TREE(ItemIdentificationConfig::showTreeSeeds),
    SEED_FRUIT_TREE(ItemIdentificationConfig::showFruitTreeSeeds),
    SEED_FLOWER(ItemIdentificationConfig::showFlowerSeeds),
    HOPS_SEED(ItemIdentificationConfig::showHopsSeeds),
    SACK(ItemIdentificationConfig::showSacks),
    HERB(ItemIdentificationConfig::showHerbs),
    LOGS(ItemIdentificationConfig::showLogs),
    LOGS_PYRE(ItemIdentificationConfig::showPyreLogs),
    PLANK(ItemIdentificationConfig::showPlanks),
    SAPLING(ItemIdentificationConfig::showSaplings),
    COMPOST(ItemIdentificationConfig::showComposts),
    ORE(ItemIdentificationConfig::showOres),
    BAR(ItemIdentificationConfig::showBars),
    GEM(ItemIdentificationConfig::showGems),
    POTION(ItemIdentificationConfig::showPotions),
    IMPLING_JAR(ItemIdentificationConfig::showImplingJars),
    TABLET(ItemIdentificationConfig::showTablets),
    SCROLL(ItemIdentificationConfig::showTeleportScrolls),
    JEWELLERY(ItemIdentificationConfig::showJewellery),
    JEWELLERY_ENCHANTED(ItemIdentificationConfig::showEnchantedJewellery);

    final Predicate<ItemIdentificationConfig> enabled;
  }
}
