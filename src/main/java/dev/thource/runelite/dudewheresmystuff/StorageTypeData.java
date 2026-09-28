package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;

/**
 * The properties every json-backed storage type has. This is the shape of one entry in a {@code
 * data/*.json} file. Enums with extra properties extend this and add their own fields.
 *
 * <p>The test-side data enums build instances of this (or a subclass) and hand them to {@code
 * EnumJsonGenerator}, so the json field names always match this class.
 */
@Getter
public class StorageTypeData {
  private final String name;
  private final int itemContainerId;
  private final String configKey;

  public StorageTypeData(String name, int itemContainerId, String configKey) {
    this.name = name;
    this.itemContainerId = itemContainerId;
    this.configKey = configKey;
  }
}