package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;

/**
 * HuntsmansKit is responsible for tracking what the player has stored in their Huntsman's Kit.
 */
@Slf4j
public class HuntsmansKit extends CarryableStorage {

  HuntsmansKit(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.HUNTSMANS_KIT, plugin);

    hasStaticItems = true;

    // General Hunter items
    items.add(new ItemStack(ItemID.BIRD_SNARE, 0, plugin));
    items.add(new ItemStack(ItemID.NOOSE_WAND, 0, plugin));
    items.add(new ItemStack(ItemID.BUTTERFLY_NET, 0, plugin));
    items.add(new ItemStack(ItemID.MAGIC_BUTTERFLY_NET, 0, plugin));
    items.add(new ItemStack(ItemID.BUTTERFLY_JAR, 0, plugin));
    items.add(new ItemStack(ItemID.IMPLING_JAR, 0, plugin));
    items.add(new ItemStack(ItemID.BOX_TRAP, 0, plugin));
    items.add(new ItemStack(ItemID.RABBIT_SNARE, 0, plugin));
    items.add(new ItemStack(ItemID.UNLIT_TORCH, 0, plugin));
    items.add(new ItemStack(ItemID.SMALL_FISHING_NET, 0, plugin));
    items.add(new ItemStack(ItemID.ROPE, 0, plugin));
    items.add(new ItemStack(ItemID.MAGIC_BOX, 0, plugin));
    items.add(new ItemStack(ItemID.IMPINABOX2, 0, plugin));
    items.add(new ItemStack(ItemID.TEASING_STICK, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTERS_SPEAR, 0, plugin));
    items.add(new ItemStack(ItemID.RING_OF_PURSUIT, 0, plugin));

    // Crafting Gear
    items.add(new ItemStack(ItemID.WOOD_CAMO_TOP, 0, plugin));
    items.add(new ItemStack(ItemID.WOOD_CAMO_LEGS, 0, plugin));
    items.add(new ItemStack(ItemID.POLAR_CAMO_TOP, 0, plugin));
    items.add(new ItemStack(ItemID.POLAR_CAMO_LEGS, 0, plugin));
    items.add(new ItemStack(ItemID.JUNGLE_CAMO_TOP, 0, plugin));
    items.add(new ItemStack(ItemID.JUNGLE_CAMO_LEGS, 0, plugin));
    items.add(new ItemStack(ItemID.DESERT_CAMO_TOP, 0, plugin));
    items.add(new ItemStack(ItemID.DESERT_CAMO_LEGS, 0, plugin));
    items.add(new ItemStack(ItemID.LARUPIA_HAT, 0, plugin));
    items.add(new ItemStack(ItemID.LARUPIA_TOP, 0, plugin));
    items.add(new ItemStack(ItemID.LARUPIA_LEGS, 0, plugin));
    items.add(new ItemStack(ItemID.GRAAHK_HEADDRESS, 0, plugin));
    items.add(new ItemStack(ItemID.GRAAHK_TOP, 0, plugin));
    items.add(new ItemStack(ItemID.GRAAHK_LEGS, 0, plugin));
    items.add(new ItemStack(ItemID.KYATT_HAT, 0, plugin));
    items.add(new ItemStack(ItemID.KYATT_TOP, 0, plugin));
    items.add(new ItemStack(ItemID.KYATT_LEGS, 0, plugin));

    // Guild Hunter Gear
    items.add(new ItemStack(ItemID.GUILD_HUNTER_HEADWEAR, 0, plugin));
    items.add(new ItemStack(ItemID.GUILD_HUNTER_LEGS, 0, plugin));
    items.add(new ItemStack(ItemID.GUILD_HUNTER_TOP, 0, plugin));
    items.add(new ItemStack(ItemID.GUILD_HUNTER_BOOTS, 0, plugin));

    // Hunter Cape/Hood
    items.add(new ItemStack(ItemID.HUNTER_CAPE, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTER_HOOD, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTER_CAPET, 0, plugin));
  }
}
