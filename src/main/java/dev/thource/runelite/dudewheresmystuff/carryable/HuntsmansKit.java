package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.ItemID;

/**
 * HuntsmansKit is responsible for tracking what the player has stored in their Huntsman's Kit.
 */
@Slf4j
public class HuntsmansKit extends CarryableStorage {

  HuntsmansKit(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.HUNTSMANS_KIT, plugin);

    hasStaticItems = true;

    // General Hunter items
    items.add(new ItemStack(ItemID.HUNTING_OJIBWAY_BIRD_SNARE, 0, plugin));
    items.add(new ItemStack(ItemID.NOOSE_WAND, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_BUTTERFLY_NET, 0, plugin));
    items.add(new ItemStack(ItemID.II_MAGIC_BUTTERFLY_NET, 0, plugin));
    items.add(new ItemStack(ItemID.BUTTERFLY_JAR, 0, plugin));
    items.add(new ItemStack(ItemID.II_IMPLING_JAR, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_BOX_TRAP, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_SNARE, 0, plugin));
    items.add(new ItemStack(ItemID.TORCH_UNLIT, 0, plugin));
    items.add(new ItemStack(ItemID.NET, 0, plugin));
    items.add(new ItemStack(ItemID.ROPE, 0, plugin));
    items.add(new ItemStack(ItemID.MAGIC_IMP_BOX, 0, plugin));
    items.add(new ItemStack(ItemID.MAGIC_IMP_BOX_FULL, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_TEASING_STICK, 0, plugin));
    items.add(new ItemStack(ItemID.HG_HUNTER_SPEAR, 0, plugin));
    items.add(new ItemStack(ItemID.RING_OF_PURSUIT, 0, plugin));

    // Crafting Gear
    items.add(new ItemStack(ItemID.HUNTING_CAMOFLAUGE_ROBE_WOOD, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_TROUSERS_WOOD, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_CAMOFLAUGE_ROBE_POLAR, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_TROUSERS_POLAR, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_CAMOFLAUGE_ROBE_JUNGLE, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_TROUSERS_JUNGLE, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_CAMOFLAUGE_ROBE_DESERT, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_TROUSERS_DESERT, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_HAT_JAGUAR, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_TORSO_JAGUAR, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_TROUSERS_JAGUAR, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_HAT_LEOPARD, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_TORSO_LEOPARD, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_TROUSERS_LEOPARD, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_HAT_TIGER, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_TORSO_TIGER, 0, plugin));
    items.add(new ItemStack(ItemID.HUNTING_TROUSERS_TIGER, 0, plugin));

    // Guild Hunter Gear
    items.add(new ItemStack(ItemID.HG_HUNTER_HOOD, 0, plugin));
    items.add(new ItemStack(ItemID.HG_HUNTER_LEGS, 0, plugin));
    items.add(new ItemStack(ItemID.HG_HUNTER_TOP, 0, plugin));
    items.add(new ItemStack(ItemID.HG_HUNTER_BOOTS, 0, plugin));

    // Hunter Cape/Hood
    items.add(new ItemStack(ItemID.SKILLCAPE_HUNTING, 0, plugin));
    items.add(new ItemStack(ItemID.SKILLCAPE_HUNTING_HOOD, 0, plugin));
    items.add(new ItemStack(ItemID.SKILLCAPE_HUNTING_TRIMMED, 0, plugin));
  }
}
