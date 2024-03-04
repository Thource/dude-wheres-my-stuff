package dev.thource.runelite.dudewheresmystuff;

import dev.thource.runelite.dudewheresmystuff.death.DeathpileItemBox;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import javax.swing.JLabel;
import lombok.Setter;
import net.runelite.client.plugins.itemidentification.ItemIdentificationMode;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.components.TextComponent;

public class ItemImageLabel extends JLabel {

  @Setter private transient ItemStack itemStack;
  private final transient DudeWheresMyStuffPlugin plugin;
  private final transient ItemBox itemBox;

  public ItemImageLabel(DudeWheresMyStuffPlugin plugin, ItemBox itemBox) {
    this.plugin = plugin;
    this.itemBox = itemBox;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    paintIdentification(g);
    paintPriority(g);
  }

  private void paintPriority(Graphics g) {
    if (itemStack == null || !(itemBox instanceof DeathpileItemBox)) {
      return;
    }

    DeathpileItemBox deathpileItemBox = (DeathpileItemBox) itemBox;
    if (!deathpileItemBox.isPrioritized()) {
      return;
    }

    String priorityString = String.valueOf(deathpileItemBox.getPriority());

    Font font = FontManager.getRunescapeSmallFont();
    Rectangle2D textBounds = g.getFontMetrics(font)
        .getStringBounds(priorityString, g);

    int textX = (int) ((getWidth() - textBounds.getWidth()) / 2);
    int textY = (int) ((getHeight() + textBounds.getHeight()) / 2);

    g.setColor(new Color(0, 0, 0, 160));
    g.fillRect(textX - 4, (int) (textY - textBounds.getHeight() - 2),
        (int) (textBounds.getWidth() + 8), (int) (textBounds.getHeight() + 4));

    final TextComponent textComponent = new TextComponent();
    textComponent.setPosition(new Point(textX, textY));
    textComponent.setFont(font);
    textComponent.setColor(Color.GREEN);
    textComponent.setText(priorityString);
    textComponent.render((Graphics2D) g);
  }

  private void paintIdentification(Graphics g) {
    if (itemStack == null
        || itemStack.getItemIdentification() == null
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

  @Override
  public boolean contains(int x, int y) {
    return false; // Allow mouse events to propagate to the parent panel
  }
}
