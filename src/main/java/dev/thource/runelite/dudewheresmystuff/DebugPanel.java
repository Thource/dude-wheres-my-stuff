package dev.thource.runelite.dudewheresmystuff;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Objects;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.DynamicGridLayout;

class DebugPanel extends TabContentPanel {
  static final int PANEL_WIDTH = 225;

  private final DudeWheresMyStuffPlugin plugin;
  private final JPanel mainPanel;

  DebugPanel(DudeWheresMyStuffPlugin plugin) {
    super();
    this.plugin = plugin;

    mainPanel = new FixedWidthPanel();
    mainPanel.setBorder(new EmptyBorder(8, 10, 10, 10));
    mainPanel.setLayout(new DynamicGridLayout(0, 1, 0, 5));
    mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPanel northPanel = new FixedWidthPanel();
    northPanel.setLayout(new BorderLayout());
    northPanel.add(mainPanel, BorderLayout.NORTH);

    JScrollPane scrollPane = new JScrollPane(northPanel);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    add(scrollPane, BorderLayout.CENTER);

    mainPanel.add(
        createCheckbox(
            "\"Create deathpile\" menu action",
            "Adds a menu option to create a deathpile on the clicked tile.<br>Shift must be held for it to appear.",
            "debug.menu.createDeathpile"));
    mainPanel.add(
        createCheckbox(
            "\"Log co-ords\" menu action",
            "Adds a menu option to log co-ords of the clicked tile.<br>Shift must be held for it to appear.",
            "debug.menu.logCoords"));
    mainPanel.add(
        createCheckbox(
            "Show remote deathpile areas",
            "Draws a rect over remote deathpile death areas and pile areas.",
            "debug.render.remoteDeathpileAreas"));
  }

  JPanel createCheckbox(String name, String description, String configName) {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setMinimumSize(new Dimension(PANEL_WIDTH, 0));
    JLabel configEntryName = new JLabel(name);
    configEntryName.setForeground(Color.WHITE);
    if (!"".equals(description)) {
      configEntryName.setToolTipText("<html>" + name + ":<br>" + description + "</html>");
    }
    panel.add(configEntryName, BorderLayout.CENTER);

    var checkbox = new JCheckBox();
    checkbox.setSelected(
        Objects.equals(
            plugin
                .getConfigManager()
                .getConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, configName),
            "true"));
    checkbox.addActionListener(
        ae ->
            plugin
                .getConfigManager()
                .setConfiguration(
                    DudeWheresMyStuffConfig.CONFIG_GROUP, configName, checkbox.isSelected()));
    panel.add(checkbox, BorderLayout.EAST);

    return panel;
  }

  @Override
  public void softUpdate() {}
}
