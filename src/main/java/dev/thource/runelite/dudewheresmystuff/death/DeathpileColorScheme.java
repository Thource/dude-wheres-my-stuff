package dev.thource.runelite.dudewheresmystuff.death;

import java.awt.Color;
import java.util.Random;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** DeathpileColorScheme is used to specify which color scheme should be used for deathpiles. */
@RequiredArgsConstructor
@Getter
public enum DeathpileColorScheme {
  FULL_COLOR("Full color", DeathpileColorSchemeType.FULL_COLOR),
  PASTEL("Pastel", DeathpileColorSchemeType.PASTEL),
  GRAYSCALE("Grayscale", DeathpileColorSchemeType.GRAYSCALE),
  REDSCALE("Redscale", DeathpileColorSchemeType.REDSCALE),
  GREENSCALE("Greenscale", DeathpileColorSchemeType.GREENSCALE),
  BLUESCALE("Bluescale", DeathpileColorSchemeType.BLUESCALE),
  MAGMA("Magma", DeathpileColorSchemeType.MAGMA),
  INFERNO("Inferno", DeathpileColorSchemeType.INFERNO),
  PLASMA("Plasma", DeathpileColorSchemeType.PLASMA),
  VIRIDIS("Viridis", DeathpileColorSchemeType.VIRIDIS),
  WHITE("White", DeathpileColorSchemeType.WHITE),
  YELLOW("Yellow", DeathpileColorSchemeType.YELLOW);

  private final String name;
  private final DeathpileColorSchemeType type;

  Color generateColor(Random random) {
    if (this.type == DeathpileColorSchemeType.MAGMA
        || this.type == DeathpileColorSchemeType.INFERNO
        || this.type == DeathpileColorSchemeType.PLASMA
        || this.type == DeathpileColorSchemeType.VIRIDIS) {
      random.nextFloat();
      return ColormapGetter.getColor(this.type, random.nextFloat());
    }

    if (this.type == DeathpileColorSchemeType.FULL_COLOR) {
      float saturation = 0.7f + random.nextFloat() * 0.3f;
      float hue = random.nextFloat();

      return Color.getHSBColor(hue, saturation, 0.8f);
    } else if (this.type == DeathpileColorSchemeType.PASTEL) {
      random.nextFloat();
      return Color.getHSBColor(random.nextFloat(), 0.5f, 0.8f);
    } else if (this.type == DeathpileColorSchemeType.GRAYSCALE) {
      random.nextFloat();
      return Color.getHSBColor(0, 0, random.nextFloat());
    } else if (this.type == DeathpileColorSchemeType.REDSCALE) {
      random.nextFloat();
      return Color.getHSBColor(0, 1, random.nextFloat());
    } else if (this.type == DeathpileColorSchemeType.GREENSCALE) {
      random.nextFloat();
      return Color.getHSBColor(0.333f, 1, random.nextFloat());
    } else if (this.type == DeathpileColorSchemeType.BLUESCALE) {
      random.nextFloat();
      return Color.getHSBColor(0.666f, 1, random.nextFloat());
    } else if (this.type == DeathpileColorSchemeType.YELLOW) {
      return Color.YELLOW;
    }

    return Color.WHITE;
  }
}
