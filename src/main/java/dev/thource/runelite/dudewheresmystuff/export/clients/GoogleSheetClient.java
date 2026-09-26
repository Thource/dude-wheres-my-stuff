package dev.thource.runelite.dudewheresmystuff.export.clients;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.thource.runelite.dudewheresmystuff.export.model.CellData;
import dev.thource.runelite.dudewheresmystuff.export.model.GridRange;
import dev.thource.runelite.dudewheresmystuff.export.model.RowData;
import dev.thource.runelite.dudewheresmystuff.export.model.Sheet;
import dev.thource.runelite.dudewheresmystuff.export.model.SheetProperties;
import dev.thource.runelite.dudewheresmystuff.export.model.Spreadsheet;
import dev.thource.runelite.dudewheresmystuff.export.model.SpreadsheetProperties;
import dev.thource.runelite.dudewheresmystuff.export.utils.GoogleSheetConnectionUtils;
import dev.thource.runelite.dudewheresmystuff.export.utils.GoogleSheetConnectionUtils.SheetsClient;
import dev.thource.runelite.dudewheresmystuff.export.utils.GoogleSheetsAuthException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoogleSheetClient {
  private final SheetsClient sheetsClient;
  private final Gson gson;

  public GoogleSheetClient(String email, Gson gson) {
    this.sheetsClient = GoogleSheetConnectionUtils.getSheetsConnection(email);
    this.gson = gson;
  }

  public JsonObject writeCellData(
      String spreadsheetId, GridRange gridRange, List<List<CellData>> cellData) {
    try {
      List<RowData> rowData =
          cellData.stream().map(x -> new RowData().setValues(x)).collect(Collectors.toList());

      JsonObject updateCellsRequest = new JsonObject();
      updateCellsRequest.addProperty("fields", "*");
      updateCellsRequest.add("range", gson.toJsonTree(gridRange));
      JsonArray rows = new JsonArray();
      rowData.forEach(row -> rows.add(gson.toJsonTree(row)));
      updateCellsRequest.add("rows", rows);

      JsonObject request = new JsonObject();
      request.add("updateCells", updateCellsRequest);

      JsonArray requests = new JsonArray();
      requests.add(request);

      JsonObject body = new JsonObject();
      body.add("requests", requests);

      return sheetsClient.batchUpdate(spreadsheetId, body);
    } catch (GoogleSheetsAuthException e) {
      throw e;
    } catch (Exception e) {
      log.error("Encountered issue with batch write: ", e);
      throw new RuntimeException(e);
    }
  }

  public List<Sheet> getSheetList(String spreadsheetId) {
    try {
      Spreadsheet spreadsheet = fetchSpreadsheet(spreadsheetId);
      return spreadsheet.getSheets();
    } catch (GoogleSheetsAuthException e) {
      throw e;
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
        Spreadsheet newSpreadsheet =
            new Spreadsheet().setProperties(new SpreadsheetProperties().setTitle(displayName));
        JsonObject response =
            sheetsClient.createSpreadsheet(gson.toJsonTree(newSpreadsheet).getAsJsonObject());
        return gson.fromJson(response, Spreadsheet.class);
      }
      return fetchSpreadsheet(spreadsheetId);
    } catch (GoogleSheetsAuthException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public boolean maybeCreateSheet(String spreadsheetId, String sheetTitle) {
    if (!getSheetExists(spreadsheetId, sheetTitle)) {
      try {
        JsonObject properties = new JsonObject();
        properties.addProperty("title", sheetTitle);

        JsonObject addSheetRequest = new JsonObject();
        addSheetRequest.add("properties", properties);

        JsonObject request = new JsonObject();
        request.add("addSheet", addSheetRequest);

        JsonArray requests = new JsonArray();
        requests.add(request);

        JsonObject body = new JsonObject();
        body.add("requests", requests);

        sheetsClient.batchUpdate(spreadsheetId, body);
      } catch (GoogleSheetsAuthException e) {
        throw e;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      return true;
    }
    return false;
  }

  public boolean maybeClearSheet(String spreadsheetId, String sheetTitle) {
    if (getSheetExists(spreadsheetId, sheetTitle)) {
      try {
        sheetsClient.clearValues(spreadsheetId, sheetTitle);
        return true;
      } catch (GoogleSheetsAuthException e) {
        throw e;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return false;
  }

  public boolean getSheetExists(String spreadsheetId, String sheetTitle) {
    try {
      Spreadsheet spreadsheet = fetchSpreadsheet(spreadsheetId);
      List<Sheet> sheets = spreadsheet.getSheets();
      return sheets.stream()
          .anyMatch(x -> x.getProperties().getTitle().equalsIgnoreCase(sheetTitle));
    } catch (GoogleSheetsAuthException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void updateSheetProperties(String spreadsheetId, SheetProperties sheetProperties) {
    try {
      JsonObject updateSheetPropertiesRequest = new JsonObject();
      updateSheetPropertiesRequest.addProperty("fields", "*");
      updateSheetPropertiesRequest.add("properties", gson.toJsonTree(sheetProperties));

      JsonObject request = new JsonObject();
      request.add("updateSheetProperties", updateSheetPropertiesRequest);

      JsonArray requests = new JsonArray();
      requests.add(request);

      JsonObject body = new JsonObject();
      body.add("requests", requests);

      sheetsClient.batchUpdate(spreadsheetId, body);
    } catch (GoogleSheetsAuthException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Spreadsheet fetchSpreadsheet(String spreadsheetId) throws IOException {
    JsonObject response = sheetsClient.getSpreadsheet(spreadsheetId);
    return gson.fromJson(response, Spreadsheet.class);
  }
}
