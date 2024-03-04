package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemBox;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Storage;
import dev.thource.runelite.dudewheresmystuff.StoragePanel;
import java.util.List;

public class DeathpileStoragePanel extends StoragePanel {

  /**
   * A constructor.
   *
   * @param plugin             the plugin
   * @param storage            the storage that this panel represents
   * @param showPrice          if prices should be shown
   * @param displayEmptyStacks if empty stacks should be shown
   */
  public DeathpileStoragePanel(DudeWheresMyStuffPlugin plugin,
      Storage<?> storage, boolean showPrice,
      boolean displayEmptyStacks) {
    super(plugin, storage, showPrice, displayEmptyStacks);
  }

  @Override
  protected ItemBox createItemBox(ItemStack itemStack) {
    return new DeathpileItemBox(plugin, itemStack, displayEmptyStacks, (Deathpile) storage);
  }

  @Override
  protected void redrawItems() {
    super.redrawItems();

    setItemBoxesPriorities();
  }

  public void setItemBoxesPriorities() {
    getItemBoxes().forEach(itemBox -> ((DeathpileItemBox) itemBox).resetPriority());

    List<ItemStack> pickupOrder = ((Deathpile) storage).getPickupOrder();
    for (int i = 0; i < pickupOrder.size(); i++) {
      ItemStack itemStack = pickupOrder.get(i);
      for (ItemBox itemBox : getItemBoxes()) {
        DeathpileItemBox deathpileItemBox = (DeathpileItemBox) itemBox;

        if (itemStack.getId() != itemBox.getItemId()
            || itemStack.getQuantity() != itemBox.getItemQuantity()
            || deathpileItemBox.isPrioritized()) {
          continue;
        }

        deathpileItemBox.setPriority(i + 1);
      }
    }
  }
}
