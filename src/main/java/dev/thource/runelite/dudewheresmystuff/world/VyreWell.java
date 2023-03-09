package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import net.runelite.api.ItemID;

public class VyreWell extends WorldStorage {

  private final ItemStack vialsOfBlood;
  private final ItemStack bloodRunes;

  protected VyreWell(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.VYRE_WELL, plugin);

    hasStaticItems = true;

    vialsOfBlood = new ItemStack(ItemID.VIAL_OF_BLOOD_22446, plugin);
    items.add(vialsOfBlood);
    bloodRunes = new ItemStack(ItemID.BLOOD_RUNE, plugin);
    items.add(bloodRunes);
  }

  @Override
  public boolean onVarbitChanged() {
    int newValue = plugin.getClient().getVarbitValue(6455);
    if (newValue == vialsOfBlood.getQuantity()) {
      return false;
    }

    vialsOfBlood.setQuantity(newValue);
    bloodRunes.setQuantity(newValue * 300L);

    return true;
  }
}
