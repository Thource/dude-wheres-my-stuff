package dev.thource.runelite.dudewheresmystuff;

import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.client.ui.PluginPanel;

/**
 * DudeWheresMyStuffPanelContainer hosts 2 DudeWheresMyStuffPanels, one for active data and one for
 * preview.
 */
public class DudeWheresMyStuffPanelContainer extends PluginPanel {

  @Getter private final DudeWheresMyStuffPanel panel;
  @Getter private final DudeWheresMyStuffPanel previewPanel;
  private boolean previewing;
  private boolean active;

  DudeWheresMyStuffPanelContainer(
      DudeWheresMyStuffPanel panel, DudeWheresMyStuffPanel previewPanel) {
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
    panel.setActive(active);

    previewPanel.setVisible(false);
    previewPanel.setActive(false);

    previewing = false;
    softUpdate();
  }

  void enablePreviewMode() {
    panel.setVisible(false);
    panel.setActive(false);

    previewPanel.setVisible(true);
    previewPanel.setActive(active);

    previewing = true;
    softUpdate();
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
      previewPanel.setActive(true);
    } else {
      panel.setActive(true);
    }

    softUpdate();
  }

  @Override
  public void onDeactivate() {
    active = false;

    previewPanel.setActive(false);
    panel.setActive(false);
  }

  void reorderStoragePanels() {
    if (previewing) {
      previewPanel.reorderStoragePanels();
    } else {
      panel.reorderStoragePanels();
    }
  }

  void setItemSortMode(ItemSortMode itemSortMode) {
    panel.setItemSortMode(itemSortMode);
    previewPanel.setItemSortMode(itemSortMode);
  }
}
