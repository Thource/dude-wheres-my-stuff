package dev.thource.runelite.dudewheresmystuff;

import com.google.common.collect.ImmutableMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.ItemID;

/** Seed serves as a way to match seed names to their item ids. */
@RequiredArgsConstructor
@Getter
public enum Seed {
  // Allotment seeds
  POTATO(ItemID.POTATO_SEED, "Potato seed"),
  ONION(ItemID.ONION_SEED, "Onion seed"),
  CABBAGE(ItemID.CABBAGE_SEED, "Cabbage seed"),
  TOMATO(ItemID.TOMATO_SEED, "Tomato seed"),
  SWEETCORN(ItemID.SWEETCORN_SEED, "Sweetcorn seed"),
  STRAWBERRY(ItemID.STRAWBERRY_SEED, "Strawberry seed"),
  WATERMELON(ItemID.WATERMELON_SEED, "Watermelon seed"),
  SNAPE_GRASS(ItemID.SNAPE_GRASS_SEED, "Snape grass seed"),

  // Flower seeds
  MARIGOLD(ItemID.MARIGOLD_SEED, "Marigold seed"),
  ROSEMARY(ItemID.ROSEMARY_SEED, "Rosemary seed"),
  NASTURTIUM(ItemID.NASTURTIUM_SEED, "Nasturtium seed"),
  WOAD(ItemID.WOAD_SEED, "Woad seed"),
  LIMPWURT(ItemID.LIMPWURT_SEED, "Limpwurt seed"),
  WHITE_LILY(ItemID.WHITE_LILY_SEED, "White lily seed"),

  // Herb seeds
  GUAM(ItemID.GUAM_SEED, "Guam seed"),
  MARRENTILL(ItemID.MARRENTILL_SEED, "Marrentill seed"),
  TARROMIN(ItemID.TARROMIN_SEED, "Tarromin seed"),
  HARRALANDER(ItemID.HARRALANDER_SEED, "Harralander seed"),
  GOUT_TUBER(ItemID.VILLAGE_RARE_TUBER, "Gout tuber"),
  RANARR(ItemID.RANARR_SEED, "Ranarr seed"),
  TOADFLAX(ItemID.TOADFLAX_SEED, "Toadflax seed"),
  IRIT(ItemID.IRIT_SEED, "Irit seed"),
  AVANTOE(ItemID.AVANTOE_SEED, "Avantoe seed"),
  KWUARM(ItemID.KWUARM_SEED, "Kwuarm seed"),
  SNAPDRAGON(ItemID.SNAPDRAGON_SEED, "Snapdragon seed"),
  CADANTINE(ItemID.CADANTINE_SEED, "Cadantine seed"),
  LANTADYME(ItemID.LANTADYME_SEED, "Lantadyme seed"),
  DWARF_WEED(ItemID.DWARF_WEED_SEED, "Dwarf weed seed"),
  TORSTOL(ItemID.TORSTOL_SEED, "Torstol seed"),

  // Hops seeds
  BARLEY(ItemID.BARLEY_SEED, "Barley seed"),
  HAMMERSTONE(ItemID.HAMMERSTONE_HOP_SEED, "Hammerstone seed"),
  ASGARNIAN(ItemID.ASGARNIAN_HOP_SEED, "Asgarnian seed"),
  JUTE(ItemID.JUTE_SEED, "Jute seed"),
  YANILLIAN(ItemID.YANILLIAN_HOP_SEED, "Yanillian seed"),
  KRANDORIAN(ItemID.KRANDORIAN_HOP_SEED, "Krandorian seed"),
  WILDBLOOD(ItemID.WILDBLOOD_HOP_SEED, "Wildblood seed"),

  // Bush seeds
  REDBERRY(ItemID.REDBERRY_BUSH_SEED, "Redberry seed"),
  CADAVABERRY(ItemID.CADAVABERRY_BUSH_SEED, "Cadavaberry seed"),
  DWELLBERRY(ItemID.DWELLBERRY_BUSH_SEED, "Dwellberry seed"),
  JANGERBERRY(ItemID.JANGERBERRY_BUSH_SEED, "Jangerberry seed"),
  WHITEBERRY(ItemID.WHITEBERRY_BUSH_SEED, "Whiteberry seed"),
  POISON_IVY(ItemID.POISONIVY_BUSH_SEED, "Poison ivy seed"),

  // Tree seeds
  ACORN(ItemID.ACORN, "Acorn"),
  WILLOW(ItemID.WILLOW_SEED, "Willow seed"),
  MAPLE(ItemID.MAPLE_SEED, "Maple seed"),
  YEW(ItemID.YEW_SEED, "Yew seed"),
  MAGIC(ItemID.MAGIC_TREE_SEED, "Magic seed"),

  // Fruit tree seeds
  APPLE(ItemID.APPLE_TREE_SEED, "Apple tree seed"),
  BANANA(ItemID.BANANA_TREE_SEED, "Banana tree seed"),
  ORANGE(ItemID.ORANGE_TREE_SEED, "Orange tree seed"),
  CURRY(ItemID.CURRY_TREE_SEED, "Curry tree seed"),
  PINEAPPLE(ItemID.PINEAPPLE_TREE_SEED, "Pineapple seed"),
  PAPAYA(ItemID.PAPAYA_TREE_SEED, "Papaya tree seed"),
  PALM(ItemID.PALM_TREE_SEED, "Palm tree seed"),
  DRAGONFRUIT(ItemID.DRAGONFRUIT_TREE_SEED, "Dragonfruit tree seed"),

  // Special seeds
  SEAWEED(ItemID.SEAWEED_SEED, "Seaweed spore"),
  GRAPE(ItemID.GRAPE_SEED, "Grape seed"),
  MUSHROOM(ItemID.MUSHROOM_SEED, "Mushroom spore"),
  BELLADONNA(ItemID.BELLADONNA_SEED, "Belladonna seed"),
  HESPORI(ItemID.HESPORI_SEED, "Hespori seed"),

  // Anima seeds
  KRONOS(ItemID.KRONOS_SEED, "Kronos seed"),
  IASOR(ItemID.IASOR_SEED, "Iasor seed"),
  ATTAS(ItemID.ATTAS_SEED, "Attas seed"),

  // Special tree seeds
  TEAK(ItemID.TEAK_SEED, "Teak seed"),
  MAHOGANY(ItemID.MAHOGANY_SEED, "Mahogany seed"),
  CALQUAT(ItemID.CALQUAT_TREE_SEED, "Calquat tree seed"),
  CRYSTAL(ItemID.CRYSTAL_TREE_SEED, "Crystal acorn"),
  SPIRIT(ItemID.SPIRIT_TREE_SEED, "Spirit seed"),
  CELASTRUS(ItemID.CELASTRUS_TREE_SEED, "Celastrus seed"),
  REDWOOD(ItemID.REDWOOD_TREE_SEED, "Redwood tree seed"),

  // Cactus seeds
  CACTUS(ItemID.CACTUS_SEED, "Cactus seed"),
  POTATO_CACTUS(ItemID.POTATO_CACTUS_SEED, "Potato cactus seed");

  private static final Map<String, Seed> FROM_NAME;

  static {
    ImmutableMap.Builder<String, Seed> nameMapBuilder = new ImmutableMap.Builder<>();
    for (Seed seed : Seed.values()) {
      nameMapBuilder.put(seed.getName().toLowerCase(Locale.ROOT), seed);
    }
    FROM_NAME = nameMapBuilder.build();
  }

  private final int itemId;
  private final String name;

  public static Optional<Seed> findByName(String name) {
    return Optional.ofNullable(FROM_NAME.get(name.toLowerCase(Locale.ROOT)));
  }
}
