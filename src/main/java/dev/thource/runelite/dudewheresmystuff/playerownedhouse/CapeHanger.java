package dev.thource.runelite.dudewheresmystuff.playerownedhouse;

import com.google.common.collect.ImmutableMap;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.Map;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;

/** CapeHanger is responsible for tracking which cape the player has on their POH cape hanger. */
public class CapeHanger extends PlayerOwnedHouseStorage {

  private static final Map<Integer, Integer[]> objectIdItemIdMap;

  static {
    ImmutableMap.Builder<Integer, Integer[]> builder = new ImmutableMap.Builder<>();

    builder.put(ObjectID.POH_MOUNTED_INFERNAL_CAPE, new Integer[]{ItemID.INFERNAL_CAPE});
    builder.put(ObjectID.POH_MOUNTED_MAX_CAPE_INFERNALCAPE,
        new Integer[]{ItemID.SKILLCAPE_MAX_INFERNALCAPE,
            ItemID.SKILLCAPE_MAX_HOOD_INFERNALCAPE});
    builder.put(ObjectID.POH_MOUNTED_DIARY_CAPE,
        new Integer[]{ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_HOOD});
    builder.put(ObjectID.POH_MOUNTED_DIARY_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_AD_TRIMMED, ItemID.SKILLCAPE_AD_HOOD});
    builder.put(ObjectID.POH_MOUNTED_FIRE_CAPE, new Integer[]{ItemID.TZHAAR_CAPE_FIRE});
    builder.put(ObjectID.POH_MOUNTED_MAX_CAPE, new Integer[]{ItemID.SKILLCAPE_MAX,
        ItemID.SKILLCAPE_MAX_HOOD});
    builder.put(ObjectID.POH_MOUNTED_MAX_CAPE_FIRE,
        new Integer[]{ItemID.SKILLCAPE_MAX_FIRECAPE, ItemID.SKILLCAPE_MAX_HOOD_FIRECAPE});
    builder.put(ObjectID.POH_MOUNTED_MAX_CAPE_SARADOMIN,
        new Integer[]{ItemID.SKILLCAPE_MAX_SARADOMIN, ItemID.SKILLCAPE_MAX_HOOD_SARADOMIN});
    builder.put(ObjectID.POH_MOUNTED_MAX_CAPE_ZAMORAK,
        new Integer[]{ItemID.SKILLCAPE_MAX_ZAMORAK, ItemID.SKILLCAPE_MAX_HOOD_ZAMORAK});
    builder.put(ObjectID.POH_MOUNTED_MAX_CAPE_GUTHIX,
        new Integer[]{ItemID.SKILLCAPE_MAX_GUTHIX, ItemID.SKILLCAPE_MAX_HOOD_GUTHIX});
    builder.put(ObjectID.POH_MOUNTED_MAX_CAPE_AVA,
        new Integer[]{ItemID.SKILLCAPE_MAX_ANMA, ItemID.SKILLCAPE_MAX_HOOD_ANMA});
    builder.put(
        ObjectID.POH_MOUNTED_MUSIC_CAPE, new Integer[]{ItemID.MUSIC_CAPE, ItemID.MUSIC_CAPE_HOOD});
    builder.put(ObjectID.POH_MOUNTED_MUSIC_CAPE_TRIM,
        new Integer[]{ItemID.MUSIC_CAPE_TRIMMED, ItemID.MUSIC_CAPE_HOOD});
    builder.put(ObjectID.POH_MOUNTED_QUEST_CAPE,
        new Integer[]{ItemID.SKILLCAPE_QP, ItemID.SKILLCAPE_QP_HOOD});
    builder.put(ObjectID.POH_MOUNTED_QUEST_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_QP_TRIMMED, ItemID.SKILLCAPE_QP_HOOD});
    builder.put(ObjectID.POH_MOUNTED_AGILITY_CAPE,
        new Integer[]{ItemID.SKILLCAPE_AGILITY, ItemID.SKILLCAPE_AGILITY_HOOD});
    builder.put(ObjectID.POH_MOUNTED_AGILITY_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_AGILITY_TRIMMED, ItemID.SKILLCAPE_AGILITY_HOOD});
    builder.put(ObjectID.POH_MOUNTED_ATTACK_CAPE,
        new Integer[]{ItemID.SKILLCAPE_ATTACK, ItemID.SKILLCAPE_ATTACK_HOOD});
    builder.put(ObjectID.POH_MOUNTED_ATTACK_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_ATTACK_TRIMMED, ItemID.SKILLCAPE_ATTACK_HOOD});
    builder.put(ObjectID.POH_MOUNTED_CONSTRUCTION_CAPE,
        new Integer[]{ItemID.SKILLCAPE_CONSTRUCTION, ItemID.SKILLCAPE_CONSTRUCTION_HOOD});
    builder.put(ObjectID.POH_MOUNTED_CONSTRUCTION_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_CONSTRUCTION_TRIMMED, ItemID.SKILLCAPE_CONSTRUCTION_HOOD});
    builder.put(ObjectID.POH_MOUNTED_COOKING_CAPE,
        new Integer[]{ItemID.SKILLCAPE_COOKING, ItemID.SKILLCAPE_COOKING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_COOKING_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_COOKING_TRIMMED, ItemID.SKILLCAPE_COOKING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_CRAFTING_CAPE,
        new Integer[]{ItemID.SKILLCAPE_CRAFTING, ItemID.SKILLCAPE_CRAFTING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_CRAFTING_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_CRAFTING_TRIMMED, ItemID.SKILLCAPE_CRAFTING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_DEFENCE_CAPE,
        new Integer[]{ItemID.SKILLCAPE_DEFENCE, ItemID.SKILLCAPE_DEFENCE_HOOD});
    builder.put(ObjectID.POH_MOUNTED_DEFENCE_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_DEFENCE_TRIMMED, ItemID.SKILLCAPE_DEFENCE_HOOD});
    builder.put(ObjectID.POH_MOUNTED_FARMING_CAPE,
        new Integer[]{ItemID.SKILLCAPE_FARMING, ItemID.SKILLCAPE_FARMING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_FARMING_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_FARMING_TRIMMED, ItemID.SKILLCAPE_FARMING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_FIREMAKING_CAPE,
        new Integer[]{ItemID.SKILLCAPE_FIREMAKING, ItemID.SKILLCAPE_FIREMAKING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_FIREMAKING_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_FIREMAKING_TRIMMED, ItemID.SKILLCAPE_FIREMAKING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_FISHING_CAPE,
        new Integer[]{ItemID.SKILLCAPE_FISHING, ItemID.SKILLCAPE_FISHING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_FISHING_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_FISHING_TRIMMED, ItemID.SKILLCAPE_FISHING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_FLETCHING_CAPE,
        new Integer[]{ItemID.SKILLCAPE_FLETCHING, ItemID.SKILLCAPE_FLETCHING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_FLETCHING_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_FLETCHING_TRIMMED, ItemID.SKILLCAPE_FLETCHING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_HERBLORE_CAPE,
        new Integer[]{ItemID.SKILLCAPE_HERBLORE, ItemID.SKILLCAPE_HERBLORE_HOOD});
    builder.put(ObjectID.POH_MOUNTED_HERBLORE_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_HERBLORE_TRIMMED, ItemID.SKILLCAPE_HERBLORE_HOOD});
    builder.put(ObjectID.POH_MOUNTED_HITPOINTS_CAPE,
        new Integer[]{ItemID.SKILLCAPE_HITPOINTS, ItemID.SKILLCAPE_HITPOINTS_HOOD});
    builder.put(ObjectID.POH_MOUNTED_HITPOINTS_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_HITPOINTS_TRIMMED, ItemID.SKILLCAPE_HITPOINTS_HOOD});
    builder.put(ObjectID.POH_MOUNTED_HUNTING_CAPE,
        new Integer[]{ItemID.SKILLCAPE_HUNTING, ItemID.SKILLCAPE_HUNTING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_HUNTING_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_HUNTING_TRIMMED, ItemID.SKILLCAPE_HUNTING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_MAGIC_CAPE, new Integer[]{ItemID.SKILLCAPE_MAGIC,
        ItemID.SKILLCAPE_MAGIC_HOOD});
    builder.put(ObjectID.POH_MOUNTED_MAGIC_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_MAGIC_TRIMMED, ItemID.SKILLCAPE_MAGIC_HOOD});
    builder.put(ObjectID.POH_MOUNTED_MINING_CAPE,
        new Integer[]{ItemID.SKILLCAPE_MINING, ItemID.SKILLCAPE_MINING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_MINING_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_MINING_TRIMMED, ItemID.SKILLCAPE_MINING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_PRAYER_CAPE,
        new Integer[]{ItemID.SKILLCAPE_PRAYER, ItemID.SKILLCAPE_PRAYER_HOOD});
    builder.put(ObjectID.POH_MOUNTED_PRAYER_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_PRAYER_TRIMMED, ItemID.SKILLCAPE_PRAYER_HOOD});
    builder.put(ObjectID.POH_MOUNTED_RANGED_CAPE,
        new Integer[]{ItemID.SKILLCAPE_RANGING, ItemID.SKILLCAPE_RANGING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_RANGED_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_RANGING_TRIMMED, ItemID.SKILLCAPE_RANGING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_RUNECRAFTING_CAPE,
        new Integer[]{ItemID.SKILLCAPE_RUNECRAFTING, ItemID.SKILLCAPE_RUNECRAFTING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_RUNECRAFTING_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_RUNECRAFTING_TRIMMED, ItemID.SKILLCAPE_RUNECRAFTING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_SLAYER_CAPE,
        new Integer[]{ItemID.SKILLCAPE_SLAYER, ItemID.SKILLCAPE_SLAYER_HOOD});
    builder.put(ObjectID.POH_MOUNTED_SLAYER_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_SLAYER_TRIMMED, ItemID.SKILLCAPE_SLAYER_HOOD});
    builder.put(ObjectID.POH_MOUNTED_SMITHING_CAPE,
        new Integer[]{ItemID.SKILLCAPE_SMITHING, ItemID.SKILLCAPE_SMITHING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_SMITHING_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_SMITHING_TRIMMED, ItemID.SKILLCAPE_SMITHING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_STRENGTH_CAPE,
        new Integer[]{ItemID.SKILLCAPE_STRENGTH, ItemID.SKILLCAPE_STRENGTH_HOOD});
    builder.put(ObjectID.POH_MOUNTED_STRENGTH_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_STRENGTH_TRIMMED, ItemID.SKILLCAPE_STRENGTH_HOOD});
    builder.put(ObjectID.POH_MOUNTED_THIEVING_CAPE,
        new Integer[]{ItemID.SKILLCAPE_THIEVING, ItemID.SKILLCAPE_THIEVING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_THIEVING_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_THIEVING_TRIMMED, ItemID.SKILLCAPE_THIEVING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_WOODCUTTING_CAPE,
        new Integer[]{ItemID.SKILLCAPE_WOODCUTTING, ItemID.SKILLCAPE_WOODCUTTING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_WOODCUTTING_CAPE_TRIM,
        new Integer[]{ItemID.SKILLCAPE_WOODCUTTING_TRIMMED, ItemID.SKILLCAPE_WOODCUTTING_HOOD});
    builder.put(ObjectID.POH_MOUNTED_MAX_CAPE_ARDY,
        new Integer[]{ItemID.SKILLCAPE_MAX_ARDY, ItemID.SKILLCAPE_MAX_HOOD_ARDY});
    builder.put(ObjectID.POH_MOUNTED_CHAMPIONSCAPE, new Integer[]{ItemID.CHAMPION_CAPE});
    builder.put(ObjectID.POH_MOUNTED_MAX_CAPE_ASSEMBLER_MASORI,
        new Integer[]{ItemID.SKILLCAPE_MAX_ASSEMBLER_MASORI,
            ItemID.SKILLCAPE_MAX_HOOD_ASSEMBLER_MASORI});
    builder.put(ObjectID.POH_MOUNTED_MAX_CAPE_SARADOMIN2,
        new Integer[]{ItemID.SKILLCAPE_MAX_SARADOMIN2, ItemID.SKILLCAPE_MAX_HOOD_SARADOMIN2});
    builder.put(ObjectID.POH_MOUNTED_MAX_CAPE_ZAMORAK2,
        new Integer[]{ItemID.SKILLCAPE_MAX_ZAMORAK2, ItemID.SKILLCAPE_MAX_HOOD_ZAMORAK2});
    builder.put(ObjectID.POH_MOUNTED_MAX_CAPE_GUTHIX2,
        new Integer[]{ItemID.SKILLCAPE_MAX_GUTHIX2, ItemID.SKILLCAPE_MAX_HOOD_GUTHIX2});
    builder.put(ObjectID.POH_MOUNTED_MAX_CAPE_ASSEMBLER,
        new Integer[]{ItemID.SKILLCAPE_MAX_ASSEMBLER, ItemID.SKILLCAPE_MAX_HOOD_ASSEMBLER});
    builder.put(ObjectID.POH_MOUNTED_MYTHICALCAPE, new Integer[]{ItemID.MYTHICAL_CAPE});
    builder.put(ObjectID.POH_MOUNTED_MAX_CAPE_MYTHICAL,
        new Integer[]{ItemID.SKILLCAPE_MAX_MYTHICAL, ItemID.SKILLCAPE_MAX_HOOD_MYTHICAL});
    builder.put(ObjectID.POH_MOUNTED_MAX_CAPE_DIZANAS,
        new Integer[]{ItemID.SKILLCAPE_MAX_DIZANAS, ItemID.SKILLCAPE_MAX_HOOD_DIZANAS});
    builder.put(ObjectID.POH_MOUNTED_COX_T1, new Integer[]{ItemID.COX_CHALLENGE_CAPE_T1});
    builder.put(ObjectID.POH_MOUNTED_COX_T2, new Integer[]{ItemID.COX_CHALLENGE_CAPE_T2});
    builder.put(ObjectID.POH_MOUNTED_COX_T3, new Integer[]{ItemID.COX_CHALLENGE_CAPE_T3});
    builder.put(ObjectID.POH_MOUNTED_COX_T4, new Integer[]{ItemID.COX_CHALLENGE_CAPE_T4});
    builder.put(ObjectID.POH_MOUNTED_COX_T5, new Integer[]{ItemID.COX_CHALLENGE_CAPE_T5});

    objectIdItemIdMap = builder.build();
  }

  protected CapeHanger(DudeWheresMyStuffPlugin plugin) {
    super(PlayerOwnedHouseStorageType.CAPE_HANGER, plugin);
  }

  @Override
  public boolean onGameObjectSpawned(GameObjectSpawned gameObjectSpawned) {
    // It is not possible to check if the player is in their own POH,
    //   so going to someone else's POH can reset your cape hanger storage.

    int id = gameObjectSpawned.getGameObject().getId();
    Integer[] itemIds = objectIdItemIdMap.get(id);

    if (id == 29166 || itemIds != null) {
      updateLastUpdated();
      items.clear();

      if (itemIds != null) {
        for (Integer itemId : itemIds) {
          items.add(new ItemStack(itemId, 1, plugin));
        }
      }

      return true;
    }

    return false;
  }
}
