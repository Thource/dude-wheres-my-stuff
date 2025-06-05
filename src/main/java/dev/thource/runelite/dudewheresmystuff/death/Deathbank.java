package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.DurationFormatter;
import dev.thource.runelite.dudewheresmystuff.SaveFieldFormatter;
import dev.thource.runelite.dudewheresmystuff.SaveFieldLoader;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
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

  protected UUID uuid = UUID.randomUUID();
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

    if (deathbank.getItems().isEmpty()) {
      deathbank.deleteData(deathStorageManager);
      return null;
    }

    return deathbank;
  }

  @Override
  protected String getConfigKey(String managerConfigKey) {
    return super.getConfigKey(managerConfigKey) + "." + uuid;
  }

  @Override
  protected ArrayList<String> getSaveValues() {
    ArrayList<String> saveValues = super.getSaveValues();

    saveValues.add(SaveFieldFormatter.format(uuid));
    saveValues.add(SaveFieldFormatter.format(locked));
    saveValues.add(SaveFieldFormatter.format(lostAt));
    saveValues.add(SaveFieldFormatter.format(deathbankType));

    return saveValues;
  }

  @Override
  protected void loadValues(ArrayList<String> values) {
    super.loadValues(values);

    uuid = SaveFieldLoader.loadUUID(values, uuid);
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
          boolean confirmed = lostAt != -1L || DudeWheresMyStuffPlugin.getConfirmation(storagePanel,
              "Are you sure you want to delete this deathbank?\nThis cannot be undone.",
              "Confirm deletion");

          if (confirmed) {
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
    createDebugMenuOptions(storageManager, popupMenu);
  }

  private void createDebugMenuOptions(StorageManager<?, ?> storageManager, JPopupMenu popupMenu) {
    if (plugin.isDeveloperMode()) {
      var debugMenu = new JMenu("Debug");
      popupMenu.add(debugMenu);

      var setTypeMenu = new JMenu("Set type");
      debugMenu.add(setTypeMenu);

      for (DeathbankType dbType : DeathbankType.values()) {
        var setType = new JMenuItem(dbType.getName());
        setType.addActionListener(
            e -> setDeathbankType(dbType));
        setTypeMenu.add(setType);
      }

      var expire = new JMenuItem("Set as lost");
      debugMenu.add(expire);
      expire.addActionListener(
          e -> {
            lostAt = System.currentTimeMillis();
            softUpdate();
            storageManager.getStorageTabPanel().reorderStoragePanels();
          });

      var lock = new JMenuItem("Toggle lock");
      debugMenu.add(lock);
      lock.addActionListener(
          e -> setLocked(!locked));
    }
  }

  void setLocked(boolean locked) {
    this.locked = locked;

    SwingUtilities.invokeLater(() -> {
      if (storagePanel != null) {
        storagePanel.setSubTitle(locked ? "Locked" : "Unlocked");
      }
    });
  }

  void setDeathbankType(DeathbankType dbType) {
    deathbankType = dbType;

    SwingUtilities.invokeLater(() -> {
      if (storagePanel != null) {
        storagePanel.setTitle(deathbankType.getName());
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

  /**
   * Checks if the deathbank has not been lost.
   *
   * @return true if lostAt == -1
   */
  public boolean isActive() {
    return lostAt == -1L;
  }

  @Override
  public boolean isWithdrawable() {
    // If the items were lost, then they can't be withdrawn
    return super.isWithdrawable() && isActive();
  }
}
