package dev.thource.runelite.dudewheresmystuff;

import dev.thource.runelite.dudewheresmystuff.death.DeathStorageType;
import dev.thource.runelite.dudewheresmystuff.death.Deathbank;
import dev.thource.runelite.dudewheresmystuff.death.Deathpile;
import java.awt.Dimension;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.vars.AccountType;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.util.SwingUtil;

class SearchTabPanel
    extends StorageTabPanel<
        StorageType, Storage<StorageType>, StorageManager<StorageType, Storage<StorageType>>> {

  @Getter private final IconTextField searchBar;
  private final DudeWheresMyStuffPanel pluginPanel;
  private final transient StorageManagerManager storageManagerManager;
  @Setter private AccountType accountType;

  SearchTabPanel(
      ItemManager itemManager,
      DudeWheresMyStuffConfig config,
      DudeWheresMyStuffPanel pluginPanel,
      StorageManagerManager storageManagerManager) {
    super(itemManager, config, null);
    this.pluginPanel = pluginPanel;
    this.storageManagerManager = storageManagerManager;

    JPanel searchBarContainer = new JPanel();
    searchBarContainer.setLayout(new BoxLayout(searchBarContainer, BoxLayout.Y_AXIS));
    searchBarContainer.setBorder(new EmptyBorder(6, 0, 2, 0));
    add(searchBarContainer, 1);

    searchBar = new IconTextField();
    searchBar.setIcon(IconTextField.Icon.SEARCH);
    searchBar.setPreferredSize(new Dimension(getWidth(), 30));
    searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
    searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
    searchBar
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              @Override
              public void insertUpdate(DocumentEvent e) {
                onSearchBarChanged();
              }

              @Override
              public void removeUpdate(DocumentEvent e) {
                onSearchBarChanged();
              }

              @Override
              public void changedUpdate(DocumentEvent e) {
                onSearchBarChanged();
              }
            });
    searchBarContainer.add(searchBar);
  }

  private void onSearchBarChanged() {
    SwingUtilities.invokeLater(this::rebuildList);
  }

  @Override
  protected boolean showPrice() {
    return false;
  }

  @Override
  protected void rebuildList() {
    SwingUtil.fastRemoveAll(itemsBoxContainer);
    itemsBoxes.clear();
    createItemsBoxes()
        .forEach(
            itemsBox -> {
              itemsBoxContainer.add(itemsBox);
              itemsBoxes.add(itemsBox);
            });

    revalidate();
  }

  private Optional<ItemsBox> createItemsBox(Storage<?> storage) {
    String searchText = searchBar.getText().toLowerCase(Locale.ROOT);
    List<ItemStack> items =
        storage.getItems().stream()
            .filter(
                i ->
                    i.getId() != -1
                        && i.getQuantity() > 0
                        && (Objects.equals(searchText, "")
                            || i.getName().toLowerCase(Locale.ROOT).contains(searchText)))
            .collect(Collectors.toList());
    if (items.isEmpty()) {
      return Optional.empty();
    }

    ItemsBox itemsBox =
        new ItemsBox(
            itemManager,
            storageManagerManager.getCarryableStorageManager().getPluginManager(),
            storageManagerManager.getCarryableStorageManager().getItemIdentificationPlugin(),
            storageManagerManager.getCarryableStorageManager().getItemIdentificationConfig(),
            storageManagerManager.getCarryableStorageManager().getClientThread(),
            storage,
            null,
            showPrice());
    for (ItemStack itemStack : items) {
      if (itemStack.getQuantity() > 0) {
        itemsBox.getItems().add(itemStack);
      }
    }
    itemsBox.setSortMode(config.itemSortMode());
    itemsBox.rebuild();
    decorateItemsBox(storage, itemsBox);

    return Optional.of(itemsBox);
  }

  private void decorateItemsBox(Storage<?> storage, ItemsBox itemsBox) {
    if (storage instanceof Deathbank) {
      if (((Deathbank) storage).getLostAt() == -1L
          && (accountType != AccountType.ULTIMATE_IRONMAN
              || storage.getType() != DeathStorageType.ZULRAH)) {
        itemsBox.setSubTitle(((Deathbank) storage).isLocked() ? "Locked" : "Unlocked");
      }
    } else if (storage instanceof Deathpile) {
      Deathpile deathpile = (Deathpile) storage;
      itemsBox.addExpiry(deathpile.getExpiryMs(pluginPanel.isPreviewPanel()));

      Region region = Region.get(deathpile.getWorldPoint().getRegionID());

      if (region == null) {
        itemsBox.setSubTitle("Unknown");
      } else {
        itemsBox.setSubTitle(region.getName());
      }
    }
  }

  private List<ItemsBox> createItemsBoxes() {
    return storageManagerManager
        .getStorages()
        .filter(Storage::isEnabled)
        .sorted(Comparator.comparing(s -> s.getType().getName()))
        .map(this::createItemsBox)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }
}
