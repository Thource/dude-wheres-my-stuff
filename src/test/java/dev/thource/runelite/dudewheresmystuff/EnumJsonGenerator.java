package dev.thource.runelite.dudewheresmystuff;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Exports every {@link Exportable} enum found in the sibling {@code data} package to a json file of
 * the same name (e.g. {@code FooData} becomes {@code FooData.json}) in the main resources {@code
 * data} directory. Each json file is an object keyed by {@link Exportable#jsonKey()} (usually the
 * constant name), in declaration order.
 *
 * <p>To add a new data set, drop a new enum implementing {@link Exportable} into the {@code data}
 * package and re-run this class; nothing needs registering.
 *
 * <p>Run it with the project directory as the working directory (a gradle JavaExec task does this
 * by default). An output directory can optionally be passed as the only argument.
 */
public final class EnumJsonGenerator {

  /** Implemented by enums whose data should be exported. */
  public interface Exportable {

    /** Key of this constant in the exported json object. */
    String jsonKey();

    /** Anything Gson can serialise (a Map or POJO); becomes this constant's value. */
    Object toJson();
  }

  private static final String DATA_PACKAGE =
      EnumJsonGenerator.class.getPackage().getName() + ".data";
  private static final Path DEFAULT_OUTPUT_DIR =
      Paths.get("src", "main", "resources").resolve(DATA_PACKAGE.replace('.', '/'));

  private static final Gson GSON =
      new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  private EnumJsonGenerator() {}

  /**
   * Finds every {@link Exportable} enum in the {@code data} package (the dir next to this class).
   */
  public static List<Class<?>> findDataEnums() throws IOException {
    URL dataDir = EnumJsonGenerator.class.getResource("data");
    if (dataDir == null || !"file".equals(dataDir.getProtocol())) {
      throw new IllegalStateException("Can't find the compiled data package " + DATA_PACKAGE);
    }

    Path dir;
    try {
      dir = Paths.get(dataDir.toURI());
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }

    List<String> classNames;
    try (Stream<Path> files = Files.list(dir)) {
      classNames =
          files
              .map(path -> path.getFileName().toString())
              // skip non-classes and nested/anonymous classes (Foo$1.class)
              .filter(name -> name.endsWith(".class") && !name.contains("$"))
              .map(name -> name.substring(0, name.length() - ".class".length()))
              .sorted()
              .collect(Collectors.toList());
    }

    List<Class<?>> enums = new ArrayList<>();
    for (String className : classNames) {
      Class<?> clazz;
      try {
        clazz = Class.forName(DATA_PACKAGE + "." + className);
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException(e);
      }

      if (clazz.isEnum() && Exportable.class.isAssignableFrom(clazz)) {
        enums.add(clazz);
      } else {
        System.err.println("Skipping " + clazz.getName() + " (not an Exportable enum)");
      }
    }

    return enums;
  }

  /** Writes every constant of {@code enumClass} to {@code output}, creating parent dirs. */
  public static <E extends Enum<E> & Exportable> void export(Class<E> enumClass, Path output)
      throws IOException {
    Map<String, Object> json = new LinkedHashMap<>();
    for (E constant : enumClass.getEnumConstants()) {
      if (json.put(constant.jsonKey(), constant.toJson()) != null) {
        throw new IllegalStateException(
            "Duplicate json key " + constant.jsonKey() + " in " + enumClass.getSimpleName());
      }
    }

    Path parent = output.toAbsolutePath().getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }

    try (Writer writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
      GSON.toJson(json, writer);
      writer.write('\n');
    }

    System.out.println("Wrote " + json.size() + " entries to " + output);
  }

  /** Exports every enum in the {@code data} package to {@code outputDir}. */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static void exportAll(Path outputDir) throws IOException {
    List<Class<?>> enums = findDataEnums();
    if (enums.isEmpty()) {
      throw new IllegalStateException("No Exportable enums found in " + DATA_PACKAGE);
    }

    for (Class<?> clazz : enums) {
      export((Class) clazz, outputDir.resolve(clazz.getSimpleName() + ".json"));
    }
  }

  public static void main(String[] args) throws IOException {
    exportAll(args.length > 0 ? Paths.get(args[0]) : DEFAULT_OUTPUT_DIR);
  }
}
