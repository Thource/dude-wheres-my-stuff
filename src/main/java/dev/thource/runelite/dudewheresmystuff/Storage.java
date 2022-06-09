package dev.thource.runelite.dudewheresmystuff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import org.apache.commons.lang3.math.NumberUtils;

/** Storage serves as a base class for all trackable data in the plugin. */
@Getter
public abstract class Storage<T extends StorageType> {

  protected final List<ItemStack> items = new ArrayList<>();
  protected Client client;
  protected ClientThread clientThread;
  protected ItemManager itemManager;
  protected T type;
  @Setter protected long lastUpdated = -1L;
  protected boolean enabled = true;
  @Setter protected boolean collapsed = false;

  protected Storage(T type, Client client, ClientThread clientThread, ItemManager itemManager) {
    this.type = type;
    this.client = client;
    this.clientThread = clientThread;
    this.itemManager = itemManager;
  }

  public long getTotalValue() {
    return items.stream().mapToLong(ItemStack::getTotalGePrice).sum();
  }

  public boolean onGameTick() {
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

  /**
   * tells the Storage that the onItemContainerChanged event was called and that it should update
   * the player's trackable data.
   *
   * @param itemContainerChanged the ItemContainerChanged event
   * @return whether the data changed
   */
  public boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    if (type.getItemContainerId() == -1
        || type.getItemContainerId() != itemContainerChanged.getContainerId()) {
      return false;
    }

    ItemContainer itemContainer = client.getItemContainer(type.getItemContainerId());
    if (itemContainer == null) {
      return false;
    }

    items.clear();
    for (Item item : itemContainer.getItems()) {
      if (item.getId() == -1) {
        items.add(new ItemStack(item.getId(), "empty slot", 1, 0, 0, false));
        continue;
      }

      ItemComposition itemComposition = itemManager.getItemComposition(item.getId());
      items.add(
          new ItemStack(
              item.getId(),
              itemComposition.getName(),
              item.getQuantity(),
              itemManager.getItemPrice(item.getId()),
              itemComposition.getHaPrice(),
              itemComposition.isStackable()));
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

      ItemComposition itemComposition = itemManager.getItemComposition(itemId);
      loadedItems.add(
          new ItemStack(
              itemId,
              itemComposition.getName(),
              itemQuantity,
              itemManager.getItemPrice(itemId),
              itemComposition.getHaPrice(),
              itemComposition.isStackable()));
    }

    return loadedItems;
  }

  /** loads the Storage data from the specified RuneLite RS profile config. */
  public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
    List<ItemStack> loadedItems = loadItems(configManager, managerConfigKey, profileKey);
    if (loadedItems == null || loadedItems.isEmpty()) {
      return;
    }

    items.clear();
    items.addAll(loadedItems);
  }

  public void disable() {
    enabled = false;
  }

  public void enable() {
    enabled = true;
  }

  public String getName() {
    return type.getName();
  }
}
