package dev.thource.runelite.dudewheresmystuff.death;

import com.google.common.base.Strings;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import javax.annotation.Nullable;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class ExpiringDeathStorageTilesOverlay extends Overlay {

  private final Client client;
  private final DeathStorageManager deathStorageManager;
  private final DudeWheresMyStuffConfig config;

  public ExpiringDeathStorageTilesOverlay(
      DudeWheresMyStuffConfig config, Client client, DeathStorageManager deathStorageManager) {
    this.config = config;
    this.client = client;
    this.deathStorageManager = deathStorageManager;

    setPosition(OverlayPosition.DYNAMIC);
    setPriority(OverlayPriority.LOW);
    setLayer(OverlayLayer.ABOVE_SCENE);
  }

  private String getExpiryText(ExpiringDeathStorage expiringDeathStorage) {
    if (expiringDeathStorage.isUseAccountPlayTime()
        && expiringDeathStorage.getDeathStorageManager().getStartPlayedMinutes() <= 0) {
      return "??";
    }

    int minutesLeft =
        (int)
            Math.floor((expiringDeathStorage.getExpiryMs() - System.currentTimeMillis()) / 60000f);

    return (minutesLeft > 0 ? minutesLeft : "<1") + "m";
  }

  @Override
  public Dimension render(Graphics2D graphics) {
    deathStorageManager
        .getExpiringDeathStorages()
        .filter(storage -> !storage.hasExpired())
        .forEach(
            storage -> {
              Color tileColor = storage.getColor();

              if (config.flashExpiringDeathpileTiles()
                  && (int)
                          Math.floor((storage.getExpiryMs() - System.currentTimeMillis()) / 60_000f)
                      <= config.deathpileExpiryWarningTime()
                  && client.getTickCount() % 2 == 0) {
                tileColor = Color.RED;
              }

              drawTile(
                  graphics,
                  storage.getWorldPoint(),
                  tileColor,
                  storage.getName() + " (" + getExpiryText(storage) + ")",
                  new BasicStroke(2));
            });

    return null;
  }

  private void drawTile(
      Graphics2D graphics,
      WorldPoint point,
      Color color,
      @Nullable String label,
      Stroke borderStroke) {
    WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

    if (point.distanceTo(playerLocation) >= 32) {
      return;
    }

    LocalPoint lp = LocalPoint.fromWorld(client, point);
    if (lp == null) {
      return;
    }

    Polygon poly = Perspective.getCanvasTilePoly(client, lp);
    if (poly != null) {
      OverlayUtil.renderPolygon(graphics, poly, color, new Color(0, 0, 0, 0.3f), borderStroke);
    }

    if (!Strings.isNullOrEmpty(label)) {
      Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, lp, label, 0);
      if (canvasTextLocation != null) {
        OverlayUtil.renderTextLocation(
            graphics,
            new Point(canvasTextLocation.getX(), canvasTextLocation.getY() + 20),
            label,
            color);
      }
    }
  }
}
