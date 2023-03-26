package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.DurationFormatter;
import dev.thource.runelite.dudewheresmystuff.SaveFieldFormatter;
import dev.thource.runelite.dudewheresmystuff.SaveFieldLoader;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** Deathbank is responsible for tracking the player's deathbanked items. */
@Slf4j
@Getter
@Setter
public class Deathbank extends DeathStorage {

  private boolean locked = false;
  @Getter private long lostAt = -1L;
  private DeathbankType deathbankType;
  private DeathStorageManager deathStorageManager;

  Deathbank(
      DeathbankType deathbankType,
      DudeWheresMyStuffPlugin plugin,
      DeathStorageManager deathStorageManager) {
    super(DeathStorageType.DEATHBANK, plugin);

    this.deathbankType = deathbankType;
    this.deathStorageManager = deathStorageManager;
  }

  @Override
  protected ArrayList<String> getSaveValues() {
    ArrayList<String> saveValues = super.getSaveValues();

    saveValues.add(SaveFieldFormatter.format(locked));
    saveValues.add(SaveFieldFormatter.format(lostAt));
    saveValues.add(SaveFieldFormatter.format(deathbankType));

    return saveValues;
  }

  @Override
  protected void loadValues(ArrayList<String> values) {
    super.loadValues(values);

    locked = SaveFieldLoader.loadBoolean(values, locked);
    lostAt = SaveFieldLoader.loadLong(values, lostAt);
    deathbankType = SaveFieldLoader.loadDeathbankType(values, deathbankType);
  }

  @Override
  protected void createStoragePanel(StorageManager<?, ?> storageManager) {
    super.createStoragePanel(storageManager);

    assert storagePanel != null; // storagePanel can't be null here as it's set in super kl
    storagePanel.setTitle(deathbankType.getName());
    storagePanel.setSubTitle(locked ? "Locked" : "Unlocked");

    createComponentPopupMenu(storageManager);
  }

  @Override
  protected void createComponentPopupMenu(StorageManager<?, ?> storageManager) {
    if (storagePanel == null) {
      return;
    }

    final JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
    storagePanel.setComponentPopupMenu(popupMenu);

    final JMenuItem clearDeathbank = new JMenuItem("Delete Deathbank");
    clearDeathbank.addActionListener(
        e -> {
          int result = JOptionPane.CANCEL_OPTION;

          try {
            result =
                JOptionPane.showConfirmDialog(
                    storagePanel,
                    "Are you sure you want to delete your deathbank?\nThis cannot be undone.",
                    "Confirm deletion",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
          } catch (Exception err) {
            log.warn("Unexpected exception occurred while check for confirm required", err);
          }

          if (result == JOptionPane.OK_OPTION) {
            if (this == deathStorageManager.getDeathbank()) {
              deathStorageManager.clearDeathbank(false);
            } else {
              deathStorageManager.getStorages().remove(this);
              deleteData(deathStorageManager);
            }
            deathStorageManager.getStorageTabPanel().reorderStoragePanels();
          }
        });
    popupMenu.add(clearDeathbank);
  }

  void setLocked(boolean locked) {
    this.locked = locked;

    SwingUtilities.invokeLater(() -> {
      if (storagePanel != null) {
        storagePanel.setSubTitle(locked ? "Locked" : "Unlocked");
      }
    });
  }

  @Override
  public void reset() {
    // deathbanks get removed instead of reset
  }

  @Override
  public void softUpdate() {
    if (storagePanel != null && lostAt != -1) {
      long timeSinceLost = System.currentTimeMillis() - lostAt;
      storagePanel.setFooterText(
          "Lost " + DurationFormatter.format(Math.abs(timeSinceLost)) + " ago");
      return;
    }

    super.softUpdate();
  }

  static Deathbank load(DudeWheresMyStuffPlugin plugin, DeathStorageManager deathStorageManager,
      String profileKey, String uuid) {
    Deathbank deathbank = new Deathbank(
        DeathbankType.UNKNOWN,
        plugin,
        deathStorageManager
    );

    deathbank.uuid = UUID.fromString(uuid);
    deathbank.load(deathStorageManager.getConfigManager(), deathStorageManager.getConfigKey(),
        profileKey);

    return deathbank;
  }

  @Override
  public boolean isWithdrawable() {
    // If the items were lost, then they can't be withdrawn
    return super.isWithdrawable() && lostAt == -1;
  }
}
