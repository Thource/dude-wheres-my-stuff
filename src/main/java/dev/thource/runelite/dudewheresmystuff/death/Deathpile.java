package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DeathStorage;
import dev.thource.runelite.dudewheresmystuff.DeathStorageManager;
import dev.thource.runelite.dudewheresmystuff.DeathStorageType;
import dev.thource.runelite.dudewheresmystuff.DeathWorldMapPoint;
import dev.thource.runelite.dudewheresmystuff.DurationFormatter;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.List;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.ItemManager;

@Getter
public class Deathpile extends DeathStorage {

  private final int playedMinutesAtCreation;
  private final WorldPoint worldPoint;
  public DeathWorldMapPoint worldMapPoint;
  private final DeathStorageManager deathStorageManager;

  public Deathpile(Client client, ItemManager itemManager, int playedMinutesAtCreation,
      WorldPoint worldPoint, DeathStorageManager deathStorageManager, List<ItemStack> deathItems) {
    super(DeathStorageType.DEATHPILE, client, itemManager);
    this.playedMinutesAtCreation = playedMinutesAtCreation;
    this.worldPoint = worldPoint;
    this.deathStorageManager = deathStorageManager;
    this.items = deathItems;
  }

  @Override
  public void reset() {
  }

  public String getExpireText(boolean previewMode) {
    String expireText = "Expire";
    long timeUntilExpiry = getExpiryMs(previewMode) - System.currentTimeMillis();
    if (timeUntilExpiry < 0) {
      expireText += "d " + DurationFormatter.format(Math.abs(timeUntilExpiry)) + " ago";
    } else {
      expireText += "s in " + DurationFormatter.format(Math.abs(timeUntilExpiry));
    }
    return expireText;
  }

  public long getExpiryMs(boolean previewMode) {
    int minutesLeft = playedMinutesAtCreation + 59 - deathStorageManager.getPlayedMinutes();
    if (previewMode) {
      return System.currentTimeMillis() + (minutesLeft * 60000L);
    }

    return System.currentTimeMillis() + (minutesLeft * 60000L) - (
        (System.currentTimeMillis() - deathStorageManager.startMs) % 60000);
  }

  public boolean hasExpired(boolean previewMode) {
    return getExpiryMs(previewMode) < System.currentTimeMillis();
  }
}
