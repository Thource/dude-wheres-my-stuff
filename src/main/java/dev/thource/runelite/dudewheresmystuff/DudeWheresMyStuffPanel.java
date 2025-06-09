/*
 * Copyright (c) 2022, Thource <https://github.com/Thource>
 * Copyright (c) 2018, Abex
 * Copyright (c) 2018, Psikoi <https://github.com/psikoi>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package dev.thource.runelite.dudewheresmystuff;

import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageTabPanel;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageTabPanel;
import dev.thource.runelite.dudewheresmystuff.death.DeathStorageTabPanel;
import dev.thource.runelite.dudewheresmystuff.minigames.MinigamesStorageTabPanel;
import dev.thource.runelite.dudewheresmystuff.playerownedhouse.PlayerOwnedHouseStorageTabPanel;
import dev.thource.runelite.dudewheresmystuff.stash.StashStorageTabPanel;
import dev.thource.runelite.dudewheresmystuff.world.WorldStorageTabPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;

/**
 * DudeWheresMyStuffPanel is responsible for hosting all the StorageTabPanels, so that the player
 * can see their data.
 */
public class DudeWheresMyStuffPanel extends JPanel {

  private static final ImageIcon SEARCH_ICON;

  static {
    SEARCH_ICON =
        new ImageIcon(
            ImageUtil.loadImageResource(
                DudeWheresMyStuffPlugin.class, "/net/runelite/client/ui/components/search.png"));
  }

  private final EnumMap<Tab, FasterMaterialTab> uiTabs = new EnumMap<>(Tab.class);
  private final OverviewTabPanel overviewTab;
  private final EnumMap<Tab, StorageTabPanel<?, ?, ?>> storageTabPanelMap =
      new EnumMap<>(Tab.class);
  private final transient ItemManager itemManager;
  /* This is the panel the tabs' respective panels will be displayed on. */
  private final JPanel display = new JPanel();
  private final FasterMaterialTabGroup tabGroup;
  private final boolean previewMode;
  private final transient StorageManagerManager storageManagerManager;
  private final transient DudeWheresMyStuffPlugin plugin;
  @Setter private boolean active;
  @Getter private String displayName = "";
  @Nullable private TabContentPanel activeTabPanel = null;

  DudeWheresMyStuffPanel(
      DudeWheresMyStuffPlugin plugin,
      ConfigManager configManager,
      StorageManagerManager storageManagerManager,
      boolean previewMode) {
    super();

    this.plugin = plugin;
    this.itemManager = plugin.getItemManager();
    this.storageManagerManager = storageManagerManager;
    this.previewMode = previewMode;
    this.tabGroup = new FasterMaterialTabGroup(display, plugin);

    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(0, 0, 0, 0));
    setBackground(ColorScheme.DARK_GRAY_COLOR);

    display.setBorder(new EmptyBorder(10, 10, 8, 10));
    tabGroup.setBorder(new EmptyBorder(10, 10, 0, 10));

    add(tabGroup, BorderLayout.NORTH);
    add(display, BorderLayout.CENTER);

    overviewTab =
        new OverviewTabPanel(
            plugin,
            this,
            itemManager,
            configManager,
            storageManagerManager,
            plugin.isDeveloperMode());
    addTab(Tab.OVERVIEW, overviewTab);

    addTab(
        Tab.DEATH,
        new DeathStorageTabPanel(plugin, storageManagerManager.getDeathStorageManager()));
    addTab(
        Tab.COINS,
        new CoinsStorageTabPanel(plugin, storageManagerManager.getCoinsStorageManager()));
    addTab(
        Tab.CARRYABLE_STORAGE,
        new CarryableStorageTabPanel(plugin, storageManagerManager.getCarryableStorageManager()));
    addTab(
        Tab.STASH_UNITS,
        new StashStorageTabPanel(plugin, storageManagerManager.getStashStorageManager()));
    addTab(
        Tab.POH_STORAGE,
        new PlayerOwnedHouseStorageTabPanel(
            plugin, storageManagerManager.getPlayerOwnedHouseStorageManager()));
    addTab(
        Tab.WORLD,
        new WorldStorageTabPanel(plugin, storageManagerManager.getWorldStorageManager()));
    addTab(
        Tab.MINIGAMES,
        new MinigamesStorageTabPanel(plugin, storageManagerManager.getMinigamesStorageManager()));

    addTab(Tab.SEARCH, new SearchTabPanel(plugin, storageManagerManager));

    addTab(Tab.DEBUG, new DebugPanel(plugin));

    FasterMaterialTab donateTab = new FasterMaterialTab(
        new ImageIcon(ImageUtil.loadImageResource(DudeWheresMyStuffPlugin.class, "kofi.png")),
        tabGroup, null);
    donateTab.setPreferredSize(new Dimension(30, 27));
    donateTab.setName("Support me");
    donateTab.setToolTipText("Buy me a coffee? :)");
    donateTab.setOnSelectEvent(() -> {
      LinkBrowser.browse("https://ko-fi.com/thource");
      return false;
    });
    tabGroup.addTabToEnd(donateTab);

    for (Tab tab : Tab.TABS) {
      if (tab == Tab.OVERVIEW) {
        continue;
      }

      Optional.ofNullable(uiTabs.get(tab)).ifPresent(materialTab -> materialTab.setVisible(false));
    }
    tabGroup.resetGrid();
  }

  void setItemSortMode(ItemSortMode itemSortMode) {
    storageTabPanelMap.forEach((tab, tabPanel) -> {
      if (tab == Tab.OVERVIEW) {
        return;
      }

      plugin.getClientThread().invoke(() -> tabPanel.getStorageManager().getStorages().stream()
          .map(Storage::getStoragePanel)
          .filter(Objects::nonNull)
          .forEach(panel -> {
            panel.refreshItems();
            SwingUtilities.invokeLater(panel::update);
          }));

      JComboBox<ItemSortMode> sortDropdown = tabPanel.getSortItemsDropdown();
      final ItemListener[] itemListeners = sortDropdown.getItemListeners();

      // We need to remove and re-add the item listeners to avoid recursion
      Arrays.stream(itemListeners).forEach(sortDropdown::removeItemListener);
      sortDropdown.setSelectedItem(itemSortMode);
      Arrays.stream(itemListeners).forEach(sortDropdown::addItemListener);
    });

    ((SearchTabPanel) storageTabPanelMap.get(Tab.SEARCH)).refreshItemSortMode();
  }

  private void addTab(Tab tab, TabContentPanel tabContentPanel) {
    if (tabContentPanel instanceof StorageTabPanel) {
      storageTabPanelMap.put(tab, (StorageTabPanel<?, ?, ?>) tabContentPanel);
    }

    JPanel wrapped = new JPanel(new BorderLayout());
    wrapped.add(tabContentPanel, BorderLayout.NORTH);
    wrapped.setBackground(ColorScheme.DARK_GRAY_COLOR);

    JScrollPane scrollPane = new JScrollPane(new ScrollableContainer(wrapped));
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(16, 0));
    scrollPane.getVerticalScrollBar().setBorder(new EmptyBorder(0, 9, 0, 0));
    scrollPane.getVerticalScrollBar().setUnitIncrement(21);
    scrollPane.setBackground(ColorScheme.DARK_GRAY_COLOR);

    // Use a placeholder icon until the async image gets loaded
    FasterMaterialTab materialTab = new FasterMaterialTab(new ImageIcon(), tabGroup, scrollPane);
    materialTab.setPreferredSize(new Dimension(30, 27));
    materialTab.setName(tab.getName());
    materialTab.setToolTipText(tab.getName());

    if (tab == Tab.SEARCH) {
      materialTab.setIcon(SEARCH_ICON);
    } else {
      AsyncBufferedImage icon = itemManager.getImage(tab.getItemId(), tab.getItemQuantity(), false);
      Runnable resize =
          () -> {
            BufferedImage subIcon = icon.getSubimage(0, 0, 32, 32);
            materialTab.setIcon(
                new ImageIcon(subIcon.getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
          };
      icon.onLoaded(resize);
      resize.run();
    }

    materialTab.setOnSelectEvent(
        () -> {
          activeTabPanel = tabContentPanel;
          softUpdate();
          return true;
        });

    uiTabs.put(tab, materialTab);
    tabGroup.addTab(materialTab);

    if (tab == Tab.OVERVIEW) {
      tabGroup.select(materialTab);
    }
  }

  void switchTab(Tab tab) {
    tabGroup.select(uiTabs.get(tab));
  }

  void softUpdate() {
    if (!active || activeTabPanel == null) {
      return;
    }

    activeTabPanel.softUpdate();
  }

  void setDisplayName(String name) {
    displayName = name;
    storageManagerManager.setDisplayName(name);
  }

  void reset() {
    SwingUtilities.invokeLater(
        () -> {
          logOut();
          softUpdate();
        });
  }

  void logOut() {
    uiTabs.forEach(
        (tab, materialTab) -> {
          if (tab == Tab.OVERVIEW) {
            return;
          }

          materialTab.setVisible(false);
        });

    overviewTab
        .getOverviews()
        .forEach(
            (tab, overviewItemPanel) -> {
              if (tab == Tab.OVERVIEW) {
                return;
              }

              overviewItemPanel.setVisible(false);
            });

    uiTabs.get(Tab.DEBUG).setVisible(plugin.isDeveloperMode());
    ((SearchTabPanel) storageTabPanelMap.get(Tab.SEARCH)).getSearchBar().setText("");
    switchTab(Tab.OVERVIEW);
    tabGroup.resetGrid();

    setDisplayName("");
  }

  void logIn(boolean isMember, int accountType, String displayName) {
    storageTabPanelMap.forEach(
        (tab, storageTabPanel) -> {
          StorageManager<?, ?> storageManager = storageTabPanel.storageManager;
          if (storageManager != null) {
            storageManager.getStorages().forEach(storage -> storage.disable(isMember, accountType));

            if (tab != Tab.DEATH
                && storageManager.getStorages().stream().noneMatch(Storage::isEnabled)) {
              storageManager.disable();
              return;
            }
          }

          Optional.ofNullable(uiTabs.get(tab))
              .ifPresent(materialTab -> materialTab.setVisible(true));
          Optional.ofNullable(overviewTab.getOverviews().get(tab))
              .ifPresent(overviewItemPanel -> overviewItemPanel.setVisible(true));
        });
    uiTabs.get(Tab.SEARCH).setVisible(true);
    uiTabs.get(Tab.DEBUG).setVisible(plugin.isDeveloperMode());
    tabGroup.resetGrid();
    setDisplayName(displayName);
  }

  public boolean isPreviewPanel() {
    return previewMode;
  }

  void reorderStoragePanels() {
    storageManagerManager
        .getStorageManagers()
        .forEach(storageManager -> storageManager.getStorageTabPanel().reorderStoragePanels());
  }
}
