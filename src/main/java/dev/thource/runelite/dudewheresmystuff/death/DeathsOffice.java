package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;

/** DeathsOffice shows the user what items they have stored in death's office. */
public class DeathsOffice extends DeathStorage {

  private final DeathStorageManager deathStorageManager;

  protected DeathsOffice(DudeWheresMyStuffPlugin plugin, DeathStorageManager deathStorageManager) {
    super(DeathStorageType.DEATHS_OFFICE, plugin);

    this.deathStorageManager = deathStorageManager;
  }
}
