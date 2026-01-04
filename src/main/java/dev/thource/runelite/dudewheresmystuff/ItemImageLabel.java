package dev.thource.runelite.dudewheresmystuff;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.annotation.Nonnull;
import javax.swing.JLabel;
import lombok.Setter;
import net.runelite.client.plugins.itemidentification.ItemIdentificationMode;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.components.TextComponent;

class ItemImageLabel extends JLabel {

  @Setter private transient ItemStack itemStack;
  private final transient DudeWheresMyStuffPlugin plugin;

  public ItemImageLabel(DudeWheresMyStuffPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (itemStack == null) {
      return;
    }

    if (itemStack.getSpriteId() != -1) {
      createQuantityTextComponent().render((Graphics2D) g);
    }

    if (itemStack.getItemIdentification() == null
        || !plugin.getPluginManager().isPluginEnabled(plugin.getItemIdentificationPlugin())
        || !itemStack
        .getItemIdentification()
        .type
        .enabled
        .test(plugin.getItemIdentificationConfig())) {
      return;
    }

    final TextComponent textComponent = new TextComponent();
    textComponent.setPosition(new Point(-1, getHeight() - 1));
    textComponent.setFont(FontManager.getRunescapeSmallFont());
    textComponent.setColor(plugin.getItemIdentificationConfig().textColor());
    ItemIdentificationMode itemIdentificationMode =
        plugin.getItemIdentificationConfig().identificationType();
    if (itemIdentificationMode == ItemIdentificationMode.SHORT) {
      textComponent.setText(itemStack.getItemIdentification().shortName);
    } else if (itemIdentificationMode == ItemIdentificationMode.MEDIUM) {
      textComponent.setText(itemStack.getItemIdentification().medName);
    }
    textComponent.render((Graphics2D) g);
  }

  @Nonnull
  private TextComponent createQuantityTextComponent() {
    final var quantity = itemStack.getQuantity();
    final TextComponent quantityText = new TextComponent();
    quantityText.setPosition(new Point(0, 10));
    quantityText.setFont(FontManager.getRunescapeSmallFont());

    if (quantity >= 10_000_000) {
      quantityText.setColor(Color.GREEN);
      quantityText.setText(quantity / 1_000_000 + "M");
    } else if (quantity >= 100_000) {
      quantityText.setColor(Color.WHITE);
      quantityText.setText(quantity / 1_000 + "K");
    } else {
      quantityText.setColor(Color.YELLOW);
      quantityText.setText(String.valueOf(quantity));
    }

    return quantityText;
  }
}
