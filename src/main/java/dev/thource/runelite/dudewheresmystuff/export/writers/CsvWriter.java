package dev.thource.runelite.dudewheresmystuff.export.writers;

import static net.runelite.client.RuneLite.RUNELITE_DIR;

import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Storage;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.export.DataExportWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import joptsimple.internal.Strings;

public class CsvWriter implements DataExportWriter {
  private static final File EXPORT_DIR = new File(RUNELITE_DIR, "dudewheresmystuff");

  private final BufferedWriter writer;

  private final String fileLocation;

  public CsvWriter(String displayName) {
    if (displayName.equals("")) {
      throw new IllegalArgumentException("No display name");
    }

    File userDir = new File(EXPORT_DIR, displayName);
    String fileName = new SimpleDateFormat("yyyyMMdd'T'HHmmss'.csv'").format(new Date());
    this.fileLocation = userDir + File.separator + fileName;
    userDir.mkdirs();
    try {
      this.writer = new BufferedWriter(new FileWriter(this.fileLocation));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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
  public void writeHeader(boolean mergeItems, boolean shouldSplitUp) {
    try {
      String headers = Strings.join(ItemStack.getHeaders(mergeItems, shouldSplitUp), ",") + '\n';
      if (mergeItems) {
        writer.write(headers);
      } else {
        writer.write(headers);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getFileLocation() {
    return this.fileLocation;
  }

  @Override
  public void close() {
    try {
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
