package dev.thource.runelite.dudewheresmystuff.death;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;

/** Deathbank is responsible for tracking the player's deathbanked items. */
@Slf4j
@Getter
@Setter
public class Deathbank extends DeathStorage {

  private boolean locked = false;
  private long lostAt = -1L;

  Deathbank(DeathStorageType deathStorageType, Client client, ItemManager itemManager) {
    super(deathStorageType, client, itemManager);
  }

  void setType(DeathStorageType type) {
    this.type = type;
  }

  @Override
  public void reset() {
    super.reset();
    locked = false;
    lostAt = -1L;
    enable();
  }
}
