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

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.ItemID;

/** Tab is used to define tabs that the user can click to swap panels. */
@RequiredArgsConstructor
@Getter
public enum Tab {
  OVERVIEW("Overview", ItemID.HW16_CLUE_LIBRARY),
  COINS("Coins", ItemID.COINS, 0xBADCA7),
  CARRYABLE_STORAGE("Carry-able Storage", ItemID.LOOTING_BAG),
  WORLD("World Storage", ItemID.ROGUESDEN_CRATE),
  MINIGAMES("Minigames", ItemID.MAGICTRAINING_PROGHAT_DULL),
  DEATH("Death Storage", ItemID.SKULL),
  POH_STORAGE("POH Storage", ItemID.POH_COS_ROOM_ARMOUR_CASE_MAHOGANY),
  STASH_UNITS("STASH Units", ItemID.POH_WALLCHART_4),
  SAILING("Sailing Storage", ItemID.VIKING_POINTLESS_SHIPTOY),
  SEARCH("Search", -1),
  DEBUG("Debug", ItemID.BLUECOG);

  public static final List<Tab> TABS =
      List.of(OVERVIEW, DEATH, COINS, CARRYABLE_STORAGE, STASH_UNITS, POH_STORAGE, WORLD,
          SAILING, MINIGAMES, SEARCH);

  private final String name;
  private final int itemId;
  private final int itemQuantity;

  Tab(String name, int itemId) {
    this.name = name;
    this.itemId = itemId;
    this.itemQuantity = 1;
  }
}
