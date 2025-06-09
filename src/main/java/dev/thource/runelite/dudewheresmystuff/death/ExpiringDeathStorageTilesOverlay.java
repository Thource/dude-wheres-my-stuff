package dev.thource.runelite.dudewheresmystuff.death;

import com.google.common.base.Strings;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.Objects;
import javax.annotation.Nullable;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class ExpiringDeathStorageTilesOverlay extends Overlay {

  private final Client client;
  private final DeathStorageManager deathStorageManager;
  private final DudeWheresMyStuffConfig config;
  private final DudeWheresMyStuffPlugin plugin;

  public ExpiringDeathStorageTilesOverlay(
      DudeWheresMyStuffConfig config,
      Client client,
      DeathStorageManager deathStorageManager,
      DudeWheresMyStuffPlugin plugin) {
    this.config = config;
    this.client = client;
    this.deathStorageManager = deathStorageManager;
    this.plugin = plugin;

    setPosition(OverlayPosition.DYNAMIC);
    setPriority(0.0f);
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
    if (plugin.isDeveloperMode()
        && Objects.equals(
            plugin
                .getConfigManager()
                .getConfiguration(
                    DudeWheresMyStuffConfig.CONFIG_GROUP, "debug.render.remoteDeathpileAreas"),
            "true")) {
      for (RemoteDeathpileAreas remoteDeathpileArea : RemoteDeathpileAreas.values()) {
        var deathArea = remoteDeathpileArea.getDeathArea();
        var deathRegions = remoteDeathpileArea.getDeathRegionIds();
        if (deathArea != null) {
          drawArea(
              graphics,
              deathArea,
              Color.ORANGE,
              remoteDeathpileArea.name() + " death area",
              new BasicStroke(2));
        } else if (deathRegions != null) {
          for (Integer deathRegionId : remoteDeathpileArea.getDeathRegionIds()) {
            drawTile(
                graphics,
                WorldPoint.fromRegion(deathRegionId, 32, 32, remoteDeathpileArea.getDeathPlane()),
                Color.ORANGE,
                remoteDeathpileArea.name() + " death region",
                new BasicStroke(2));
          }
        }

        drawArea(
            graphics,
            remoteDeathpileArea.getPileArea(),
            Color.GREEN,
            remoteDeathpileArea.name() + " pile area",
            new BasicStroke(2));
      }
    }

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

              var worldPoint = storage.getWorldPoint();
              if (worldPoint != null) {
                drawTile(
                    graphics,
                    storage.getWorldPoint(),
                    tileColor,
                    storage.getName() + " (" + getExpiryText(storage) + ")",
                    new BasicStroke(2));
              }

              var worldArea = storage.getWorldArea();
              if (worldArea != null) {
                drawArea(
                    graphics,
                    storage.getWorldArea(),
                    tileColor,
                    storage.getName() + " area (" + getExpiryText(storage) + ")",
                    new BasicStroke(2));
              }
            });

    return null;
  }

  private void drawArea(
      Graphics2D graphics,
      WorldArea area,
      Color color,
      @Nullable String label,
      Stroke borderStroke) {
    WorldPoint playerLocation =
        WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());

    if (area.distanceTo(playerLocation) >= 32) {
      return;
    }

    var worldView = client.getTopLevelWorldView();
    for (WorldPoint worldPoint : WorldPoint.toLocalInstance(worldView, area.toWorldPoint())) {
      LocalPoint lp =
          LocalPoint.fromWorld(
              worldView,
              worldPoint.getX() + area.getWidth() / 2,
              worldPoint.getY() + area.getHeight() / 2);
      if (lp == null) {
        return;
      }

      Polygon poly =
          Perspective.getCanvasTileAreaPoly(
              client, lp, area.getWidth(), area.getHeight(), area.getPlane(), 0);
      if (poly != null) {
        OverlayUtil.renderPolygon(graphics, poly, color, new Color(0, 0, 0, 0.3f), borderStroke);
      }

      if (!Strings.isNullOrEmpty(label)) {
        Point canvasTextLocation =
            Perspective.getCanvasTextLocation(client, graphics, lp, label, 0);
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

    var worldView = client.getTopLevelWorldView();
    LocalPoint lp = LocalPoint.fromWorld(worldView, point);
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
