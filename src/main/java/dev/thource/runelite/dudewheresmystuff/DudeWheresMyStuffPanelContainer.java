package dev.thource.runelite.dudewheresmystuff;

import javax.swing.border.EmptyBorder;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.PluginPanel;

public class DudeWheresMyStuffPanelContainer extends PluginPanel {

  final DudeWheresMyStuffPanel panel;
  final DudeWheresMyStuffPanel previewPanel;
  boolean previewing;
  private boolean active;

  DudeWheresMyStuffPanelContainer(DudeWheresMyStuffPanel panel, DudeWheresMyStuffPanel previewPanel,
      ConfigManager configManager) {
    super(false);

    this.panel = panel;
    this.previewPanel = previewPanel;

    setLayout(new InvisibleGridLayout(0, 1));
    setBorder(new EmptyBorder(0, 0, 0, 0));

    add(panel);
    add(previewPanel);

    reset();
  }

  void reset() {
    panel.reset();
    previewPanel.reset();

    disablePreviewMode();
  }

  void disablePreviewMode() {
    panel.setVisible(true);
    panel.active = active;

    previewPanel.setVisible(false);
    previewPanel.active = false;

    previewing = false;
    update();
  }

  void enablePreviewMode() {
    panel.setVisible(false);
    panel.active = false;

    previewPanel.setVisible(true);
    previewPanel.active = active;

    previewing = true;
    update();
  }

  /**
   * Updates the active tab panel, if this plugin panel is displayed.
   */
  void update() {
    if (!active) {
      return;
    }

    previewPanel.update();
    panel.update();
  }

  void softUpdate() {
    if (!active) {
      return;
    }

    previewPanel.softUpdate();
    panel.softUpdate();
  }

  @Override
  public void onActivate() {
    active = true;

    if (previewing) {
      previewPanel.active = true;
    } else {
      panel.active = true;
    }

    update();
  }

  @Override
  public void onDeactivate() {
    active = false;

    previewPanel.active = false;
    panel.active = false;
  }
}
