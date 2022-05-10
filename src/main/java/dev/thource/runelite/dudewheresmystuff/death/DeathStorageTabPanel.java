package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPanel;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemsBox;
import dev.thource.runelite.dudewheresmystuff.Region;
import dev.thource.runelite.dudewheresmystuff.Storage;
import dev.thource.runelite.dudewheresmystuff.StorageTabPanel;
import java.util.Comparator;
import java.util.Objects;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.vars.AccountType;
import net.runelite.client.game.ItemManager;

/** DeathStorageTabPanel is responsible for displaying death storage data to the player. */
@Slf4j
public class DeathStorageTabPanel
    extends StorageTabPanel<DeathStorageType, DeathStorage, DeathStorageManager> {

  private final boolean developerMode;
  private final DudeWheresMyStuffPanel pluginPanel;
  private final transient Client client;
  @Setter private AccountType accountType;

  /** A constructor. */
  public DeathStorageTabPanel(
      ItemManager itemManager,
      DudeWheresMyStuffConfig config,
      DudeWheresMyStuffPanel pluginPanel,
      DeathStorageManager storageManager,
      boolean developerMode,
      Client client) {
    super(itemManager, config, storageManager);
    this.developerMode = developerMode;
    this.pluginPanel = pluginPanel;
    this.client = client;
  }

  @Override
  protected Comparator<DeathStorage> getStorageSorter() {
    return Comparator.comparingLong(
        s -> {
          if (s instanceof Deathpile) {
            Deathpile deathpile = (Deathpile) s;

            // Move expired deathpiles to the bottom of the list and sort them the opposite way
            // (newest first)
            if (deathpile.hasExpired(pluginPanel.isPreviewPanel())) {
              return Long.MAX_VALUE - deathpile.getExpiryMs(pluginPanel.isPreviewPanel());
            }

            return Long.MIN_VALUE + deathpile.getExpiryMs(pluginPanel.isPreviewPanel());
          } else {
            Deathbank deathbank = (Deathbank) s;

            if (deathbank.getLostAt() != -1L) {
              return Long.MAX_VALUE - deathbank.getLostAt();
            }

            return Long.MIN_VALUE;
          }
        });
  }

  @Override
  protected void rebuildList() {
    itemsBoxContainer.removeAll();

    itemsBoxes.clear();

    if (developerMode && !pluginPanel.isPreviewPanel()) {
      addDebugDeathBox();
    }

    storageManager.getStorages().stream()
        .filter(Storage::isEnabled)
        .filter(
            storage -> {
              if (config.showEmptyStorages()) {
                return true;
              }

              return storage.getItems().stream()
                  .anyMatch(itemStack -> itemStack.getId() != -1 && itemStack.getQuantity() > 0);
            })
        .sorted(getStorageSorter())
        .forEach(
            storage -> {
              ItemsBox itemsBox = new ItemsBox(itemManager, storage, null, false, showPrice());
              for (ItemStack itemStack : storage.getItems()) {
                if (itemStack.getQuantity() > 0) {
                  itemsBox.getItems().add(itemStack);
                }
              }
              if (itemsBox.getItems().isEmpty()) {
                return;
              }

              itemsBox.setSortMode(config.itemSortMode());
              itemsBox.rebuild();
              itemsBoxes.add(itemsBox);
              itemsBoxContainer.add(itemsBox);

              decorateItemsBox(storage, itemsBox);
            });

    revalidate();
  }

  private void addDebugDeathBox() {
    ItemsBox debugDeathBox =
        new ItemsBox(itemManager, "Death items debug", null, false, showPrice());
    for (ItemStack itemStack : storageManager.getDeathItems()) {
      if (itemStack.getQuantity() > 0) {
        debugDeathBox.getItems().add(itemStack);
      }
    }
    debugDeathBox.rebuild();
    itemsBoxes.add(debugDeathBox);
    itemsBoxContainer.add(debugDeathBox);

    final JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
    debugDeathBox.setComponentPopupMenu(popupMenu);

    final JMenuItem createDeathpile = new JMenuItem("Create Deathpile");
    createDeathpile.addActionListener(
        e ->
            storageManager
                .getStorages()
                .add(
                    new Deathpile(
                        client,
                        itemManager,
                        storageManager.getPlayedMinutes(),
                        WorldPoint.fromLocalInstance(
                            client,
                            Objects.requireNonNull(client.getLocalPlayer()).getLocalLocation()),
                        storageManager,
                        storageManager.getDeathItems())));
    popupMenu.add(createDeathpile);
  }

  private void decorateItemsBox(Storage<?> storage, ItemsBox itemsBox) {
    if (storage instanceof Deathbank) {
      decorateDeathbankItemsBox((Deathbank) storage, itemsBox);
    } else if (storage instanceof Deathpile) {
      decorateDeathpileItemsBox((Deathpile) storage, itemsBox);
    }
  }

  private void decorateDeathbankItemsBox(Deathbank deathbank, ItemsBox itemsBox) {
    if (deathbank.getLostAt() == -1L
        && (accountType != AccountType.ULTIMATE_IRONMAN
            || deathbank.getType() != DeathStorageType.ZULRAH)) {
      itemsBox.setSubTitle(deathbank.isLocked() ? "Locked" : "Unlocked");
    }

    final JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
    itemsBox.setComponentPopupMenu(popupMenu);

    final JMenuItem clearDeathbank = new JMenuItem("Delete Deathbank");
    clearDeathbank.addActionListener(
        e -> {
          int result = JOptionPane.OK_OPTION;

          try {
            result =
                JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete your deathbank?\nThis cannot be undone.",
                    "Confirm deletion",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
          } catch (Exception err) {
            log.warn("Unexpected exception occurred while check for confirm required", err);
          }

          if (result == JOptionPane.OK_OPTION) {
            if (deathbank == storageManager.getDeathbank()) {
              storageManager.clearDeathbank(false);
            } else {
              storageManager.getStorages().remove(deathbank);
            }
            rebuildList();
            storageManager.save();
          }
        });
    popupMenu.add(clearDeathbank);
  }

  private void decorateDeathpileItemsBox(Deathpile deathpile, ItemsBox itemsBox) {
    itemsBox.addExpiry(deathpile.getExpiryMs(pluginPanel.isPreviewPanel()));

    final JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
    itemsBox.setComponentPopupMenu(popupMenu);

    final JMenuItem deleteDeathpile = new JMenuItem("Delete Deathpile");
    deleteDeathpile.addActionListener(
        e -> {
          int result = JOptionPane.OK_OPTION;

          try {
            result =
                JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this deathpile?\nThis cannot be undone.",
                    "Confirm deletion",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
          } catch (Exception err) {
            log.warn("Unexpected exception occurred while check for confirm required", err);
          }

          if (result == JOptionPane.OK_OPTION) {
            storageManager.getStorages().remove(deathpile);
            rebuildList();
            storageManager.refreshMapPoints();
            storageManager.save();
          }
        });
    popupMenu.add(deleteDeathpile);

    Region region = Region.get(deathpile.getWorldPoint().getRegionID());

    if (region == null) {
      itemsBox.setSubTitle("Unknown");
    } else {
      itemsBox.setSubTitle(region.getName());
    }
  }
}
