package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.DurationFormatter;
import dev.thource.runelite.dudewheresmystuff.Saved;
import java.util.UUID;
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

  @Saved(index = 3) public boolean locked = false;
  @Saved(index = 4) public long lostAt = -1L;
  @Saved(index = 5) public DeathbankType deathbankType;
  private DeathStorageManager deathStorageManager;

  Deathbank(
      DeathbankType deathbankType,
      DudeWheresMyStuffPlugin plugin,
      DeathStorageManager deathStorageManager) {
    super(DeathStorageType.DEATHBANK, plugin);

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
    // deathbanks get removed instead of reset
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

  static Deathbank load(DudeWheresMyStuffPlugin plugin, DeathStorageManager deathStorageManager, String profileKey, String uuid) {
    Deathbank deathbank = new Deathbank(
        DeathbankType.UNKNOWN,
        plugin,
        deathStorageManager
    );

    deathbank.uuid = UUID.fromString(uuid);
    deathbank.load(deathStorageManager.getConfigManager(), deathStorageManager.getConfigKey(), profileKey);

    return deathbank;
  }
}
