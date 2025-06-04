package dev.thource.runelite.dudewheresmystuff;

import dev.thource.runelite.dudewheresmystuff.death.DeathbankType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

/**
 * SaveFieldFormatter converts strings into various data types so that they can be loaded.
 *
 * <p>This class is the opposite of SaveFieldFormatter.
 */
public class SaveFieldLoader {

  private SaveFieldLoader() {
  }

  /**
   * Pulls a string from the string list, converts it to a long and returns it or the default
   * value.
   *
   * @param stringList the string list
   * @param dfault     the fallback value to return
   * @return the converted first string of the string list, or the fallback value
   */
  public static long loadLong(List<String> stringList, long dfault) {
    if (stringList.isEmpty()) {
      return dfault;
    }

    try {
      return Long.parseLong(stringList.remove(0));
    } catch (NumberFormatException e) {
      return dfault;
    }
  }

  /**
   * Pulls a string from the string list, converts it to a UUID and returns it or the default
   * value.
   *
   * @param stringList the string list
   * @param dfault     the fallback value to return
   * @return the converted first string of the string list, or the fallback value
   */
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  public static UUID loadUUID(List<String> stringList, UUID dfault) {
    if (stringList.isEmpty()) {
      return dfault;
    }

    try {
      return UUID.fromString(stringList.remove(0));
    } catch (IllegalArgumentException e) {
      return dfault;
    }
  }

  /**
   * Pulls a string from the string list and uses it to set the quantities of the ItemStacks in
   * itemStacks.
   *
   * @param stringList the string list
   * @param itemStacks the list of items to modify the quantities of
   */
  public static void loadItemsIntoList(List<String> stringList, List<ItemStack> itemStacks) {
    if (stringList.isEmpty()) {
      return;
    }

    int i = 0;
    for (String quantity : stringList.remove(0).split(",")) {
      if (i >= itemStacks.size()) {
        break;
      }

      try {
        itemStacks.get(i).setQuantity(Long.parseLong(quantity));
      } catch (NumberFormatException e) {
        // do nothing
      }
      i++;
    }
  }

  /**
   * Pulls a string from the string list, converts it to a list of ItemStack and returns it or the
   * default value.
   *
   * @param stringList the string list
   * @param dfault     the fallback value to return
   * @return the converted first string of the string list, or the fallback value
   */
  public static List<ItemStack> loadItems(List<String> stringList, List<ItemStack> dfault,
      DudeWheresMyStuffPlugin plugin) {
    if (stringList.isEmpty()) {
      return dfault;
    }

    ArrayList<ItemStack> itemStacks = new ArrayList<>();
    for (String stackData : stringList.remove(0).split(",")) {
      String[] stackDataSplit = stackData.split("x");
      if (stackDataSplit.length != 2) {
        continue;
      }

      try {
        itemStacks.add(
            new ItemStack(Integer.parseInt(stackDataSplit[0]), Long.parseLong(stackDataSplit[1]),
                plugin));
      } catch (NumberFormatException e) {
        // do nothing
      }
    }

    return itemStacks;
  }

  /**
   * Pulls a string from the string list, converts it to a boolean and returns it or the default
   * value.
   *
   * @param stringList the string list
   * @param dfault     the fallback value to return
   * @return the converted first string of the string list, or the fallback value
   */
  public static boolean loadBoolean(List<String> stringList, boolean dfault) {
    if (stringList.isEmpty()) {
      return dfault;
    }

    return stringList.remove(0).equals("true");
  }

  /**
   * Pulls a string from the string list, converts it to a DeathbankType and returns it or the
   * default value.
   *
   * @param stringList the string list
   * @param dfault     the fallback value to return
   * @return the converted first string of the string list, or the fallback value
   */
  public static DeathbankType loadDeathbankType(List<String> stringList,
      DeathbankType dfault) {
    if (stringList.isEmpty()) {
      return dfault;
    }

    try {
      return DeathbankType.valueOf(stringList.remove(0));
    } catch (IllegalArgumentException e) {
      return dfault;
    }
  }

  /**
   * Pulls a string from the string list, converts it to a WorldPoint and returns it or the default
   * value.
   *
   * @param stringList the string list
   * @param dfault     the fallback value to return
   * @return the converted first string of the string list, or the fallback value
   */
  public static WorldPoint loadWorldPoint(List<String> stringList, WorldPoint dfault) {
    if (stringList.isEmpty()) {
      return dfault;
    }

    String[] splitData = stringList.remove(0).split(",");
    if (splitData.length != 3) {
      stringList.add(0, String.join(",", splitData));
      return dfault;
    }

    try {
      return new WorldPoint(Integer.parseInt(splitData[0]), Integer.parseInt(splitData[1]),
          Integer.parseInt(splitData[2]));
    } catch (NumberFormatException e) {
      return dfault;
    }
  }

  /**
   * Pulls a string from the string list, converts it to a WorldArea and returns it or the default
   * value.
   *
   * @param stringList the string list
   * @param dfault     the fallback value to return
   * @return the converted first string of the string list, or the fallback value
   */
  public static WorldArea loadWorldArea(List<String> stringList, WorldArea dfault) {
    if (stringList.isEmpty()) {
      return dfault;
    }

    String[] splitData = stringList.remove(0).split(",");
    if (splitData.length != 5) {
      stringList.add(0, String.join(",", splitData));
      return dfault;
    }

    try {
      return new WorldArea(Integer.parseInt(splitData[0]), Integer.parseInt(splitData[1]),
          Integer.parseInt(splitData[2]), Integer.parseInt(splitData[3]),
          Integer.parseInt(splitData[4]));
    } catch (NumberFormatException e) {
      return dfault;
    }
  }

  /**
   * Pulls a string from the string list, converts it to an int and returns it or the default
   * value.
   *
   * @param stringList the string list
   * @param dfault     the fallback value to return
   * @return the converted first string of the string list, or the fallback value
   */
  public static int loadInt(List<String> stringList, int dfault) {
    if (stringList.isEmpty()) {
      return dfault;
    }

    try {
      return Integer.parseInt(stringList.remove(0));
    } catch (NumberFormatException e) {
      return dfault;
    }
  }
}
