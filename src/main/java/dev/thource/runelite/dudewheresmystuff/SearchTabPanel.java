package dev.thource.runelite.dudewheresmystuff;

import dev.thource.runelite.dudewheresmystuff.death.Deathbank;
import dev.thource.runelite.dudewheresmystuff.death.Deathpile;
import net.runelite.api.Client;
import net.runelite.api.VarClientInt;
import net.runelite.api.vars.AccountType;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.IconTextField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SearchTabPanel extends StorageTabPanel<StorageType, Storage<StorageType>, StorageManager<StorageType, Storage<StorageType>>> {
    private final DeathStorageManager deathStorageManager;
    private final CoinsStorageManager coinsStorageManager;
    private final CarryableStorageManager carryableStorageManager;
    private final WorldStorageManager worldStorageManager;

    private final JPanel itemsBoxContainer;
    private final IconTextField searchBar;
    private Client client;

    SearchTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config, DudeWheresMyStuffPanel pluginPanel, DeathStorageManager deathStorageManager, CoinsStorageManager coinsStorageManager, CarryableStorageManager carryableStorageManager, WorldStorageManager worldStorageManager, Client client) {
        super(itemManager, config, pluginPanel, null);
        this.deathStorageManager = deathStorageManager;
        this.coinsStorageManager = coinsStorageManager;
        this.carryableStorageManager = carryableStorageManager;
        this.worldStorageManager = worldStorageManager;
        this.client = client;

        searchBar = new IconTextField();
        searchBar.setIcon(IconTextField.Icon.SEARCH);
        searchBar.setPreferredSize(new Dimension(getWidth(), 30));
        searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        searchBar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onSearchBarChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onSearchBarChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onSearchBarChanged();
            }
        });
        add(searchBar);

        itemsBoxContainer = new JPanel();
        itemsBoxContainer.setLayout(new BoxLayout(itemsBoxContainer, BoxLayout.Y_AXIS));
        add(itemsBoxContainer);
    }

    private void onSearchBarChanged() {
        rebuildList(coinsStorageManager.client.getVar(VarClientInt.MEMBERSHIP_STATUS) == 1);
    }

    @Override
    protected boolean showPrice() {
        return false;
    }

    @Override
    protected void rebuildList(boolean isMember) {
        itemsBoxContainer.removeAll();

        String searchText = searchBar.getText().toLowerCase(Locale.ROOT);
        itemsBoxes.clear();
        Stream.of(
                        deathStorageManager.storages.stream()
                                .filter(s -> s.getType() != DeathStorageType.DEATHPILE || !((Deathpile) s).hasExpired()),
                        coinsStorageManager.storages.stream()
                                .filter(storage -> storage.getType() != CoinsStorageType.INVENTORY && storage.getType() != CoinsStorageType.LOOTING_BAG),
                        carryableStorageManager.storages.stream(),
                        worldStorageManager.storages.stream()
                ).flatMap(i -> i)
                .sorted(Comparator.comparing(s -> s.getType().getName()))
                .forEach((storage) -> {
                    if (storage.getType().isMembersOnly() && !isMember) return;

                    List<ItemStack> items = storage.getItems().stream()
                            .filter(i -> i.getQuantity() > 0 && (Objects.equals(searchText, "") || i.getName().toLowerCase(Locale.ROOT).contains(searchText)))
                            .collect(Collectors.toList());
                    if (items.isEmpty()) return;

                    ItemsBox itemsBox = new ItemsBox(itemManager, storage, null, false, showPrice());
                    for (ItemStack itemStack : items) {
                        if (itemStack.getQuantity() > 0)
                            itemsBox.getItems().add(itemStack);
                    }

                    if (storage instanceof Deathbank) {
                        if (client.getAccountType() != AccountType.ULTIMATE_IRONMAN || storage.getType() != DeathStorageType.ZULRAH)
                            itemsBox.setSubTitle(((Deathbank) storage).isLocked() ? "Locked" : "Unlocked");
                    } else if (storage instanceof Deathpile) {
                        Deathpile deathpile = (Deathpile) storage;
                        itemsBox.addExpiry(deathpile.getExpiryMs());

                        Region region = Region.get(deathpile.getWorldPoint().getRegionID());

                        if (region == null) {
                            itemsBox.setSubTitle("Unknown");
                        } else {
                            itemsBox.setSubTitle(region.getName());
                        }
                    }

                    itemsBox.rebuild();
                    itemsBoxes.add(itemsBox);
                    itemsBoxContainer.add(itemsBox);
                });

        itemsBoxContainer.revalidate();
    }
}
