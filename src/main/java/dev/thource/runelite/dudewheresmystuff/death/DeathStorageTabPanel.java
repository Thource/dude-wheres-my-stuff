package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.OverviewItemPanel;
import dev.thource.runelite.dudewheresmystuff.StorageTabPanel;
import java.awt.Color;
import java.util.Comparator;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;

/** DeathStorageTabPanel is responsible for displaying death storage data to the player. */
@Slf4j
public class DeathStorageTabPanel
    extends StorageTabPanel<DeathStorageType, DeathStorage, DeathStorageManager> {

  public DeathStorageTabPanel(DudeWheresMyStuffPlugin plugin, DeathStorageManager storageManager) {
    super(plugin, storageManager);
  }

  @Override
  public void reorderStoragePanels() {
    super.reorderStoragePanels();

    if (plugin.getConfig().showDeathStorageRiskWarning()) {
      OverviewItemPanel warningPanel =
          new OverviewItemPanel(
              plugin.getItemManager(),
              null,
              () -> false,
              ItemID.SIGIL_OF_CONSISTENCY,
              1,
              "WARNING!");
      warningPanel.updateStatus(
          "<HTML>The information displayed<br> in this tab can be inaccurate!<br><br>By relying on"
              + " this information,<br>you are risking your items!</HTML>");
      warningPanel.setTitleColor(Color.RED);
      warningPanel.setToolTipText(
          "<html>Every effort has been made to make death tracking as accurate<br>"
              + "as possible, but the information shown in this plugin isn't <br>"
              + "guaranteed to be 100% accurate.<br><br>"
              + "Jagex send very little information about death storages to <br>"
              + "the client, so the plugin has to try to piece your death storages<br>"
              + "together based on the information that it can get.<br><br>"
              + "Timers may be wrong, items may be wrong, it's possible for you<br>"
              + "to have death storages that aren't visible in the plugin.<br><br>"
              + "Please do not rely 100% on this plugin to track your death<br>"
              + "storages, you may lose items.</html>");
      storagePanelContainer.add(warningPanel, 0);
      storagePanelContainer.revalidate();
    }
  }

  @Override
  protected Comparator<DeathStorage> getStorageSorter() {
    return Comparator.comparingLong(
        s -> {
          if (s instanceof ExpiringDeathStorage) {
            ExpiringDeathStorage storage = (ExpiringDeathStorage) s;

            // Move expired deathpiles/graves to the bottom of the list and sort them the opposite
            // way (newest first)
            if (storage.hasExpired()) {
              return Long.MAX_VALUE - storage.getExpiryMs();
            }

            return Long.MIN_VALUE + storage.getExpiryMs();
          } else if (s instanceof DeathItems || s instanceof DeathsOffice) {
            return Long.MIN_VALUE;
          } else {
            Deathbank deathbank = (Deathbank) s;

            if (deathbank.getLostAt() != -1L) {
              return Long.MAX_VALUE - deathbank.getLostAt();
            }

            return Long.MIN_VALUE + 1;
          }
        });
  }
}
