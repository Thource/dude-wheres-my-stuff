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

import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;

abstract class StorageTabPanel<ST extends StorageType, S extends Storage<ST>, SM extends StorageManager<ST, S>> extends TabContentPanel {
    protected final DudeWheresMyStuffConfig config;
    protected final SM storageManager;
    protected final ItemManager itemManager;

    StorageTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config, DudeWheresMyStuffPanel pluginPanel, SM storageManager) {
        this.itemManager = itemManager;
        this.config = config;
        this.storageManager = storageManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        rebuildList();
    }

    protected Comparator<S> getStorageSorter() {
        return Comparator.comparingLong(S::getTotalValue)
                .reversed()
                .thenComparing(s -> s.getType().getName());
    }

    protected boolean showPrice() {
        return true;
    }

    protected void rebuildList() {
        removeAll();

        storageManager.storages.stream().sorted(getStorageSorter()).forEach((storage) -> {
            ItemsBox itemsBox = new ItemsBox(itemManager, storage, null, false, showPrice());
            for (ItemStack itemStack : storage.getItems()) {
                if (itemStack.getQuantity() > 0)
                    itemsBox.getItems().add(itemStack);
            }
            itemsBox.rebuild();
            add(itemsBox);
        });

        revalidate();
    }

    @Override
    public void update() {
        rebuildList();
    }

    public void softUpdate() {
        for (Component component : getComponents()) {
            if (component instanceof ItemsBox) ((ItemsBox) component).updateLastUpdateLabel();
        }
    }
}
