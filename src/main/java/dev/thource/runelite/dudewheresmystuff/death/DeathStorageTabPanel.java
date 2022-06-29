package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.EnhancedSwingUtilities;
import dev.thource.runelite.dudewheresmystuff.StorageTabPanel;
import java.util.Comparator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.vars.AccountType;

/** DeathStorageTabPanel is responsible for displaying death storage data to the player. */
@Slf4j
public class DeathStorageTabPanel
    extends StorageTabPanel<DeathStorageType, DeathStorage, DeathStorageManager> {

  @Setter private AccountType accountType;

  /** A constructor. */
  public DeathStorageTabPanel(DudeWheresMyStuffPlugin plugin, DeathStorageManager storageManager) {
    super(plugin, storageManager);
  }

  @Override
  public void reorderStoragePanels() {
    EnhancedSwingUtilities.fastRemoveAll(storagePanelContainer);
    storagePanels.clear();

    storageManager.getStorages().stream()
        .filter(
            storage ->
                !(storage instanceof Deathbank)
                    || storage.getLastUpdated() != -1) // Hide deathbank if it has no data
        .filter(
            storage ->
                plugin.getConfig().showEmptyStorages()
                    || !storage.getStoragePanel().getItemBoxes().isEmpty())
        .sorted(getStorageSorter())
        .forEach(
            storage -> {
              storagePanelContainer.add(storage.getStoragePanel());
              storagePanels.add(storage.getStoragePanel());
            });

    storagePanelContainer.revalidate();
  }

  @Override
  protected Comparator<DeathStorage> getStorageSorter() {
    return Comparator.comparingLong(
        s -> {
          if (s instanceof Deathpile) {
            Deathpile deathpile = (Deathpile) s;

            // Move expired deathpiles to the bottom of the list and sort them the opposite way
            // (newest first)
            if (deathpile.hasExpired()) {
              return Long.MAX_VALUE - deathpile.getExpiryMs();
            }

            return Long.MIN_VALUE + deathpile.getExpiryMs();
          } else if (s instanceof DeathItems) {
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
