package dev.thource.runelite.dudewheresmystuff.export.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/** Plain replacement for {@code com.google.api.services.sheets.v4.model.Spreadsheet}. */
@Getter
@Setter
@Accessors(chain = true)
public class Spreadsheet {
  private String spreadsheetId;
  private String spreadsheetUrl;
  private SpreadsheetProperties properties;
  private List<Sheet> sheets;
}
