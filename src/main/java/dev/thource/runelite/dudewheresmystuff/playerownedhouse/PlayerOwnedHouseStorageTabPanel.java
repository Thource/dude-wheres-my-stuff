package dev.thource.runelite.dudewheresmystuff.playerownedhouse;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.StorageTabPanel;
import net.runelite.client.game.ItemManager;

/** PlayerOwnedHouseStorageTabPanel is responsible for displaying POH storage data to the player. */
public class PlayerOwnedHouseStorageTabPanel
    extends StorageTabPanel<
        PlayerOwnedHouseStorageType, PlayerOwnedHouseStorage, PlayerOwnedHouseStorageManager> {

  public PlayerOwnedHouseStorageTabPanel(
      ItemManager itemManager,
      DudeWheresMyStuffConfig config,
      PlayerOwnedHouseStorageManager storageManager) {
    super(itemManager, config, storageManager);
  }
}
