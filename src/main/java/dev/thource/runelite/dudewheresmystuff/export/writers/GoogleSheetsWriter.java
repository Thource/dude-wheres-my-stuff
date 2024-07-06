package dev.thource.runelite.dudewheresmystuff.export.writers;

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Storage;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.export.DataExportWriter;
import dev.thource.runelite.dudewheresmystuff.export.clients.GoogleSheetClient;
import dev.thource.runelite.dudewheresmystuff.export.utils.SheetUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoogleSheetsWriter implements DataExportWriter {
  DudeWheresMyStuffPlugin plugin;

  @Getter private String spreadsheetId;

  private final String displayName;

  private final GoogleSheetClient googleSheetClient;

  private final List<List<CellData>> itemBuffer;

  public GoogleSheetsWriter(DudeWheresMyStuffPlugin plugin, String displayName) {
    this.plugin = plugin;
    this.spreadsheetId = plugin.getConfig().googleSpreadSheetId();
    this.displayName = displayName;
    this.googleSheetClient = new GoogleSheetClient("rldudewms@gmail.com");
    itemBuffer = new ArrayList<>();
  }

  @Override
  public void writeItemStack(
      ItemStack itemStack,
      @Nullable StorageManager<?, ?> storageManager,
      @Nullable Storage<?> storage,
      boolean mergeItems) {
    itemBuffer.add(itemStack.getCellDataList(mergeItems, storageManager, storage));
  }

  @Override
  public void writeHeader(boolean mergeItems, boolean shouldSplitUp) {
    Spreadsheet spreadsheet = googleSheetClient.createOrGetSpreadsheet(spreadsheetId, displayName);
    spreadsheetId = spreadsheet.getSpreadsheetId();
    plugin.getConfig().setGoogleSpreadSheetId(spreadsheetId);
    plugin.getConfig().setGoogleSpreadSheetUrl(spreadsheet.getSpreadsheetUrl());
    googleSheetClient.maybeClearSheet(spreadsheetId, displayName);
    googleSheetClient.maybeCreateSheet(spreadsheetId, displayName);

    List<CellData> headers =
        ItemStack.getHeaders(mergeItems, shouldSplitUp).stream()
            .map(x -> new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(x)))
            .collect(Collectors.toList());
    Sheet sheet = googleSheetClient.getSheet(spreadsheetId, displayName);

    GridRange gridRange =
        SheetUtils.getGridRange(sheet.getProperties().getSheetId(), 0, 0, headers.size() + 1, 1);
    googleSheetClient.writeCellData(spreadsheetId, gridRange, List.of(headers));
  }

  @Override
  public String getFileLocation() {
    return googleSheetClient.createOrGetSpreadsheet(spreadsheetId, displayName).getSpreadsheetUrl();
  }

  @Override
  public void close() {
    if (itemBuffer.isEmpty()) return;
    Sheet sheet = googleSheetClient.getSheet(spreadsheetId, displayName);
    GridRange range =
        SheetUtils.getGridRange(
            sheet.getProperties().getSheetId(),
            0,
            1,
            itemBuffer.get(0).size() + 1,
            itemBuffer.size() + 1);
    googleSheetClient.writeCellData(spreadsheetId, range, itemBuffer);
    itemBuffer.clear();
  }
}
