package dev.thource.runelite.dudewheresmystuff.death;

import java.awt.Color;

/**
 * Maps a value in [0, 1] to a colour from one of the matplotlib perceptually uniform colormaps.
 *
 * <p>The colormaps are approximated rather than stored as 256-entry lookup tables: each RGB channel
 * is a degree-14 Chebyshev series fitted to the original table, evaluated with Clenshaw's
 * algorithm. The result differs from the table by at most 2/255 on any channel (about 0.2/255 on
 * average), which is not visible.
 */
public class ColormapGetter {
  // Chebyshev coefficients (lowest order first) for the red, green and blue channels.
  private static final double[][] MAGMA = {
      {0.59095647, 0.55111188, -0.11833703, -0.062131741, 0.016574153, 0.0076228095, 0.0075621818, -0.0056603502, -0.0035897758, 0.0019419533, 0.0014953883, -0.00056189832, 0.00012892697, -0.0010803143, 0.00016962321},
      {0.36992432, 0.4802064, 0.14400073, 0.020505908, -0.019505932, -0.0073427362, -0.003412086, 0.0080666355, 0.0045109963, -0.0073766128, 0.0010651147, 0.001184907, -0.0011496793, 0.0021908151, -0.0013901962},
      {0.41412283, 0.24591619, -0.043656315, 0.15329274, 0.0067415229, -0.027314763, 0.0004380793, -0.0073086092, 0.007668289, 0.005476774, -0.0056316609, -0.00043196065, 0.0013662932, -0.00063878717, 0.0020886362}
  };

  private static final double[][] INFERNO = {
      {0.59074684, 0.53204493, -0.13230302, -0.050350751, 0.020308593, 0.0086868053, 0.013886497, 0.0012318051, 0.0034835689, 0.002725659, -0.00015755687, 0.00018006148, -0.00026521872, -0.000629256, -4.2053199e-05},
      {0.37701125, 0.49964462, 0.14588354, 0.0061879794, -0.019175365, -0.0067579462, -0.0061358509, 0.0036697806, -0.0010507223, -0.0045026309, 0.0035206595, -0.0013577602, -0.00028379965, 0.0013271961, -0.0013331965},
      {0.28083762, 0.10754545, 0.0063399522, 0.23050647, 0.050194363, -0.0027988266, -0.010642536, -0.023615218, 0.0012973573, 0.004137108, 0.0023897901, 0.0023615661, -0.0052470007, -0.0047756202, -0.0014314322}
  };

  private static final double[][] PLASMA = {
      {0.65629487, 0.45154139, -0.14788337, -0.012290299, -0.0089042865, 7.379087e-05, -0.0024255698, 0.0035121611, -0.0013179475, 0.00076292853, -0.00018282772, 0.00052571086, -0.00021601114, 0.00021327071, -5.0672193e-05},
      {0.37690523, 0.49460312, 0.12050573, -0.027824582, 0.011631249, 0.0062307285, -0.0097892164, 0.0027701766, 0.0027504319, -0.0035423768, 0.0013405323, 0.00094009984, -0.0014278707, 0.00078639897, 0.00039404174},
      {0.4092744, -0.26116122, -0.073931584, 0.070734733, -0.001600093, -0.00041132938, 0.0058469048, -0.002524885, -0.0058450041, -0.0017472197, -0.0020310146, -0.0018170537, -0.00094960864, -0.00031904103, -0.00036955574}
  };

  private static final double[][] VIRIDIS = {
      {0.4086853, 0.29831589, 0.25497365, 0.080221111, -0.033161525, -0.019683176, -0.0017831453, 0.0065440167, 0.0020825603, -0.003684529, -0.0014451943, 0.0017378613, 0.00088955861, -0.00076137773, -0.00074343175},
      {0.51872114, 0.4546685, -0.057370976, -0.0048518759, -0.0084699768, 0.00075123525, 0.002141333, 0.00084270925, -0.00032586493, 0.00011329123, 0.00018163665, -0.00029445936, 0.00024935115, -0.00019663473, 0.0001661704},
      {0.3758897, -0.14672065, -0.17065813, 0.021142421, 0.014829224, 0.028819786, 0.014676789, 0.00487351, 0.0041313558, 0.0011999667, -0.00071538166, -0.00093454522, -0.0010765444, -0.00075584931, -0.00052475128}
  };

  static Color getColor(DeathpileColorSchemeType type, float value) {
    double[][] coefficients;
    if (type == DeathpileColorSchemeType.MAGMA) {
      coefficients = MAGMA;
    } else if (type == DeathpileColorSchemeType.INFERNO) {
      coefficients = INFERNO;
    } else if (type == DeathpileColorSchemeType.PLASMA) {
      coefficients = PLASMA;
    } else if (type == DeathpileColorSchemeType.VIRIDIS) {
      coefficients = VIRIDIS;
    } else {
      return Color.WHITE;
    }

    // The series is defined on [-1, 1].
    double x = 2 * Math.min(Math.max(value, 0f), 1f) - 1;

    return new Color(
        channel(coefficients[0], x), channel(coefficients[1], x), channel(coefficients[2], x));
  }

  /** Evaluates a Chebyshev series at x using Clenshaw's algorithm, as an 8-bit channel value. */
  private static int channel(double[] c, double x) {
    double b1 = 0;
    double b2 = 0;
    for (int k = c.length - 1; k >= 1; k--) {
      double b0 = c[k] + 2 * x * b1 - b2;
      b2 = b1;
      b1 = b0;
    }

    double value = c[0] + x * b1 - b2;
    return (int) Math.round(Math.min(Math.max(value, 0), 1) * 255);
  }
}