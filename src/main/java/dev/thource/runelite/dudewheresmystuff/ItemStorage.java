package dev.thource.runelite.dudewheresmystuff;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Getter;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;

public class ItemStorage<T extends StorageType> extends Storage<T> {
    @Nullable
    protected final ItemContainerWatcher itemContainerWatcher;
    @Saved(index = 1) @Getter protected List<ItemStack> items = new ArrayList<>();
    protected boolean hasStaticItems = false;

    protected ItemStorage(T type, DudeWheresMyStuffPlugin plugin) {
        super(type, plugin);

        itemContainerWatcher = ItemContainerWatcher.getWatcher(type.getItemContainerId());
    }

    @Override
    public boolean onGameTick() {
        if (itemContainerWatcher != null && itemContainerWatcher.wasJustUpdated()) {
            items.clear();
            items.addAll(itemContainerWatcher.getItems());
            lastUpdated = System.currentTimeMillis();

            return true;
        }

        return false;
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

    @Override
    public long getTotalValue() {
        if (items.isEmpty()) { // avoids a NPE from .sum() on empty stream
            return 0;
        }

        return items.stream().mapToLong(ItemStack::getTotalGePrice).sum();
    }

    @Override
    protected String getItemStackListSaveString(List<ItemStack> list) {
        if (hasStaticItems) {
            return list.stream()
               .map(item -> Long.toString(item.getQuantity()))
               .collect(Collectors.joining(","));
        }

        return super.getItemStackListSaveString(list);
    }

    @Override
    protected void loadItemStackListForField(Field field, String data) throws IllegalAccessException {
        if (hasStaticItems) {
            @SuppressWarnings("unchecked")
            List<ItemStack> list = (List<ItemStack>) field.get(this);

            int i = 0;
            for (String quantity : data.split(",")) {
                if (i >= list.size()) {
                    break;
                }

                list.get(i).setQuantity(Long.parseLong(quantity));
                i++;
            }

            return;
        }

        super.loadItemStackListForField(field, data);
    }

    @Override
    public void reset() {
        if (hasStaticItems) {
            items.forEach(item -> item.setQuantity(0));
        } else {
            items.clear();
        }

        super.reset();
    }
}
