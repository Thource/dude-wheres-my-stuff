package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;

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

  @Override
  public boolean onVarbitChanged() {
    boolean updated = false;

    for (int i = 0; i < VARBITS.length; i++) {
      int varbit = VARBITS[i];
      ItemStack itemStack = items.get(i);

      int newQuantity = plugin.getClient().getVarbitValue(varbit);
      if (newQuantity == itemStack.getQuantity()) {
        continue;
      }

      itemStack.setQuantity(newQuantity);
      updated = true;
    }

    return updated;
  }
}
