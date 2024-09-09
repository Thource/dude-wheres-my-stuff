package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

/** Deathpile is responsible for tracking the player's deathpiled items. */
@Getter
@Slf4j
public class Deathpile extends ExpiringDeathStorage {

  Deathpile(
      DudeWheresMyStuffPlugin plugin,
      boolean useAccountPlayTime,
      WorldPoint worldPoint,
      DeathStorageManager deathStorageManager,
      List<ItemStack> deathItems) {
    super(
        plugin,
        useAccountPlayTime,
        worldPoint,
        deathStorageManager,
        deathItems,
        DeathStorageType.DEATHPILE);
    refreshColor();
  }

  static Deathpile load(
      DudeWheresMyStuffPlugin plugin,
      DeathStorageManager deathStorageManager,
      String profileKey,
      String uuid) {
    Deathpile deathpile = new Deathpile(plugin, true, null, deathStorageManager, new ArrayList<>());

    deathpile.uuid = UUID.fromString(uuid);
    deathpile.load(
        deathStorageManager.getConfigManager(), deathStorageManager.getConfigKey(), profileKey);

    return deathpile;
  }

  private Color generateColor() {
    if (worldPoint == null) {
      return Color.WHITE;
    }

    Random rand =
        new Random(
            worldPoint.getX() * 200L + worldPoint.getY() * 354L + worldPoint.getPlane() * 42L);

    return plugin.getConfig().deathpileColorScheme().generateColor(rand);
  }

  @Override
  protected void loadValues(ArrayList<String> values) {
    super.loadValues(values);

    refreshColor();
  }

  void refreshColor() {
    color = generateColor();
  }

  @Override
  public int getTotalLifeInMinutes() {
    return 60;
  }
}
