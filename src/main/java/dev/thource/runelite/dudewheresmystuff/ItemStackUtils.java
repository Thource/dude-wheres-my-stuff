package dev.thource.runelite.dudewheresmystuff;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/** ItemStackUtils provides methods that interact with Lists of ItemStack. */
public class ItemStackUtils {

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
              if (compoundedItemStack.getId() != itemStack.getId()) {
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
    if (itemToAdd.isStackable()) {
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
}
