package dev.thource.runelite.dudewheresmystuff.coins;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemStorage;
import java.util.Optional;
import lombok.Getter;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;

/**
 * CoinsStorage is responsible for tracking storages that hold the players coins (coffers,
 * inventory, etc).
 */
@Getter
public class CoinsStorage extends ItemStorage<CoinsStorageType> {

  protected final ItemStack coinStack = new ItemStack(995, "Coins", 0, 1, 0, true);

  protected CoinsStorage(CoinsStorageType type, DudeWheresMyStuffPlugin plugin) {
    super(type, plugin);

    hasStaticItems = true;

    items.add(coinStack);
  }

  @Override
  public boolean onGameTick() {
    if (itemContainerWatcher != null && itemContainerWatcher.wasJustUpdated()) {
      Optional<ItemStack> coinsItem =
          itemContainerWatcher.getItems().stream().filter(i -> i.getId() == 995).findFirst();
      coinStack.setQuantity(coinsItem.map(ItemStack::getQuantity).orElse(0L));

      lastUpdated = System.currentTimeMillis();

      return true;
    }

    return false;
  }

  @Override
  public boolean onVarbitChanged() {
    if (type.getVarbitId() == -1) {
      return false;
    }

    int coins = plugin.getClient().getVarbitValue(type.getVarbitId()) * type.getMultiplier();
    if (coinStack.getQuantity() == coins) {
      return false;
    }

    coinStack.setQuantity(coins);
    return true;
  }

  @Override
  public boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    if (itemContainerWatcher != null
        || type.getItemContainerId() == -1
        || type.getItemContainerId() != itemContainerChanged.getContainerId()) {
      return false;
    }

    ItemContainer itemContainer = plugin.getClient().getItemContainer(type.getItemContainerId());
    if (itemContainer == null) {
      return false;
    }

    lastUpdated = System.currentTimeMillis();
    int coins = itemContainer.count(995);
    if (coinStack.getQuantity() == coins) {
      return !this.getType().isAutomatic();
    }

    coinStack.setQuantity(coins);
    return true;
  }
}
