package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Storage;
import lombok.Getter;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.events.ItemContainerChanged;

/**
 * CarryableStorage is responsible for tracking storages that the player can carry (looting bag,
 * rune pouch, etc).
 */
@Getter
public class CarryableStorage extends Storage<CarryableStorageType> {

  protected CarryableStorage(CarryableStorageType type, DudeWheresMyStuffPlugin plugin) {
    super(type, plugin);
  }

  @Override
  public boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    boolean updated = super.onItemContainerChanged(itemContainerChanged);

    if (type == CarryableStorageType.EQUIPMENT && updated) {
      ItemStack empty = new ItemStack(-1, "empty", 1, 0, 0, false);

      if (items.size() < 14) {
        for (int i = items.size(); i < 14; i++) {
          items.add(empty);
        }
      }

      // move ammo into the correct place if the slot exists
      ItemStack ammo = items.remove(EquipmentInventorySlot.AMMO.getSlotIdx());
      items.add(3, ammo);

      items.remove(12); // remove empty space between boots and ring

      // pad it out to fit the 4 wide grid
      items.add(0, empty);
      items.add(2, empty);
      items.add(3, empty);
      items.add(7, empty);
      items.add(11, empty);
      items.add(15, empty);

      items.forEach(
          itemStack -> itemStack.setId(plugin.getItemManager().canonicalize(itemStack.getId())));
    }

    return updated;
  }
}
