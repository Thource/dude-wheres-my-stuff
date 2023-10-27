package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.Region;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

public class SoonestDeathpileOverlay extends Overlay {

  @Inject DudeWheresMyStuffPlugin plugin;
  @Inject DudeWheresMyStuffConfig config;

  private Deathpile soonestExpiringDeathpile;
  private int soonestExpiringDeathpileMinutesLeft = -1;
  private boolean soonestExpiringDeathpileColor;
  private String regionName = "Unknown";

  @Inject
  public SoonestDeathpileOverlay() {
    setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
  }

  // Updates every game tick
  public void updateSoonestDeathpile() {

    soonestExpiringDeathpile = plugin.soonestDeathpile;

    if (soonestExpiringDeathpile != null) {
      Region region = Region.get(soonestExpiringDeathpile.getWorldPoint().getRegionID());
      regionName = (region == null ? "Unknown" : region.getName());

      // Switches between two overlay colors
      soonestExpiringDeathpileColor = !soonestExpiringDeathpileColor;
      soonestExpiringDeathpileMinutesLeft = (int) Math.floor(
          (soonestExpiringDeathpile.getExpiryMs() - System.currentTimeMillis()) / 60_000f);
    } else {
      // Reset / clear variables if there's no death pile
      regionName = "Unknown";
      soonestExpiringDeathpileMinutesLeft = -1;
    }
  }

  // If there is a Death pile expiring soon that matches the config criteria
  private boolean shouldRenderOverlay() {
    return soonestExpiringDeathpile != null
        && soonestExpiringDeathpileMinutesLeft <= config.timeUntilDeathpileExpires()
        && config.warnDeathPileExpiring();
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
        .deriveFont(Font.PLAIN, config.warnDeathpileExpiringFontSize());
    graphics.setFont(font);
    String deathpileExpiringText =
        "Your " + regionName + " deathpile " + soonestExpiringDeathpile.getExpireText()
            .toLowerCase();

    // Alternates between two colors, this could be customized by the user later
    Color textColor = soonestExpiringDeathpileColor ? Color.RED : Color.WHITE;
    graphics.setColor(textColor);

    FontMetrics metrics = graphics.getFontMetrics(font);

    int textWidth = metrics.stringWidth(deathpileExpiringText);
    int textHeight = metrics.getHeight();

    graphics.drawString(deathpileExpiringText, 0, textHeight);

    return new Dimension(textWidth, textHeight);
  }
}
