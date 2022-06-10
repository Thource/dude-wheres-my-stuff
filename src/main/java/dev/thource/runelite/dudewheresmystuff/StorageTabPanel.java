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
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.SwingUtil;

/** StorageTabPanel is a base class that shows the player their data. */
public abstract class StorageTabPanel<
        T extends StorageType, S extends Storage<T>, M extends StorageManager<T, S>>
    extends TabContentPanel {

  protected final transient DudeWheresMyStuffConfig config;
  @Getter protected final transient M storageManager;
  protected final transient ItemManager itemManager;
  protected final JPanel itemsBoxContainer;
  protected final JComboBox<ItemSortMode> sortItemsDropdown;
  protected final transient List<ItemsBox> itemsBoxes = new ArrayList<>();

  protected StorageTabPanel(
      ItemManager itemManager, DudeWheresMyStuffConfig config, M storageManager) {
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
    sortItemsDropdown.addItemListener(
        i -> {
          ItemSortMode newSortMode = (ItemSortMode) i.getItem();
          if (config.itemSortMode() == newSortMode) {
            return;
          }

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
        .thenComparingInt(s -> -s.getItems().size())
        .thenComparing(s -> s.getType().getName());
  }

  protected boolean showPrice() {
    return true;
  }

  protected void rebuildList() {
    SwingUtil.fastRemoveAll(itemsBoxContainer);

    itemsBoxes.clear();
    storageManager.getStorages().stream()
        .sorted(getStorageSorter())
        .filter(Storage::isEnabled)
        .filter(
            storage -> {
              if (config.showEmptyStorages()) {
                return true;
              }

              return storage.getItems().stream()
                  .anyMatch(itemStack -> itemStack.getId() != -1 && itemStack.getQuantity() > 0);
            })
        .forEach(
            storage -> {
              ItemsBox itemsBox =
                  new ItemsBox(
                      itemManager,
                      storageManager.getPluginManager(),
                      storageManager.getItemIdentificationPlugin(),
                      storageManager.getItemIdentificationConfig(),
                      storageManager.getClientThread(),
                      storage,
                      null,
                      false,
                      showPrice());
              for (ItemStack itemStack : storage.getItems()) {
                if (itemStack.getQuantity() > 0) {
                  itemsBox.getItems().add(itemStack);
                }
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
