package dev.thource.runelite.dudewheresmystuff.stash;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.ItemsBox;
import dev.thource.runelite.dudewheresmystuff.StorageTabPanel;
import net.runelite.client.game.ItemManager;

/** StashStorageTabPanel is responsible for displaying STASH unit data to the player. */
public class StashStorageTabPanel
    extends StorageTabPanel<StashStorageType, StashStorage, StashStorageManager> {

  public StashStorageTabPanel(
      ItemManager itemManager, DudeWheresMyStuffConfig config, StashStorageManager storageManager) {
    super(itemManager, config, storageManager);
  }

  @Override
  protected void rebuildList() {
    super.rebuildList();

    for (ItemsBox itemsBox : itemsBoxes) {
      itemsBox
          .getLogTitle()
          .setToolTipText(((StashStorage) itemsBox.getStorage()).getStashUnit().getChartText());
    }
  }
}
