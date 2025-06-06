package dev.thource.runelite.dudewheresmystuff.death;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Getter;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@Getter
enum RemoteDeathpileAreas {
  GE_TEST(new WorldArea(3161, 3480, 8, 8, 0), new WorldArea(3165, 3478, 1, 1, 0)),
  LUMMY_REGION_TEST(Collections.singletonList(12850), 0, new WorldArea(3223, 3225, 1, 1, 0)),

  BOSS_ARAXXOR(new WorldArea(3616, 9797, 35, 40, 0), new WorldArea(3656, 9812, 7, 10, 0)),
  BOSS_CERBERUS(List.of(4883, 5140, 5395), 0, new WorldArea(1307, 1246, 8, 8, 0)),
  BOSS_COLOSSEUM(new WorldArea(1805, 3087, 40, 40, 0), new WorldArea(1799, 9503, 8, 8, 0)),
  //  BOSS_DT2_DUKE(new WorldArea(), new WorldArea()),
  //  BOSS_DT2_LEVIATHAN(new WorldArea(), new WorldArea()),
  //  BOSS_DT2_VARDORVIS(new WorldArea(), new WorldArea()),
  //  BOSS_DT2_WHISPERER(new WorldArea(), new WorldArea()),
  BOSS_GWD_ARMADYL(new WorldArea(2823, 5295, 21, 15, 2), new WorldArea(2838, 5292, 3, 3, 2)),
  BOSS_GWD_BANDOS(new WorldArea(2863, 5351, 15, 21, 2), new WorldArea(2860, 5353, 3, 3, 2)),
  BOSS_GWD_SARADOMIN(new WorldArea(2888, 5257, 21, 20, 0), new WorldArea(2909, 5264, 3, 3, 0)),
  BOSS_GWD_ZAMORAK(new WorldArea(2917, 5317, 21, 16, 2), new WorldArea(2924, 5333, 3, 3, 2)),
  //  BOSS_MUSPAH(new WorldArea(), new WorldArea()),
  BOSS_ROYAL_TITANS(new WorldArea(2908, 9560, 7, 15, 0), new WorldArea(2949, 9571, 7, 7, 0)),
  BOSS_SKOTIZO(Collections.singletonList(9048), 0, new WorldArea(1658, 10042, 12, 12, 0)),
  BOSS_YAMA(Collections.singletonList(6045), 0, new WorldArea(1438, 10074, 4, 6, 0)),
//  QUEST_BENEATH_CURSED_SANDS_AKH(new WorldArea(), new WorldArea()),
//  QUEST_BENEATH_CURSED_SANDS_CHAMPION(new WorldArea(), new WorldArea()),
//  QUEST_CURSE_OF_ARRAV(new WorldArea(), new WorldArea()),
//  QUEST_DESERT_TREASURE_2_SHADOW_REALM(new WorldArea(), new WorldArea()),
//  QUEST_DESERT_TREASURE_2_STRANGLEWOOD(new WorldArea(), new WorldArea()),
//  QUEST_FREMENNIK_EXILES(new WorldArea(), new WorldArea()),
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

  static WorldArea getPileArea(WorldPoint worldPoint) {
    var regionId = worldPoint.getRegionID();
    var remotePoint =
        Arrays.stream(values()).filter(l -> {
          if (l.getDeathArea() != null) {
            return l.getDeathArea().contains(worldPoint);
          }

          if (l.getDeathRegionIds() != null) {
            return worldPoint.getPlane() == l.getDeathPlane()
                && l.getDeathRegionIds().contains(regionId);
          }

          return false;
        }).findFirst();

    if (remotePoint.isPresent()) {
      return remotePoint.get().getPileArea();
    }

    return worldPoint.toWorldArea();
  }
}
