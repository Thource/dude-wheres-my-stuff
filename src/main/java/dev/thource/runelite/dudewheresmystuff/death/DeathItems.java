package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.StoragePanel;
import java.util.Collections;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.ConfigManager;

/** DeathItems shows the user what items they will lose on death. */
@Slf4j
public class DeathItems extends DeathStorage {

  private final DeathStorageManager deathStorageManager;

  protected DeathItems(DudeWheresMyStuffPlugin plugin, DeathStorageManager deathStorageManager) {
    super(DeathStorageType.DEATH_ITEMS, plugin);

    this.deathStorageManager = deathStorageManager;
  }

  @Override
  protected void createStoragePanel(StorageManager<?, ?> storageManager) {
    storagePanel = new StoragePanel(plugin, this, false, false, true);

    storagePanel.collapse(true);

    createComponentPopupMenu(storageManager);
  }

  public void createDebugDeathpile(WorldPoint worldPoint) {
    var deathpile = deathStorageManager.createDeathpile(RemoteDeathpileAreas.getPileArea(plugin.getClient(), worldPoint), items);
    SwingUtilities.invokeLater(() ->
        plugin.getClientThread().invoke(() ->
            deathStorageManager.updateStorages(Collections.singletonList(deathpile))));
  }

  public void createDebugGrave(WorldPoint worldPoint) {
    var grave = deathStorageManager.createGrave(RemoteDeathpileAreas.getPileArea(plugin.getClient(), worldPoint), items);
    SwingUtilities.invokeLater(() ->
        plugin.getClientThread().invoke(() ->
            deathStorageManager.updateStorages(Collections.singletonList(grave))));
  }

  public void createDebugDeathbank() {
    if (deathStorageManager.getDeathbank() != null) {
      deathStorageManager.getDeathbank().setLostAt(System.currentTimeMillis());
    }

    deathStorageManager.createMysteryDeathbank(DeathbankType.UNKNOWN);
    var deathbank = deathStorageManager.getDeathbank();
    deathbank.getItems().clear();
    deathbank.getItems().addAll(items);
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

    final JMenuItem createDeathpileItem = new JMenuItem("Create Deathpile");
    createDeathpileItem.addActionListener(e -> {
      var client = plugin.getClient();
      createDebugDeathpile(
          WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()));
    });
    popupMenu.add(createDeathpileItem);

    final JMenuItem createGrave = new JMenuItem("Create Grave");
    createGrave.addActionListener(e -> {
      var client = plugin.getClient();
      createDebugGrave(
          WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()));
    });
    popupMenu.add(createGrave);

    final JMenuItem createDeathbank = new JMenuItem("Create Deathbank");
    createDeathbank.addActionListener(e -> createDebugDeathbank());
    popupMenu.add(createDeathbank);
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
