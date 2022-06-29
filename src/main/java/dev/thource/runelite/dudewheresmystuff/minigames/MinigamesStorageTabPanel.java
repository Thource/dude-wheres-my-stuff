package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StorageTabPanel;
import java.util.Comparator;

/** MinigamesStorageTabPanel is responsible for displaying minigame data to the player. */
public class MinigamesStorageTabPanel
    extends StorageTabPanel<MinigamesStorageType, MinigamesStorage, MinigamesStorageManager> {

  /** A constructor. */
  public MinigamesStorageTabPanel(
      DudeWheresMyStuffPlugin plugin, MinigamesStorageManager storageManager) {
    super(plugin, storageManager);

    remove(sortItemsDropdown);
  }

  @Override
  protected Comparator<MinigamesStorage> getStorageSorter() {
    return Comparator.comparing(s -> s.getType().getName());
  }
  //
  //  @Override
  //  protected void rebuildList() {
  //    removeAll();
  //
  //    storagePanels.clear();
  //    storageManager.getStorages().stream()
  //        .filter(Storage::isEnabled)
  //        .filter(
  //            storage -> {
  //              if (config.showEmptyStorages()) {
  //                return true;
  //              }
  //
  //              return storage.getItems().stream()
  //                  .anyMatch(itemStack -> itemStack.getId() != -1 && itemStack.getQuantity() >
  // 0);
  //            })
  //        .sorted(getStorageSorter())
  //        .forEach(
  //            storage -> {
  //              StoragePanel storagePanel =
  //                  new StoragePanel(
  //                      itemManager,
  //                      storageManager.getPluginManager(),
  //                      storageManager.getItemIdentificationPlugin(),
  //                      storageManager.getItemIdentificationConfig(), storage,
  //                      null,
  //                      showPrice());
  //              for (ItemStack itemStack : storage.getItems()) {
  //                if (storage.getType().isAutomatic()
  //                    || storage.getLastUpdated() != -1L
  //                    || itemStack.getQuantity() > 0) {
  //                  storagePanel.getItems().add(itemStack);
  //                }
  //              }
  //              storagePanel.rebuild();
  //              storagePanels.add(storagePanel);
  //              add(storagePanel);
  //            });
  //
  //    revalidate();
  //  }

}
