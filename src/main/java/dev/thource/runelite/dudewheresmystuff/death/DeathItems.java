package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.StoragePanel;
import java.util.Objects;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.ConfigManager;

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
    if (!plugin.isDeveloperMode() || deathStorageManager.isPreviewManager()) {
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
    items.clear();
    items.addAll(deathStorageManager.getDeathItems());

    storagePanel.update();
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
}
