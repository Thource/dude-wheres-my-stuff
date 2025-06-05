package dev.thource.runelite.dudewheresmystuff.death;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@RequiredArgsConstructor
@Getter
enum RemoteDeathpileAreas {
  GE_TEST(new WorldArea(3161, 3480, 8, 8, 0), new WorldArea(3165, 3478, 1, 1, 0)),

  BOSS_ARAXXOR(new WorldArea(3616, 9797, 35, 40, 0), new WorldArea(3656, 9812, 7, 10, 0)),
  //  BOSS_CERBERUS(new WorldArea(), new WorldArea()),
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
//  BOSS_SKOTIZO(new WorldArea(), new WorldArea()),
//  BOSS_YAMA(new WorldArea(), new WorldArea()),
//  QUEST_BENEATH_CURSED_SANDS_AKH(new WorldArea(), new WorldArea()),
//  QUEST_BENEATH_CURSED_SANDS_CHAMPION(new WorldArea(), new WorldArea()),
//  QUEST_CURSE_OF_ARRAV(new WorldArea(), new WorldArea()),
//  QUEST_DESERT_TREASURE_2_SHADOW_REALM(new WorldArea(), new WorldArea()),
//  QUEST_DESERT_TREASURE_2_STRANGLEWOOD(new WorldArea(), new WorldArea()),
//  QUEST_FREMENNIK_EXILES(new WorldArea(), new WorldArea()),
;

  private final WorldArea deathArea;
  private final WorldArea pileArea;

  static WorldArea getPileArea(WorldPoint worldPoint) {
    var remotePoint =
        Arrays.stream(values()).filter(l -> l.getDeathArea().contains(worldPoint)).findFirst();

    if (remotePoint.isPresent()) {
      return remotePoint.get().getPileArea();
    }

    return worldPoint.toWorldArea();
  }
}
