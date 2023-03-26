package dev.thource.runelite.dudewheresmystuff;

import dev.thource.runelite.dudewheresmystuff.death.DeathbankType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.runelite.api.coords.WorldPoint;

public class SaveFieldLoader {

  public static long loadLong(ArrayList<String> stringList, long dfault) {
    if (stringList.isEmpty()) {
      return dfault;
    }

    try {
      return Long.parseLong(stringList.remove(0));
    } catch (NumberFormatException e) {
      return dfault;
    }
  }

  public static UUID loadUUID(ArrayList<String> stringList, UUID dfault) {
    if (stringList.isEmpty()) {
      return dfault;
    }

    try {
      return UUID.fromString(stringList.remove(0));
    } catch (IllegalArgumentException e) {
      return dfault;
    }
  }

  public static void loadItemsIntoList(ArrayList<String> stringList, List<ItemStack> itemStacks) {
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

  public static List<ItemStack> loadItems(ArrayList<String> stringList, List<ItemStack> dfault,
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

  public static boolean loadBoolean(ArrayList<String> stringList, boolean dfault) {
    if (stringList.isEmpty()) {
      return dfault;
    }

    return stringList.remove(0).equals("true");
  }

  public static DeathbankType loadDeathbankType(ArrayList<String> stringList,
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

  public static WorldPoint loadWorldPoint(ArrayList<String> stringList, WorldPoint dfault) {
    if (stringList.isEmpty()) {
      return dfault;
    }

    String[] splitData = stringList.remove(0).split(",");
    if (splitData.length != 3) {
      return dfault;
    }

    try {
      return new WorldPoint(Integer.parseInt(splitData[0]), Integer.parseInt(splitData[1]),
          Integer.parseInt(splitData[2]));
    } catch (NumberFormatException e) {
      return dfault;
    }
  }

  public static int loadInt(ArrayList<String> stringList, int dfault) {
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
