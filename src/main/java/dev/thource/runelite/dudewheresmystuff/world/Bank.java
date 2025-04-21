package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemStackUtils;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;

/** Bank is responsible for tracking what the player has in their bank. */
public class Bank extends WorldStorage {

  protected Bank(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.BANK, plugin);
  }

  @Override
  public boolean onGameTick() {
    var updated = super.onGameTick();

    var client = plugin.getClient();
    var depositBoxWidget = client.getWidget(InterfaceID.BankDepositbox.FRAME);
    if (depositBoxWidget != null && !depositBoxWidget.isHidden()) {
      PotionStorage potionStorage = null;
      if (client.getVarbitValue(VarbitID.BANK_DEPOSITPOTION) == 1) {
        potionStorage = plugin.getWorldStorageManager().getPotionStorage();
      }

      for (ItemStack itemStack :
          ItemContainerWatcher.getInventoryWatcher().getItemsRemovedLastTick()) {
        // Check if the item will be sent to PotionStorage
        if (potionStorage != null
            && potionStorage.getDoseMap().get(itemStack.getCanonicalId()) != null) {
          continue;
        }

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
