package dev.thource.runelite.dudewheresmystuff;

import static net.runelite.client.RuneLite.RUNELITE_DIR;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import javax.annotation.Nullable;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class DataExporter {

  private static final File EXPORT_DIR = new File(RUNELITE_DIR, "dudewheresmystuff");

  private final String displayName;
  private final StorageManagerManager storageManagerManager;
  @Setter private boolean mergeItems = false;

  DataExporter(String displayName, StorageManagerManager storageManagerManager) {
    this.displayName = displayName;
    this.storageManagerManager = storageManagerManager;
  }

  void writeItemStack(BufferedWriter writer, ItemStack itemStack) throws IOException {
    writeItemStack(writer, itemStack, null, null);
  }

  void writeItemStack(BufferedWriter writer, ItemStack itemStack,
      @Nullable StorageManager<?, ?> storageManager, @Nullable Storage<?> storage)
      throws IOException {
    String name = itemStack.getName();
    if (!mergeItems && itemStack.getId() != itemStack.getCanonicalId() && itemStack.isStackable()) {
      name += " (noted)";
    }
    String escapedName = name.replace(",", "").replace("\n", "");

    writer.write(String.format("%d,%s,%d", itemStack.getCanonicalId(), escapedName,
        itemStack.getQuantity()));
    if (!mergeItems && storage != null && storageManager != null) {
      writer.write(String.format(",%s,%s", storageManager.getConfigKey(), storage.getName()));
    }
    writer.write(String.format("%n"));
  }

  void writeMergedItems(BufferedWriter writer) throws IOException {
    // Include a CSV header describing the columns
    writer.write("ID,Name,Quantity\n");

    for (ItemStack itemStack : getMergedItems()) {
      writeItemStack(writer, itemStack);
    }
  }

  void writeStorageManager(BufferedWriter writer, StorageManager<?, ?> storageManager)
      throws IOException {
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

        writeItemStack(writer, itemStack, storageManager, storage);
      }
    }
  }

  void writeAll(BufferedWriter writer) throws IOException {
    // Include a CSV header describing the columns
    writer.write("ID,Name,Quantity,Storage Category,Storage Type\n");

    for (StorageManager<?, ?> storageManager : storageManagerManager.getStorageManagers()) {
      writeStorageManager(writer, storageManager);
    }
  }

  String export() throws IllegalArgumentException, IOException {
    if (displayName.equals("")) {
      throw new IllegalArgumentException("No display name");
    }

    File userDir = new File(EXPORT_DIR, displayName);
    String fileName = new SimpleDateFormat("yyyyMMdd'T'HHmmss'.csv'").format(
        new Date());
    String filePath = userDir + File.separator + fileName;

    //noinspection ResultOfMethodCallIgnored
    userDir.mkdirs();
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
      if (mergeItems) {
        writeMergedItems(writer);
      } else {
        writeAll(writer);
      }
    }

    return filePath;
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

    storageManagerManager.getStorages()
        .filter(Storage::isWithdrawable)
        .map(Storage::getItems)
        .flatMap(List::stream)
        .forEach((ItemStack stack) -> {
          if (stack.getQuantity() == 0 || stack.getId() == -1) {
            return;
          }

          int id = stack.getCanonicalId();

          ItemStack existing = items.get(id);
          if (existing == null) {
            // No item yet, insert a copy so that we can modify their quantities later if necessary
            items.put(id, new ItemStack(stack));
          } else {
            // This item was already in there. Update the quantity to include the new stack.
            existing.setQuantity(stack.getQuantity() + existing.getQuantity());
          }
        });

    return items.values();
  }
}
