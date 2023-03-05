package dev.thource.runelite.dudewheresmystuff;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.vars.AccountType;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.IconTextField;

class SearchTabPanel
    extends StorageTabPanel<
        StorageType, Storage<StorageType>, StorageManager<StorageType, Storage<StorageType>>> {

  private static final String EMPTY_SEARCH_TEXT =
      "<html>Type at least 3 characters in the search bar to find your stuff</html>";
  private static final String NO_RESULTS_TEXT =
      "<html>No items found for your search criteria</html>";
  @Getter private final IconTextField searchBar;
  private final transient StorageManagerManager storageManagerManager;
  private final JLabel searchStatusLabel;
  private final JPanel searchStatusPanel;
  @Setter private AccountType accountType;

  SearchTabPanel(DudeWheresMyStuffPlugin plugin, StorageManagerManager storageManagerManager) {
    super(plugin, new SearchStorageManager(plugin));
    this.storageManagerManager = storageManagerManager;

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

    JPanel searchBarContainer = new JPanel();
    searchBarContainer.setLayout(new BoxLayout(searchBarContainer, BoxLayout.Y_AXIS));
    searchBarContainer.setBorder(new EmptyBorder(6, 0, 2, 0));
    searchBarContainer.add(searchBar);

    searchStatusLabel = new JLabel(EMPTY_SEARCH_TEXT);
    searchStatusLabel.setFont(FontManager.getRunescapeFont());
    searchStatusLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
    searchStatusLabel.setVerticalAlignment(SwingConstants.CENTER);
    searchStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);

    searchStatusPanel = new JPanel(new BorderLayout());
    searchStatusPanel.setBorder(new EmptyBorder(7, 7, 7, 7));
    searchStatusPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
    searchStatusPanel.add(searchStatusLabel);

    add(searchBarContainer, 1);
    add(searchStatusPanel, 2);
  }

  private void onSearchBarChanged() {
    if (searchBar.getText().length() >= 3) {
      searchStatusPanel.setVisible(false);

      storagePanels.forEach(
          panel -> {
            ((SearchStoragePanel) panel).setSearchText(searchBar.getText());
            panel.update();
          });
      reorderStoragePanels();

      if (storagePanelContainer.getComponentCount() == 0) {
        searchStatusLabel.setText(NO_RESULTS_TEXT);
        searchStatusPanel.setVisible(true);
      }
    } else {
      searchStatusLabel.setText(EMPTY_SEARCH_TEXT);
      searchStatusPanel.setVisible(true);

      EnhancedSwingUtilities.fastRemoveAll(storagePanelContainer);
      storagePanelContainer.revalidate();
    }

    softUpdate();
  }

  @Override
  public void reorderStoragePanels() {
    EnhancedSwingUtilities.fastRemoveAll(storagePanelContainer);
    storagePanels.stream()
        .filter(panel -> !panel.getItemBoxes().isEmpty())
        .sorted(Comparator.comparing(panel -> panel.getStorage().getName()))
        .forEach(storagePanelContainer::add);

    storagePanelContainer.revalidate();
  }

  @Override
  protected Comparator<Storage<StorageType>> getStorageSorter() {
    return Comparator.comparing(Storage::getName);
  }

  @Override
  public void softUpdate() {
    List<? extends Storage<? extends Enum<? extends Enum<?>>>> storages =
        storageManagerManager.getStorages().filter(Storage::isEnabled).collect(Collectors.toList());

    storages.forEach(
        storage -> {
          if (storagePanels.stream().noneMatch(panel -> panel.getStorage() == storage)) {
            storagePanels.add(new SearchStoragePanel(plugin, storage));
          }
        });

    ListIterator<StoragePanel> iterator = storagePanels.listIterator();
    while (iterator.hasNext()) {
      Storage<?> storage = iterator.next().getStorage();

      if (!storages.contains(storage)) {
        iterator.remove();
      }
    }

    storagePanels.stream()
        .filter(panel -> panel.getParent() != null)
        .forEach(
            panel -> {
              panel.getStorage().softUpdate();

              StoragePanel sourcePanel = panel.getStorage().getStoragePanel();
              panel.setTitle(sourcePanel.getTitle());
              panel.setTitleToolTip(sourcePanel.getTitleToolTip());
              panel.setSubTitle(sourcePanel.getSubTitle());
              panel.setFooterText(sourcePanel.getFooterText());
            });

    super.softUpdate();
  }
}
