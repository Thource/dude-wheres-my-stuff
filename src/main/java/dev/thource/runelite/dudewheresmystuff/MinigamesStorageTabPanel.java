package dev.thource.runelite.dudewheresmystuff;

import java.util.Comparator;
import net.runelite.client.game.ItemManager;

class MinigamesStorageTabPanel extends
    StorageTabPanel<MinigamesStorageType, MinigamesStorage, MinigamesStorageManager> {

  MinigamesStorageTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config,
      DudeWheresMyStuffPanel pluginPanel, MinigamesStorageManager storageManager) {
    super(itemManager, config, pluginPanel, storageManager);

    remove(sortItemsDropdown);
  }

  @Override
  protected Comparator<MinigamesStorage> getStorageSorter() {
    return Comparator.comparing(s -> s.getType().getName());
  }

  @Override
  protected void rebuildList() {
    removeAll();

    itemsBoxes.clear();
    storageManager.storages.stream().sorted(getStorageSorter()).forEach((storage) -> {
      if (!storage.isEnabled()) {
        return;
      }

      ItemsBox itemsBox = new ItemsBox(itemManager, storage, null, false, showPrice());
      for (ItemStack itemStack : storage.getItems()) {
        if (storage.getType().isAutomatic() || storage.getLastUpdated() != -1L
            || itemStack.getQuantity() > 0) {
          itemsBox.getItems().add(itemStack);
        }
      }
      itemsBox.rebuild();
      itemsBoxes.add(itemsBox);
      add(itemsBox);
    });

    revalidate();
  }

  @Override
  protected boolean showPrice() {
    return false;
  }
}