package dev.thource.runelite.dudewheresmystuff.export.clients;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import dev.thource.runelite.dudewheresmystuff.export.utils.GoogleSheetConnectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoogleSheetClient {
  private final Sheets sheetsService;

  public GoogleSheetClient(String email) {
    this.sheetsService = GoogleSheetConnectionUtils.getSheetsConnection(email);
  }

  public BatchUpdateSpreadsheetResponse writeCellData(
      String spreadsheetId, GridRange gridRange, List<List<CellData>> cellData) {
    try {
      List<RowData> rowData =
          cellData.stream().map(x -> new RowData().setValues(x)).collect(Collectors.toList());
      return sheetsService
          .spreadsheets()
          .batchUpdate(
              spreadsheetId,
              new BatchUpdateSpreadsheetRequest()
                  .setRequests(
                      List.of(
                          new Request()
                              .setUpdateCells(
                                  new UpdateCellsRequest()
                                      .setFields("*")
                                      .setRange(gridRange)
                                      .setRows(new ArrayList<>(rowData))))))
          .execute();
    } catch (Exception e) {
      log.error("Encountered issue with batch write: ", e);
      throw new RuntimeException(e);
    }
  }

  public List<Sheet> getSheetList(String spreadsheetId) {
    try {
      Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
      return spreadsheet.getSheets();

    } catch (Exception e) {
      log.error(
          String.format("Encountered error in getSheetList, spreadsheetId: %s", spreadsheetId), e);
      throw new RuntimeException(e);
    }
  }

  public Sheet getSheet(String spreadsheetId, String sheetTitle) {
    List<Sheet> sheets = getSheetList(spreadsheetId);
    return sheets.stream()
        .filter(x -> x.getProperties().getTitle().equalsIgnoreCase(sheetTitle))
        .findFirst()
        .orElseThrow(
            () -> {
              RuntimeException e =
                  new RuntimeException(
                      String.format(
                          "Failed to retrieve sheet that should have been created for spreadsheetId: %s",
                          spreadsheetId));
              log.error("Unexpected Exception While Writing Headers", e);
              return e;
            });
  }

  public Spreadsheet createOrGetSpreadsheet(String spreadsheetId, String displayName) {
    try {
      if (Objects.equals(spreadsheetId, "")) {
        return sheetsService
            .spreadsheets()
            .create(
                new Spreadsheet().setProperties(new SpreadsheetProperties().setTitle(displayName)))
            .execute();
      }
      return sheetsService.spreadsheets().get(spreadsheetId).execute();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public boolean maybeCreateSheet(String spreadsheetId, String sheetTitle) {
    if (!getSheetExists(spreadsheetId, sheetTitle)) {
      try {
        sheetsService
            .spreadsheets()
            .batchUpdate(
                spreadsheetId,
                new BatchUpdateSpreadsheetRequest()
                    .setRequests(
                        List.of(
                            new Request()
                                .setAddSheet(
                                    new AddSheetRequest()
                                        .setProperties(
                                            new SheetProperties().setTitle(sheetTitle))))))
            .execute();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      return true;
    }
    return false;
  }

  public boolean maybeClearSheet(String spreadsheetId, String sheetTitle) {
    if (getSheetExists(spreadsheetId, sheetTitle)) {
      ClearValuesRequest request = new ClearValuesRequest();
      try {
        sheetsService.spreadsheets().values().clear(spreadsheetId, sheetTitle, request).execute();
        return true;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return false;
  }

  public boolean getSheetExists(String spreadsheetId, String sheetTitle) {
    try {
      Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
      List<Sheet> sheets = spreadsheet.getSheets();
      return sheets.stream()
          .anyMatch(x -> x.getProperties().getTitle().equalsIgnoreCase(sheetTitle));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
