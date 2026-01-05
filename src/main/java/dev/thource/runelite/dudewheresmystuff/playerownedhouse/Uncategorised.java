package dev.thource.runelite.dudewheresmystuff.playerownedhouse;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.Getter;
import net.runelite.api.Item;
import net.runelite.api.events.ItemContainerChanged;

/** Uncategorised is responsible for tracking items that aren't accounted for in other POH
 * storages. */
@Getter
public class Uncategorised extends PlayerOwnedHouseStorage {

  protected Uncategorised(DudeWheresMyStuffPlugin plugin) {
    super(PlayerOwnedHouseStorageType.UNCATEGORISED, plugin);
  }

  @Override
  public boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    var containerId = itemContainerChanged.getContainerId();
    if (containerId > 0x8000) {
      containerId -= 0x8000;
    }

    if (containerId != type.getItemContainerId()) {
      return false;
    }

    updateLastUpdated();
    items.clear();
    var registeredIds =
        Arrays.stream(PlayerOwnedHouseStorageType.values())
            .filter(
                t ->
                    t.getItemContainerId() == type.getItemContainerId()
                        && t.getStorableItemIds() != null)
            .flatMap(t -> t.getStorableItemIds().stream())
            .collect(Collectors.toList());
    for (Item item : itemContainerChanged.getItemContainer().getItems()) {
      if (!registeredIds.contains(item.getId())) {
        items.add(new ItemStack(item.getId(), item.getQuantity(), plugin));
      }
    }

    return true;
  }
}
