package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DurationFormatter;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.List;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

/** Deathpile is responsible for tracking the player's deathpiled items. */
@Getter
public class Deathpile extends DeathStorage {

  private final int playedMinutesAtCreation;
  private final WorldPoint worldPoint;
  private final DeathStorageManager deathStorageManager;

  Deathpile(
      Client client,
      ClientThread clientThread,
      ItemManager itemManager,
      int playedMinutesAtCreation,
      WorldPoint worldPoint,
      DeathStorageManager deathStorageManager,
      List<ItemStack> deathItems) {
    super(DeathStorageType.DEATHPILE, client, clientThread, itemManager);
    this.playedMinutesAtCreation = playedMinutesAtCreation;
    this.worldPoint = worldPoint;
    this.deathStorageManager = deathStorageManager;
    this.items.addAll(deathItems);
  }

  @Override
  public void reset() {
    // deathpiles get removed instead of reset
  }

  String getExpireText(boolean previewMode) {
    String expireText = "Expire";
    long timeUntilExpiry = getExpiryMs(previewMode) - System.currentTimeMillis();
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
   * @param previewMode whether preview mode is enabled
   * @return Unix timestamp of the expiry
   */
  public long getExpiryMs(boolean previewMode) {
    int minutesLeft = playedMinutesAtCreation + 59 - deathStorageManager.getPlayedMinutes();
    if (previewMode) {
      return System.currentTimeMillis() + (minutesLeft * 60000L);
    }

    return System.currentTimeMillis()
        + (minutesLeft * 60000L)
        - ((System.currentTimeMillis() - deathStorageManager.startMs) % 60000);
  }

  public boolean hasExpired(boolean previewMode) {
    return getExpiryMs(previewMode) < System.currentTimeMillis();
  }
}
