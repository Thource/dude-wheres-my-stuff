package dev.thource.runelite.dudewheresmystuff.export.utils;

import dev.thource.runelite.dudewheresmystuff.export.model.GridRange;

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
