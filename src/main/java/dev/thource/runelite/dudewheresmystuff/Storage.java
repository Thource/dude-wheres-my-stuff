package dev.thource.runelite.dudewheresmystuff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.vars.AccountType;
import net.runelite.client.config.ConfigManager;
import org.apache.commons.lang3.math.NumberUtils;

/** Storage serves as a base class for all trackable data in the plugin. */
@Getter
public abstract class Storage<T extends StorageType> {

  protected final List<ItemStack> items = new ArrayList<>();
  protected final ItemContainerWatcher itemContainerWatcher;
  protected final DudeWheresMyStuffPlugin plugin;
  protected T type;
  @Setter protected long lastUpdated = -1L;
  protected boolean enabled = true;
  protected StoragePanel storagePanel;

  protected Storage(T type, DudeWheresMyStuffPlugin plugin) {
    this.type = type;
    this.plugin = plugin;

    SwingUtilities.invokeLater(() -> this.storagePanel = createStoragePanel());

    itemContainerWatcher = ItemContainerWatcher.getWatcher(type.getItemContainerId());
  }

  protected StoragePanel createStoragePanel() {
    return new StoragePanel(plugin, this, true, false);
  }

  public long getTotalValue() {
    if (items.isEmpty()) { // avoids a NPE from .sum() on empty stream
      return 0;
    }

    return items.stream().mapToLong(ItemStack::getTotalGePrice).sum();
  }

  public boolean onGameTick() {
    if (itemContainerWatcher != null && itemContainerWatcher.wasJustUpdated()) {
      items.clear();
      items.addAll(itemContainerWatcher.getItems());
      lastUpdated = System.currentTimeMillis();

      return true;
    }

    return false;
  }

  public boolean onWidgetLoaded(WidgetLoaded widgetLoaded) {
    return false;
  }

  public boolean onWidgetClosed(WidgetClosed widgetClosed) {
    return false;
  }

  public boolean onChatMessage(ChatMessage chatMessage) {
    return false;
  }

  public boolean onVarbitChanged() {
    return false;
  }

  public boolean onMenuOptionClicked(MenuOptionClicked menuOption) {
    return false;
  }

  /**
   * tells the Storage that the onItemContainerChanged event was called and that it should update
   * the player's trackable data.
   *
   * @param itemContainerChanged the ItemContainerChanged event
   * @return whether the data changed
   */
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

    items.clear();
    for (Item item : itemContainer.getItems()) {
      if (item.getId() == -1) {
        items.add(new ItemStack(item.getId(), "empty slot", 1, 0, 0, false));
        continue;
      }

      ItemComposition itemComposition = plugin.getItemManager().getItemComposition(item.getId());
      if (itemComposition.getPlaceholderTemplateId() == -1) {
        items.add(new ItemStack(item.getId(), item.getQuantity(), plugin));
      }
    }

    lastUpdated = System.currentTimeMillis();

    return true;
  }

  /**
   * resets the Storage back to it's initial state, useful for when the player logs out, for
   * example.
   */
  public void reset() {
    items.clear();
    lastUpdated = -1;
    enable();
  }

  /** saves the Storage data to the player's RuneLite RS profile config. */
  public void save(ConfigManager configManager, String managerConfigKey) {
    String data =
        lastUpdated
            + ";"
            + items.stream()
                .map(item -> item.getId() + "," + item.getQuantity())
                .collect(Collectors.joining("="));

    configManager.setRSProfileConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, getConfigKey(managerConfigKey), data);
  }

  protected String getConfigKey(String managerConfigKey) {
    return managerConfigKey + "." + type.getConfigKey();
  }

  protected List<ItemStack> loadItems(
      ConfigManager configManager, String managerConfigKey, String profileKey) {
    String data =
        configManager.getConfiguration(
            DudeWheresMyStuffConfig.CONFIG_GROUP,
            profileKey,
            getConfigKey(managerConfigKey),
            String.class);
    if (data == null) {
      return Collections.emptyList();
    }

    String[] dataSplit = data.split(";");
    if (dataSplit.length < 1) {
      return Collections.emptyList();
    }

    this.lastUpdated = NumberUtils.toLong(dataSplit[0], -1);
    if (dataSplit.length != 2) {
      return Collections.emptyList();
    }

    List<ItemStack> loadedItems = new ArrayList<>();
    for (String itemStackString : dataSplit[1].split("=")) {
      String[] itemStackData = itemStackString.split(",");
      if (itemStackData.length != 2) {
        continue;
      }

      int itemId = NumberUtils.toInt(itemStackData[0]);
      int itemQuantity = NumberUtils.toInt(itemStackData[1]);

      loadedItems.add(new ItemStack(itemId, itemQuantity, plugin));
    }

    return loadedItems;
  }

  /** loads the Storage data from the specified RuneLite RS profile config. */
  public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
    items.clear();
    List<ItemStack> loadedItems = loadItems(configManager, managerConfigKey, profileKey);
    if (loadedItems == null || loadedItems.isEmpty()) {
      return;
    }

    items.addAll(loadedItems);
  }

  public void disable() {
    enabled = false;

    if (storagePanel != null) {
      SwingUtilities.invokeLater(() -> storagePanel.setVisible(false));
    }
  }

  public void disable(boolean isMember, AccountType accountType) {
    if ((type.isMembersOnly() && !isMember)
        || (type.getAccountTypeBlacklist() != null
            && type.getAccountTypeBlacklist().contains(accountType))) {
      disable();
    }
  }

  public void enable() {
    enabled = true;

    if (storagePanel != null) {
      SwingUtilities.invokeLater(() -> storagePanel.setVisible(true));
    }
  }

  public String getName() {
    return type.getName();
  }

  public void softUpdate() {
    if (!type.isAutomatic()) {
      if (lastUpdated == -1) {
        storagePanel.setFooterText("No data");
      } else {
        long timeSinceLastUpdate = System.currentTimeMillis() - lastUpdated;
        storagePanel.setFooterText(
            "Updated " + DurationFormatter.format(Math.abs(timeSinceLastUpdate)) + " ago");
      }
    }
  }
}
