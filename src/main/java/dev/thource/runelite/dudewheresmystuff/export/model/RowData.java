package dev.thource.runelite.dudewheresmystuff.export.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/** Plain replacement for {@code com.google.api.services.sheets.v4.model.RowData}. */
@Getter
@Setter
@Accessors(chain = true)
public class RowData {
  private List<CellData> values;
}
