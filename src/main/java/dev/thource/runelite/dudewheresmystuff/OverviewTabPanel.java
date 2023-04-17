/*
 * Copyright (c) 2022, Thource <https://github.com/Thource>
 * Copyright (c) 2018, Daniel Teo <https://github.com/takuyakanbr>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package dev.thource.runelite.dudewheresmystuff;

import com.google.common.collect.ImmutableMap;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;

@Slf4j
class OverviewTabPanel extends TabContentPanel {

  private static final String LOGGED_OUT_SUMMARY = "Log in to find your stuff!";
  private static final String GP_TOTAL = "%,d gp";
  private static final String DELETE_SAVE_WARNING =
      "Are you sure you want to delete your save data?\nThis cannot be undone.";
  private static final String DELETE_ALL_DEATHPILES_WARNING = "Are you sure you want to delete all"
      + " of your deathpiles (including any active ones)?\nThis cannot be undone.";
  private static final String DELETE_ALL_EXPIRED_DEATHPILES_WARNING = "Are you sure you want to"
      + " delete all of your expired deathpiles?\nThis cannot be undone.";
  private static final String DELETE_ALL_DEATHBANKS_WARNING = "Are you sure you want to delete all"
      + " of your deathbanks (including any active ones)?\nThis cannot be undone.";
  private static final String DELETE_ALL_LOST_DEATHBANKS_WARNING = "Are you sure you want to"
      + " delete all of your lost deathbanks?\nThis cannot be undone.";
  private static final String DELETE_ALL_SAVE_WARNING =
      "Are you sure you want to delete ALL of your save data?\nThis cannot be undone.";
  private static final String DELETE_ALL_SAVE_FINAL_WARNING = "Are you REALLY sure you want to "
      + "delete ALL of your save data?\nThis REALLY cannot be undone.";
  private static final String EXPORT_ITEMS_TEXT = "Export items to CSV";
  private static final String CONFIRM_DELETION_TEXT = "Confirm deletion";

  @Getter private final Map<Tab, OverviewItemPanel> overviews;
  private final OverviewItemPanel summaryOverview;
  private final DudeWheresMyStuffPanel pluginPanel;
  private final transient DudeWheresMyStuffPlugin plugin;
  private final transient StorageManagerManager storageManagerManager;

  OverviewTabPanel(
      DudeWheresMyStuffPlugin plugin,
      DudeWheresMyStuffPanel pluginPanel,
      ItemManager itemManager,
      ConfigManager configManager,
      StorageManagerManager storageManagerManager,
      boolean developerMode) {
    this.pluginPanel = pluginPanel;
    this.plugin = plugin;
    this.storageManagerManager = storageManagerManager;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBackground(ColorScheme.DARK_GRAY_COLOR);

    summaryOverview =
        new OverviewItemPanel(itemManager, null, () -> false, ItemID.NOTES, 1, LOGGED_OUT_SUMMARY);
    add(summaryOverview);
    add(Box.createVerticalStrut(8));
    if (developerMode) {
      summaryOverview.addMouseListener(
          new MouseAdapter() {
            int clicks;
            long lastClick;

            @Override
            public void mouseClicked(MouseEvent e) {
              if (System.currentTimeMillis() - lastClick < 500) {
                if (++clicks == 5) {
                  clicks = 0;
                  FakeDataService.createData(configManager);
                  log.info("Created fake data!");
                  resetSummaryContextMenu();
                }
              } else {
                clicks = 1;
              }

              lastClick = System.currentTimeMillis();
            }
          });
    }
    resetSummaryContextMenu();

    //noinspection UnstableApiUsage
    overviews =
        Tab.TABS.stream()
            .filter(v -> v != Tab.OVERVIEW && v != Tab.SEARCH)
            .collect(
                ImmutableMap.toImmutableMap(
                    Function.identity(),
                    t -> {
                      OverviewItemPanel p =
                          new OverviewItemPanel(itemManager, pluginPanel, t, t.getName());
                      add(p);
                      add(Box.createVerticalStrut(8));
                      p.setVisible(false);
                      return p;
                    }));

    createDeathStoragePopupMenu();
  }

  private void createDeathStoragePopupMenu() {
    JMenuItem deleteAllDeathpiles = new JMenuItem("Delete all deathpiles");
    deleteAllDeathpiles.addActionListener(
        e -> {
          if (DudeWheresMyStuffPlugin.getConfirmation(this, DELETE_ALL_DEATHPILES_WARNING,
              CONFIRM_DELETION_TEXT)) {
            storageManagerManager.getDeathStorageManager().deleteDeathpiles(true);
          }
        });
    JMenuItem deleteExpiredDeathpiles = new JMenuItem("Delete expired deathpiles");
    deleteExpiredDeathpiles.addActionListener(
        e -> {
          if (DudeWheresMyStuffPlugin.getConfirmation(this, DELETE_ALL_EXPIRED_DEATHPILES_WARNING,
              CONFIRM_DELETION_TEXT)) {
            storageManagerManager.getDeathStorageManager().deleteDeathpiles(false);
          }
        });
    JMenuItem deleteAllDeathbanks = new JMenuItem("Delete all deathbanks");
    deleteAllDeathbanks.addActionListener(
        e -> {
          if (DudeWheresMyStuffPlugin.getConfirmation(this, DELETE_ALL_DEATHBANKS_WARNING,
              CONFIRM_DELETION_TEXT)) {
            storageManagerManager.getDeathStorageManager().deleteDeathbanks(true);
          }
        });
    JMenuItem deleteLostDeathbanks = new JMenuItem("Delete lost deathbanks");
    deleteLostDeathbanks.addActionListener(
        e -> {
          if (DudeWheresMyStuffPlugin.getConfirmation(this, DELETE_ALL_LOST_DEATHBANKS_WARNING,
              CONFIRM_DELETION_TEXT)) {
            storageManagerManager.getDeathStorageManager().deleteDeathbanks(false);
          }
        });

    JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.add(deleteAllDeathpiles);
    popupMenu.add(deleteExpiredDeathpiles);
    popupMenu.add(deleteAllDeathbanks);
    popupMenu.add(deleteLostDeathbanks);
    overviews.get(Tab.DEATH).setComponentPopupMenu(popupMenu);
  }

  @Override
  public void softUpdate() {
    resetSummaryContextMenu();

    if (Objects.equals(pluginPanel.getDisplayName(), "")) {
      summaryOverview.setTitle(LOGGED_OUT_SUMMARY);
      summaryOverview.updateStatus("Right-click to preview data.");
    } else {
      summaryOverview.setTitle(pluginPanel.getDisplayName());

      if (pluginPanel.isPreviewPanel()
          && !Objects.equals(pluginPanel.getDisplayName(), "Thource")) {
        summaryOverview.updateStatus(
            String.format(
                "<html><body style=\"margin: 0; padding: 0;\">%,d gp<br>Right-click to exit preview"
                    + ".</body></html>",
                getTotalValue()));
      } else {
        summaryOverview.updateStatus(String.format(GP_TOTAL, getTotalValue()));
      }
    }

    overviews
        .get(Tab.DEATH)
        .updateStatus(
            String.format(
                GP_TOTAL, storageManagerManager.getDeathStorageManager().getTotalValue()));
    overviews
        .get(Tab.COINS)
        .updateStatus(
            String.format(
                GP_TOTAL, storageManagerManager.getCoinsStorageManager().getTotalValue()));
    overviews
        .get(Tab.CARRYABLE_STORAGE)
        .updateStatus(
            String.format(
                GP_TOTAL, storageManagerManager.getCarryableStorageManager().getTotalValue()));
    overviews
        .get(Tab.STASH_UNITS)
        .updateStatus(
            String.format(
                GP_TOTAL, storageManagerManager.getStashStorageManager().getTotalValue()));
    overviews
        .get(Tab.POH_STORAGE)
        .updateStatus(
            String.format(
                GP_TOTAL,
                storageManagerManager.getPlayerOwnedHouseStorageManager().getTotalValue()));
    overviews
        .get(Tab.WORLD)
        .updateStatus(
            String.format(
                GP_TOTAL, storageManagerManager.getWorldStorageManager().getTotalValue()));
  }

  private long getTotalValue() {
    return storageManagerManager.getItems().stream().mapToLong(ItemStack::getTotalGePrice).sum();
  }

  void resetSummaryContextMenu() {
    SwingUtilities.invokeLater(
        () -> {
          final JPopupMenu popupMenu = new JPopupMenu();
          popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
          summaryOverview.setComponentPopupMenu(popupMenu);

          if (pluginPanel.isPreviewPanel()) {
            addDeleteDeathbankMenuOption(popupMenu);
            addExitPreviewModeMenuOption(popupMenu);
          } else {
            addPreviewModeMenuOption(popupMenu);
            addDeleteAllDataMenuOption(popupMenu);
          }
          addExportToCsvMenuOption(popupMenu);
        });
  }

  private void addExportToCsvMenuOption(JPopupMenu popupMenu) {
    if (!Objects.equals(pluginPanel.getDisplayName(), "")) {
      final JMenuItem exportItems = new JMenuItem(EXPORT_ITEMS_TEXT);
      exportItems.addActionListener(e -> storageManagerManager.exportItems());
      popupMenu.add(exportItems);
    }
  }

  private void addDeleteAllDataMenuOption(JPopupMenu popupMenu) {
    if (plugin.getProfilesWithData().findAny().isPresent()) {
      popupMenu.add(new Separator());

      final JMenuItem deleteAllData = new JMenuItem("Delete all data");
      deleteAllData.addActionListener(
          e -> {
            if (DudeWheresMyStuffPlugin.getConfirmation(this, DELETE_ALL_SAVE_WARNING,
                CONFIRM_DELETION_TEXT) && DudeWheresMyStuffPlugin.getConfirmation(this,
                DELETE_ALL_SAVE_FINAL_WARNING, CONFIRM_DELETION_TEXT)) {
              plugin.deleteAllData();

              resetSummaryContextMenu();
            }
          });
      popupMenu.add(deleteAllData);
    }
  }

  private void addExitPreviewModeMenuOption(JPopupMenu popupMenu) {
    final JMenuItem exitPreviewMode = new JMenuItem("Exit preview mode");
    exitPreviewMode.addActionListener(e -> plugin.disablePreviewMode(false));
    popupMenu.add(exitPreviewMode);
  }

  private void addDeleteDeathbankMenuOption(JPopupMenu popupMenu) {
    final JMenuItem clearDeathbank = new JMenuItem("Delete data");
    clearDeathbank.addActionListener(
        e -> {
          if (DudeWheresMyStuffPlugin.getConfirmation(this, DELETE_SAVE_WARNING,
              CONFIRM_DELETION_TEXT)) {
            plugin.disablePreviewMode(true);
            resetSummaryContextMenu();
          }
        });
    popupMenu.add(clearDeathbank);
  }

  private void addPreviewModeMenuOption(JPopupMenu popupMenu) {
    JMenu previewMenu = new JMenu("Preview data");

    plugin.getProfilesWithData()
        .filter(runeScapeProfile -> !plugin.getDisplayName(runeScapeProfile)
            .equals(pluginPanel.getDisplayName()))
        .forEach(
            profile -> {
              String displayName = plugin.getDisplayName(profile);
              final JMenuItem previewItem = new JMenuItem(displayName);
              previewItem.addActionListener(
                  e -> {
                    if (Objects.equals(pluginPanel.getDisplayName(), displayName)) {
                      return;
                    }

                    plugin.enablePreviewMode(profile.getKey(), displayName);
                  });
              previewMenu.add(previewItem);
            });

    if (previewMenu.getSubElements().length != 0) {
      popupMenu.add(previewMenu);
    }
  }
}
