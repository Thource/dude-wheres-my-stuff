package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.Region;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.util.QuantityFormatter;

class ExpiringDeathStorageInfoBox extends InfoBox {

  @Getter private final ExpiringDeathStorage storage;
  private final DudeWheresMyStuffConfig config;
  private final String regionName;
  private final BufferedImage icon;
  private final BufferedImage yellowIcon;
  private final BufferedImage redIcon;
  @Setter @Getter private boolean imageDirty = false;
  private final Client client;

  public ExpiringDeathStorageInfoBox(@Nonnull DudeWheresMyStuffPlugin plugin,
      ExpiringDeathStorage storage, int iconId) {
    super(null, plugin);

    config = plugin.getConfig();
    client = plugin.getClient();
    icon = plugin.getItemManager().getImage(iconId);
    yellowIcon = tintImage(icon, new Color(255, 210, 0, 80));
    redIcon = tintImage(icon, new Color(230, 0, 0, 80));

    setImage(icon);
    this.storage = storage;

    var region = storage.getRegion();
    regionName = (region == null ? "Unknown" : region.getName());
    refreshTooltip();
  }

  private static BufferedImage tintImage(BufferedImage originalImage, Color color) {
    BufferedImage tintedImage = new BufferedImage(originalImage.getWidth(),
        originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

    Graphics2D g2d = tintedImage.createGraphics();
    g2d.setColor(color);
    g2d.fillRect(0, 0, tintedImage.getWidth(), tintedImage.getHeight());
    g2d.drawImage(originalImage, 0, 0, null);
    g2d.dispose();

    return tintedImage;
  }

  void refreshTooltip() {
    String deathpileValue = QuantityFormatter.quantityToStackSize(
        storage.getTotalValue());

    setTooltip(
        regionName + " " + this.storage.getName().toLowerCase() + " (" + deathpileValue
            + " gp)");

    if (storage.isUseAccountPlayTime()
        && storage.getDeathStorageManager().getStartPlayedMinutes() <= 0) {
      if (getImage() != yellowIcon) {
        setImage(yellowIcon);
        imageDirty = true;
      }

      return;
    }

    if (config.flashExpiringDeathpileInfoboxes()) {
      int minutesLeft =
          (int) Math.floor(
              (storage.getExpiryMs() - System.currentTimeMillis()) / 60000f);

      if (minutesLeft <= config.deathpileExpiryWarningTime()) {
        if ((client.getTickCount() + 1) % 2 == 0) {
          setImage(redIcon);
        } else {
          setImage(icon);
        }

        imageDirty = true;
        return;
      }
    }

    if (getImage() != icon) {
      setImage(icon);
      imageDirty = true;
    }
  }

  @Override
  public String getText() {
    if (storage.isUseAccountPlayTime()
        && storage.getDeathStorageManager().getStartPlayedMinutes() <= 0) {
      return "??";
    }

    int minutesLeft =
        (int) Math.floor(
            (storage.getExpiryMs() - System.currentTimeMillis()) / 60000f);

    return (minutesLeft > 0 ? minutesLeft : "<1") + "m";
  }

  @Override
  public Color getTextColor() {
    return storage.getColor();
  }
}
