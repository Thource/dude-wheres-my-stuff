package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStorage;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.StoragePanel;
import lombok.Getter;

@Getter
abstract class MinigamesStorage extends ItemStorage<MinigamesStorageType> {

  protected MinigamesStorage(MinigamesStorageType type, DudeWheresMyStuffPlugin plugin) {
    super(type, plugin);

    hasStaticItems = true;
  }

  @Override
  protected void createStoragePanel(StorageManager<?, ?> storageManager) {
    storagePanel = new StoragePanel(plugin, this, false, true);

    createComponentPopupMenu(storageManager);
  }

  @Override
  public void reset() {
    items.forEach(item -> item.setQuantity(0));
    lastUpdated = -1L;
    enable();
  }
}
