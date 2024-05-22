package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;

/**
 * ForestryKit is responsible for tracking what the player has stored in their Forestry Kit.
 */
@Slf4j
public class ForestryKit extends CarryableStorage {

  ForestryKit(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.FORESTRY_KIT, plugin);

    hasStaticItems = true;

    // Basic Forestry Kit storage
    items.add(new ItemStack(ItemID.ANIMAINFUSED_BARK, plugin));
    items.add(new ItemStack(ItemID.NATURE_OFFERINGS, plugin));
    items.add(new ItemStack(ItemID.FORESTERS_RATION, plugin));
    items.add(new ItemStack(ItemID.SECATEURS_ATTACHMENT, plugin));
    items.add(new ItemStack(ItemID.LEAVES, plugin));
    items.add(new ItemStack(ItemID.OAK_LEAVES, plugin));
    items.add(new ItemStack(ItemID.WILLOW_LEAVES, plugin));
    items.add(new ItemStack(ItemID.MAPLE_LEAVES, plugin));
    items.add(new ItemStack(ItemID.YEW_LEAVES, plugin));
    items.add(new ItemStack(ItemID.MAGIC_LEAVES, plugin));
    items.add(new ItemStack(ItemID.FORESTRY_TOP, plugin));
    items.add(new ItemStack(ItemID.FORESTRY_LEGS, plugin));
    items.add(new ItemStack(ItemID.FORESTRY_HAT, plugin));
    items.add(new ItemStack(ItemID.FORESTRY_BOOTS, plugin));
    items.add(new ItemStack(ItemID.WOODCUT_CAPET, plugin));

    // TODO: Forestry Basket Log Storage
  }
}
