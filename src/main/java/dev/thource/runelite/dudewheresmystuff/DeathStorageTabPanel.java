package dev.thource.runelite.dudewheresmystuff;

import dev.thource.runelite.dudewheresmystuff.death.Deathbank;
import dev.thource.runelite.dudewheresmystuff.death.Deathpile;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.vars.AccountType;
import net.runelite.client.game.ItemManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.Comparator;

@Slf4j
class DeathStorageTabPanel extends StorageTabPanel<DeathStorageType, DeathStorage, DeathStorageManager> {
    public AccountType accountType;
    private final boolean developerMode;
    private DudeWheresMyStuffPanel pluginPanel;

    DeathStorageTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config, DudeWheresMyStuffPanel pluginPanel, DeathStorageManager storageManager, boolean developerMode) {
        super(itemManager, config, pluginPanel, storageManager);
        this.developerMode = developerMode;
        this.pluginPanel = pluginPanel;
    }

    @Override
    protected Comparator<DeathStorage> getStorageSorter() {
        return Comparator.comparingLong(s -> {
            if (s instanceof Deathpile) {
                Deathpile deathpile = (Deathpile) s;

                // Move expired deathpiles to the bottom of the list and sort them the opposite way (newest first)
                if (deathpile.hasExpired(pluginPanel.previewMode))
                    return Long.MAX_VALUE - deathpile.getExpiryMs(pluginPanel.previewMode);

                return Long.MIN_VALUE + deathpile.getExpiryMs(pluginPanel.previewMode);
            } else {
                Deathbank deathbank = (Deathbank) s;

                if (deathbank.lostAt != -1L)
                    return Long.MAX_VALUE - deathbank.lostAt;

                return Long.MIN_VALUE;
            }
        });
    }

    @Override
    protected void rebuildList() {
        itemsBoxContainer.removeAll();

        itemsBoxes.clear();

        if (developerMode && !pluginPanel.previewMode) {
            ItemsBox debugDeathBox = new ItemsBox(itemManager, "Death items debug", null, false, showPrice());
            for (ItemStack itemStack : storageManager.getDeathItems()) {
                if (itemStack.getQuantity() > 0)
                    debugDeathBox.getItems().add(itemStack);
            }
            debugDeathBox.rebuild();
            itemsBoxes.add(debugDeathBox);
            itemsBoxContainer.add(debugDeathBox);
        }

        storageManager.storages.stream().sorted(getStorageSorter()).forEach((storage) -> {
            if (!storage.isEnabled()) return;

            ItemsBox itemsBox = new ItemsBox(itemManager, storage, null, false, showPrice());
            for (ItemStack itemStack : storage.getItems()) {
                if (itemStack.getQuantity() > 0)
                    itemsBox.getItems().add(itemStack);
            }
            if (itemsBox.getItems().isEmpty()) return;

            itemsBox.setSortMode(config.itemSortMode());
            itemsBox.rebuild();
            itemsBoxes.add(itemsBox);
            itemsBoxContainer.add(itemsBox);

            if (storage instanceof Deathbank) {
                if (((Deathbank) storage).getLostAt() == -1L && (accountType != AccountType.ULTIMATE_IRONMAN || storage.getType() != DeathStorageType.ZULRAH))
                    itemsBox.setSubTitle(((Deathbank) storage).isLocked() ? "Locked" : "Unlocked");

                final JPopupMenu popupMenu = new JPopupMenu();
                popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
                itemsBox.setComponentPopupMenu(popupMenu);

                final JMenuItem clearDeathbank = new JMenuItem("Delete Deathbank");
                clearDeathbank.addActionListener(e -> {
                    int result = JOptionPane.OK_OPTION;

                    try {
                        result = JOptionPane.showConfirmDialog(
                                this,
                                "Are you sure you want to delete your deathbank?\nThis cannot be undone.", "Confirm deletion",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                    } catch (Exception err) {
                        log.warn("Unexpected exception occurred while check for confirm required", err);
                    }

                    if (result == JOptionPane.OK_OPTION) {
                        if (storage == storageManager.deathbank) {
                            storageManager.clearDeathbank(false);
                        } else {
                            storageManager.storages.remove(storage);
                        }
                        rebuildList();
                        storageManager.save();
                    }
                });
                popupMenu.add(clearDeathbank);
            } else if (storage instanceof Deathpile) {
                Deathpile deathpile = (Deathpile) storage;
                itemsBox.addExpiry(deathpile.getExpiryMs(pluginPanel.previewMode));

                final JPopupMenu popupMenu = new JPopupMenu();
                popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
                itemsBox.setComponentPopupMenu(popupMenu);

                final JMenuItem deleteDeathpile = new JMenuItem("Delete Deathpile");
                deleteDeathpile.addActionListener(e -> {
                    int result = JOptionPane.OK_OPTION;

                    try {
                        result = JOptionPane.showConfirmDialog(
                                this,
                                "Are you sure you want to delete this deathpile?\nThis cannot be undone.", "Confirm deletion",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                    } catch (Exception err) {
                        log.warn("Unexpected exception occurred while check for confirm required", err);
                    }

                    if (result == JOptionPane.OK_OPTION) {
                        storageManager.storages.remove(deathpile);
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
        });

        revalidate();
    }
}
