package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.*;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

import java.util.List;

@Getter
public class Deathpile extends DeathStorage {
    private final int playedMinutesAtCreation;
    private final WorldPoint worldPoint;
    public DeathWorldMapPoint worldMapPoint;
    private DeathStorageManager deathStorageManager;

    public Deathpile(Client client, ItemManager itemManager, int playedMinutesAtCreation, WorldPoint worldPoint, DeathStorageManager deathStorageManager, List<ItemStack> deathItems) {
        super(DeathStorageType.DEATHPILE, client, itemManager);
        this.playedMinutesAtCreation = playedMinutesAtCreation;
        this.worldPoint = worldPoint;
        this.deathStorageManager = deathStorageManager;
        this.items = deathItems;
    }

    public String getExpireText() {
        String expireText = "Expire";
        long timeUntilExpiry = getExpiryMs() - System.currentTimeMillis();
        if (timeUntilExpiry < 0) {
            expireText += "d " + DurationFormatter.format(Math.abs(timeUntilExpiry)) + " ago";
        } else {
            expireText += "s in " + DurationFormatter.format(Math.abs(timeUntilExpiry));
        }
        return expireText;
    }

    public long getExpiryMs() {
        int minutesLeft = playedMinutesAtCreation + 59 - deathStorageManager.getPlayedMinutes();

        return System.currentTimeMillis() + (minutesLeft * 60000L) - ((System.currentTimeMillis() - deathStorageManager.startMs) % 60000);
    }

    public boolean hasExpired() {
        return getExpiryMs() < System.currentTimeMillis();
    }
}
