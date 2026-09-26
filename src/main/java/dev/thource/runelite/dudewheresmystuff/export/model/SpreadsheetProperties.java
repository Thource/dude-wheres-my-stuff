package dev.thource.runelite.dudewheresmystuff.export.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/** Plain replacement for {@code com.google.api.services.sheets.v4.model.SpreadsheetProperties}. */
@Getter
@Setter
@Accessors(chain = true)
public class SpreadsheetProperties {
  private String title;
}
