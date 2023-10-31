package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import javax.annotation.Nonnull;
import net.runelite.api.ItemID;

class GraveInfoBox extends ExpiringDeathStorageInfoBox {

  public GraveInfoBox(@Nonnull DudeWheresMyStuffPlugin plugin, Grave storage) {
    super(plugin, storage, ItemID.GRAVESTONE);
  }
}
