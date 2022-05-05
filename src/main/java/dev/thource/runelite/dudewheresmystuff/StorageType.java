package dev.thource.runelite.dudewheresmystuff;

/** StorageType is used to identify Storages. */
public interface StorageType {

  String getName();

  int getItemContainerId();

  boolean isAutomatic();

  String getConfigKey();

  boolean isMembersOnly();
}
