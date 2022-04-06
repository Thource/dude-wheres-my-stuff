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
import java.util.Comparator;

class CoinsTabPanel extends TabContentPanel {
    private final DudeWheresMyStuffConfig config;
    private final CoinsManager coinsManager;
    private final ItemManager itemManager;

    CoinsTabPanel(ItemManager itemManager, DudeWheresMyStuffConfig config, DudeWheresMyStuffPanel pluginPanel, CoinsManager coinsManager) {
        this.itemManager = itemManager;
        this.config = config;
        this.coinsManager = coinsManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        rebuildList();
    }

    private void rebuildList() {
        removeAll();

        coinsManager.storages.stream().sorted(Comparator.comparingLong(CoinStorage::getCoins)
                .reversed()
                .thenComparing(s -> s.getType().getName())).forEach((storage) -> {
            ItemsBox itemsBox = new ItemsBox(itemManager, storage.getType().getName(), null, false);
            if (storage.getCoins() > 0) {
                itemsBox.getItems().add(new ItemStack(995, "Coins", storage.getCoins(), 1, 0, true));
            }
            itemsBox.rebuild();
            add(itemsBox);
        });

        revalidate();
    }

    @Override
    public int getUpdateInterval() {
        return 50; // 10 seconds
    }

    @Override
    public void update() {
        rebuildList();
    }
}
