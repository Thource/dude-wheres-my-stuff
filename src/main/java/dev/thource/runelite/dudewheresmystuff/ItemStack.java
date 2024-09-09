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

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import java.util.List;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;

/** ItemStack represents an OSRS item with a quantity. */
@Getter
@EqualsAndHashCode
public class ItemStack {

  private int id;
  @Setter private long quantity;
  @EqualsAndHashCode.Exclude @Setter private String name;
  @EqualsAndHashCode.Exclude private int gePrice;
  @EqualsAndHashCode.Exclude private int haPrice;
  @EqualsAndHashCode.Exclude @Setter private boolean stackable;
  @EqualsAndHashCode.Exclude private ItemIdentification itemIdentification;
  @EqualsAndHashCode.Exclude private int canonicalId;

  /**
   * A constructor.
   *
   * @param id OSRS item ID
   * @param plugin plugin
   */
  public ItemStack(int id, DudeWheresMyStuffPlugin plugin) {
    this.id = id;
    this.name = "Loading";
    this.quantity = 0L;

    plugin.getClientThread().invoke(() -> populateFromComposition(plugin));
  }

  /**
   * A constructor.
   *
   * @param id OSRS item ID
   * @param quantity quantity
   * @param plugin plugin
   */
  public ItemStack(int id, long quantity, DudeWheresMyStuffPlugin plugin) {
    this(id, plugin);

    this.quantity = quantity;
  }

  /**
   * A constructor.
   *
   * @param itemStack itemStack to clone
   */
  public ItemStack(ItemStack itemStack) {
    this(
        itemStack.getId(),
        itemStack.getName(),
        itemStack.getQuantity(),
        itemStack.getGePrice(),
        itemStack.getHaPrice(),
        itemStack.isStackable());

    this.itemIdentification = itemStack.getItemIdentification();
    this.canonicalId = itemStack.canonicalId;
  }

  /**
   * A constructor.
   *
   * <p>WARNING: ItemStacks created using this constructor will not have an ItemIdentification
   * attached.
   *
   * @param id the item's id
   * @param name the item's name
   * @param quantity the quantity of the item
   * @param gePrice the GE price
   * @param haPrice the high alchemy price
   * @param stackable if the item is stackable
   */
  public ItemStack(
      int id, String name, long quantity, int gePrice, int haPrice, boolean stackable) {
    this.id = id;
    this.name = name;
    this.quantity = quantity;
    this.gePrice = gePrice;
    this.haPrice = haPrice;
    this.stackable = stackable;
    this.canonicalId = id;
  }

  /**
   * Populates an item's data from the item id.
   *
   * @param plugin plugin
   */
  private boolean populateFromComposition(DudeWheresMyStuffPlugin plugin) {
    if (plugin.getClient().getGameState().getState() < GameState.LOGIN_SCREEN.getState()) {
      return false;
    }

    ItemManager itemManager = plugin.getItemManager();
    ItemComposition composition = itemManager.getItemComposition(id);
    name = composition.getName();
    gePrice = itemManager.getItemPrice(id);
    haPrice = composition.getHaPrice();
    stackable = composition.isStackable();
    canonicalId = itemManager.canonicalize(id);
    itemIdentification = ItemIdentification.get(canonicalId);

    return true;
  }

  long getTotalGePrice() {
    return gePrice * quantity;
  }

  long getTotalHaPrice() {
    return haPrice * quantity;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setId(int id, DudeWheresMyStuffPlugin plugin) {
    this.id = id;
    populateFromComposition(plugin);
  }

  public void stripPrices() {
    haPrice = 0;
    gePrice = 0;
  }

  public String toCsvString(
      boolean mergeItems,
      @Nullable StorageManager<?, ?> storageManager,
      @Nullable Storage<?> storage) {
    boolean canSplitUp = (!mergeItems && storage != null && storageManager != null);
    String fmtString = "%d,%s,%d" + (mergeItems || !canSplitUp ? "" : ",%s,%s") + "%n";
    String escapedName = getEscapedName(mergeItems);

    return (mergeItems || !canSplitUp)
        ? String.format(fmtString, getCanonicalId(), escapedName, getQuantity())
        : String.format(
            fmtString,
            getCanonicalId(),
            escapedName,
            getQuantity(),
            storageManager.getConfigKey(),
            storage.getName());
  }

  public static List<String> getHeaders(boolean mergeItems, boolean shouldSplitUp) {
    boolean canSplitUp = (!mergeItems && shouldSplitUp);

    if (canSplitUp) {
      return List.of("ID", "Name", "Quantity", "Storage Category", "Storage Type");
    } else {
      return List.of("ID", "Name", "Quantity");
    }
  }

  public List<CellData> getCellDataList(
      boolean mergeItems,
      @Nullable StorageManager<?, ?> storageManager,
      @Nullable Storage<?> storage) {
    boolean canSplitUp = (!mergeItems && storage != null && storageManager != null);
    final CellData ID =
        new CellData()
            .setUserEnteredValue(new ExtendedValue().setNumberValue((double) getCanonicalId()));
    final CellData NAME =
        new CellData()
            .setUserEnteredValue(new ExtendedValue().setStringValue(getEscapedName(false)));
    final CellData QUANTITY =
        new CellData()
            .setUserEnteredValue(new ExtendedValue().setNumberValue((double) getQuantity()));
    if (mergeItems || !canSplitUp) {
      return List.of(ID, NAME, QUANTITY);
    } else {
      final CellData STORAGE_CATEGORY =
          new CellData()
              .setUserEnteredValue(
                  new ExtendedValue().setStringValue(storageManager.getConfigKey()));
      final CellData STORAGE_TYPE =
          new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(storage.getName()));
      return List.of(ID, NAME, QUANTITY, STORAGE_CATEGORY, STORAGE_TYPE);
    }
  }

  private String getEscapedName(boolean mergeItems) {
    String name = getName();
    if (!mergeItems && getId() != getCanonicalId() && isStackable()) {
      name += " (noted)";
    }
    return name.replace(",", "").replace("\n", "");
  }
}
