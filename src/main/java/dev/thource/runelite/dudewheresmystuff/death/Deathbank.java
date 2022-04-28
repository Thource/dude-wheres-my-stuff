package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DeathStorage;
import dev.thource.runelite.dudewheresmystuff.DeathStorageType;
import dev.thource.runelite.dudewheresmystuff.DeathWorldMapPoint;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;

@Getter
public class Deathbank extends DeathStorage {

  public boolean locked = false;
  public DeathWorldMapPoint worldMapPoint;
  public long lostAt = -1L;

  public Deathbank(DeathStorageType deathStorageType, Client client, ItemManager itemManager) {
    super(deathStorageType, client, itemManager);
  }

  @Override
  public void reset() {
    super.reset();
    locked = false;
    lostAt = -1L;
    enable();
  }
}
