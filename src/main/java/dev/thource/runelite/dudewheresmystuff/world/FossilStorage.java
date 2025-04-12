package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

/** FossilStorage is responsible for tracking the players fossil island fossil storage. */
public class FossilStorage extends WorldStorage {

  protected FossilStorage(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.FOSSIL_STORAGE, plugin);

    hasStaticItems = true;

    varbits =
        new int[] {
          VarbitID.FOSSIL_STORAGE_SMALL_UNID,
          VarbitID.FOSSIL_STORAGE_MEDIUM_UNID,
          VarbitID.FOSSIL_STORAGE_LARGE_UNID,
          VarbitID.FOSSIL_STORAGE_RARE_UNID,
          VarbitID.FOSSIL_STORAGE_SMALL_1,
          VarbitID.FOSSIL_STORAGE_SMALL_2,
          VarbitID.FOSSIL_STORAGE_SMALL_3,
          VarbitID.FOSSIL_STORAGE_SMALL_4,
          VarbitID.FOSSIL_STORAGE_SMALL_5,
          VarbitID.FOSSIL_STORAGE_MEDIUM_1,
          VarbitID.FOSSIL_STORAGE_MEDIUM_2,
          VarbitID.FOSSIL_STORAGE_MEDIUM_3,
          VarbitID.FOSSIL_STORAGE_MEDIUM_4,
          VarbitID.FOSSIL_STORAGE_MEDIUM_5,
          VarbitID.FOSSIL_STORAGE_LARGE_1,
          VarbitID.FOSSIL_STORAGE_LARGE_2,
          VarbitID.FOSSIL_STORAGE_LARGE_3,
          VarbitID.FOSSIL_STORAGE_LARGE_4,
          VarbitID.FOSSIL_STORAGE_LARGE_5,
          VarbitID.FOSSIL_STORAGE_PLANT_1,
          VarbitID.FOSSIL_STORAGE_PLANT_2,
          VarbitID.FOSSIL_STORAGE_PLANT_3,
          VarbitID.FOSSIL_STORAGE_PLANT_4,
          VarbitID.FOSSIL_STORAGE_PLANT_5,
          VarbitID.FOSSIL_STORAGE_RARE_1,
          VarbitID.FOSSIL_STORAGE_RARE_2,
          VarbitID.FOSSIL_STORAGE_RARE_3,
          VarbitID.FOSSIL_STORAGE_RARE_4,
          VarbitID.FOSSIL_STORAGE_RARE_5,
          VarbitID.FOSSIL_STORAGE_RARE_6,
        };

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
