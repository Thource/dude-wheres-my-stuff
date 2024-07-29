package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import javax.annotation.Nonnull;
import net.runelite.api.ItemID;

class DeathpileInfoBox extends ExpiringDeathStorageInfoBox {

  public DeathpileInfoBox(@Nonnull DudeWheresMyStuffPlugin plugin, Deathpile deathpile) {
    super(plugin, deathpile, ItemID.BONES);
  }
}
