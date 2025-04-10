package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import net.runelite.api.gameval.ItemID;

/**
 * VyreWell is responsible for tracking how many blood runes and vials the player has stored in the
 * vyre well.
 */
public class VyreWell extends WorldStorage {

  private final ItemStack vialsOfBlood;
  private final ItemStack bloodRunes;

  protected VyreWell(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.VYRE_WELL, plugin);

    hasStaticItems = true;

    varbits = new int[]{6455};

    vialsOfBlood = new ItemStack(ItemID.VIAL_BLOOD, plugin);
    items.add(vialsOfBlood);
    bloodRunes = new ItemStack(ItemID.BLOODRUNE, plugin);
    items.add(bloodRunes);
  }

  @Override
  public boolean onVarbitChanged() {
    boolean updated = super.onVarbitChanged();

    if (updated) {
      bloodRunes.setQuantity(vialsOfBlood.getQuantity() * 200L);
    }

    return updated;
  }
}
