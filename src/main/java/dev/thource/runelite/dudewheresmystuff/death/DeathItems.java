package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StoragePanel;
import java.util.Objects;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import net.runelite.api.coords.WorldPoint;

public class DeathItems extends DeathStorage {

  private final DeathStorageManager deathStorageManager;

  protected DeathItems(DudeWheresMyStuffPlugin plugin, DeathStorageManager deathStorageManager) {
    super(DeathStorageType.DEATH_ITEMS, plugin);

    this.deathStorageManager = deathStorageManager;
  }

  @Override
  protected void createStoragePanel() {
    storagePanel = new StoragePanel(plugin, this, false, false);

    storagePanel.collapse(true);

    if (plugin.isDeveloperMode() && !deathStorageManager.isPreviewManager()) {
      final JPopupMenu popupMenu = new JPopupMenu();
      popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
      storagePanel.setComponentPopupMenu(popupMenu);

      final JMenuItem createDeathpile = new JMenuItem("Create Deathpile");
      createDeathpile.addActionListener(
          e -> {
            Deathpile deathpile =
                new Deathpile(
                    plugin,
                    true,
                    deathStorageManager.getPlayedMinutes() + 59,
                    WorldPoint.fromLocalInstance(
                        plugin.getClient(),
                        Objects.requireNonNull(plugin.getClient().getLocalPlayer())
                            .getLocalLocation()),
                    deathStorageManager,
                    items);
            deathpile.createStoragePanel();
            deathStorageManager.getStorages().add(deathpile);
            deathStorageManager.getStorageTabPanel().reorderStoragePanels();
          });
      popupMenu.add(createDeathpile);
    }
  }

  @Override
  public void softUpdate() {
    items.clear();
    items.addAll(deathStorageManager.getDeathItems());

    storagePanel.update();
  }
}
