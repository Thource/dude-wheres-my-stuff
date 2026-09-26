package dev.thource.runelite.dudewheresmystuff.export.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/** Plain replacement for {@code com.google.api.services.sheets.v4.model.GridProperties}. */
@Getter
@Setter
@Accessors(chain = true)
public class GridProperties {
  private Integer rowCount;
  private Integer columnCount;
}
