package dev.thource.runelite.dudewheresmystuff;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

/** StorageTabPanel is a base class that shows the player their data. */
public abstract class StorageTabPanel<
        T extends StorageType, S extends Storage<T>, M extends StorageManager<T, S>>
    extends TabContentPanel {

  protected final transient DudeWheresMyStuffPlugin plugin;
  @Getter protected final transient M storageManager;
  protected final JPanel storagePanelContainer;
  @Getter protected final JComboBox<ItemSortMode> sortItemsDropdown;
  protected final transient List<StoragePanel> storagePanels = new ArrayList<>();

  protected StorageTabPanel(DudeWheresMyStuffPlugin plugin, M storageManager) {
    this.plugin = plugin;
    this.storageManager = storageManager;
    storageManager.setStorageTabPanel(this);

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
    sortItemsDropdown.setSelectedItem(plugin.getConfig().itemSortMode());
    sortItemsDropdown.addItemListener(i -> plugin.getConfig().setItemSortMode((ItemSortMode) i.getItem()));
    sortItemsDropdown.setPreferredSize(new Dimension(-1, 30));
    sortItemsContainer.add(sortItemsDropdown);

    storagePanelContainer = new JPanel();
    storagePanelContainer.setLayout(new BoxLayout(storagePanelContainer, BoxLayout.Y_AXIS));
    add(storagePanelContainer);
  }

  protected Comparator<S> getStorageSorter() {
    return Comparator.comparingLong(S::getTotalValue)
        .reversed()
        .thenComparingInt(s -> -s.getItems().size())
        .thenComparing(s -> s.getType().getName());
  }

  public void reorderStoragePanels() {
    EnhancedSwingUtilities.fastRemoveAll(storagePanelContainer);
    storagePanels.clear();

    storageManager.getStorages().stream()
        .filter(
            storage ->
                plugin.getConfig().showEmptyStorages()
                    || !storage.getStoragePanel().getItemBoxes().isEmpty())
        .sorted(getStorageSorter())
        .forEach(
            storage -> {
              storagePanelContainer.add(storage.getStoragePanel());
              storagePanels.add(storage.getStoragePanel());
            });

    storagePanelContainer.revalidate();
  }

  @Override
  public void softUpdate() {
    storagePanels.forEach(panel -> panel.getStorage().softUpdate());
  }
}
