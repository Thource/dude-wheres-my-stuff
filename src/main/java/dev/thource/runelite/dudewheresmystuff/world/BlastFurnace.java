package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;

public class BlastFurnace extends WorldStorage {

  private static final int[] VARBITS = {
      Varbits.BLAST_FURNACE_COPPER_ORE,
      Varbits.BLAST_FURNACE_TIN_ORE,
      Varbits.BLAST_FURNACE_IRON_ORE,
      Varbits.BLAST_FURNACE_COAL,
      Varbits.BLAST_FURNACE_MITHRIL_ORE,
      Varbits.BLAST_FURNACE_ADAMANTITE_ORE,
      Varbits.BLAST_FURNACE_RUNITE_ORE,
      Varbits.BLAST_FURNACE_SILVER_ORE,
      Varbits.BLAST_FURNACE_GOLD_ORE,
      Varbits.BLAST_FURNACE_BRONZE_BAR,
      Varbits.BLAST_FURNACE_IRON_BAR,
      Varbits.BLAST_FURNACE_STEEL_BAR,
      Varbits.BLAST_FURNACE_MITHRIL_BAR,
      Varbits.BLAST_FURNACE_ADAMANTITE_BAR,
      Varbits.BLAST_FURNACE_RUNITE_BAR,
      Varbits.BLAST_FURNACE_SILVER_BAR,
      Varbits.BLAST_FURNACE_GOLD_BAR
  };

  protected BlastFurnace(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.BLAST_FURNACE, plugin);

    hasStaticItems = true;

    items.add(new ItemStack(ItemID.COPPER_ORE, plugin));
    items.add(new ItemStack(ItemID.TIN_ORE, plugin));
    items.add(new ItemStack(ItemID.IRON_ORE, plugin));
    items.add(new ItemStack(ItemID.COAL, plugin));
    items.add(new ItemStack(ItemID.MITHRIL_ORE, plugin));
    items.add(new ItemStack(ItemID.ADAMANTITE_ORE, plugin));
    items.add(new ItemStack(ItemID.RUNITE_ORE, plugin));
    items.add(new ItemStack(ItemID.SILVER_ORE, plugin));
    items.add(new ItemStack(ItemID.GOLD_ORE, plugin));
    items.add(new ItemStack(ItemID.BRONZE_BAR, plugin));
    items.add(new ItemStack(ItemID.IRON_BAR, plugin));
    items.add(new ItemStack(ItemID.STEEL_BAR, plugin));
    items.add(new ItemStack(ItemID.MITHRIL_BAR, plugin));
    items.add(new ItemStack(ItemID.ADAMANTITE_BAR, plugin));
    items.add(new ItemStack(ItemID.RUNITE_BAR, plugin));
    items.add(new ItemStack(ItemID.SILVER_BAR, plugin));
    items.add(new ItemStack(ItemID.GOLD_BAR, plugin));
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
