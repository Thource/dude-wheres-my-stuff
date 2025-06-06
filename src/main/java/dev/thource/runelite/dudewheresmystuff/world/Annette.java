package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

/** Annette is responsible for tracking how many drift nets the player has stored with Annette. */
public class Annette extends WorldStorage {

  protected Annette(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.ANNETTE, plugin);

    hasStaticItems = true;
    varbits = new int[]{VarbitID.FOSSIL_DRIFTNET_STORE};

    items.add(new ItemStack(ItemID.FOSSIL_DRIFT_NET, plugin));
  }
}
