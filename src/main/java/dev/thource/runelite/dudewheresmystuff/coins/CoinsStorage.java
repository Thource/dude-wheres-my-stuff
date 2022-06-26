package dev.thource.runelite.dudewheresmystuff.coins;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Storage;
import java.util.Optional;
import lombok.Getter;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * CoinsStorage is responsible for tracking storages that hold the players coins (coffers,
 * inventory, etc).
 */
@Getter
public class CoinsStorage extends Storage<CoinsStorageType> {

  protected final ItemStack coinStack = new ItemStack(995, "Coins", 0, 1, 0, true);

  protected CoinsStorage(CoinsStorageType type, DudeWheresMyStuffPlugin plugin) {
    super(type, plugin);

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

  @Override
  public void reset() {
    coinStack.setQuantity(0);
    lastUpdated = -1;
    enable();
  }

  @Override
  public void save(ConfigManager configManager, String managerConfigKey) {
    String data = lastUpdated + ";" + coinStack.getQuantity();

    configManager.setRSProfileConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, managerConfigKey + "." + type.getConfigKey(), data);
  }

  @Override
  public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
    String data =
        configManager.getConfiguration(
            DudeWheresMyStuffConfig.CONFIG_GROUP,
            profileKey,
            managerConfigKey + "." + type.getConfigKey(),
            String.class);
    if (data == null) {
      return;
    }

    String[] dataSplit = data.split(";");
    if (dataSplit.length != 2) {
      return;
    }

    this.lastUpdated = NumberUtils.toLong(dataSplit[0], -1);
    coinStack.setQuantity(NumberUtils.toInt(dataSplit[1], 0));
  }
}
