/*
 * Copyright (c) 2022, Thource <https://github.com/Thource>
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

/** ItemStack represents an OSRS item with a quantity. */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ItemStack {

  @Setter private int id;
  @Setter private String name;
  @Setter private long quantity;
  private int gePrice;
  private int haPrice;
  private boolean stackable;

  /**
   * A constructor.
   *
   * @param id OSRS item ID
   * @param client client
   * @param clientThread clientThread
   * @param itemManager itemManager
   */
  public ItemStack(int id, Client client, ClientThread clientThread, ItemManager itemManager) {
    this.id = id;
    this.name = "Loading";
    this.quantity = 0L;

    if (client.isClientThread()) {
      populateFromComposition(itemManager);

      return;
    }

    clientThread.invoke(() -> this.populateFromComposition(itemManager));
  }

  /**
   * A constructor.
   *
   * @param itemStack itemStack to clone
   */
  public ItemStack(ItemStack itemStack) {
    this.id = itemStack.getId();
    this.name = itemStack.getName();
    this.quantity = itemStack.getQuantity();
    this.gePrice = itemStack.getGePrice();
    this.haPrice = itemStack.getHaPrice();
    this.stackable = itemStack.isStackable();
  }

  /**
   * Populates an item's data from the item id.
   *
   * @param itemManager itemManager
   */
  public void populateFromComposition(ItemManager itemManager) {
    ItemComposition composition = itemManager.getItemComposition(id);
    this.name = composition.getName();
    this.gePrice = itemManager.getItemPrice(id);
    this.haPrice = composition.getHaPrice();
    this.stackable = composition.isStackable();
  }

  long getTotalGePrice() {
    return gePrice * quantity;
  }

  long getTotalHaPrice() {
    return haPrice * quantity;
  }
}
