package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.DurationFormatter;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
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
  private long lostAt = -1L;
  private DeathStorageManager deathStorageManager;

  Deathbank(
      DeathStorageType deathStorageType,
      DudeWheresMyStuffPlugin plugin,
      DeathStorageManager deathStorageManager) {
    super(deathStorageType, plugin);

    this.deathStorageManager = deathStorageManager;
  }

  @Override
  protected void createStoragePanel() {
    super.createStoragePanel();

    if (!deathStorageManager.isPreviewManager()) {
      final JPopupMenu popupMenu = new JPopupMenu();
      popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
      storagePanel.setComponentPopupMenu(popupMenu);

      final JMenuItem clearDeathbank = new JMenuItem("Delete Deathbank");
      clearDeathbank.addActionListener(
          e -> {
            int result = JOptionPane.OK_OPTION;

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
              }
              deathStorageManager.getStorageTabPanel().reorderStoragePanels();
              deathStorageManager.save();
            }
          });
      popupMenu.add(clearDeathbank);
    }
  }

  void setType(DeathStorageType type) {
    this.type = type;

    storagePanel.setTitle(type.getName());
  }

  public void setLocked(boolean locked) {
    this.locked = locked;

    storagePanel.setSubTitle(locked ? "Locked" : "Unlocked");
  }

  @Override
  public void reset() {
    super.reset();
    setType(DeathStorageType.UNKNOWN_DEATHBANK);
    setLocked(false);
    lostAt = -1L;
  }

  @Override
  public void softUpdate() {
    if (lostAt != -1) {
      long timeSinceLost = System.currentTimeMillis() - lostAt;
      storagePanel.setFooterText(
          "Lost " + DurationFormatter.format(Math.abs(timeSinceLost)) + " ago");
      return;
    }

    super.softUpdate();
  }
}
