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
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;

@Slf4j
class OverviewTabPanel extends TabContentPanel {

  static final String LOGGED_OUT_SUMMARY = "Log in to find your stuff!";
  final Map<Tab, OverviewItemPanel> overviews;
  final OverviewItemPanel summaryOverview;
  private final DudeWheresMyStuffConfig config;
  private final DeathStorageManager deathStorageManager;
  private final CoinsStorageManager coinsStorageManager;
  private final CarryableStorageManager carryableStorageManager;
  private final WorldStorageManager worldStorageManager;
  private final DudeWheresMyStuffPanel pluginPanel;
  private final DudeWheresMyStuffPlugin plugin;
  private final ConfigManager configManager;

  OverviewTabPanel(DudeWheresMyStuffPlugin plugin, DudeWheresMyStuffPanel pluginPanel,
      DudeWheresMyStuffConfig config, ItemManager itemManager, ConfigManager configManager,
      DeathStorageManager deathStorageManager, CoinsStorageManager coinsStorageManager,
      CarryableStorageManager carryableStorageManager, WorldStorageManager worldStorageManager,
      boolean developerMode) {
    this.config = config;
    this.configManager = configManager;
    this.deathStorageManager = deathStorageManager;
    this.coinsStorageManager = coinsStorageManager;
    this.carryableStorageManager = carryableStorageManager;
    this.worldStorageManager = worldStorageManager;
    this.pluginPanel = pluginPanel;
    this.plugin = plugin;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBackground(ColorScheme.DARK_GRAY_COLOR);

    summaryOverview = new OverviewItemPanel(itemManager, null, () -> false, ItemID.NOTES, 1,
        LOGGED_OUT_SUMMARY);
    add(summaryOverview);
    add(Box.createVerticalStrut(8));
    if (developerMode) {
      summaryOverview.addMouseListener(new MouseListener() {
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

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
      });
    }
    resetSummaryContextMenu();

    overviews = Stream.of(Tab.TABS)
        .filter(v -> v != Tab.OVERVIEW && v != Tab.SEARCH)
        .collect(ImmutableMap.toImmutableMap(
            Function.identity(),
            t ->
            {
              OverviewItemPanel p = new OverviewItemPanel(itemManager, pluginPanel, t, t.getName());
              add(p);
              add(Box.createVerticalStrut(8));
              p.setVisible(false);
              return p;
            }
        ));
  }

  @Override
  public void update() {
    resetSummaryContextMenu();

    if (Objects.equals(pluginPanel.displayName, "")) {
      summaryOverview.setTitle(LOGGED_OUT_SUMMARY);
      summaryOverview.updateStatus("Right-click to preview data.", Color.LIGHT_GRAY);
    } else {
      summaryOverview.setTitle(pluginPanel.displayName);

      if (pluginPanel.previewMode && !Objects.equals(pluginPanel.displayName, "Thource")) {
        summaryOverview.updateStatus(String.format(
            "<html><body style=\"margin: 0; padding: 0;\">%,d gp<br>Right-click to exit preview.</body></html>",
            getTotalValue()), Color.LIGHT_GRAY);
      } else {
        summaryOverview.updateStatus(String.format("%,d gp", getTotalValue()), Color.LIGHT_GRAY);
      }
    }

    overviews.get(Tab.DEATH)
        .updateStatus(String.format("%,d gp", deathStorageManager.getTotalValue()),
            Color.LIGHT_GRAY);
    overviews.get(Tab.COINS)
        .updateStatus(String.format("%,d gp", coinsStorageManager.getTotalValue()),
            Color.LIGHT_GRAY);
    overviews.get(Tab.CARRYABLE_STORAGE)
        .updateStatus(String.format("%,d gp", carryableStorageManager.getTotalValue()),
            Color.LIGHT_GRAY);
    overviews.get(Tab.WORLD)
        .updateStatus(String.format("%,d gp", worldStorageManager.getTotalValue()),
            Color.LIGHT_GRAY);
  }

  private long getTotalValue() {
    return getAllItems().stream().mapToLong(ItemStack::getTotalGePrice).sum();
  }

  private List<ItemStack> getAllItems() {
    List<ItemStack> items = new ArrayList<>();

    for (CarryableStorage storage : carryableStorageManager.storages) {
      items.addAll(storage.getItems());
    }

    for (CoinsStorage storage : coinsStorageManager.storages) {
      if (storage.getType() == CoinsStorageType.INVENTORY
          || storage.getType() == CoinsStorageType.LOOTING_BAG) {
        continue;
      }

      items.addAll(storage.getItems());
    }

    for (WorldStorage storage : worldStorageManager.storages) {
      items.addAll(storage.getItems());
    }

    return items;
  }

  void resetSummaryContextMenu() {
    SwingUtilities.invokeLater(() -> {
      final JPopupMenu popupMenu = new JPopupMenu();
      popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
      summaryOverview.setComponentPopupMenu(popupMenu);

      if (pluginPanel.previewMode) {
        final JMenuItem clearDeathbank = new JMenuItem("Delete data");
        clearDeathbank.addActionListener(e -> {
          int result = JOptionPane.OK_OPTION;

          try {
            result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete your save data?\nThis cannot be undone.",
                "Confirm deletion",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
          } catch (Exception err) {
            log.warn("Unexpected exception occurred while check for confirm required", err);
          }

          if (result == JOptionPane.OK_OPTION) {
            plugin.disablePreviewMode(true);
            resetSummaryContextMenu();
          }
        });
        popupMenu.add(clearDeathbank);

        final JMenuItem exitPreviewMode = new JMenuItem("Exit preview mode");
        exitPreviewMode.addActionListener(e -> plugin.disablePreviewMode(false));
        popupMenu.add(exitPreviewMode);
      } else {
        configManager.getRSProfiles().forEach(profile -> {
            if (configManager.getConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP,
                profile.getKey(),
                "isMember") == null) {
                return;
            }

          final JMenuItem previewItem = new JMenuItem(profile.getDisplayName());
          previewItem.addActionListener(
              e -> plugin.enablePreviewMode(profile.getKey(), profile.getDisplayName()));
          popupMenu.add(previewItem);
        });
      }
    });
  }
}
