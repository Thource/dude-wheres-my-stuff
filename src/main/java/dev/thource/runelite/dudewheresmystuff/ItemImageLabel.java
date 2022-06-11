package dev.thource.runelite.dudewheresmystuff;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.JLabel;
import lombok.Setter;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.itemidentification.ItemIdentificationConfig;
import net.runelite.client.plugins.itemidentification.ItemIdentificationMode;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.components.TextComponent;

public class ItemImageLabel extends JLabel {
  private final transient ItemIdentificationConfig itemIdentificationConfig;
  private final transient PluginManager pluginManager;
  private final transient Plugin itemIdentificationPlugin;
  @Setter private transient ItemStack itemStack;

  public ItemImageLabel(
      ItemIdentificationConfig itemIdentificationConfig,
      PluginManager pluginManager,
      Plugin itemIdentificationPlugin) {
    super();
    this.itemIdentificationConfig = itemIdentificationConfig;
    this.pluginManager = pluginManager;
    this.itemIdentificationPlugin = itemIdentificationPlugin;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (itemStack == null
        || itemStack.getItemIdentification() == null
        || !pluginManager.isPluginEnabled(itemIdentificationPlugin)
        || !itemStack.getItemIdentification().type.enabled.test(itemIdentificationConfig)) {
      return;
    }

    final TextComponent textComponent = new TextComponent();
    textComponent.setPosition(new Point(-1, getHeight() - 1));
    textComponent.setFont(FontManager.getRunescapeSmallFont());
    textComponent.setColor(itemIdentificationConfig.textColor());
    ItemIdentificationMode itemIdentificationMode = itemIdentificationConfig.identificationType();
    if (itemIdentificationMode == ItemIdentificationMode.SHORT) {
      textComponent.setText(itemStack.getItemIdentification().shortName);
    } else if (itemIdentificationMode == ItemIdentificationMode.MEDIUM) {
      textComponent.setText(itemStack.getItemIdentification().medName);
    }
    textComponent.render((Graphics2D) g);
  }
}
