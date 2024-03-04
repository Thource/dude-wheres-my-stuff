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
  protected StoragePanel newStoragePanel() {
    return new StoragePanel(plugin, this, false, true);
  }
}
