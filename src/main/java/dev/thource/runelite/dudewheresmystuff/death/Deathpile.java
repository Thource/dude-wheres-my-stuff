package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.QueuedMessage;

/** Deathpile is responsible for tracking the player's deathpiled items. */
@Getter
@Slf4j
public class Deathpile extends ExpiringDeathStorage {

  private long lastGroundItemSync;

  Deathpile(
      DudeWheresMyStuffPlugin plugin,
      boolean useAccountPlayTime,
      WorldArea worldArea,
      DeathStorageManager deathStorageManager,
      List<ItemStack> deathItems) {
    super(
        plugin,
        useAccountPlayTime,
        worldArea,
        deathStorageManager,
        deathItems,
        DeathStorageType.DEATHPILE);
    refreshColor();
  }

  @Override
  public boolean onGameTick() {
    var updated = super.onGameTick();

    if (hasExpired()) {
      return updated;
    }

    if (worldPoint == null) {
      if (locate()) {
        updated = true;
      }
    }

    if (syncExpiryFromGroundItems()) {
      updated = true;
    }

    return updated;
  }

  private List<TileItem> getTileGroundItems(
      Tile[][] sceneTilesPlane, Scene scene, WorldPoint tilePoint) {
    var sceneX = tilePoint.getX() - scene.getBaseX();
    if (sceneX >= sceneTilesPlane.length) {
      return null;
    }

    var sceneY = tilePoint.getY() - scene.getBaseY();
    var sceneTilesX = sceneTilesPlane[sceneX];
    if (sceneY >= sceneTilesX.length) {
      return null;
    }

    var sceneTile = sceneTilesX[sceneY];
    if (sceneTile == null) {
      return null;
    }

    return sceneTile.getGroundItems();
  }

  private boolean locate() {
    if (worldArea == null) {
      return false;
    }

    var client = plugin.getClient();
    WorldPoint playerLocation =
        WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
    if (worldArea.distanceTo(playerLocation) >= 32) {
      return false;
    }

    var worldView = client.getTopLevelWorldView();
    LocalPoint lp =
        LocalPoint.fromWorld(
            worldView,
            worldArea.getX() + worldArea.getWidth() / 2,
            worldArea.getY() + worldArea.getHeight() / 2);
    if (lp == null) {
      return false;
    }

    var scene = worldView.getScene();
    var sceneTiles = scene.getTiles();
    if (worldArea.getPlane() >= sceneTiles.length) {
      return false;
    }

    var sceneTilesPlane = sceneTiles[worldArea.getPlane()];
    for (WorldPoint tilePoint : worldArea.toWorldPointList()) {
      var groundItems = getTileGroundItems(sceneTilesPlane, scene, tilePoint);
      if (groundItems == null) {
        continue;
      }

      var itemsToMatch = new ArrayList<>(items);
      for (TileItem groundItem : groundItems) {
        var itemToRemove =
            itemsToMatch.stream()
                .filter(
                    is ->
                        is.getId() == groundItem.getId()
                            && is.getQuantity() == groundItem.getQuantity())
                .findAny();
        itemToRemove.ifPresent(itemsToMatch::remove);

        if (itemsToMatch.size() <= items.size() / 2) {
          break;
        }
      }

      if (itemsToMatch.size() <= items.size() / 2) {
        worldPoint = tilePoint;
        worldArea = null;
        deathStorageManager.refreshMapPoints();
        SwingUtilities.invokeLater(this::setSubTitle);
        return true;
      }
    }

    return false;
  }

  private boolean syncExpiryFromGroundItems() {
    if (worldPoint == null
        || isUseAccountPlayTime()
        || System.currentTimeMillis() - lastGroundItemSync < 60000) {
      return false;
    }

    var client = plugin.getClient();
    WorldPoint playerLocation =
        WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
    if (worldPoint.distanceTo(playerLocation) >= 32) {
      return false;
    }

    var worldView = client.getTopLevelWorldView();
    LocalPoint lp = LocalPoint.fromWorld(worldView, worldPoint);
    if (lp == null) {
      return false;
    }

    var scene = worldView.getScene();
    var sceneTiles = scene.getTiles();
    if (worldPoint.getPlane() >= sceneTiles.length) {
      return false;
    }

    var sceneTilesPlane = sceneTiles[worldPoint.getPlane()];
    var groundItems = getTileGroundItems(sceneTilesPlane, scene, worldPoint);
    if (groundItems == null) {
      return false;
    }

    var groundItemsMappedByDespawnTime = new HashMap<Integer, ArrayList<TileItem>>();
    for (var groundItem : groundItems) {
      if (items.stream()
          .anyMatch(
              is ->
                  is.getId() == groundItem.getId()
                      && is.getQuantity() == groundItem.getQuantity())) {
        groundItemsMappedByDespawnTime
            .computeIfAbsent(groundItem.getDespawnTime(), (t) -> new ArrayList<>())
            .add(groundItem);
      }
    }

    var matchingItemsEntry =
        groundItemsMappedByDespawnTime.entrySet().stream()
            .filter(entry -> entry.getValue().size() > items.size() / 2)
            .sorted(Comparator.comparingInt(e -> e.getValue().size()))
            .filter(
                e -> {
                  var itemsToMatch = new ArrayList<>(items);

                  for (TileItem groundItem : groundItems) {
                    var itemToRemove =
                        itemsToMatch.stream()
                            .filter(
                                is ->
                                    is.getId() == groundItem.getId()
                                        && is.getQuantity() == groundItem.getQuantity())
                            .findAny();
                    itemToRemove.ifPresent(itemsToMatch::remove);
                    if (itemsToMatch.size() <= items.size() / 2) {
                      break;
                    }
                  }

                  return itemsToMatch.size() <= items.size() / 2;
                })
            .findFirst();

    if (matchingItemsEntry.isPresent()) {
      this.lastGroundItemSync = System.currentTimeMillis();

      var oldExpiryTime = this.expiryTime;
      this.expiryTime =
          matchingItemsEntry.get().getKey()
              - plugin.getConfig().deathpileContingencyMinutes() * 100;
      var difference = this.expiryTime - oldExpiryTime;

      final ChatMessageBuilder message =
          new ChatMessageBuilder()
              .append(new Color(206, 162, 65), "[DWMS] ")
              .append(
                  getRegionName()
                      + " deathpile expiry has been resynced from ground items, "
                      + (difference > 0 ? "added" : "removed")
                      + " "
                      + Math.abs(difference)
                      + " ticks.");

      plugin
          .getChatMessageManager()
          .queue(
              QueuedMessage.builder()
                  .type(ChatMessageType.CONSOLE)
                  .runeLiteFormattedMessage(message.build())
                  .build());

      if (this.expiryTime < 1) {
        final ChatMessageBuilder contingencyMessage =
            new ChatMessageBuilder()
                .append(new Color(206, 162, 65), "[DWMS] ")
                .append(ChatColorType.HIGHLIGHT)
                .append(
                    " Your contingency setting has caused the deathpile to expire! Real ticks remaining: "
                        + matchingItemsEntry.get().getKey());

        plugin
            .getChatMessageManager()
            .queue(
                QueuedMessage.builder()
                    .type(ChatMessageType.CONSOLE)
                    .runeLiteFormattedMessage(contingencyMessage.build())
                    .build());
      }

      return true;
    }

    return false;
  }

  static Deathpile load(
      DudeWheresMyStuffPlugin plugin,
      DeathStorageManager deathStorageManager,
      String profileKey,
      String uuid) {
    Deathpile deathpile = new Deathpile(plugin, true, null, deathStorageManager, new ArrayList<>());

    deathpile.uuid = UUID.fromString(uuid);
    deathpile.load(
        deathStorageManager.getConfigManager(), deathStorageManager.getConfigKey(), profileKey);

    return deathpile;
  }

  private Color generateColor() {
    if (worldPoint == null) {
      return Color.WHITE;
    }

    Random rand =
        new Random(
            worldPoint.getX() * 200L + worldPoint.getY() * 354L + worldPoint.getPlane() * 42L);

    return plugin.getConfig().deathpileColorScheme().generateColor(rand);
  }

  @Override
  public void softUpdate() {
    super.softUpdate();

    storagePanel.setSubTitle(items.size() + " stacks");
  }

  @Override
  protected void loadValues(ArrayList<String> values) {
    super.loadValues(values);

    refreshColor();
  }

  void refreshColor() {
    color = generateColor();
  }

  @Override
  public int getTotalLifeInMinutes() {
    return 60;
  }
}
