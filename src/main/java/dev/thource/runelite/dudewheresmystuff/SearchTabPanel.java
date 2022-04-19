/*
 * Copyright (c) 2018, Daniel Teo <https://github.com/takuyakanbr>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package dev.thource.runelite.dudewheresmystuff;

import net.runelite.api.VarClientInt;
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
    private final CoinsManager coinsManager;
    private final CarryableManager carryableManager;

    private final JPanel itemsBoxContainer;
    private final IconTextField searchBar;

    SearchTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config, DudeWheresMyStuffPanel pluginPanel, CoinsManager coinsManager, CarryableManager carryableManager) {
        super(itemManager, config, pluginPanel, null);
        this.coinsManager = coinsManager;
        this.carryableManager = carryableManager;

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
        rebuildList(coinsManager.client.getVar(VarClientInt.MEMBERSHIP_STATUS) == 1);
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
                        coinsManager.storages.stream()
                                .filter(storage -> storage.getType() != CoinStorageType.INVENTORY && storage.getType() != CoinStorageType.LOOTING_BAG),
                        carryableManager.storages.stream()
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
                    itemsBox.rebuild();
                    itemsBoxes.add(itemsBox);
                    itemsBoxContainer.add(itemsBox);
                });

        itemsBoxContainer.revalidate();
    }
}
