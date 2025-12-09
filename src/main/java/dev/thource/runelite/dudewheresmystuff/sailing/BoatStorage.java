package dev.thource.runelite.dudewheresmystuff.sailing;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.SaveFieldFormatter;
import dev.thource.runelite.dudewheresmystuff.SaveFieldLoader;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import lombok.Getter;
import net.runelite.api.gameval.DBTableID;
import net.runelite.client.config.ConfigManager;

/** SailingStorage is the base class for sailing storages that are specifically boats. */
@Getter
public abstract class BoatStorage extends SailingStorage {

  protected int name2Id;
  protected int name3Id;

  protected BoatStorage(SailingStorageType type, DudeWheresMyStuffPlugin plugin) {
    super(type, plugin);
  }

  @Override
  protected void createStoragePanel(StorageManager<?, ?> storageManager) {
    super.createStoragePanel(storageManager);
    assert storagePanel != null; // storagePanel can't be null here as it's set in super

    updateName();
    updateLocationText();
  }

  protected void updateName() {
    if (storagePanel == null) {
      return;
    }

    if (name2Id == 0 || name3Id == 0) {
      storagePanel.setTitle(type.getName());
      return;
    }

    var client = plugin.getClient();
    plugin
        .getClientThread()
        .invoke(
            () -> {
              var name2 =
                  client
                      .getDBTableField(
                          DBTableID.SailingBoatNameOptions.Row.SAILING_BOAT_NAME_DESCRIPTOR_OPTIONS,
                          1,
                          0)[name2Id - 1];
              var name3 =
                  client
                      .getDBTableField(
                          DBTableID.SailingBoatNameOptions.Row.SAILING_BOAT_NAME_NOUN_OPTIONS,
                          1,
                          0)[name3Id - 1];
              SwingUtilities.invokeLater(
                  () -> storagePanel.setTitle(name2 + " " + name3));
            });
  }

  protected abstract void updateLocationText();

  @Override
  protected ArrayList<String> getSaveValues() {
    ArrayList<String> saveValues = super.getSaveValues();

    saveValues.add(SaveFieldFormatter.format(name2Id));
    saveValues.add(SaveFieldFormatter.format(name3Id));

    return saveValues;
  }

  @Override
  protected void loadValues(ArrayList<String> values) {
    super.loadValues(values);

    name2Id = SaveFieldLoader.loadInt(values, name2Id);
    name3Id = SaveFieldLoader.loadInt(values, name3Id);
  }

  @Override
  public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
    super.load(configManager, managerConfigKey, profileKey);

    updateName();
    updateLocationText();
  }
}
