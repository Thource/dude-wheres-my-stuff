package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.DurationFormatter;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Region;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

/** Deathpile is responsible for tracking the player's deathpiled items. */
@Getter
@Slf4j
public class Deathpile extends DeathStorage {

  private final int playedMinutesAtCreation;
  private final WorldPoint worldPoint;
  private final DeathStorageManager deathStorageManager;

  Deathpile(
      DudeWheresMyStuffPlugin plugin,
      int playedMinutesAtCreation,
      WorldPoint worldPoint,
      DeathStorageManager deathStorageManager,
      List<ItemStack> deathItems) {
    super(DeathStorageType.DEATHPILE, plugin);
    this.playedMinutesAtCreation = playedMinutesAtCreation;
    this.worldPoint = worldPoint;
    this.deathStorageManager = deathStorageManager;
    this.items.addAll(deathItems);
  }

  @Override
  protected void createStoragePanel() {
    super.createStoragePanel();

    Region region = Region.get(worldPoint.getRegionID());
    if (region == null) {
      storagePanel.setSubTitle("Unknown");
    } else {
      storagePanel.setSubTitle(region.getName());
    }

    if (!deathStorageManager.isPreviewManager()) {
      final JPopupMenu popupMenu = new JPopupMenu();
      popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
      storagePanel.setComponentPopupMenu(popupMenu);

      final JMenuItem deleteDeathpile = new JMenuItem("Delete Deathpile");
      deleteDeathpile.addActionListener(
          e -> {
            int result = JOptionPane.OK_OPTION;

            try {
              result =
                  JOptionPane.showConfirmDialog(
                      storagePanel,
                      "Are you sure you want to delete this deathpile?\nThis cannot be undone.",
                      "Confirm deletion",
                      JOptionPane.OK_CANCEL_OPTION,
                      JOptionPane.WARNING_MESSAGE);
            } catch (Exception err) {
              log.warn("Unexpected exception occurred while check for confirm required", err);
            }

            if (result == JOptionPane.OK_OPTION) {
              deathStorageManager.getStorages().remove(this);
              deathStorageManager.refreshMapPoints();
              deathStorageManager.getStorageTabPanel().reorderStoragePanels();
              deathStorageManager.save();
            }
          });
      popupMenu.add(deleteDeathpile);
    }
  }

  @Override
  public void reset() {
    // deathpiles get removed instead of reset
  }

  String getExpireText() {
    String expireText = "Expire";
    long timeUntilExpiry = getExpiryMs() - System.currentTimeMillis();
    if (timeUntilExpiry < 0) {
      expireText += "d " + DurationFormatter.format(Math.abs(timeUntilExpiry)) + " ago";
    } else {
      expireText += "s in " + DurationFormatter.format(Math.abs(timeUntilExpiry));
    }
    return expireText;
  }

  /**
   * Returns a unix timestamp of the expiry.
   *
   * <p>If previewMode is true, this will change so that it is static when displayed.
   *
   * @return Unix timestamp of the expiry
   */
  public long getExpiryMs() {
    int minutesLeft = playedMinutesAtCreation + 59 - deathStorageManager.getPlayedMinutes();
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
    storagePanel.setFooterText(getExpireText());
  }
}
