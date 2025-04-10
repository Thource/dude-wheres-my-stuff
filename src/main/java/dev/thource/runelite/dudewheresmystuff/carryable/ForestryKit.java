package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.ItemID;

/**
 * ForestryKit is responsible for tracking what the player has stored in their Forestry Kit.
 */
@Slf4j
public class ForestryKit extends CarryableStorage {

  ForestryKit(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.FORESTRY_KIT, plugin);

    hasStaticItems = true;

    // Basic Forestry Kit storage
    items.add(new ItemStack(ItemID.FORESTRY_CURRENCY, plugin));
    items.add(new ItemStack(ItemID.NATURE_OFFERINGS, plugin));
    items.add(new ItemStack(ItemID.FORESTRY_RATION, plugin));
    items.add(new ItemStack(ItemID.FORESTRY_SECATEURS_ATTACHMENT, plugin));
    items.add(new ItemStack(ItemID.LEAVES, plugin));
    items.add(new ItemStack(ItemID.LEAVES_OAK, plugin));
    items.add(new ItemStack(ItemID.LEAVES_WILLOW, plugin));
    items.add(new ItemStack(ItemID.LEAVES_MAPLE, plugin));
    items.add(new ItemStack(ItemID.LEAVES_YEW, plugin));
    items.add(new ItemStack(ItemID.LEAVES_MAGIC, plugin));
    items.add(new ItemStack(ItemID.FORESTRY_LUMBERJACK_TOP, plugin));
    items.add(new ItemStack(ItemID.FORESTRY_LUMBERJACK_LEGS, plugin));
    items.add(new ItemStack(ItemID.FORESTRY_LUMBERJACK_HAT, plugin));
    items.add(new ItemStack(ItemID.FORESTRY_LUMBERJACK_BOOTS, plugin));
    items.add(new ItemStack(ItemID.SKILLCAPE_WOODCUTTING_TRIMMED, plugin));

    // TODO: Forestry Basket Log Storage
  }
}
