package dev.thource.runelite.dudewheresmystuff;

import dev.thource.runelite.dudewheresmystuff.death.DeathbankType;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.runelite.api.coords.WorldPoint;

/** SaveFieldFormatter converts various data types into strings so that they can be saved. */
public class SaveFieldFormatter {

  private SaveFieldFormatter() {
  }

  /**
   * Converts an ItemStack list into a string.
   *
   * @param list           the item stack list
   * @param quantitiesOnly whether the result should be composed of only quantities
   * @return a string representation of the item stack list
   */
  public static String format(List<ItemStack> list, boolean quantitiesOnly) {
    return list.stream()
        .map(item -> format(item, quantitiesOnly))
        .collect(Collectors.joining(","));
  }

  /**
   * Converts an ItemStack into a string.
   *
   * @param itemStack    the item stack
   * @param quantityOnly whether the result should be composed of only quantity
   * @return a string representation of the item stack
   */
  public static String format(ItemStack itemStack, boolean quantityOnly) {
    if (quantityOnly) {
      return String.valueOf(itemStack.getQuantity());
    }

    return itemStack.getId() + "x" + itemStack.getQuantity();
  }

  public static String format(WorldPoint worldPoint) {
    return worldPoint.getX() + "," + worldPoint.getY() + "," + worldPoint.getPlane();
  }

  public static String format(DeathbankType val) {
    return String.valueOf(val);
  }

  public static String format(boolean val) {
    return String.valueOf(val);
  }

  public static String format(int val) {
    return String.valueOf(val);
  }

  public static String format(long val) {
    return String.valueOf(val);
  }

  public static String format(UUID val) {
    return val.toString();
  }
}
