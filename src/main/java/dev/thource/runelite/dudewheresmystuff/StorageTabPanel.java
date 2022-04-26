package dev.thource.runelite.dudewheresmystuff;

import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

abstract class StorageTabPanel<ST extends StorageType, S extends Storage<ST>, SM extends StorageManager<ST, S>> extends TabContentPanel {
    protected final DudeWheresMyStuffConfig config;
    protected final SM storageManager;
    protected final ItemManager itemManager;
    final List<ItemsBox> itemsBoxes = new ArrayList<>();
    protected final JPanel itemsBoxContainer;
    protected final JComboBox<ItemSortMode> sortItemsDropdown;

    StorageTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config, DudeWheresMyStuffPanel pluginPanel, SM storageManager) {
        this.itemManager = itemManager;
        this.config = config;
        this.storageManager = storageManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel sortItemsContainer = new JPanel();
        sortItemsContainer.setLayout(new BoxLayout(sortItemsContainer, BoxLayout.X_AXIS));
        add(sortItemsContainer);

        JLabel sortItemsLabel = new JLabel();
        sortItemsLabel.setFont(FontManager.getRunescapeFont());
        sortItemsLabel.setForeground(Color.WHITE);
        sortItemsLabel.setText("Sort items by");
        sortItemsLabel.setBorder(new EmptyBorder(0, 0, 0, 8));
        sortItemsContainer.add(sortItemsLabel);

        sortItemsDropdown = new JComboBox<>();
        sortItemsDropdown.setFont(FontManager.getRunescapeFont());
        sortItemsDropdown.setForeground(Color.WHITE);
        sortItemsDropdown.addItem(ItemSortMode.VALUE);
        sortItemsDropdown.addItem(ItemSortMode.UNSORTED);
        sortItemsDropdown.setSelectedItem(config.itemSortMode());
        sortItemsDropdown.addItemListener((i) -> {
            ItemSortMode newSortMode = (ItemSortMode) i.getItem();
            if (config.itemSortMode() == newSortMode) return;

            config.setItemSortMode((ItemSortMode) i.getItem());
            update();
        });
        sortItemsDropdown.setPreferredSize(new Dimension(-1, 30));
        sortItemsContainer.add(sortItemsDropdown);

        itemsBoxContainer = new JPanel();
        itemsBoxContainer.setLayout(new BoxLayout(itemsBoxContainer, BoxLayout.Y_AXIS));
        add(itemsBoxContainer);
    }

    protected Comparator<S> getStorageSorter() {
        return Comparator.comparingLong(S::getTotalValue)
                .reversed()
                .thenComparing(s -> s.getType().getName());
    }

    protected boolean showPrice() {
        return true;
    }

    protected void rebuildList() {
        itemsBoxContainer.removeAll();

        itemsBoxes.clear();
        storageManager.storages.stream().sorted(getStorageSorter()).forEach((storage) -> {
            if (!storage.isEnabled()) return;

            ItemsBox itemsBox = new ItemsBox(itemManager, storage, null, false, showPrice());
            for (ItemStack itemStack : storage.getItems()) {
                if (itemStack.getQuantity() > 0)
                    itemsBox.getItems().add(itemStack);
            }
            itemsBox.setSortMode(config.itemSortMode());
            itemsBox.rebuild();
            itemsBoxes.add(itemsBox);
            itemsBoxContainer.add(itemsBox);
        });

        revalidate();
    }

    @Override
    public void update() {
        sortItemsDropdown.setSelectedItem(config.itemSortMode());
        rebuildList();
    }

    public void softUpdate() {
        itemsBoxes.forEach(ItemsBox::updateLabels);
    }
}
