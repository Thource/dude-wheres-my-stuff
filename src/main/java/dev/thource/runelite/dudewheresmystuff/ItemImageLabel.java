package dev.thource.runelite.dudewheresmystuff;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import net.runelite.client.plugins.itemidentification.ItemIdentificationConfig;
import net.runelite.client.plugins.itemidentification.ItemIdentificationMode;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.components.TextComponent;

public class ItemImageLabel extends JLabel {
  private final transient ItemIdentificationConfig itemIdentificationConfig;
  private ItemIdentification itemIdentification;

  public ItemImageLabel(ItemIdentificationConfig itemIdentificationConfig) {
    super();
    this.itemIdentificationConfig = itemIdentificationConfig;
  }

  public void setItemIdentification(ItemIdentification itemIdentification) {
    this.itemIdentification = itemIdentification;

    if (itemIdentification != null) {
      SwingUtilities.invokeLater(this::repaint);
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (itemIdentification == null) {
      return;
    }

    final TextComponent textComponent = new TextComponent();
    textComponent.setPosition(new Point(-1, getHeight() - 1));
    textComponent.setFont(FontManager.getRunescapeSmallFont());
    textComponent.setColor(itemIdentificationConfig.textColor());
    ItemIdentificationMode itemIdentificationMode = itemIdentificationConfig.identificationType();
    if (itemIdentificationMode == ItemIdentificationMode.SHORT) {
      textComponent.setText(itemIdentification.shortName);
    } else if (itemIdentificationMode == ItemIdentificationMode.MEDIUM) {
      textComponent.setText(itemIdentification.medName);
    }
    textComponent.render((Graphics2D) g);
  }
}
