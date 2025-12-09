package dev.thource.runelite.dudewheresmystuff.sailing;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.DurationFormatter;
import dev.thource.runelite.dudewheresmystuff.SaveFieldFormatter;
import dev.thource.runelite.dudewheresmystuff.SaveFieldLoader;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

/**
 * LostBoatStorage is responsible for tracking what the player lost when their boat capsized.
 */
@Getter
public class LostBoatStorage extends BoatStorage {

  protected UUID uuid = UUID.randomUUID();
  protected WorldPoint worldPoint;

  protected LostBoatStorage(DudeWheresMyStuffPlugin plugin) {
    super(SailingStorageType.LOST_BOAT, plugin);
  }

  protected LostBoatStorage(
      DudeWheresMyStuffPlugin plugin, ActiveBoatStorage boatStorage, WorldPoint worldPoint) {
    super(SailingStorageType.LOST_BOAT, plugin);

    this.worldPoint = worldPoint;
    items.addAll(boatStorage.getItems());
    name2Id = boatStorage.getName2Id();
    name3Id = boatStorage.getName3Id();
    updateLastUpdated();
  }

  @Override
  protected void createComponentPopupMenu(StorageManager<?, ?> storageManager) {
    if (storagePanel == null) {
      return;
    }

    final JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
    storagePanel.setComponentPopupMenu(popupMenu);

    final JMenuItem delete = new JMenuItem("Delete lost boat");
    delete.addActionListener(
        e -> ((SailingStorageManager) storageManager).deleteStorage(this));
    popupMenu.add(delete);
  }

  @Override
  public void softUpdate() {
    if (storagePanel == null) {
      return;
    }

    var timeLost = System.currentTimeMillis() - lastUpdated;
    storagePanel.setFooterText("Lost " + DurationFormatter.format(Math.abs(timeLost)) + " ago");
  }

  static LostBoatStorage load(
      DudeWheresMyStuffPlugin plugin,
      SailingStorageManager sailingStorageManager,
      String profileKey,
      String uuid) {
    var lostBoat = new LostBoatStorage(plugin);

    lostBoat.uuid = UUID.fromString(uuid);
    lostBoat.load(
        sailingStorageManager.getConfigManager(), sailingStorageManager.getConfigKey(), profileKey);

    return lostBoat;
  }

  @Override
  protected void updateLocationText() {
    if (storagePanel == null) {
      return;
    }

    SwingUtilities.invokeLater(
        () -> storagePanel.setSubTitle(worldPoint.getX() + ", " + worldPoint.getY()));
  }

  @Override
  protected String getConfigKey(String managerConfigKey) {
    return super.getConfigKey(managerConfigKey) + "." + uuid;
  }

  @Override
  protected ArrayList<String> getSaveValues() {
    ArrayList<String> saveValues = super.getSaveValues();

    saveValues.add(SaveFieldFormatter.format(worldPoint));

    return saveValues;
  }

  @Override
  protected void loadValues(ArrayList<String> values) {
    super.loadValues(values);

    worldPoint = SaveFieldLoader.loadWorldPoint(values, worldPoint);
  }

  @Override
  public boolean isWithdrawable() {
    return false;
  }
}
