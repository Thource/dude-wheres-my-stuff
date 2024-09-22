package dev.thource.runelite.dudewheresmystuff.playerownedhouse;

import com.google.common.collect.ImmutableMap;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.events.GameObjectSpawned;

/** CapeHanger is responsible for tracking which cape the player has on their POH cape hanger. */
public class CapeHanger extends PlayerOwnedHouseStorage {

  private static final Map<Integer, Integer[]> objectIdItemIdMap;

  static {
    ImmutableMap.Builder<Integer, Integer[]> builder = new ImmutableMap.Builder<>();

    builder.put(ObjectID.MOUNTED_INFERNAL_CAPE, new Integer[]{ItemID.INFERNAL_CAPE});
    builder.put(ObjectID.MOUNTED_MAX_CAPE,
        new Integer[]{ItemID.INFERNAL_MAX_CAPE, ItemID.INFERNAL_MAX_HOOD});
    builder.put(ObjectID.MOUNTED_ACHIEVEMENT_DIARY_CAPE,
        new Integer[]{ItemID.ACHIEVEMENT_DIARY_CAPE, ItemID.ACHIEVEMENT_DIARY_HOOD});
    builder.put(ObjectID.MOUNTED_ACHIEVEMENT_DIARY_CAPE_29168,
        new Integer[]{ItemID.ACHIEVEMENT_DIARY_CAPE_T, ItemID.ACHIEVEMENT_DIARY_HOOD});
    builder.put(ObjectID.MOUNTED_FIRE_CAPE, new Integer[]{ItemID.FIRE_CAPE});
    builder.put(ObjectID.MOUNTED_MAX_CAPE_29170, new Integer[]{ItemID.MAX_CAPE, ItemID.MAX_HOOD});
    builder.put(ObjectID.MOUNTED_MAX_CAPE_29171,
        new Integer[]{ItemID.FIRE_MAX_CAPE, ItemID.FIRE_MAX_HOOD});
    builder.put(ObjectID.MOUNTED_MAX_CAPE_29172,
        new Integer[]{ItemID.SARADOMIN_MAX_CAPE, ItemID.SARADOMIN_MAX_HOOD});
    builder.put(ObjectID.MOUNTED_MAX_CAPE_29173,
        new Integer[]{ItemID.ZAMORAK_MAX_CAPE, ItemID.ZAMORAK_MAX_HOOD});
    builder.put(ObjectID.MOUNTED_MAX_CAPE_29174,
        new Integer[]{ItemID.GUTHIX_MAX_CAPE, ItemID.GUTHIX_MAX_HOOD});
    builder.put(ObjectID.MOUNTED_MAX_CAPE_29175,
        new Integer[]{ItemID.ACCUMULATOR_MAX_CAPE, ItemID.ACCUMULATOR_MAX_HOOD});
    builder.put(ObjectID.MOUNTED_MUSIC_CAPE, new Integer[]{ItemID.MUSIC_CAPE, ItemID.MUSIC_HOOD});
    builder.put(ObjectID.MOUNTED_MUSIC_CAPE_29177,
        new Integer[]{ItemID.MUSIC_CAPET, ItemID.MUSIC_HOOD});
    builder.put(ObjectID.MOUNTED_QUEST_CAPE,
        new Integer[]{ItemID.QUEST_POINT_CAPE, ItemID.QUEST_POINT_HOOD});
    builder.put(ObjectID.MOUNTED_QUEST_CAPE_29179,
        new Integer[]{ItemID.QUEST_POINT_CAPE_T, ItemID.QUEST_POINT_HOOD});
    builder.put(ObjectID.MOUNTED_AGILITY_CAPE,
        new Integer[]{ItemID.AGILITY_CAPE, ItemID.AGILITY_HOOD});
    builder.put(ObjectID.MOUNTED_AGILITY_CAPE_T,
        new Integer[]{ItemID.AGILITY_CAPET, ItemID.AGILITY_HOOD});
    builder.put(ObjectID.MOUNTED_ATTACK_CAPE,
        new Integer[]{ItemID.ATTACK_CAPE, ItemID.ATTACK_HOOD});
    builder.put(ObjectID.MOUNTED_ATTACK_CAPE_T,
        new Integer[]{ItemID.ATTACK_CAPET, ItemID.ATTACK_HOOD});
    builder.put(ObjectID.MOUNTED_CONSTRUCTION_CAPE,
        new Integer[]{ItemID.CONSTRUCT_CAPE, ItemID.CONSTRUCT_HOOD});
    builder.put(ObjectID.MOUNTED_CONSTRUCTION_CAPE_T,
        new Integer[]{ItemID.CONSTRUCT_CAPET, ItemID.CONSTRUCT_HOOD});
    builder.put(ObjectID.MOUNTED_COOKING_CAPE,
        new Integer[]{ItemID.COOKING_CAPE, ItemID.COOKING_HOOD});
    builder.put(ObjectID.MOUNTED_COOKING_CAPE_T,
        new Integer[]{ItemID.COOKING_CAPET, ItemID.COOKING_HOOD});
    builder.put(ObjectID.MOUNTED_CRAFTING_CAPE,
        new Integer[]{ItemID.CRAFTING_CAPE, ItemID.CRAFTING_HOOD});
    builder.put(ObjectID.MOUNTED_CRAFTING_CAPE_T,
        new Integer[]{ItemID.CRAFTING_CAPET, ItemID.CRAFTING_HOOD});
    builder.put(ObjectID.MOUNTED_DEFENCE_CAPE,
        new Integer[]{ItemID.DEFENCE_CAPE, ItemID.DEFENCE_HOOD});
    builder.put(ObjectID.MOUNTED_DEFENCE_CAPE_T,
        new Integer[]{ItemID.DEFENCE_CAPET, ItemID.DEFENCE_HOOD});
    builder.put(ObjectID.MOUNTED_FARMING_CAPE,
        new Integer[]{ItemID.FARMING_CAPE, ItemID.FARMING_HOOD});
    builder.put(ObjectID.MOUNTED_FARMING_CAPE_T,
        new Integer[]{ItemID.FARMING_CAPET, ItemID.FARMING_HOOD});
    builder.put(ObjectID.MOUNTED_FIREMAKING_CAPE,
        new Integer[]{ItemID.FIREMAKING_CAPE, ItemID.FIREMAKING_HOOD});
    builder.put(ObjectID.MOUNTED_FIREMAKING_CAPE_T,
        new Integer[]{ItemID.FIREMAKING_CAPET, ItemID.FIREMAKING_HOOD});
    builder.put(ObjectID.MOUNTED_FISHING_CAPE,
        new Integer[]{ItemID.FISHING_CAPE, ItemID.FISHING_HOOD});
    builder.put(ObjectID.MOUNTED_FISHING_CAPE_T,
        new Integer[]{ItemID.FISHING_CAPET, ItemID.FISHING_HOOD});
    builder.put(ObjectID.MOUNTED_FLETCHING_CAPE,
        new Integer[]{ItemID.FLETCHING_CAPE, ItemID.FLETCHING_HOOD});
    builder.put(ObjectID.MOUNTED_FLETCHING_CAPE_T,
        new Integer[]{ItemID.FLETCHING_CAPET, ItemID.FLETCHING_HOOD});
    builder.put(ObjectID.MOUNTED_HERBLORE_CAPE,
        new Integer[]{ItemID.HERBLORE_CAPE, ItemID.HERBLORE_HOOD});
    builder.put(ObjectID.MOUNTED_HERBLORE_CAPE_T,
        new Integer[]{ItemID.HERBLORE_CAPET, ItemID.HERBLORE_HOOD});
    builder.put(ObjectID.MOUNTED_HITPOINTS_CAPE,
        new Integer[]{ItemID.HITPOINTS_CAPE, ItemID.HITPOINTS_HOOD});
    builder.put(ObjectID.MOUNTED_HITPOINTS_CAPE_T,
        new Integer[]{ItemID.HITPOINTS_CAPET, ItemID.HITPOINTS_HOOD});
    builder.put(ObjectID.MOUNTED_HUNTING_CAPE,
        new Integer[]{ItemID.HUNTER_CAPE, ItemID.HUNTER_HOOD});
    builder.put(ObjectID.MOUNTED_HUNTING_CAPE_T,
        new Integer[]{ItemID.HUNTER_CAPET, ItemID.HUNTER_HOOD});
    builder.put(ObjectID.MOUNTED_MAGIC_CAPE, new Integer[]{ItemID.MAGIC_CAPE, ItemID.MAGIC_HOOD});
    builder.put(ObjectID.MOUNTED_MAGIC_CAPE_T,
        new Integer[]{ItemID.MAGIC_CAPET, ItemID.MAGIC_HOOD});
    builder.put(ObjectID.MOUNTED_MINING_CAPE,
        new Integer[]{ItemID.MINING_CAPE, ItemID.MINING_HOOD});
    builder.put(ObjectID.MOUNTED_MINING_CAPE_T,
        new Integer[]{ItemID.MINING_CAPET, ItemID.MINING_HOOD});
    builder.put(ObjectID.MOUNTED_PRAYER_CAPE,
        new Integer[]{ItemID.PRAYER_CAPE, ItemID.PRAYER_HOOD});
    builder.put(ObjectID.MOUNTED_PRAYER_CAPE_T,
        new Integer[]{ItemID.PRAYER_CAPET, ItemID.PRAYER_HOOD});
    builder.put(ObjectID.MOUNTED_RANGED_CAPE,
        new Integer[]{ItemID.RANGING_CAPE, ItemID.RANGING_HOOD});
    builder.put(ObjectID.MOUNTED_RANGED_CAPE_T,
        new Integer[]{ItemID.RANGING_CAPET, ItemID.RANGING_HOOD});
    builder.put(ObjectID.MOUNTED_RUNECRAFTING_CAPE,
        new Integer[]{ItemID.RUNECRAFT_CAPE, ItemID.RUNECRAFT_HOOD});
    builder.put(ObjectID.MOUNTED_RUNECRAFTING_CAPE_T,
        new Integer[]{ItemID.RUNECRAFT_CAPET, ItemID.RUNECRAFT_HOOD});
    builder.put(ObjectID.MOUNTED_SLAYER_CAPE,
        new Integer[]{ItemID.SLAYER_CAPE, ItemID.SLAYER_HOOD});
    builder.put(ObjectID.MOUNTED_SLAYER_CAPE_T,
        new Integer[]{ItemID.SLAYER_CAPET, ItemID.SLAYER_HOOD});
    builder.put(ObjectID.MOUNTED_SMITHING_CAPE,
        new Integer[]{ItemID.SMITHING_CAPE, ItemID.SMITHING_HOOD});
    builder.put(ObjectID.MOUNTED_SMITHING_CAPE_T,
        new Integer[]{ItemID.SMITHING_CAPET, ItemID.SMITHING_HOOD});
    builder.put(ObjectID.MOUNTED_STRENGTH_CAPE,
        new Integer[]{ItemID.STRENGTH_CAPE, ItemID.STRENGTH_HOOD});
    builder.put(ObjectID.MOUNTED_STRENGTH_CAPE_T,
        new Integer[]{ItemID.STRENGTH_CAPET, ItemID.STRENGTH_HOOD});
    builder.put(ObjectID.MOUNTED_THIEVING_CAPE,
        new Integer[]{ItemID.THIEVING_CAPE, ItemID.THIEVING_HOOD});
    builder.put(ObjectID.MOUNTED_THIEVING_CAPE_T,
        new Integer[]{ItemID.THIEVING_CAPET, ItemID.THIEVING_HOOD});
    builder.put(ObjectID.MOUNTED_WOODCUTTING_CAPE,
        new Integer[]{ItemID.WOODCUTTING_CAPE, ItemID.WOODCUTTING_HOOD});
    builder.put(ObjectID.MOUNTED_WOODCUTTING_CAPE_T,
        new Integer[]{ItemID.WOODCUT_CAPET, ItemID.WOODCUTTING_HOOD});
    builder.put(ObjectID.MOUNTED_MAX_CAPE_29625,
        new Integer[]{ItemID.ARDOUGNE_MAX_CAPE, ItemID.ARDOUGNE_MAX_HOOD});
    builder.put(ObjectID.MOUNTED_CHAMPIONS_CAPE, new Integer[]{ItemID.CHAMPIONS_CAPE});
    builder.put(ObjectID.MOUNTED_MAX_CAPE_31925,
        new Integer[]{ItemID.MASORI_ASSEMBLER_MAX_CAPE, ItemID.MASORI_ASSEMBLER_MAX_HOOD});
    builder.put(ObjectID.MOUNTED_MAX_CAPE_31979,
        new Integer[]{ItemID.IMBUED_SARADOMIN_MAX_CAPE, ItemID.IMBUED_SARADOMIN_MAX_HOOD});
    builder.put(ObjectID.MOUNTED_MAX_CAPE_31980,
        new Integer[]{ItemID.IMBUED_ZAMORAK_MAX_CAPE, ItemID.IMBUED_ZAMORAK_MAX_HOOD});
    builder.put(ObjectID.MOUNTED_MAX_CAPE_31981,
        new Integer[]{ItemID.IMBUED_GUTHIX_MAX_CAPE, ItemID.IMBUED_GUTHIX_MAX_HOOD});
    builder.put(ObjectID.MOUNTED_MAX_CAPE_31982,
        new Integer[]{ItemID.ASSEMBLER_MAX_CAPE, ItemID.ASSEMBLER_MAX_HOOD});
    builder.put(ObjectID.MOUNTED_MYTHICAL_CAPE, new Integer[]{ItemID.MYTHICAL_CAPE_22114});
    builder.put(ObjectID.MOUNTED_MAX_CAPE_39621,
        new Integer[]{ItemID.MYTHICAL_MAX_CAPE, ItemID.MYTHICAL_MAX_HOOD});
    builder.put(ObjectID.MOUNTED_DIZANAS_MAX_CAPE,
        new Integer[]{ItemID.DIZANAS_MAX_CAPE, ItemID.DIZANAS_MAX_HOOD});
    builder.put(ObjectID.MOUNTED_XERICS_GUARD, new Integer[]{ItemID.XERICS_GUARD});
    builder.put(ObjectID.MOUNTED_XERICS_WARRIOR, new Integer[]{ItemID.XERICS_WARRIOR});
    builder.put(ObjectID.MOUNTED_XERICS_SENTINEL, new Integer[]{ItemID.XERICS_SENTINEL});
    builder.put(ObjectID.MOUNTED_XERICS_GENERAL, new Integer[]{ItemID.XERICS_GENERAL});
    builder.put(ObjectID.MOUNTED_XERICS_CHAMPION, new Integer[]{ItemID.XERICS_CHAMPION});

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
