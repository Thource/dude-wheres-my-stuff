package dev.thource.runelite.dudewheresmystuff;

import dev.thource.runelite.dudewheresmystuff.death.DeathbankType;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.runelite.api.coords.WorldPoint;

public class SaveFieldFormatter {

  public static String format(List<ItemStack> list, boolean quantitiesOnly) {
    return list.stream()
        .map(item -> format(item, quantitiesOnly))
        .collect(Collectors.joining(","));
  }

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
