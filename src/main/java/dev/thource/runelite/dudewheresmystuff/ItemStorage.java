package dev.thource.runelite.dudewheresmystuff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Getter;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;

/** ItemStorage builds upon Storage by adding items and some other functionality. */
public class ItemStorage<T extends StorageType> extends Storage<T> {

  @Nullable protected int[] varbits = null;
  // used when there are items before the varbit items
  protected int varbitItemOffset = 0;
  @Nullable protected final ItemContainerWatcher itemContainerWatcher;
  @Getter protected List<ItemStack> items = new ArrayList<>();
  protected boolean hasStaticItems = false;

  protected ItemStorage(T type, DudeWheresMyStuffPlugin plugin) {
    super(type, plugin);

    itemContainerWatcher = ItemContainerWatcher.getWatcher(type.getItemContainerId());
  }

  @Override
  public boolean onVarbitChanged() {
    if (varbits == null) {
      return false;
    }

    boolean updated = false;

    for (int i = 0; i < varbits.length; i++) {
      int varbit = varbits[i];
      ItemStack itemStack = items.get(i + varbitItemOffset);

      int newPoints = plugin.getClient().getVarbitValue(varbit);
      if (newPoints == itemStack.getQuantity()) {
        continue;
      }

      itemStack.setQuantity(newPoints);
      updated = true;
    }

    return updated;
  }

  @Override
  public boolean onGameTick() {
    if (itemContainerWatcher != null && itemContainerWatcher.wasJustUpdated()) {
      items.clear();
      items.addAll(itemContainerWatcher.getItems());
      lastUpdated = System.currentTimeMillis();

      return true;
    }

    return false;
  }

  @Override
  public boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    if (itemContainerWatcher != null
        || type.getItemContainerId() != itemContainerChanged.getContainerId()) {
      return false;
    }

    ItemContainer itemContainer = itemContainerChanged.getItemContainer();
    if (itemContainer == null) {
      return false;
    }

    resetItems();
    for (Item item : itemContainer.getItems()) {
      if (hasStaticItems) {
        items.stream()
            .filter(i -> item.getId() == i.getId())
            .findFirst()
            .ifPresent(i -> i.setQuantity(item.getQuantity()));
        continue;
      }

      if (item.getId() == -1) {
        items.add(new ItemStack(item.getId(), "empty slot", 1, 0, 0, false));
        continue;
      }

      ItemComposition itemComposition = plugin.getItemManager().getItemComposition(item.getId());
      if (itemComposition.getPlaceholderTemplateId() == -1) {
        items.add(new ItemStack(item.getId(), item.getQuantity(), plugin));
      }
    }

    lastUpdated = System.currentTimeMillis();

    return true;
  }

  @Override
  protected ArrayList<String> getSaveValues() {
    ArrayList<String> saveValues = super.getSaveValues();

    saveValues.add(SaveFieldFormatter.format(items, hasStaticItems));

    return saveValues;
  }

  @Override
  protected void loadValues(ArrayList<String> values) {
    super.loadValues(values);

    if (hasStaticItems) {
      SaveFieldLoader.loadItemsIntoList(values, items);
    } else {
      items = SaveFieldLoader.loadItems(values, items, plugin);
    }
  }

  @Override
  public long getTotalValue() {
    long sum = 0L;
    for (ItemStack item : items) {
      sum += item.getTotalGePrice();
    }
    return sum;
  }

  @Override
  public void reset() {
    resetItems();

    super.reset();
  }

  private void resetItems() {
    if (hasStaticItems) {
      items.forEach(item -> item.setQuantity(0));
    } else {
      items.clear();
    }
  }

  /**
   * Removes a quantity of items with the id specified.
   *
   * @param id       the item id of the item to remove
   * @param quantity the amount of the item to remove
   * @return the amount of items removed from the storage
   */
  public long remove(int id, long quantity) {
    if (quantity <= 0) {
      return 0;
    }

    long itemsRemoved = 0;
    Iterator<ItemStack> listIterator = items.iterator();
    while (listIterator.hasNext() && quantity > 0) {
      ItemStack itemStack = listIterator.next();
      if (itemStack.getId() != id) {
        continue;
      }

      long qtyToRemove = Math.min(quantity, itemStack.getQuantity());
      quantity -= qtyToRemove;
      itemsRemoved += qtyToRemove;
      itemStack.setQuantity(itemStack.getQuantity() - qtyToRemove);
      if (itemStack.getQuantity() <= 0) {
        listIterator.remove();
      }
    }

    return itemsRemoved;
  }
}
