package dev.thource.runelite.dudewheresmystuff;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the json-loaded data for one enum. Enums can't be injected, so the owning enum keeps a
 * static instance of this, exposes a {@code load(Gson)} method that the plugin calls with its
 * injected Gson, and looks its own data up with {@link #get(Enum)}.
 *
 * <p>The json file is {@code data/<fileName>.json} (relative to this package). It's an object keyed
 * by constant name whose values deserialise to {@code D}. {@code fileName} is the name of the
 * test-side data enum that {@code EnumJsonGenerator} exported, e.g. {@code
 * PlayerOwnedHouseStorageTypeData}.
 *
 * <p>This class does no reflection of its own: the enum passes in its own {@code values()}, the
 * data is stored by ordinal, and the json is parsed as a tree before each entry is handed to the
 * injected Gson.
 *
 * @param <E> the enum the data belongs to
 * @param <D> the per-constant data type (e.g. {@link StorageTypeData} or a subclass)
 */
public final class JsonEnumData<E extends Enum<E>, D> {

  private final E[] constants;
  private final Class<D> dataClass;
  private final String resource;

  // indexed by ordinal; null until loaded, assigned once fully built so safely published
  private volatile List<D> data;

  /**
   * @param constants the enum's {@code values()}
   * @param dataClass the data type each json entry deserialises to
   * @param fileName json file name without the {@code .json} extension
   */
  public JsonEnumData(E[] constants, Class<D> dataClass, String fileName) {
    this.constants = constants;
    this.dataClass = dataClass;
    this.resource = "data/" + fileName + ".json";
  }

  /** Loads the data. Safe to call more than once; only the first call does any work. */
  public synchronized void load(Gson gson) {
    if (data != null) {
      return;
    }

    JsonObject entries = read(gson);
    List<D> loaded = new ArrayList<>(constants.length);
    for (E constant : constants) {
      JsonElement entry = entries.get(constant.name());
      if (entry == null || entry.isJsonNull()) {
        throw new IllegalStateException(
            "No entry for " + constant.name() + " in " + resource + "; regenerate the json");
      }

      loaded.add(gson.fromJson(entry, dataClass));
    }

    data = Collections.unmodifiableList(loaded);
  }

  /** Returns the data for {@code constant}; throws if {@link #load(Gson)} hasn't run yet. */
  public D get(E constant) {
    List<D> loaded = data;
    if (loaded == null) {
      throw new IllegalStateException(resource + " isn't loaded; call the enum's load(Gson) first");
    }

    return loaded.get(constant.ordinal());
  }

  private JsonObject read(Gson gson) {
    try (InputStream in = JsonEnumData.class.getResourceAsStream(resource)) {
      if (in == null) {
        throw new IllegalStateException("Missing resource " + resource);
      }

      try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
        return gson.fromJson(reader, JsonObject.class);
      }
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read " + resource, e);
    }
  }
}
