package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import net.runelite.api.gameval.ItemID;

/** FossilStorage is responsible for tracking the players fossil island fossil storage. */
public class FossilStorage extends WorldStorage {

  private static final int[] VARBITS = {
      5828,
      5829,
      5830,
      5831,
      5832,
      5833,
      5834,
      5835,
      5836,
      5837,
      5838,
      5839,
      5840,
      5841,
      5842,
      5843,
      5844,
      5845,
      5846,
      5847,
      5848,
      5849,
      5850,
      5851,
      5852,
      5853,
      5854,
      5855,
      5856,
      5952
  };

  protected FossilStorage(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.FOSSIL_STORAGE, plugin);

    hasStaticItems = true;

    varbits = VARBITS;

    items.add(new ItemStack(ItemID.FOSSIL_SMALL_UNID, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_MEDIUM_UNID, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_LARGE_UNID, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_RARE_UNID, plugin));

    items.add(new ItemStack(ItemID.FOSSIL_SMALL_1, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_SMALL_2, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_SMALL_3, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_SMALL_4, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_SMALL_5, plugin));

    items.add(new ItemStack(ItemID.FOSSIL_MEDIUM_1, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_MEDIUM_2, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_MEDIUM_3, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_MEDIUM_4, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_MEDIUM_5, plugin));

    items.add(new ItemStack(ItemID.FOSSIL_LARGE_1, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_LARGE_2, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_LARGE_3, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_LARGE_4, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_LARGE_5, plugin));

    items.add(new ItemStack(ItemID.FOSSIL_PLANT_1, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_PLANT_2, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_PLANT_3, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_PLANT_4, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_PLANT_5, plugin));

    items.add(new ItemStack(ItemID.FOSSIL_RARE_1, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_RARE_2, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_RARE_3, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_RARE_4, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_RARE_5, plugin));
    items.add(new ItemStack(ItemID.FOSSIL_RARE_6, plugin));
  }
}
