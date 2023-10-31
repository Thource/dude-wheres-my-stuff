package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.Region;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

public class ExpiringDeathStorageTextOverlay extends Overlay {

  private final DudeWheresMyStuffConfig config;
  private final DeathStorageManager deathStorageManager;
  private ExpiringDeathStorage storage;
  private String regionName = null;
  private final Client client;

  public ExpiringDeathStorageTextOverlay(DudeWheresMyStuffConfig config,
      DeathStorageManager deathStorageManager, Client client) {
    this.config = config;
    this.deathStorageManager = deathStorageManager;
    this.client = client;

    setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
  }

  // Updates every game tick
  public void updateSoonestExpiringDeathStorage() {
    ExpiringDeathStorage newStorage;
    if (client.getVarbitValue(Varbits.ACCOUNT_TYPE) == 2) {
      newStorage = deathStorageManager.getSoonestExpiringDeathpile();
    } else {
      newStorage = deathStorageManager.getGrave();
    }
    if (newStorage == storage) {
      return;
    }

    storage = newStorage;
    if (storage != null) {
      Region region = Region.get(storage.getWorldPoint().getRegionID());
      regionName = (region == null ? null : region.getName());
    }
  }

  // If there is a storage expiring soon that matches the config criteria
  private boolean shouldRenderOverlay() {
    return storage != null
        && config.showDeathpileExpiryText()
        && (int) Math.floor((storage.getExpiryMs() - System.currentTimeMillis()) / 60_000f)
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

    String text = "Your " + storage.getName().toLowerCase() + " ";
    if (regionName != null) {
      text += "in " + regionName + " ";
    }
    text += storage.getExpireText().toLowerCase();

    // Alternates between two colors, this could be customized by the user later
    Color textColor = client.getTickCount() % 2 == 0 ? Color.RED : Color.WHITE;
    graphics.setColor(textColor);

    FontMetrics metrics = graphics.getFontMetrics(font);

    int textWidth = metrics.stringWidth(text);
    int textHeight = metrics.getHeight();

    graphics.drawString(text, 0, textHeight);

    return new Dimension(textWidth, textHeight);
  }
}
