package dev.thource.runelite.dudewheresmystuff.export.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/** Plain replacement for {@code com.google.api.services.sheets.v4.model.ExtendedValue}. */
@Getter
@Setter
@Accessors(chain = true)
public class ExtendedValue {
  private String stringValue;
  private Double numberValue;
  private Boolean boolValue;
  private String formulaValue;
}
