package dev.thource.runelite.dudewheresmystuff;

import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorage;
import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageManager;
import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import net.runelite.api.gameval.ItemID;

/** ItemStackUtils provides methods that interact with Lists of ItemStack. */
public class ItemStackUtils {

  public static final List<Integer> ITEM_IDS_DESTROYED_ON_DEATH = Collections.unmodifiableList(
      Arrays.asList(
          ItemID.LOOTING_BAG,
          ItemID.LOOTING_BAG_OPEN,
          ItemID.BH_CLUE_BOX,
          ItemID.FLAMTAER_BAG,
          ItemID.FLAMTAER_BAG_DUMMY,
          ItemID.LUNAR_HELMET,
          ItemID.LUNAR_TORSO,
          ItemID.LUNAR_LEGS,
          ItemID.LUNAR_GLOVES,
          ItemID.LUNAR_BOOTS,
          ItemID.LUNAR_CAPE,
          ItemID.LUNAR_AMULET,
          ItemID.LUNAR_RING,
          ItemID.LUNAR_MOONCLAN_LIMINAL_STAFF,
          ItemID.RING_OF_CHAROS,
          ItemID.RING_OF_CHAROS_UNLOCKED,
          ItemID.CHRONICLE,
          ItemID.STEEL_GAUNTLETS,
          ItemID.GAUNTLETS_OF_COOKING,
          ItemID.GAUNTLETS_OF_GOLDSMITHING,
          ItemID.GAUNTLETS_OF_CHAOS,
          ItemID.II_MAGIC_BUTTERFLY_NET,
          ItemID.II_JAR_GENERATOR,
          ItemID.DAMNED_AMULET_DEGRADED,
          ItemID.LEAGUE_3_BRONZE_TROPHY,
          ItemID.LEAGUE_3_IRON_TROPHY,
          ItemID.LEAGUE_3_STEEL_TROPHY,
          ItemID.LEAGUE_3_MITHRIL_TROPHY,
          ItemID.LEAGUE_3_ADAMANT_TROPHY,
          ItemID.LEAGUE_3_RUNE_TROPHY,
          ItemID.LEAGUE_3_DRAGON_TROPHY,
          ItemID.TRAILBLAZER_BRONZE_TROPHY,
          ItemID.TRAILBLAZER_IRON_TROPHY,
          ItemID.TRAILBLAZER_STEEL_TROPHY,
          ItemID.TRAILBLAZER_MITHRIL_TROPHY,
          ItemID.TRAILBLAZER_ADAMANT_TROPHY,
          ItemID.TRAILBLAZER_RUNE_TROPHY,
          ItemID.TRAILBLAZER_DRAGON_TROPHY,
          ItemID.TWISTED_BRONZE_TROPHY,
          ItemID.TWISTED_IRON_TROPHY,
          ItemID.TWISTED_STEEL_TROPHY,
          ItemID.TWISTED_MITHRIL_TROPHY,
          ItemID.TWISTED_ADAMANT_TROPHY,
          ItemID.TWISTED_RUNE_TROPHY,
          ItemID.TWISTED_DRAGON_TROPHY,
          ItemID.CRYSTAL_SEED_OLD_SMALL,
          ItemID.EYEGLO_CRYSTAL_SAW
      )
  );

  private ItemStackUtils() {
  }

  /**
   * Compounds a List of ItemStacks, merging ItemStacks with the same id.
   *
   * @param itemStacks           ItemStacks to be compounded
   * @param compoundUnstackables Whether unstackable ItemStacks should be compounded
   * @return a new List of new ItemStacks
   */
  public static List<ItemStack> compound(List<ItemStack> itemStacks, boolean compoundUnstackables) {
    ArrayList<ItemStack> compoundedItemStacks = new ArrayList<>();

    itemStacks.forEach(
        itemStack -> {
          if (itemStack.getId() == -1) {
            return;
          }

          boolean wasCompounded = false;
          if (itemStack.isStackable() || compoundUnstackables) {
            for (ItemStack compoundedItemStack : compoundedItemStacks) {
              if (compoundedItemStack.getId() != itemStack.getId() || compoundedItemStack.getId() < 0) {
                continue;
              }

              compoundedItemStack.setQuantity(
                  compoundedItemStack.getQuantity() + itemStack.getQuantity());
              wasCompounded = true;
            }
          }

          if (!wasCompounded) {
            compoundedItemStacks.add(new ItemStack(itemStack));
          }
        });

    return compoundedItemStacks;
  }

  /**
   * Adds an ItemStack to the supplied List of ItemStacks. If the item is stackable and already
   * exists in the list, it will merge quantities. If there's an empty slot, it will fill it,
   * otherwise it'll be added to the end of the list.
   *
   * @param items     ItemStacks to be added to
   * @param itemToAdd ItemStack to add
   */
  public static void addItemStack(List<ItemStack> items, ItemStack itemToAdd) {
    addItemStack(items, itemToAdd, false);
  }

  /**
   * Adds an ItemStack to the supplied List of ItemStacks. If the item is stackable and already
   * exists in the list, it will merge quantities. If there's an empty slot, it will fill it,
   * otherwise it'll be added to the end of the list.
   *
   * @param items         ItemStacks to be added to
   * @param itemToAdd     ItemStack to add
   * @param forceStacking Whether to stack non-stackable items (like in the bank)
   */
  public static void addItemStack(List<ItemStack> items, ItemStack itemToAdd,
      boolean forceStacking) {
    if (itemToAdd.isStackable() || forceStacking) {
      for (ItemStack item : items) {
        if (item.getId() != itemToAdd.getId()) {
          continue;
        }

        item.setQuantity(item.getQuantity() + itemToAdd.getQuantity());
        return;
      }
    }

    ListIterator<ItemStack> listIterator = items.listIterator();
    while (listIterator.hasNext()) {
      ItemStack item = listIterator.next();
      if (item.getId() != -1) {
        continue;
      }

      listIterator.set(itemToAdd);
      return;
    }

    items.add(itemToAdd);
  }

  /**
   * Removes an ItemStack from the supplied List of ItemStacks. Any removed ItemStacks will be
   * replaced with empty slots.
   *
   * @param items            ItemStacks to remove from
   * @param itemToRemove     ItemStack to remove
   * @param replaceWithEmpty whether fully removed items should be replaced by an "empty" item
   */
  public static void removeItemStack(
      List<ItemStack> items, ItemStack itemToRemove, boolean replaceWithEmpty) {
    if (itemToRemove.getId() == -1) {
      return;
    }

    long quantityToRemove = itemToRemove.getQuantity();

    ListIterator<ItemStack> listIterator = items.listIterator();
    while (listIterator.hasNext() && quantityToRemove > 0) {
      ItemStack inventoryItem = listIterator.next();

      if (inventoryItem.getId() != itemToRemove.getId()) {
        continue;
      }

      long qtyToRemove = Math.min(quantityToRemove, inventoryItem.getQuantity());
      quantityToRemove -= qtyToRemove;
      inventoryItem.setQuantity(inventoryItem.getQuantity() - qtyToRemove);
      if (inventoryItem.getQuantity() == 0) {
        if (replaceWithEmpty) {
          listIterator.set(new ItemStack(-1, "empty", 1, 0, 0, false));
        } else {
          listIterator.remove();
        }
      }
    }
  }

  public static void removeItemStack(List<ItemStack> items, ItemStack itemToRemove) {
    removeItemStack(items, itemToRemove, true);
  }

  /**
   * Removes a list of items from another list of items.
   *
   * @param itemsToModify the list of items to remove from
   * @param itemsToRemove the list of items to be removed
   */
  public static void removeItems(List<ItemStack> itemsToModify, List<ItemStack> itemsToRemove) {
    for (ItemStack itemStack : itemsToRemove) {
      ItemStackUtils.removeItemStack(itemsToModify, itemStack, false);
    }
  }

  public static List<ItemStack> explodeStorageItems(List<ItemStack> itemStacks,
      CarryableStorageManager carryableStorageManager) {
    ArrayList<ItemStack> explodedItemStacks = new ArrayList<>();
    itemStacks.forEach(itemStack -> explodedItemStacks.add(new ItemStack(itemStack)));

    boolean isLootingBagPresent = false;
    ListIterator<ItemStack> itemStacksIterator = explodedItemStacks.listIterator();
    while (itemStacksIterator.hasNext()) {
      ItemStack itemStack = itemStacksIterator.next();

      for (CarryableStorage storage : carryableStorageManager.getStorages()) {
        CarryableStorageType storageType = storage.getType();

        if (storageType == CarryableStorageType.LOOTING_BAG
            || storageType.getContainerIds().isEmpty()
            || !storageType.getContainerIds().contains(itemStack.getId())
            || storageType.getEmptyOnDeathVarbit() == -1
            || storage.getPlugin().getClient().getVarbitValue(storageType.getEmptyOnDeathVarbit())
                != 1) {
          continue;
        }

        storage.getItems().forEach(itemStacksIterator::add);
      }

      if (CarryableStorageType.LOOTING_BAG.getContainerIds().contains(itemStack.getId())) {
        isLootingBagPresent = true;
      }
    }

    if (isLootingBagPresent) {
      carryableStorageManager.getStorages().stream()
          .filter(s -> s.getType() == CarryableStorageType.LOOTING_BAG)
          .findFirst()
          .ifPresent(lootingBag -> explodedItemStacks.addAll(lootingBag.getItems()));
    }

    return explodedItemStacks;
  }

  public static List<ItemStack> filterDestroyedOnDeath(List<ItemStack> itemStacks) {
    ArrayList<ItemStack> filteredItemStacks = new ArrayList<>();
    itemStacks.forEach(itemStack -> filteredItemStacks.add(new ItemStack(itemStack)));

    filteredItemStacks.removeIf(
        itemStack -> ITEM_IDS_DESTROYED_ON_DEATH.contains(itemStack.getId()));

    return filteredItemStacks;
  }
}
