package dev.thource.runelite.dudewheresmystuff.export.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/** Plain replacement for {@code com.google.api.services.sheets.v4.model.SheetProperties}. */
@Getter
@Setter
@Accessors(chain = true)
public class SheetProperties {
  private Integer sheetId;
  private String title;
  private GridProperties gridProperties;
}
