package dev.thource.runelite.dudewheresmystuff;

import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

abstract class StorageTabPanel<ST extends StorageType, S extends Storage<ST>, SM extends StorageManager<ST, S>> extends TabContentPanel {
    protected final DudeWheresMyStuffConfig config;
    protected final SM storageManager;
    protected final ItemManager itemManager;
    final List<ItemsBox> itemsBoxes = new ArrayList<>();

    StorageTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config, DudeWheresMyStuffPanel pluginPanel, SM storageManager) {
        this.itemManager = itemManager;
        this.config = config;
        this.storageManager = storageManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
    }

    protected Comparator<S> getStorageSorter() {
        return Comparator.comparingLong(S::getTotalValue)
                .reversed()
                .thenComparing(s -> s.getType().getName());
    }

    protected boolean showPrice() {
        return true;
    }

    protected void rebuildList(boolean isMember) {
        removeAll();

        itemsBoxes.clear();
        storageManager.storages.stream().sorted(getStorageSorter()).forEach((storage) -> {
            if (storage.getType().isMembersOnly() && !isMember) return;

            ItemsBox itemsBox = new ItemsBox(itemManager, storage, null, false, showPrice());
            for (ItemStack itemStack : storage.getItems()) {
                if (itemStack.getQuantity() > 0)
                    itemsBox.getItems().add(itemStack);
            }
            itemsBox.rebuild();
            itemsBoxes.add(itemsBox);
            add(itemsBox);
        });

        revalidate();
    }

    @Override
    public void update(boolean isMember) {
        rebuildList(isMember);
    }

    public void softUpdate() {
        itemsBoxes.forEach(ItemsBox::updateLabels);
    }
}
