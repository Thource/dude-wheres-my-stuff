package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.DurationFormatter;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Region;
import dev.thource.runelite.dudewheresmystuff.SaveFieldFormatter;
import dev.thource.runelite.dudewheresmystuff.SaveFieldLoader;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.util.ImageUtil;

public abstract class ExpiringDeathStorage extends DeathStorage {

  private static final ImageIcon WARNING_ICON =
      new ImageIcon(ImageUtil.loadImageResource(DudeWheresMyStuffPlugin.class, "warning.png"));
  @Getter protected final DeathStorageManager deathStorageManager;
  @Getter protected WorldPoint worldPoint;
  @Getter protected WorldArea worldArea;
  @Getter @Setter protected int expiryTime;
  protected long expiredAt = -1L;
  @Getter @Setter protected DeathWorldMapPoint worldMapPoint;
  @Getter protected Color color = Color.WHITE;
  protected UUID uuid = UUID.randomUUID();
  // when useAccountPlayTime is true, expiryTime is the account played minutes that the
  // deathpile will expire at.
  // when useAccountPlayTime is false, expiryTime is the amount of ticks left until
  // the deathpile expires, ticking down only while the player is logged in.
  //
  // This is unused for graves, because grave timers are 100% accurate and provided by jagex
  @Getter private boolean useAccountPlayTime;

  ExpiringDeathStorage(
      DudeWheresMyStuffPlugin plugin,
      boolean useAccountPlayTime,
      WorldArea worldArea,
      DeathStorageManager deathStorageManager,
      List<ItemStack> deathItems,
      DeathStorageType storageType) {
    super(storageType, plugin);
    this.useAccountPlayTime = useAccountPlayTime;
    if (worldArea != null && worldArea.getWidth() == 1 && worldArea.getHeight() == 1) {
      this.worldPoint = worldArea.toWorldPoint();
    } else {
      this.worldArea = worldArea;
    }
    this.deathStorageManager = deathStorageManager;
    this.items.addAll(deathItems);

    int duration = getTotalLifeInMinutes();
    if (this instanceof Deathpile) {
      duration -= plugin.getConfig().deathpileContingencyMinutes();
    } else {
      duration -= 1;
    }
    this.expiryTime =
        useAccountPlayTime ? deathStorageManager.getPlayedMinutes() + duration : duration * 100;
  }

  @Override
  protected String getConfigKey(String managerConfigKey) {
    return super.getConfigKey(managerConfigKey) + "." + uuid;
  }

  @Override
  protected ArrayList<String> getSaveValues() {
    ArrayList<String> saveValues = super.getSaveValues();

    saveValues.add(SaveFieldFormatter.format(uuid));
    if (worldPoint != null) {
      saveValues.add(SaveFieldFormatter.format(worldPoint));
    } else {
      saveValues.add(SaveFieldFormatter.format(worldArea));
    }
    saveValues.add(SaveFieldFormatter.format(useAccountPlayTime));
    saveValues.add(SaveFieldFormatter.format(expiryTime));
    saveValues.add(SaveFieldFormatter.format(expiredAt));

    return saveValues;
  }

  @Override
  protected void loadValues(ArrayList<String> values) {
    super.loadValues(values);

    uuid = SaveFieldLoader.loadUUID(values, uuid);
    var point = SaveFieldLoader.loadWorldPoint(values, null);
    if (point != null) {
      worldPoint = point;
    } else {
      worldArea = SaveFieldLoader.loadWorldArea(values, null);
    }
    useAccountPlayTime = SaveFieldLoader.loadBoolean(values, useAccountPlayTime);
    expiryTime = SaveFieldLoader.loadInt(values, expiryTime);
    expiredAt = SaveFieldLoader.loadLong(values, expiredAt);
  }

  @Override
  protected void createStoragePanel(StorageManager<?, ?> storageManager) {
    super.createStoragePanel(storageManager);
    assert storagePanel != null;

    setSubTitle();

    JLabel footerLabel = storagePanel.getFooterLabel();
    if (hasExpired()) {
      if (!useAccountPlayTime) {
        footerLabel.setIconTextGap(66);
        footerLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        footerLabel.setIcon(WARNING_ICON);
        footerLabel.setToolTipText(
            "This "
                + this.getName().toLowerCase()
                + " is using tick-based tracking, which means "
                + "that the timer could be out of sync. To use the more accurate play time based "
                + "timers, enable cross-client timers in the plugin settings.");
      } else if (deathStorageManager.getStartPlayedMinutes() <= 0) {
        footerLabel.setToolTipText(
            "This "
                + this.getName().toLowerCase()
                + " is using play time based tracking, but the "
                + "plugin doesn't know what your current play time is. To update your play time, "
                + "swap the quest interface to the \"Character summary\" tab (brown star).");
      }
    }

    createComponentPopupMenu(storageManager);
  }

  protected String getRegionName() {
    var region = getRegion();
    if (region == null) {
      return "Unknown";
    }

    return region.getName();
  }

  protected void setSubTitle() {
    if (storagePanel == null) {
      return;
    }

    storagePanel.setSubTitle(getRegionName());
  }

  public Region getRegion() {
    if (worldPoint == null && worldArea == null) {
      return null;
    }

    return Region.get((worldPoint != null ? worldPoint : worldArea.toWorldPoint()).getRegionID());
  }

  @Override
  protected void createComponentPopupMenu(StorageManager<?, ?> storageManager) {
    if (storagePanel == null) {
      return;
    }

    final JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
    storagePanel.setComponentPopupMenu(popupMenu);

    final JMenuItem delete = new JMenuItem("Delete " + this.getName());
    delete.addActionListener(
        e -> {
          boolean confirmed =
              hasExpired()
                  || DudeWheresMyStuffPlugin.getConfirmation(
                      storagePanel,
                      "Are you sure you want to delete this "
                          + this.getName().toLowerCase()
                          + "?\nThis cannot be undone.",
                      "Confirm deletion");

          if (confirmed) {
            deathStorageManager.deleteStorage(this);
          }
        });
    popupMenu.add(delete);

    createDebugMenuOptions(storageManager, popupMenu);
  }

  private void createDebugMenuOptions(StorageManager<?, ?> storageManager, JPopupMenu popupMenu) {
    if (plugin.isDeveloperMode()) {
      JMenu debugMenu = new JMenu("Debug");
      popupMenu.add(debugMenu);

      JMenuItem setExpiresIn = new JMenuItem("Set expires in");
      debugMenu.add(setExpiresIn);
      setExpiresIn.addActionListener(
          e -> {
            int minutes = 0;
            try {
              minutes =
                  Integer.parseInt(JOptionPane.showInputDialog("Enter expiry in minutes from now"));
            } catch (NumberFormatException nfe) {
              // Do nothing
            }

            if (minutes <= 0) {
              return;
            }

            if (useAccountPlayTime) {
              expiryTime = deathStorageManager.getPlayedMinutes() + minutes;
            } else {
              expiryTime = minutes * 100;
            }
            expiredAt = -1L;
            softUpdate();
            storageManager.getStorageTabPanel().reorderStoragePanels();
          });

      JMenuItem expire = new JMenuItem("Expire");
      debugMenu.add(expire);
      expire.addActionListener(
          e -> {
            expiredAt = -1L;
            expiryTime = 0;
            softUpdate();
            storageManager.getStorageTabPanel().reorderStoragePanels();
          });
    }
  }

  @Override
  public boolean onGameTick() {
    if (expiredAt != -1L) {
      return false;
    }

    if (!useAccountPlayTime) {
      expiryTime--;
      if (expiryTime <= 0) {
        expiredAt = System.currentTimeMillis();

        SwingUtilities.invokeLater(
            () -> {
              if (storagePanel == null) {
                return;
              }

              JLabel footerLabel = storagePanel.getFooterLabel();
              footerLabel.setIcon(null);
              footerLabel.setToolTipText(null);
            });
      }

      return true;
    }

    if (deathStorageManager.getStartPlayedMinutes() > 0
        && deathStorageManager.getPlayedMinutes() >= expiryTime) {
      expiredAt = System.currentTimeMillis();

      return true;
    }

    return false;
  }

  @Override
  public void reset() {
    // these get removed instead of reset
  }

  String getExpireText() {
    if (expiredAt != -1L) {
      return "Expired " + DurationFormatter.format(System.currentTimeMillis() - expiredAt) + " ago";
    }

    if (useAccountPlayTime && deathStorageManager.getStartPlayedMinutes() <= 0) {
      return "Waiting for play time";
    }

    return "Expires in " + DurationFormatter.format(getExpiryMs() - System.currentTimeMillis());
  }

  public abstract int getTotalLifeInMinutes();

  /**
   * Returns a unix timestamp of the expiry.
   *
   * <p>If previewMode is true, this will change so that it is static when displayed.
   *
   * @return Unix timestamp of the expiry
   */
  public long getExpiryMs() {
    if (expiredAt != -1L) {
      return expiredAt;
    }

    if (!useAccountPlayTime) {
      return System.currentTimeMillis() + (expiryTime * 600L);
    }

    // We don't know the player's play time yet, so assume the storage is fresh for sorting purposes
    if (deathStorageManager.getStartPlayedMinutes() <= 0) {
      return System.currentTimeMillis() + getTotalLifeInMinutes() * 60_000L;
    }

    int minutesLeft = expiryTime - deathStorageManager.getPlayedMinutes();
    if (deathStorageManager.isPreviewManager()) {
      return System.currentTimeMillis() + (minutesLeft * 60000L);
    }

    return System.currentTimeMillis()
        + (minutesLeft * 60000L)
        - ((System.currentTimeMillis() - deathStorageManager.startMs) % 60000);
  }

  public boolean hasExpired() {
    return getExpiryMs() < System.currentTimeMillis();
  }

  @Override
  public void softUpdate() {
    if (storagePanel == null) {
      return;
    }

    storagePanel.setFooterText(getExpireText());
  }

  @Override
  public boolean isWithdrawable() {
    return super.isWithdrawable() && !hasExpired();
  }
}
