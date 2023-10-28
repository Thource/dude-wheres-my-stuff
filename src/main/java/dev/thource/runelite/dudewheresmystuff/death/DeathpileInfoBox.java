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
import net.runelite.api.ItemID;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.util.QuantityFormatter;

class DeathpileInfoBox extends InfoBox {

  static BufferedImage icon;
  static BufferedImage yellowIcon;
  static BufferedImage redIcon;

  @Getter private final Deathpile deathpile;
  private final DudeWheresMyStuffConfig config;
  private final String regionName;
  @Setter @Getter private boolean imageDirty = false;

  public DeathpileInfoBox(@Nonnull DudeWheresMyStuffPlugin plugin, Deathpile deathpile) {
    super(null, plugin);

    config = plugin.getConfig();

    if (icon == null) {
      icon = plugin.getItemManager().getImage(ItemID.BONES);
      yellowIcon = tintImage(icon, new Color(255, 210, 0, 80));
      redIcon = tintImage(icon, new Color(230, 0, 0, 80));
    }

    setImage(icon);
    this.deathpile = deathpile;

    Region region = Region.get(deathpile.getWorldPoint().getRegionID());
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
    String deathpileValue = QuantityFormatter.quantityToStackSize(deathpile.getTotalValue());

    setTooltip(regionName + " deathpile (" + deathpileValue + " gp)");

    if (deathpile.isUseAccountPlayTime()
        && deathpile.getDeathStorageManager().getStartPlayedMinutes() <= 0) {
      if (getImage() != yellowIcon) {
        setImage(yellowIcon);
        imageDirty = true;
      }

      return;
    }

    if (config.flashExpiringDeathpileInfoboxes()) {
      int minutesLeft =
          (int) Math.floor((deathpile.getExpiryMs() - System.currentTimeMillis()) / 60000f);

      if (minutesLeft <= config.deathpileExpiryWarningTime()) {
        if ((int) Math.floor((deathpile.getExpiryMs() - System.currentTimeMillis()) / 600f) % 2 == 0) {
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
    if (deathpile.isUseAccountPlayTime()
        && deathpile.getDeathStorageManager().getStartPlayedMinutes() <= 0) {
      return "??";
    }

    int minutesLeft =
        (int) Math.floor((deathpile.getExpiryMs() - System.currentTimeMillis()) / 60000f);

    return (minutesLeft > 0 ? minutesLeft : "<1") + "m";
  }

  @Override
  public Color getTextColor() {
    return deathpile.getColor();
  }
}
