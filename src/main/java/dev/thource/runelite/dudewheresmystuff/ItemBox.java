package dev.thource.runelite.dudewheresmystuff;

import javax.annotation.Nullable;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.QuantityFormatter;

class ItemBox extends JPanel {

  private static final String HTML_CLOSE_TAG = "</html>";

  @Getter private int itemId = -1;
  @Getter private long itemQuantity = 1;

  ItemBox(
      DudeWheresMyStuffPlugin plugin, @Nullable ItemStack itemStack, boolean displayEmptyStacks) {
    setBackground(ColorScheme.DARKER_GRAY_COLOR);

    if (itemStack != null
        && itemStack.getId() != -1
        && (displayEmptyStacks || itemStack.getQuantity() != 0)) {
      itemId = itemStack.getId();
      itemQuantity = itemStack.getQuantity();

      final ItemImageLabel imageLabel = new ItemImageLabel(plugin);
      imageLabel.setToolTipText(buildToolTip(itemStack));
      imageLabel.setVerticalAlignment(SwingConstants.CENTER);
      imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

      imageLabel.setItemStack(itemStack);
      AsyncBufferedImage itemImage =
          plugin
              .getItemManager()
              .getImage(
                  itemStack.getId(),
                  (int) Math.min(itemStack.getQuantity(), Integer.MAX_VALUE),
                  itemStack.isStackable() || itemStack.getQuantity() > 1);
      itemImage.addTo(imageLabel);

      add(imageLabel);
    }
  }

  // Suppress string literal warnings, defining a constant for "</html>" is dumb
  private static String buildToolTip(ItemStack item) {
    final String name = item.getName();
    final long quantity = item.getQuantity();
    final long gePrice = item.getTotalGePrice();
    final long haPrice = item.getTotalHaPrice();
    final StringBuilder sb = new StringBuilder("<html>");
    sb.append(name).append(" x ").append(QuantityFormatter.formatNumber(quantity));
    if (item.getId() == ItemID.COINS_995) {
      sb.append(HTML_CLOSE_TAG);
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
      sb.append(HTML_CLOSE_TAG);
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
    sb.append(HTML_CLOSE_TAG);
    return sb.toString();
  }
}
