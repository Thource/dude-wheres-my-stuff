package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemsBox;
import dev.thource.runelite.dudewheresmystuff.Storage;
import dev.thource.runelite.dudewheresmystuff.StorageTabPanel;
import java.util.Comparator;
import net.runelite.client.game.ItemManager;

public class MinigamesStorageTabPanel extends
    StorageTabPanel<MinigamesStorageType, MinigamesStorage, MinigamesStorageManager> {

  public MinigamesStorageTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config,
      MinigamesStorageManager storageManager) {
    super(itemManager, config, storageManager);

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
    storageManager.getStorages().stream().filter(Storage::isEnabled).sorted(getStorageSorter())
        .forEach(storage -> {
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
