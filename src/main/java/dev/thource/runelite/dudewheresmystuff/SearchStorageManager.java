package dev.thource.runelite.dudewheresmystuff;

public class SearchStorageManager extends StorageManager<StorageType, Storage<StorageType>> {

  protected SearchStorageManager(DudeWheresMyStuffPlugin plugin) {
    super(plugin);
  }

  @Override
  public void load() {
    // No loading necessary
  }

  @Override
  public void save() {
    // No saving necessary
  }

  @Override
  public String getConfigKey() {
    return null;
  }

  @Override
  public Tab getTab() {
    return Tab.SEARCH;
  }
}
