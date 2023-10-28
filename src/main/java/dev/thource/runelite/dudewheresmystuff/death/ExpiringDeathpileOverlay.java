package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.Region;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

public class ExpiringDeathpileOverlay extends Overlay {

  private final DudeWheresMyStuffConfig config;
  private final DeathStorageManager deathStorageManager;
  private Deathpile deathpile;
  private String regionName = null;

  public ExpiringDeathpileOverlay(DudeWheresMyStuffConfig config,
      DeathStorageManager deathStorageManager) {
    this.config = config;
    this.deathStorageManager = deathStorageManager;

    setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
  }

  // Updates every game tick
  public void updateSoonestDeathpile() {
    Deathpile newDeathpile = deathStorageManager.getSoonestExpiringDeathpile();
    if (newDeathpile == deathpile) {
      return;
    }

    deathpile = newDeathpile;
    if (deathpile != null) {
      Region region = Region.get(deathpile.getWorldPoint().getRegionID());
      regionName = (region == null ? null : region.getName());
    }
  }

  // If there is a Death pile expiring soon that matches the config criteria
  private boolean shouldRenderOverlay() {
    return deathpile != null
        && config.showDeathpileExpiryText()
        && (int) Math.floor((deathpile.getExpiryMs() - System.currentTimeMillis()) / 60_000f)
        <= config.deathpileExpiryWarningTime();
  }

  @Override
  public Dimension render(Graphics2D graphics) {
    if (shouldRenderOverlay()) {
      return renderText(graphics);
    }

    return null;
  }

  private Dimension renderText(Graphics2D graphics) {
    Font font = FontManager.getRunescapeFont()
        .deriveFont(Font.PLAIN, config.deathpileExpiryWarningFontSize());
    graphics.setFont(font);

    String deathpileExpiringText = "Your deathpile ";
    if (regionName != null) {
      deathpileExpiringText += "in " + regionName + " ";
    }
    deathpileExpiringText += deathpile.getExpireText().toLowerCase();

    // Alternates between two colors, this could be customized by the user later
    Color textColor =
        (int) Math.floor((deathpile.getExpiryMs() - System.currentTimeMillis()) / 600f) % 2 == 0
            ? Color.RED : Color.WHITE;
    graphics.setColor(textColor);

    FontMetrics metrics = graphics.getFontMetrics(font);

    int textWidth = metrics.stringWidth(deathpileExpiringText);
    int textHeight = metrics.getHeight();

    graphics.drawString(deathpileExpiringText, 0, textHeight);

    return new Dimension(textWidth, textHeight);
  }
}
