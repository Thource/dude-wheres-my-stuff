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

import com.google.common.base.Strings;
import dev.thource.runelite.dudewheresmystuff.death.Deathbank;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.util.Text;

/** ItemsBox is responsible for displaying items to the player. */
public class ItemsBox extends JPanel {

  private static final int ITEMS_PER_ROW = 4;
  private static final int TITLE_PADDING = 5;
  private static final String UNKNOWN_TEXT = "Unknown";

  private final JPanel itemContainer = new JPanel();
  private final JLabel subTitleLabel = new JLabel();
  private final JPanel logTitle = new JPanel();
  private final transient ItemManager itemManager;

  @Getter(AccessLevel.PACKAGE)
  private final String id;

  @Getter(AccessLevel.PACKAGE)
  private final boolean showAlchPrices;

  @Getter private final List<ItemStack> items = new ArrayList<>();
  private JLabel priceLabel = null;
  private JLabel lastUpdatedLabel = null;
  private JLabel expiryLabel = null;
  private transient Storage<?> storage = null;

  private long totalPrice;
  private long expiryMs;
  private ItemSortMode itemSortMode = ItemSortMode.UNSORTED;

  private ItemsBox(
      final ItemManager itemManager,
      final String name,
      final boolean automatic,
      @Nullable final String subtitle,
      final boolean showAlchPrices,
      boolean showPrice) {
    this.id = name;
    this.itemManager = itemManager;
    this.showAlchPrices = showAlchPrices;

    setLayout(new BorderLayout(0, 1));
    setBorder(new EmptyBorder(5, 0, 0, 0));

    logTitle.setLayout(new BoxLayout(logTitle, BoxLayout.X_AXIS));
    logTitle.setBorder(new EmptyBorder(7, 7, 7, 7));
    logTitle.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());

    JLabel titleLabel = new JLabel();
    titleLabel.setText(Text.removeTags(id));
    titleLabel.setFont(FontManager.getRunescapeSmallFont());
    titleLabel.setForeground(Color.WHITE);
    // Set a size to make BoxLayout truncate the name
    titleLabel.setMinimumSize(new Dimension(1, titleLabel.getPreferredSize().height));
    logTitle.add(titleLabel);

    subTitleLabel.setFont(FontManager.getRunescapeSmallFont());
    subTitleLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);

    if (!Strings.isNullOrEmpty(subtitle)) {
      subTitleLabel.setText(subtitle);
      subTitleLabel.setToolTipText(subtitle);
    }

    logTitle.add(Box.createRigidArea(new Dimension(TITLE_PADDING, 0)));
    logTitle.add(subTitleLabel);
    logTitle.add(Box.createHorizontalGlue());
    logTitle.add(Box.createRigidArea(new Dimension(TITLE_PADDING, 0)));

    if (showPrice) {
      priceLabel = new JLabel();
      priceLabel.setFont(FontManager.getRunescapeSmallFont());
      priceLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
      logTitle.add(priceLabel);
    }

    add(logTitle, BorderLayout.NORTH);
    add(itemContainer, BorderLayout.CENTER);

    if (!automatic) {
      JPanel lastUpdatedPanel = new JPanel();
      lastUpdatedPanel.setLayout(new BoxLayout(lastUpdatedPanel, BoxLayout.X_AXIS));
      lastUpdatedPanel.setBorder(new EmptyBorder(7, 7, 7, 7));
      lastUpdatedPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

      lastUpdatedLabel = new JLabel();
      lastUpdatedLabel.setText(UNKNOWN_TEXT);
      lastUpdatedLabel.setToolTipText("Open the relevant container or shop to update.");
      lastUpdatedLabel.setFont(FontManager.getRunescapeSmallFont());
      lastUpdatedLabel.setForeground(Color.WHITE);
      // Set a size to make BoxLayout truncate the name
      lastUpdatedLabel.setMinimumSize(new Dimension(1, lastUpdatedLabel.getPreferredSize().height));
      lastUpdatedPanel.add(lastUpdatedLabel);

      add(lastUpdatedPanel, BorderLayout.SOUTH);
    }
  }

  public ItemsBox(
      final ItemManager itemManager,
      final String name,
      @Nullable final String subtitle,
      final boolean showAlchPrices,
      boolean showPrice) {
    this(itemManager, name, true, subtitle, showAlchPrices, showPrice);
  }

  /** A constructor. */
  public ItemsBox(
      final ItemManager itemManager,
      final Storage<?> storage,
      @Nullable final String subtitle,
      final boolean showAlchPrices,
      boolean showPrice) {
    this(
        itemManager,
        storage.getType().getName(),
        storage.getType().isAutomatic(),
        subtitle,
        showAlchPrices,
        showPrice);
    this.storage = storage;
  }

  // Suppress string literal warnings, defining a constant for "</html>" is dumb
  @SuppressWarnings("java:S1192")
  private static String buildToolTip(ItemStack item) {
    final String name = item.getName();
    final long quantity = item.getQuantity();
    final long gePrice = item.getTotalGePrice();
    final long haPrice = item.getTotalHaPrice();
    final StringBuilder sb = new StringBuilder("<html>");
    sb.append(name).append(" x ").append(QuantityFormatter.formatNumber(quantity));
    if (item.getId() == ItemID.COINS_995) {
      sb.append("</html>");
      return sb.toString();
    }

    if (gePrice > 0) {
      sb.append("<br>GE: ").append(QuantityFormatter.quantityToStackSize(gePrice));
      if (quantity > 1) {
        sb.append(" (")
            .append(QuantityFormatter.quantityToStackSize(item.getGePrice()))
            .append(" ea)");
      }
    }

    if (item.getId() == ItemID.PLATINUM_TOKEN) {
      sb.append("</html>");
      return sb.toString();
    }

    if (haPrice > 0) {
      sb.append("<br>HA: ").append(QuantityFormatter.quantityToStackSize(haPrice));
      if (quantity > 1) {
        sb.append(" (")
            .append(QuantityFormatter.quantityToStackSize(item.getHaPrice()))
            .append(" ea)");
      }
    }
    sb.append("</html>");
    return sb.toString();
  }

  /** Rebuild the ItemsBox, re-rendering the elements. */
  public void rebuild() {
    buildItems();

    if (priceLabel != null) {
      priceLabel.setText(QuantityFormatter.quantityToStackSize(totalPrice) + " gp");
      priceLabel.setToolTipText(QuantityFormatter.formatNumber(totalPrice) + " gp");
    }

    updateLabels();

    revalidate();
  }

  public void setSubTitle(String text) {
    subTitleLabel.setText(text);
    subTitleLabel.setToolTipText(text);
  }

  /**
   * Set an expiry date on the box, expiry is displayed in a label under the box.
   *
   * @param expiryMs Unix timestamp of the expiry
   */
  public void addExpiry(long expiryMs) {
    this.expiryMs = expiryMs;

    JPanel expiryPanel = new JPanel();
    expiryPanel.setLayout(new BoxLayout(expiryPanel, BoxLayout.X_AXIS));
    expiryPanel.setBorder(new EmptyBorder(7, 7, 7, 7));
    expiryPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

    expiryLabel = new JLabel();
    expiryLabel.setText("Expires in");
    expiryLabel.setFont(FontManager.getRunescapeSmallFont());
    expiryLabel.setForeground(Color.WHITE);
    // Set a size to make BoxLayout truncate the name
    expiryLabel.setMinimumSize(new Dimension(1, expiryLabel.getPreferredSize().height));
    expiryPanel.add(expiryLabel);

    add(expiryPanel, BorderLayout.SOUTH);

    updateExpiryLabel();
  }

  void updateLabels() {
    updateLastUpdatedLabel();
    updateExpiryLabel();
  }

  private void updateLastUpdatedLabel() {
    if (lastUpdatedLabel == null) {
      return;
    }
    if (storage instanceof Deathbank && ((Deathbank) storage).getLostAt() != -1L) {
      lastUpdatedLabel.setText(
          "Lost "
              + DurationFormatter.format(
                  System.currentTimeMillis() - ((Deathbank) storage).getLostAt())
              + " ago");
      return;
    }

    if (storage.getLastUpdated() == -1L) {
      if (!Objects.equals(lastUpdatedLabel.getText(), UNKNOWN_TEXT)) {
        lastUpdatedLabel.setText(UNKNOWN_TEXT);
      }

      return;
    }

    lastUpdatedLabel.setText(
        "Last updated "
            + DurationFormatter.format(System.currentTimeMillis() - storage.getLastUpdated())
            + " ago");
  }

  private void updateExpiryLabel() {
    if (expiryLabel == null) {
      return;
    }

    String expireText = "Expire";
    long timeUntilExpiry = expiryMs - System.currentTimeMillis();
    if (timeUntilExpiry < 0) {
      expireText += "d " + DurationFormatter.format(Math.abs(timeUntilExpiry)) + " ago";
    } else {
      expireText += "s in " + DurationFormatter.format(Math.abs(timeUntilExpiry));
    }

    expiryLabel.setText(expireText);
  }

  void collapse() {
    if (!isCollapsed()) {
      itemContainer.setVisible(false);
      applyDimmer(false, logTitle);
    }
  }

  void expand() {
    if (items.isEmpty()) {
      return;
    }

    if (isCollapsed()) {
      itemContainer.setVisible(true);
      applyDimmer(true, logTitle);
    }
  }

  boolean isCollapsed() {
    return !itemContainer.isVisible();
  }

  private void applyDimmer(boolean brighten, JPanel panel) {
    for (Component component : panel.getComponents()) {
      Color color = component.getForeground();

      component.setForeground(brighten ? color.brighter() : color.darker());
    }
  }

  /**
   * This method creates stacked items from the item list, calculates total price and then displays
   * all the items in the UI.
   */
  private void buildItems() {
    totalPrice = 0;

    List<ItemStack> newItems = this.items;

    ToLongFunction<ItemStack> getPrice =
        showAlchPrices ? ItemStack::getTotalHaPrice : ItemStack::getTotalGePrice;

    totalPrice = newItems.stream().mapToLong(getPrice).sum();

    itemContainer.removeAll();
    itemContainer.setLayout(null);

    if (itemSortMode == ItemSortMode.VALUE) {
      newItems.sort(Comparator.comparingLong(getPrice).reversed());
    }

    if (itemSortMode != ItemSortMode.UNSORTED) {
      newItems = ItemStackUtils.compound(newItems, true);
    }

    if (newItems.stream().anyMatch(itemStack -> itemStack.getId() != -1)) {
      // Calculates how many rows need to be display to fit all items
      final int rowSize =
          ((newItems.size() % ITEMS_PER_ROW == 0) ? 0 : 1) + newItems.size() / ITEMS_PER_ROW;

      itemContainer.setLayout(new GridLayout(rowSize, ITEMS_PER_ROW, 1, 1));

      createItemPanels(newItems, rowSize).forEach(itemContainer::add);
    }

    itemContainer.revalidate();
  }

  private List<JPanel> createItemPanels(List<ItemStack> newItems, int rowSize) {
    return IntStream.range(0, rowSize * ITEMS_PER_ROW)
        .mapToObj(
            i -> {
              final JPanel slotContainer = new JPanel();
              slotContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
              if (i < newItems.size()) {
                final ItemStack item = newItems.get(i);

                final JLabel imageLabel = new JLabel();
                imageLabel.setToolTipText(buildToolTip(item));
                imageLabel.setVerticalAlignment(SwingConstants.CENTER);
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

                if (item.getId() != -1) {
                  AsyncBufferedImage itemImage =
                      itemManager.getImage(
                          item.getId(),
                          (int) Math.min(item.getQuantity(), Integer.MAX_VALUE),
                          item.isStackable() || item.getQuantity() > 1);
                  itemImage.addTo(imageLabel);
                }

                slotContainer.add(imageLabel);
              }

              return slotContainer;
            })
        .collect(Collectors.toList());
  }

  public void setSortMode(ItemSortMode itemSortMode) {
    this.itemSortMode = itemSortMode;
  }
}
