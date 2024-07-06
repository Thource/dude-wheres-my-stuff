package dev.thource.runelite.dudewheresmystuff.export;

import java.io.IOException;

public interface DataExporter {
  String export(boolean mergeItems) throws IOException;
}
