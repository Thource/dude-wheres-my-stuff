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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.api.Constants;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

class OverviewItemPanel extends JPanel {

  private static final ImageIcon ARROW_RIGHT_ICON;

  private static final Color HOVER_COLOR = ColorScheme.DARKER_GRAY_HOVER_COLOR;

  static {
    ARROW_RIGHT_ICON = new ImageIcon(
        ImageUtil.loadImageResource(DudeWheresMyStuffPlugin.class, "/util/arrow_right.png"));
  }

  private final JPanel textContainer;
  private final JLabel statusLabel;
  private final JLabel arrowLabel;
  private final transient BooleanSupplier isSelectable;
  private final JLabel titleLabel;
  private boolean isHighlighted;

  OverviewItemPanel(ItemManager itemManager, DudeWheresMyStuffPanel pluginPanel, @Nullable Tab tab,
      String title) {
    this(itemManager, () -> pluginPanel.switchTab(tab), () -> true,
        tab != null ? tab.getItemID() : -1, tab != null ? tab.getItemQuantity() : -1, title);
  }

  OverviewItemPanel(ItemManager itemManager, @Nullable Runnable onTabSwitched,
      BooleanSupplier isSelectable, int iconItemID, int iconItemQuantity, String title) {
    this.isSelectable = isSelectable;

    setBackground(ColorScheme.DARKER_GRAY_COLOR);
    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(7, 7, 7, 7));

    if (iconItemID != -1) {
      JLabel iconLabel = new JLabel();
      iconLabel.setMinimumSize(
          new Dimension(Constants.ITEM_SPRITE_WIDTH, Constants.ITEM_SPRITE_HEIGHT));
      itemManager.getImage(iconItemID, iconItemQuantity, false).addTo(iconLabel);
      add(iconLabel, BorderLayout.WEST);
    }

    textContainer = new JPanel();
    textContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
    textContainer.setLayout(new GridLayout(2, 1));
    textContainer.setBorder(new EmptyBorder(5, 7, 5, 7));

    if (isSelectable.getAsBoolean()) {
      addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent mouseEvent) {
          if (onTabSwitched != null) {
            onTabSwitched.run();
          }

          setHighlighted(false);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
          setHighlighted(true);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
          setHighlighted(true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
          setHighlighted(false);
        }
      });
    }

    titleLabel = new JLabel(title);
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setFont(FontManager.getRunescapeSmallFont());

    statusLabel = new JLabel();
    statusLabel.setForeground(Color.LIGHT_GRAY);
    statusLabel.setFont(FontManager.getRunescapeSmallFont());

    textContainer.add(titleLabel);
    textContainer.add(statusLabel);

    add(textContainer, BorderLayout.CENTER);

    arrowLabel = new JLabel(ARROW_RIGHT_ICON);
    arrowLabel.setVisible(isSelectable.getAsBoolean());
    add(arrowLabel, BorderLayout.EAST);
  }

  void updateStatus(String text) {
    statusLabel.setText(text);

    arrowLabel.setVisible(isSelectable.getAsBoolean());

    if (isHighlighted && !isSelectable.getAsBoolean()) {
      setHighlighted(false);
    }
  }

  private void setHighlighted(boolean highlighted) {
    if (highlighted && !isSelectable.getAsBoolean()) {
      return;
    }

    setBackground(highlighted ? HOVER_COLOR : ColorScheme.DARKER_GRAY_COLOR);
    setCursor(new Cursor(highlighted && getMousePosition(true) != null ? Cursor.HAND_CURSOR
        : Cursor.DEFAULT_CURSOR));
    textContainer.setBackground(highlighted ? HOVER_COLOR : ColorScheme.DARKER_GRAY_COLOR);

    isHighlighted = highlighted;
  }

  void setTitle(String title) {
    this.titleLabel.setText(title);
  }
}
