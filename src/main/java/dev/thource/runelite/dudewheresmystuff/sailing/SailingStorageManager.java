package dev.thource.runelite.dudewheresmystuff.sailing;

import com.google.inject.Inject;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import java.util.Collections;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

/** SailingStorageManager is responsible for managing all SailingStorages. */
@Slf4j
public class SailingStorageManager extends StorageManager<SailingStorageType, SailingStorage> {

  @Inject
  private SailingStorageManager(DudeWheresMyStuffPlugin plugin) {
    super(plugin);

    storages.add(new ActiveBoatStorage(SailingStorageType.BOAT_1, plugin));
    storages.add(new ActiveBoatStorage(SailingStorageType.BOAT_2, plugin));
    storages.add(new ActiveBoatStorage(SailingStorageType.BOAT_3, plugin));
    storages.add(new ActiveBoatStorage(SailingStorageType.BOAT_4, plugin));
    storages.add(new ActiveBoatStorage(SailingStorageType.BOAT_5, plugin));
  }

  @Override
  public void onGameTick() {
    super.onGameTick();
  }

  @Override
  public void load(String profileKey) {
    super.load(profileKey);

    if (!enabled) {
      return;
    }

    loadLostBoats(profileKey);
  }

  private void loadLostBoats(String profileKey) {
    for (var configurationKey :
        configManager.getRSProfileConfigurationKeys(
            DudeWheresMyStuffConfig.CONFIG_GROUP,
            profileKey,
            getConfigKey() + "." + SailingStorageType.LOST_BOAT.getConfigKey() + ".")) {
      var lostBoat =
          LostBoatStorage.load(plugin, this, profileKey, configurationKey.split("\\.")[2]);
      SwingUtilities.invokeLater(
          () -> {
            lostBoat.createStoragePanel(this);

            if (lostBoat.getStoragePanel() != null) {
              plugin
                  .getClientThread()
                  .invoke(
                      () -> {
                        lostBoat.getStoragePanel().refreshItems();
                        SwingUtilities.invokeLater(() -> lostBoat.getStoragePanel().update());
                      });
            }
          });
      storages.add(lostBoat);
    }
  }

  @Override
  public String getConfigKey() {
    return "sailing";
  }

  public void deleteStorage(LostBoatStorage lostBoatStorage) {
    storages.remove(lostBoatStorage);
    SwingUtilities.invokeLater(() -> getStorageTabPanel().reorderStoragePanels());
    lostBoatStorage.deleteData(this);
  }

  public void deleteLostBoats() {
    var iterator = storages.iterator();
    while (iterator.hasNext()) {
      var storage = iterator.next();
      if (!(storage instanceof LostBoatStorage)) {
        continue;
      }

      iterator.remove();
      storage.deleteData(this);
    }
    SwingUtilities.invokeLater(storageTabPanel::reorderStoragePanels);
  }

  /**
   * Creates a lost boat storage from an active boat.
   *
   * @param activeBoatStorage The active boat storage that was capsized and needs to be
   *     converted to a lost boat
   */
  public void createLostBoat(ActiveBoatStorage activeBoatStorage) {
    var client = plugin.getClient();
    var lostBoat =
        new LostBoatStorage(
            plugin,
            activeBoatStorage,
            WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()));
    SwingUtilities.invokeLater(() -> lostBoat.createStoragePanel(this));
    // Add to storages later to avoid CME
    clientThread.invokeLater(
        () -> {
          storages.add(lostBoat);
          SwingUtilities.invokeLater(
              () ->
                  plugin
                      .getClientThread()
                      .invoke(() -> updateStorages(Collections.singletonList(lostBoat))));
        });
  }
}
