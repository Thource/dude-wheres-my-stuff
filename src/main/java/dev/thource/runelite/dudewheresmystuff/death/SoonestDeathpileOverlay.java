package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class SoonestDeathpileOverlay extends Overlay {

  @Inject DudeWheresMyStuffPlugin plugin;

  @Inject DudeWheresMyStuffConfig config;

  @Inject
  public SoonestDeathpileOverlay() {
    setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
  }

  @Override
  public Dimension render(Graphics2D graphics) {
    if (!shouldRenderOverlay()) {
      return null;
    }

    return renderText(graphics);
  }

  // If there is a Death pile expiring soon that matchs the config criteria
  private boolean shouldRenderOverlay() {
    return plugin.soonestExpiringDeathpileMessage != null &&
        plugin.soonestExpiringDeathpileMinutesLeft <= config.timeUntilDeathpileExpires() &&
        config.warnDeathPileExpiring();
  }

  private Dimension renderText (Graphics2D graphics){
    Font font = FontManager.getRunescapeFont().deriveFont(Font.PLAIN, config.warnDeathpileExpiringFontSize());
    graphics.setFont(font);
    String deathpileExpiringText = "Your deathpile " + plugin.soonestExpiringDeathpileMessage.toLowerCase();

    // Alternates between two colors, this could be customized later
    Color textColor = plugin.soonestExpiringDeathpileColor ? Color.RED : Color.WHITE;
    graphics.setColor(textColor);

    FontMetrics metrics = graphics.getFontMetrics(font);

    int textWidth = metrics.stringWidth(deathpileExpiringText);
    int textHeight = metrics.getHeight();

    graphics.drawString(deathpileExpiringText,0,0 + textHeight);

    return new Dimension(textWidth, textHeight);
  }
}
