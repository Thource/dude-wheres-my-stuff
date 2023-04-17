package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.StoragePanel;
import java.util.Objects;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.ConfigManager;

/** DeathItems shows the user what items they will lose on death. */
public class DeathItems extends DeathStorage {

  private final DeathStorageManager deathStorageManager;

  protected DeathItems(DudeWheresMyStuffPlugin plugin, DeathStorageManager deathStorageManager) {
    super(DeathStorageType.DEATH_ITEMS, plugin);

    this.deathStorageManager = deathStorageManager;
  }

  @Override
  protected void createStoragePanel(StorageManager<?, ?> storageManager) {
    storagePanel = new StoragePanel(plugin, this, false, false);

    storagePanel.collapse(true);

    createComponentPopupMenu(storageManager);
  }

  @Override
  protected void createComponentPopupMenu(StorageManager<?, ?> storageManager) {
    if (!plugin.isDeveloperMode() || deathStorageManager.isPreviewManager()
        || storagePanel == null) {
      return;
    }

    final JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
    storagePanel.setComponentPopupMenu(popupMenu);

    final JMenuItem createDeathpile = new JMenuItem("Create Deathpile");
    createDeathpile.addActionListener(
        e -> {
          WorldPoint location =
              Objects.requireNonNull(plugin.getClient().getLocalPlayer()).getWorldLocation();
          deathStorageManager.createDeathpile(location, items);
          deathStorageManager.getStorageTabPanel().reorderStoragePanels();
        });
    popupMenu.add(createDeathpile);
  }

  @Override
  public void softUpdate() {
    plugin.getClientThread().invoke(() -> {
      items.clear();
      items.addAll(deathStorageManager.getDeathItems());

      if (storagePanel != null) {
        storagePanel.refreshItems();

        SwingUtilities.invokeLater(() -> storagePanel.update());
      }
    });
  }

  @Override
  public void save(ConfigManager configManager, String profileKey,
      String managerConfigKey) {
    // No saving
  }

  @Override
  public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
    // No loading
  }

  @Override
  public boolean isWithdrawable() {
    return false;
  }
}
