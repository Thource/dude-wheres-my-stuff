package dev.thource.runelite.dudewheresmystuff;

public interface StorageType {

  String getName();

  int getItemContainerId();

  boolean isAutomatic();

  String getConfigKey();

  boolean isMembersOnly();
}
