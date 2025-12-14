package dev.thource.runelite.dudewheresmystuff.death;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@Getter
enum RemoteDeathpileAreas {
  BOSS_ARAXXOR(new WorldArea(3616, 9797, 35, 40, 0), new WorldArea(3657, 9812, 5, 7, 0)),
  BOSS_CERBERUS(List.of(4883, 5140, 5395), 0, new WorldArea(1307, 1247, 7, 6, 0)),
  BOSS_COLOSSEUM(new WorldArea(1805, 3087, 40, 40, 0), new WorldArea(1800, 9504, 5, 5, 0)),
  BOSS_DOOM(List.of(5269), 0, new WorldArea(1309, 9549, 5, 8, 0)),
  BOSS_DT2_DUKE(new WorldArea(3028, 6434, 23, 24, 0), new WorldArea(3038, 6428, 3, 5, 0)),
  BOSS_DT2_LEVIATHAN(List.of(8291), 0, new WorldArea(2059, 6433, 8, 5, 0)),
  BOSS_DT2_VARDORVIS(new WorldArea(1118, 3407, 23, 21, 0), new WorldArea(1113, 3426, 7, 8, 0)),
  BOSS_DT2_WHISPERER(List.of(9571, 10595, 12898), 0, new WorldArea(2921, 5821, 3, 6, 0)),
  BOSS_HUEYCOATL(new WorldArea(1501, 3273, 23, 26, 0), new WorldArea(1521, 3287, 10, 11, 0)),
  BOSS_GWD_ARMADYL(new WorldArea(2823, 5295, 21, 15, 2), new WorldArea(2834, 5288, 11, 7, 2)),
  BOSS_GWD_BANDOS(new WorldArea(2863, 5351, 15, 21, 2), new WorldArea(2857, 5349, 6, 11, 2)),
  BOSS_GWD_SARADOMIN(new WorldArea(2888, 5257, 21, 20, 0), new WorldArea(2908, 5261, 7, 10, 0)),
  BOSS_GWD_ZAMORAK(new WorldArea(2917, 5317, 21, 16, 2), new WorldArea(2923, 5333, 7, 7, 2)),
  BOSS_MUSPAH(List.of(11330), 0, new WorldArea(2908, 10313, 7, 10, 0)),
  BOSS_ROYAL_TITANS(new WorldArea(2908, 9560, 7, 15, 0), new WorldArea(2949, 9571, 7, 7, 0)),
  BOSS_SKOTIZO(List.of(9048), 0, new WorldArea(1658, 10042, 12, 12, 0)),
  BOSS_YAMA(List.of(6045), 0, new WorldArea(1438, 10074, 4, 6, 0)),
  QUEST_SECRETS_OF_THE_NORTH_MUSPAH(List.of(11330), 0, new WorldArea(2844, 10333, 5, 6, 0)),
  //  QUEST_BENEATH_CURSED_SANDS_AKH(new WorldArea(), new WorldArea()),
  QUEST_BENEATH_CURSED_SANDS_CHAMPION(List.of(13456), 0, new WorldArea(3411, 2843, 7, 10, 0)),
  QUEST_CURSE_OF_ARRAV(new WorldArea(3613, 4578, 28, 10, 0), new WorldArea(3577, 4602, 5, 5, 0)),
  //  QUEST_DESERT_TREASURE_2_SHADOW_REALM(new WorldArea(), new WorldArea()),
  //  QUEST_DESERT_TREASURE_2_STRANGLEWOOD(new WorldArea(), new WorldArea()),
  //  QUEST_FREMENNIK_EXILES(new WorldArea(), new WorldArea()),
  QUEST_MONKEY_MADNESS_2_KRUK(
      new WorldArea(2517, 9201, 33, 28, 1), new WorldArea(2531, 9232, 16, 9, 1)),
  QUEST_MONKEY_MADNESS_2_END_SURFACE(List.of(8023), 0, new WorldArea(2427, 3514, 14, 6, 0)),
  QUEST_MONKEY_MADNESS_2_END_CAVE(List.of(8280, 8536), 0, new WorldArea(2427, 3514, 14, 6, 0)),
  ;

  @Nullable private final WorldArea deathArea;
  @Nullable private final List<Integer> deathRegionIds;
  private final int deathPlane;
  private final WorldArea pileArea;

  RemoteDeathpileAreas(List<Integer> deathRegionIds, int deathPlane, WorldArea pileArea) {
    this.deathArea = null;
    this.deathRegionIds = deathRegionIds;
    this.deathPlane = deathPlane;
    this.pileArea = pileArea;
  }

  RemoteDeathpileAreas(WorldArea deathArea, WorldArea pileArea) {
    this.deathArea = deathArea;
    this.deathRegionIds = null;
    this.deathPlane = -1;
    this.pileArea = pileArea;
  }

  static WorldArea getPileArea(Client client, WorldPoint worldPoint) {
    var regionId = worldPoint.getRegionID();
    if (regionId == 11330) {
      return (Quest.SECRETS_OF_THE_NORTH.getState(client) == QuestState.FINISHED
              ? BOSS_MUSPAH
              : QUEST_SECRETS_OF_THE_NORTH_MUSPAH)
          .getPileArea();
    }

    if ((regionId == 8023 || regionId == 8280 || regionId == 8536)
        && Quest.MONKEY_MADNESS_II.getState(client) == QuestState.FINISHED) {
      return worldPoint.toWorldArea();
    }

    var remotePoint =
        Arrays.stream(values())
            .filter(
                l -> {
                  if (l.getDeathArea() != null) {
                    return l.getDeathArea().contains(worldPoint);
                  }

                  if (l.getDeathRegionIds() != null) {
                    return worldPoint.getPlane() == l.getDeathPlane()
                        && l.getDeathRegionIds().contains(regionId);
                  }

                  return false;
                })
            .findFirst();

    if (remotePoint.isPresent()) {
      return remotePoint.get().getPileArea();
    }

    return worldPoint.toWorldArea();
  }
}
