/*
 * Copyright (c) 2022, Thource <https://github.com/Thource>
 * Copyright (c) 2018, Abex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package dev.thource.runelite.dudewheresmystuff;

import com.google.common.collect.ImmutableMap;
import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageManager;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageManager;
import dev.thource.runelite.dudewheresmystuff.death.DeathStorageManager;
import dev.thource.runelite.dudewheresmystuff.minigames.MinigamesStorageManager;
import dev.thource.runelite.dudewheresmystuff.playerownedhouse.PlayerOwnedHouseStorageManager;
import dev.thource.runelite.dudewheresmystuff.stash.StashStorageManager;
import dev.thource.runelite.dudewheresmystuff.world.WorldStorageManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

/** Tab is used to define tabs that the user can click to swap panels. */
@RequiredArgsConstructor
@Getter
public enum Tab {
  OVERVIEW("Overview", ItemID.NOTES),
  COINS("Coins", ItemID.COINS_995, 0xBADCA7),
  CARRYABLE_STORAGE("Carry-able Storage", ItemID.LOOTING_BAG),
  WORLD("World Storage", ItemID.ROGUES_EQUIPMENT_CRATE),
  MINIGAMES("Minigames", ItemID.PROGRESS_HAT),
  DEATH("Death Storage", ItemID.SKULL),
  POH_STORAGE("POH Storage", ItemID.MAHOGANY_ARMOUR_CASE),
  STASH_UNITS("STASH Units", ItemID.STASH_CHART),
  SEARCH("Search", -1);

  public static final ImmutableMap<Class<? extends StorageManager<?, ?>>, Tab> MANAGER_TAB_MAP =
      ImmutableMap.<Class<? extends StorageManager<?, ?>>, Tab>builder()
          .put(DeathStorageManager.class, DEATH)
          .put(CoinsStorageManager.class, COINS)
          .put(CarryableStorageManager.class, CARRYABLE_STORAGE)
          .put(WorldStorageManager.class, WORLD)
          .put(StashStorageManager.class, STASH_UNITS)
          .put(PlayerOwnedHouseStorageManager.class, POH_STORAGE)
          .put(MinigamesStorageManager.class, MINIGAMES)
          .build();
  public static final List<Tab> TABS =
      Collections.unmodifiableList(
          Arrays.asList(
              OVERVIEW,
              DEATH,
              COINS,
              CARRYABLE_STORAGE,
              STASH_UNITS,
              POH_STORAGE,
              WORLD,
              MINIGAMES,
              SEARCH));

  private final String name;
  private final int itemId;
  private final int itemQuantity;

  Tab(String name, int itemId) {
    this.name = name;
    this.itemId = itemId;
    this.itemQuantity = 1;
  }
}
