package dev.thource.runelite.dudewheresmystuff.export.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Plain replacement for {@code com.google.api.services.sheets.v4.model.GridRange}. Field names
 * match the Google Sheets REST API JSON keys exactly so instances serialize/deserialize correctly
 * via Gson.
 */
@Getter
@Setter
@Accessors(chain = true)
public class GridRange {
  private Integer sheetId;
  private Integer startRowIndex;
  private Integer endRowIndex;
  private Integer startColumnIndex;
  private Integer endColumnIndex;
}
