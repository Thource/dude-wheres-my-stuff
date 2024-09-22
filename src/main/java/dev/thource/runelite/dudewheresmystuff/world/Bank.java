package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemStackUtils;
import net.runelite.api.widgets.Widget;

/** Bank is responsible for tracking what the player has in their bank. */
public class Bank extends WorldStorage {

  protected Bank(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.BANK, plugin);
  }

  @Override
  public boolean onGameTick() {
    boolean updated = super.onGameTick();

    Widget depositBoxWidget = plugin.getClient().getWidget(192, 1);
    if (depositBoxWidget != null && !depositBoxWidget.isHidden()) {
      for (ItemStack itemStack : ItemContainerWatcher.getInventoryWatcher()
          .getItemsRemovedLastTick()) {
        ItemStackUtils.addItemStack(items, itemStack, true);

        updated = true;
      }

      if (updated) {
        updateLastUpdated();
      }
    }

    return updated;
  }
}
