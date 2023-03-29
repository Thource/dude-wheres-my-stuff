/*
 * Copyright (c) 2022, Thource <https://github.com/Thource>
 * Copyright (c) 2018, Psikoi <https://github.com/psikoi>
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.util.Text;

/** StoragePanel is responsible for displaying items to the player. */
public class StoragePanel extends JPanel {

  private static final int ITEMS_PER_ROW = 4;
  private static final int TITLE_PADDING = 5;

  private final JPanel titlePanel = new JPanel();
  private final JLabel titleLabel = new JLabel();
  private final JLabel subTitleLabel = new JLabel();
  private final JLabel priceLabel;
  private final JPanel itemContainer = new JPanel();
  private final JPanel footerPanel = new JPanel();
  @Getter private final JLabel footerLabel = new JLabel();
  private final transient DudeWheresMyStuffPlugin plugin;
  @Getter private final transient Storage<?> storage;
  private final boolean displayEmptyStacks;
  @Getter private List<ItemBox> itemBoxes = new ArrayList<>();
  private ItemSortMode itemSortMode = ItemSortMode.UNSORTED;

  /**
   * A constructor.
   *
   * @param plugin             the plugin
   * @param storage            the storage that this panel represents
   * @param showPrice          if prices should be shown
   * @param displayEmptyStacks if empty stacks should be shown
   */
  public StoragePanel(
      DudeWheresMyStuffPlugin plugin,
      Storage<?> storage,
      boolean showPrice,
      boolean displayEmptyStacks) {
    this.plugin = plugin;
    this.storage = storage;
    this.displayEmptyStacks = displayEmptyStacks;

    setLayout(new BorderLayout(0, 1));
    setBorder(new EmptyBorder(5, 0, 0, 0));
    setBackground(null);

    titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
    titlePanel.setBorder(new EmptyBorder(7, 7, 7, 7));
    titlePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
    titlePanel.setInheritsPopupMenu(true);

    titleLabel.setText(Text.removeTags(storage.getName()));
    titleLabel.setFont(FontManager.getRunescapeSmallFont());
    titleLabel.setForeground(Color.WHITE);
    // Set a size to make BoxLayout truncate the name
    titleLabel.setMinimumSize(new Dimension(1, titleLabel.getPreferredSize().height));
    titleLabel.setInheritsPopupMenu(true);

    subTitleLabel.setFont(FontManager.getRunescapeSmallFont());
    subTitleLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
    subTitleLabel.setInheritsPopupMenu(true);

    titlePanel.add(titleLabel);
    titlePanel.add(Box.createRigidArea(new Dimension(TITLE_PADDING, 0)));
    titlePanel.add(subTitleLabel);
    titlePanel.add(Box.createHorizontalGlue());
    titlePanel.add(Box.createRigidArea(new Dimension(TITLE_PADDING, 0)));

    MouseAdapter toggleListener =
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (e.getButton() == 1) {
              toggle();
            }
          }
        };
    titlePanel.addMouseListener(toggleListener);
    subTitleLabel.addMouseListener(toggleListener);

    if (showPrice) {
      priceLabel = new JLabel();
      priceLabel.setFont(FontManager.getRunescapeSmallFont());
      priceLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
      titlePanel.add(priceLabel);
      priceLabel.addMouseListener(toggleListener);
    } else {
      priceLabel = null;
    }

    itemContainer.setBackground(ColorScheme.DARK_GRAY_COLOR);

    footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.X_AXIS));
    footerPanel.setBorder(new EmptyBorder(7, 7, 7, 7));
    footerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
    footerPanel.setVisible(false);

    footerLabel.setFont(FontManager.getRunescapeSmallFont());
    footerLabel.setForeground(Color.WHITE);
    footerLabel.setText("Placeholder");
    // Set a size to make BoxLayout truncate the name
    footerLabel.setMinimumSize(new Dimension(1, footerLabel.getPreferredSize().height));
    footerLabel.setText("");
    footerPanel.add(footerLabel);

    add(titlePanel, BorderLayout.NORTH);
    add(itemContainer, BorderLayout.CENTER);
    add(footerPanel, BorderLayout.SOUTH);

    update();
  }

  public String getTitle() {
    return titleLabel.getText();
  }

  public void setTitle(String text) {
    titleLabel.setText(text);
  }

  public String getSubTitle() {
    return subTitleLabel.getText();
  }

  public void setSubTitle(String text) {
    subTitleLabel.setText(text);
    subTitleLabel.setToolTipText(text);
  }

  public String getFooterText() {
    return footerLabel.getText();
  }

  public void setFooterText(String text) {
    footerPanel.setVisible(!Objects.equals(text, ""));
    footerLabel.setText(text);
  }

  private void updatePrice() {
    if (priceLabel != null) {
      long totalPrice = storage.getTotalValue();
      priceLabel.setText(QuantityFormatter.quantityToStackSize(totalPrice) + " gp");
      priceLabel.setToolTipText(QuantityFormatter.formatNumber(totalPrice) + " gp");
    }
  }

  private void redrawItems() {
    EnhancedSwingUtilities.fastRemoveAll(itemContainer);
    itemContainer.setLayout(null);

    if (itemBoxes.stream().anyMatch(itemBox -> itemBox.getItemId() != -1)) {
      // Calculates how many rows need to be display to fit all items
      final int rowSize =
          ((itemBoxes.size() % ITEMS_PER_ROW == 0) ? 0 : 1) + itemBoxes.size() / ITEMS_PER_ROW;

      itemContainer.setLayout(new GridLayout(rowSize, ITEMS_PER_ROW, 1, 1));

      for (int i = 0; i < rowSize * ITEMS_PER_ROW; i++) {
        if (i < itemBoxes.size()) {
          itemContainer.add(itemBoxes.get(i));
        } else {
          itemContainer.add(new ItemBox(plugin, null, displayEmptyStacks));
        }
      }
    }
  }

  protected List<ItemStack> getItems() {
    List<ItemStack> items = new ArrayList<>(storage.getItems());

    if (itemSortMode == ItemSortMode.VALUE) {
      items.sort(
          Comparator.comparingLong(ItemStack::getTotalGePrice)
              .thenComparing(ItemStack::getTotalHaPrice)
              .reversed());
    }

    if (itemSortMode != ItemSortMode.UNSORTED) {
      items = ItemStackUtils.compound(items, true);
    }

    return items;
  }

  private void updateItems() {
    if (storage.getType().isAutomatic() || storage.getLastUpdated() != -1) {
      itemBoxes =
          getItems().stream()
              .filter(item -> displayEmptyStacks || item.getQuantity() > 0)
              .map(
                  itemStack -> {
                    ListIterator<ItemBox> itemBoxesIterator = itemBoxes.listIterator();
                    while (itemBoxesIterator.hasNext()) {
                      ItemBox box = itemBoxesIterator.next();
                      if (box.getItemId() == itemStack.getId()
                          && box.getItemQuantity() == itemStack.getQuantity()) {
                        itemBoxesIterator.remove();
                        return box;
                      }
                    }

                    return new ItemBox(plugin, itemStack, displayEmptyStacks);
                  })
              .collect(Collectors.toList());
    } else {
      itemBoxes.clear();
    }

    redrawItems();
  }

  /** Updates the total price and all items in the storage. */
  public void update() {
    updatePrice();
    updateItems();

    revalidate();
  }

  /** Toggles the storage between expanded/collapsed state. */
  public void toggle() {
    if (isCollapsed()) {
      expand();
    } else {
      collapse();
    }
  }

  public void collapse() {
    collapse(false);
  }

  /**
   * Collapses the storage, reducing it to just the header and footer bars.
   *
   * @param force collapse the storage even if there are no items in it
   */
  public void collapse(boolean force) {
    if (!force && itemBoxes.isEmpty()) {
      return;
    }

    if (!isCollapsed()) {
      itemContainer.setVisible(false);
      applyDimmer(false, titlePanel);
    }
  }

  /** Expands the storage, making the items visible again. */
  public void expand() {
    if (itemBoxes.isEmpty()) {
      return;
    }

    if (isCollapsed()) {
      itemContainer.setVisible(true);
      applyDimmer(true, titlePanel);
    }
  }

  public boolean isCollapsed() {
    return !itemContainer.isVisible();
  }

  private void applyDimmer(boolean brighten, JPanel panel) {
    for (Component component : panel.getComponents()) {
      Color color = component.getForeground();

      component.setForeground(brighten ? color.brighter() : color.darker());
    }
  }

  /**
   * Changes which sorting method is used to sort the items within the panel.
   *
   * @param itemSortMode the new sort mode
   */
  public void setSortMode(ItemSortMode itemSortMode) {
    this.itemSortMode = itemSortMode;

    updateItems();
    revalidate();
  }

  public String getTitleToolTip() {
    return titleLabel.getToolTipText();
  }

  public void setTitleToolTip(String text) {
    titleLabel.setToolTipText(text);
  }
}
