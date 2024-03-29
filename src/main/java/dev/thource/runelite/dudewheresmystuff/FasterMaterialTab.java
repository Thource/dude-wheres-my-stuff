/*
 * Copyright (c) 2018, Psikoi <https://github.com/psikoi>
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

import com.google.common.base.Strings;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BooleanSupplier;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.ColorScheme;

/**
 * This class represents a Material Design inspired tab.
 *
 * <p>Each tab will communicate with it's containing group when it's clicked and that group will
 * display the tab's content on its own display.
 *
 * @author Psikoi
 */
public class FasterMaterialTab extends JLabel {

  private static final Border SELECTED_BORDER =
      new CompoundBorder(
          BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.BRAND_ORANGE),
          BorderFactory.createEmptyBorder(5, 10, 4, 10));

  private static final Border UNSELECTED_BORDER = BorderFactory.createEmptyBorder(5, 10, 5, 10);

  /* The tab's associated content display */
  @Getter private final JComponent content;

  /* To be executed when the tab is selected */
  @Setter private transient BooleanSupplier onSelectEvent;

  @Getter private boolean selected;

  FasterMaterialTab(FasterMaterialTabGroup group, JComponent content) {
    super("");

    this.content = content;

    unselect();

    addMouseListener(
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent mouseEvent) {
            group.select(FasterMaterialTab.this);
          }
        });

    if (!Strings.isNullOrEmpty("")) {
      addMouseListener(
          new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
              FasterMaterialTab tab = (FasterMaterialTab) e.getSource();
              tab.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
              FasterMaterialTab tab = (FasterMaterialTab) e.getSource();
              if (!tab.isSelected()) {
                tab.setForeground(Color.GRAY);
              }
            }
          });
    }
  }

  FasterMaterialTab(ImageIcon icon, FasterMaterialTabGroup group, JComponent content) {
    this(group, content);
    setIcon(icon);
    setOpaque(true);
    setVerticalAlignment(SwingConstants.CENTER);
    setHorizontalAlignment(SwingConstants.CENTER);
    setBackground(ColorScheme.DARKER_GRAY_COLOR);

    addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseEntered(MouseEvent e) {
            FasterMaterialTab tab = (FasterMaterialTab) e.getSource();
            tab.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
          }

          @Override
          public void mouseExited(MouseEvent e) {
            FasterMaterialTab tab = (FasterMaterialTab) e.getSource();
            tab.setBackground(ColorScheme.DARKER_GRAY_COLOR);
          }
        });
  }

  boolean select() {
    if (onSelectEvent != null && !onSelectEvent.getAsBoolean()) {
      return false;
    }

    setBorder(SELECTED_BORDER);
    setForeground(Color.WHITE);
    selected = true;
    return true;
  }

  void unselect() {
    setBorder(UNSELECTED_BORDER);
    setForeground(Color.GRAY);
    selected = false;
  }
}
