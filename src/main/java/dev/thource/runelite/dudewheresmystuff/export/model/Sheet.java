package dev.thource.runelite.dudewheresmystuff.export.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/** Plain replacement for {@code com.google.api.services.sheets.v4.model.Sheet}. */
@Getter
@Setter
@Accessors(chain = true)
public class Sheet {
  private SheetProperties properties;
}
