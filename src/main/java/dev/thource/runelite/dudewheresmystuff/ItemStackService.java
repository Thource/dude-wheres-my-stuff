package dev.thource.runelite.dudewheresmystuff;

import java.util.ArrayList;
import java.util.List;

public class ItemStackService {

  public static List<ItemStack> compound(List<ItemStack> itemStacks, boolean compoundUnstackables) {
    ArrayList<ItemStack> compoundedItemStacks = new ArrayList<>();

    itemStacks.forEach(itemStack -> {
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
}
