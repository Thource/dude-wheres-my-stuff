package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.Storage;
import dev.thource.runelite.dudewheresmystuff.StoragePanel;
import lombok.Getter;

@Getter
abstract class MinigamesStorage extends Storage<MinigamesStorageType> {

  protected MinigamesStorage(MinigamesStorageType type, DudeWheresMyStuffPlugin plugin) {
    super(type, plugin);
  }

  @Override
  protected void createStoragePanel() {
    storagePanel = new StoragePanel(plugin, this, false, true);
  }

  @Override
  public void reset() {
    items.forEach(item -> item.setQuantity(0));
    lastUpdated = -1L;
    enable();
  }
}
