package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import net.runelite.api.ItemID;

/** FossilStorage is responsible for tracking the players fossil island fossil storage. */
public class FossilStorage extends WorldStorage {

  private static final int[] VARBITS = {
    5828, 5829, 5830, 5831, 5832, 5833, 5834, 5835, 5836, 5837, 5838, 5839, 5840, 5841, 5842, 5843,
    5844, 5845, 5846, 5847, 5848, 5849, 5850, 5851, 5852, 5853, 5854, 5855, 5856, 5952
  };

  protected FossilStorage(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.FOSSIL_STORAGE, plugin);

    hasStaticItems = true;

    varbits = VARBITS;

    items.add(new ItemStack(ItemID.UNIDENTIFIED_SMALL_FOSSIL, plugin));
    items.add(new ItemStack(ItemID.UNIDENTIFIED_MEDIUM_FOSSIL, plugin));
    items.add(new ItemStack(ItemID.UNIDENTIFIED_LARGE_FOSSIL, plugin));
    items.add(new ItemStack(ItemID.UNIDENTIFIED_RARE_FOSSIL, plugin));

    items.add(new ItemStack(ItemID.SMALL_FOSSILISED_LIMBS, plugin));
    items.add(new ItemStack(ItemID.SMALL_FOSSILISED_SPINE, plugin));
    items.add(new ItemStack(ItemID.SMALL_FOSSILISED_RIBS, plugin));
    items.add(new ItemStack(ItemID.SMALL_FOSSILISED_PELVIS, plugin));
    items.add(new ItemStack(ItemID.SMALL_FOSSILISED_SKULL, plugin));

    items.add(new ItemStack(ItemID.MEDIUM_FOSSILISED_LIMBS, plugin));
    items.add(new ItemStack(ItemID.MEDIUM_FOSSILISED_SPINE, plugin));
    items.add(new ItemStack(ItemID.MEDIUM_FOSSILISED_RIBS, plugin));
    items.add(new ItemStack(ItemID.MEDIUM_FOSSILISED_PELVIS, plugin));
    items.add(new ItemStack(ItemID.MEDIUM_FOSSILISED_SKULL, plugin));

    items.add(new ItemStack(ItemID.LARGE_FOSSILISED_LIMBS, plugin));
    items.add(new ItemStack(ItemID.LARGE_FOSSILISED_SPINE, plugin));
    items.add(new ItemStack(ItemID.LARGE_FOSSILISED_RIBS, plugin));
    items.add(new ItemStack(ItemID.LARGE_FOSSILISED_PELVIS, plugin));
    items.add(new ItemStack(ItemID.LARGE_FOSSILISED_SKULL, plugin));

    items.add(new ItemStack(ItemID.FOSSILISED_ROOTS, plugin));
    items.add(new ItemStack(ItemID.FOSSILISED_STUMP, plugin));
    items.add(new ItemStack(ItemID.FOSSILISED_BRANCH, plugin));
    items.add(new ItemStack(ItemID.FOSSILISED_LEAF, plugin));
    items.add(new ItemStack(ItemID.FOSSILISED_MUSHROOM, plugin));

    items.add(new ItemStack(ItemID.RARE_FOSSILISED_LIMBS, plugin));
    items.add(new ItemStack(ItemID.RARE_FOSSILISED_SPINE, plugin));
    items.add(new ItemStack(ItemID.RARE_FOSSILISED_RIBS, plugin));
    items.add(new ItemStack(ItemID.RARE_FOSSILISED_PELVIS, plugin));
    items.add(new ItemStack(ItemID.RARE_FOSSILISED_SKULL, plugin));
    items.add(new ItemStack(ItemID.RARE_FOSSILISED_TUSK, plugin));
  }
}
