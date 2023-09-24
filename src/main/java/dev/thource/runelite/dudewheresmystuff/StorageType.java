package dev.thource.runelite.dudewheresmystuff;

import java.util.List;

/** StorageType is used to identify Storages. */
public interface StorageType {

  String getName();

  int getItemContainerId();

  boolean isAutomatic();

  String getConfigKey();

  boolean isMembersOnly();

  List<Integer> getAccountTypeBlacklist();
}
