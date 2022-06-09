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
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.vars.AccountType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;

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
  private final FasterMaterialTabGroup tabGroup = new FasterMaterialTabGroup(display);
  private final boolean previewMode;
  @Setter private boolean active;
  @Getter private String displayName = "";
  @Nullable private TabContentPanel activeTabPanel = null;

  DudeWheresMyStuffPanel(
      DudeWheresMyStuffPlugin plugin,
      DudeWheresMyStuffConfig config,
      ItemManager itemManager,
      ConfigManager configManager,
      StorageManagerManager storageManagerManager,
      boolean previewMode,
      Client client) {
    super();

    this.itemManager = itemManager;
    this.previewMode = previewMode;

    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(0, 0, 0, 0));
    setBackground(ColorScheme.DARK_GRAY_COLOR);

    display.setBorder(new EmptyBorder(10, 10, 8, 10));

    tabGroup.setLayout(new InvisibleGridLayout(0, 6, 7, 7));
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
            plugin.isDeveloperModeEnabled());
    addTab(Tab.OVERVIEW, overviewTab);

    addTab(
        Tab.DEATH,
        new DeathStorageTabPanel(
            itemManager,
            config,
            this,
            storageManagerManager.getDeathStorageManager(),
            plugin.isDeveloperModeEnabled(),
            client));
    addTab(
        Tab.COINS,
        new CoinsStorageTabPanel(
            itemManager, config, storageManagerManager.getCoinsStorageManager()));
    addTab(
        Tab.CARRYABLE_STORAGE,
        new CarryableStorageTabPanel(
            itemManager, config, storageManagerManager.getCarryableStorageManager()));
    addTab(
        Tab.STASH_UNITS,
        new StashStorageTabPanel(
            itemManager, config, storageManagerManager.getStashStorageManager()));
    addTab(
        Tab.POH_STORAGE,
        new PlayerOwnedHouseStorageTabPanel(
            itemManager, config, storageManagerManager.getPlayerOwnedHouseStorageManager()));
    addTab(
        Tab.WORLD,
        new WorldStorageTabPanel(
            itemManager, config, storageManagerManager.getWorldStorageManager()));
    addTab(
        Tab.MINIGAMES,
        new MinigamesStorageTabPanel(
            itemManager, config, storageManagerManager.getMinigamesStorageManager()));

    addTab(Tab.SEARCH, new SearchTabPanel(itemManager, config, this, storageManagerManager));

    for (Tab tab : Tab.TABS) {
      if (tab == Tab.OVERVIEW) {
        continue;
      }

      Optional.ofNullable(uiTabs.get(tab)).ifPresent(materialTab -> materialTab.setVisible(false));
    }
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

          tabContentPanel.update();
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

  void update() {
    if (!active || activeTabPanel == null) {
      return;
    }

    SwingUtilities.invokeLater(() -> activeTabPanel.update());
  }

  void softUpdate() {
    if (!active || activeTabPanel == null || !(activeTabPanel instanceof StorageTabPanel)) {
      return;
    }

    ((StorageTabPanel<?, ?, ?>) activeTabPanel).softUpdate();
  }

  void setDisplayName(String name) {
    displayName = name;
  }

  void reset() {
    logOut();
    update();
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

    SwingUtilities.invokeLater(
        () -> ((SearchTabPanel) storageTabPanelMap.get(Tab.SEARCH)).getSearchBar().setText(""));
    switchTab(Tab.OVERVIEW);

    setDisplayName("");
  }

  void logIn(boolean isMember, AccountType accountType, String displayName) {
    ((DeathStorageTabPanel) storageTabPanelMap.get(Tab.DEATH)).setAccountType(accountType);
    storageTabPanelMap.forEach(
        (tab, storageTabPanel) -> {
          StorageManager<?, ?> storageManager = storageTabPanel.storageManager;
          if (storageManager != null) {
            if (tab != Tab.DEATH && storageManager.isMembersOnly() && !isMember) {
              storageManager.disable();
              return;
            }

            for (Storage<?> storage : storageManager.getStorages()) {
              if (storage.getType().isMembersOnly() && !isMember) {
                storage.disable();
              }
            }
          }

          Optional.ofNullable(uiTabs.get(tab))
              .ifPresent(materialTab -> materialTab.setVisible(true));
          Optional.ofNullable(overviewTab.getOverviews().get(tab))
              .ifPresent(overviewItemPanel -> overviewItemPanel.setVisible(true));
        });
    uiTabs.get(Tab.SEARCH).setVisible(true);
    setDisplayName(displayName);
  }

  public boolean isPreviewPanel() {
    return previewMode;
  }
}
