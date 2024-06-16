package dev.thource.runelite.dudewheresmystuff.export.exporters;

import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Storage;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.StorageManagerManager;
import dev.thource.runelite.dudewheresmystuff.export.DataExportWriter;
import dev.thource.runelite.dudewheresmystuff.export.DataExporter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StorageManagerExporter implements DataExporter {

  private final DataExportWriter writer;

  private final StorageManagerManager storageManagerManager;

  public StorageManagerExporter(
      DataExportWriter writer, StorageManagerManager storageManagerManager) {
    this.writer = writer;
    this.storageManagerManager = storageManagerManager;
  }

  private String writeMergedItems() throws IOException {
    // Include a CSV header describing the columns
    writer.writeHeader(true, false);

    for (ItemStack itemStack : getMergedItems()) {
      writer.writeItemStack(itemStack, null, null, true);
    }

    return writer.getFileLocation();
  }

  private void writeStorageManager(StorageManager<?, ?> storageManager, boolean mergeItems) {
    if (!storageManager.isEnabled()) {
      return;
    }

    for (Storage<?> storage : storageManager.getStorages()) {
      if (!storage.isEnabled() || !storage.isWithdrawable()) {
        continue;
      }

      for (ItemStack itemStack : storage.getItems()) {
        if (itemStack.getQuantity() == 0 || itemStack.getId() == -1) {
          continue;
        }

        writer.writeItemStack(itemStack, storageManager, storage, mergeItems);
      }
    }
  }

  private String writeAll() throws IOException {

    // Include a CSV header describing the columns
    writer.writeHeader(false, true);

    for (StorageManager<?, ?> storageManager : storageManagerManager.getStorageManagers()) {
      writeStorageManager(storageManager, false);
    }
    return writer.getFileLocation();
  }

  public String export(boolean mergeItems) throws IllegalArgumentException, IOException {
    if (mergeItems) {
      return writeMergedItems();
    } else {
      return writeAll();
    }
  }

  /**
   * Gets all known withdrawable items
   *
   * <p>If the same item is in multiple storages, the item stacks are combined. "Same item" refers
   * to items with the same canonical ID, but note that the actual ID of the stack will be set to
   * the ID of one of the items arbitrarily. It is therefore recommended that callers do not use the
   * IDs, only the canonical IDs.
   *
   * @return The item stacks
   */
  private Collection<ItemStack> getMergedItems() {
    // We need to deduplicate and combine item stacks if they're in multiple
    // storages. This is a map from the stack's canonical (unnoted,
    // un-placeholdered) ID to its stack.
    TreeMap<Integer, ItemStack> items = new TreeMap<>();

    storageManagerManager
        .getStorages()
        .filter(Storage::isWithdrawable)
        .map(Storage::getItems)
        .flatMap(List::stream)
        .forEach(
            (ItemStack stack) -> {
              if (stack.getQuantity() == 0 || stack.getId() == -1) {
                return;
              }

              int id = stack.getCanonicalId();

              ItemStack existing = items.get(id);
              if (existing == null) {
                // No item yet, insert a copy so that we can modify their quantities later if
                // necessary
                items.put(id, new ItemStack(stack));
              } else {
                // This item was already in there. Update the quantity to include the new stack.
                existing.setQuantity(stack.getQuantity() + existing.getQuantity());
              }
            });

    return items.values();
  }
}
