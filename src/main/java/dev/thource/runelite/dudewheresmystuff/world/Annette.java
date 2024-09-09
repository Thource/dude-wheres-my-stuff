package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import net.runelite.api.ItemID;

/** Annette is responsible for tracking how many drift nets the player has stored with Annette. */
public class Annette extends WorldStorage {

  protected Annette(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.ANNETTE, plugin);

    hasStaticItems = true;
    varbits = new int[] {243};

    items.add(new ItemStack(ItemID.DRIFT_NET, plugin));
  }
}
