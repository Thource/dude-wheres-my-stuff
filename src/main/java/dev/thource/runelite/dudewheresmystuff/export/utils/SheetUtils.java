package dev.thource.runelite.dudewheresmystuff.export.utils;

import com.google.api.services.sheets.v4.model.GridRange;

public class SheetUtils {
  public static GridRange getGridRange(
      int sheetId, int startColumnNumber, int startRow, int endColumnNumber, int endRow) {
    return new GridRange()
        .setSheetId(sheetId)
        .setStartColumnIndex(startColumnNumber)
        .setStartRowIndex(startRow)
        .setEndColumnIndex(endColumnNumber)
        .setEndRowIndex(endRow);
  }
}
