package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
abstract class Storage<T extends StorageType> {
    protected Client client;
    protected ItemManager itemManager;

    protected T type;
    protected List<ItemStack> items = new ArrayList<>();

    protected long lastUpdated = -1L;
    protected boolean enabled = true;

    Storage(T type, Client client, ItemManager itemManager) {
        this.type = type;
        this.client = client;
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

    public boolean onVarbitChanged() {
        return false;
    }

    public boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
        if (type.getItemContainerId() == -1 || type.getItemContainerId() != itemContainerChanged.getContainerId())
            return false;

        ItemContainer itemContainer = client.getItemContainer(type.getItemContainerId());
        if (itemContainer == null) return false;

        items.clear();
        for (Item item : itemContainer.getItems()) {
            if (item.getId() == -1) {
                items.add(new ItemStack(item.getId(), "empty slot", 1, 0, 0, false));
                continue;
            }

            ItemComposition itemComposition = itemManager.getItemComposition(item.getId());
            items.add(new ItemStack(item.getId(), itemComposition.getName(), item.getQuantity(), itemManager.getItemPrice(item.getId()), itemComposition.getHaPrice(), itemComposition.isStackable()));
        }

        lastUpdated = System.currentTimeMillis();

        return true;
    }

    public void reset() {
        items.clear();
        lastUpdated = -1;
        enable();
    }

    public void save(ConfigManager configManager, String managerConfigKey) {
        String data = lastUpdated + ";"
                + items.stream().map(item -> item.getId() + "," + item.getQuantity()).collect(Collectors.joining("="));

//        System.out.println("save " + managerConfigKey + "." + type.getConfigKey() + ": " + data);
        configManager.setRSProfileConfiguration(
                DudeWheresMyStuffConfig.CONFIG_GROUP,
                managerConfigKey + "." + type.getConfigKey(),
                data
        );
    }

    protected List<ItemStack> loadItems(ConfigManager configManager, String managerConfigKey, String profileKey) {
        String data = configManager.getConfiguration(
                DudeWheresMyStuffConfig.CONFIG_GROUP,
                profileKey,
                managerConfigKey + "." + type.getConfigKey(),
                String.class
        );
        if (data == null) return null;

//        System.out.println("load " + managerConfigKey + "." + type.getConfigKey() + ": " + data);
        String[] dataSplit = data.split(";");
        if (dataSplit.length != 2) return null;

        this.lastUpdated = NumberUtils.toLong(dataSplit[0], -1);

        List<ItemStack> items = new ArrayList<>();
        for (String itemStackString : dataSplit[1].split("=")) {
            String[] itemStackData = itemStackString.split(",");
            if (itemStackData.length != 2) continue;

            int itemId = NumberUtils.toInt(itemStackData[0]);
            int itemQuantity = NumberUtils.toInt(itemStackData[1]);

            ItemComposition itemComposition = itemManager.getItemComposition(itemId);
            items.add(new ItemStack(itemId, itemComposition.getName(), itemQuantity, itemManager.getItemPrice(itemId), itemComposition.getHaPrice(), itemComposition.isStackable()));
        }

        return items;
    }

    public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
        List<ItemStack> loadedItems = loadItems(configManager, managerConfigKey, profileKey);
        if (loadedItems == null || loadedItems.isEmpty()) return;

        items.clear();
        items.addAll(loadedItems);
    }

    public void disable() {
        enabled = false;
    }

    public void enable() {
        enabled = true;
    }
}
