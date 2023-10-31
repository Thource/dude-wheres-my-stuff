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

/** DeathsOffice shows the user what items they have stored in death's office. */
public class DeathsOffice extends DeathStorage {

  private final DeathStorageManager deathStorageManager;

  protected DeathsOffice(DudeWheresMyStuffPlugin plugin, DeathStorageManager deathStorageManager) {
    super(DeathStorageType.DEATHS_OFFICE, plugin);

    this.deathStorageManager = deathStorageManager;
  }
}
