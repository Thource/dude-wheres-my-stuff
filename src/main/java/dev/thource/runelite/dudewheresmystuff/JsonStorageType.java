package dev.thource.runelite.dudewheresmystuff;

/**
 * Base for storage type enums whose data comes from json. Implementing enums only supply {@link
 * #getData()}; the shared getters delegate to it.
 *
 * @param <D> the enum's data type; {@link StorageTypeData} itself, or a subclass with extra fields
 */
public interface JsonStorageType<D extends StorageTypeData> extends StorageType {

  D getData();

  default String getName() {
    return getData().getName();
  }

  default int getItemContainerId() {
    return getData().getItemContainerId();
  }

  default String getConfigKey() {
    return getData().getConfigKey();
  }
}
