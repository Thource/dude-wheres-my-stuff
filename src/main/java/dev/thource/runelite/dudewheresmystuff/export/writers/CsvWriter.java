package dev.thource.runelite.dudewheresmystuff.export.writers;

import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Storage;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.export.DataExportWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import joptsimple.internal.Strings;
import net.runelite.client.util.Filepath;

public class CsvWriter implements DataExportWriter {
  private final BufferedWriter writer;

  private final Filepath filepath;

  public CsvWriter(String displayName, Filepath pluginDirectory) throws IOException {
    if (displayName.isEmpty()) {
      throw new IllegalArgumentException("No display name");
    }

    var userDir = pluginDirectory.join(displayName);
    if (!userDir.exists()) {
      userDir.createDirectories();
    }

    filepath = userDir.join(new SimpleDateFormat("yyyyMMdd'T'HHmmss'.csv'").format(new Date()));
    this.writer = filepath.openBufferedWriter(StandardOpenOption.CREATE, StandardOpenOption.WRITE);
  }

  @Override
  public void writeItemStack(
      ItemStack itemStack,
      @Nullable StorageManager<?, ?> storageManager,
      @Nullable Storage<?> storage,
      boolean mergeItems) {
    try {
      writer.write(itemStack.toCsvString(mergeItems, storageManager, storage));
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void writeHeader(boolean mergeItems, boolean shouldSplitUp) throws IOException {
    String headers = Strings.join(ItemStack.getHeaders(mergeItems, shouldSplitUp), ",") + '\n';
    writer.write(headers);
  }

  @Override
  public String getFileLocation() {
    return this.filepath.toString();
  }

  @Override
  public void close() throws IOException {
    writer.close();
  }
}
