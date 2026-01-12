package dev.thource.runelite.dudewheresmystuff.playerownedhouse;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Region;
import dev.thource.runelite.dudewheresmystuff.SaveFieldFormatter;
import dev.thource.runelite.dudewheresmystuff.SaveFieldLoader;
import dev.thource.runelite.dudewheresmystuff.Var;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import net.runelite.api.Item;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.config.ConfigManager;

/** Menagerie is responsible for tracking which pets the player has in their POH menagerie. */
public class Menagerie extends PlayerOwnedHouseStorage {

  private static final List<Integer> ITEM_CONTAINER_ITEM_IDS =
      Arrays.asList(
          ItemID.VT_USELESS_ROCK,
          ItemID.KITTENOBJECT,
          ItemID.KITTENOBJECT_LIGHT,
          ItemID.KITTENOBJECT_BROWN,
          ItemID.KITTENOBJECT_BLACK,
          ItemID.KITTENOBJECT_BROWNGREY,
          ItemID.KITTENOBJECT_BLUEGREY,
          ItemID.GROWNCATOBJECT,
          ItemID.GROWNCATOBJECT_LIGHT,
          ItemID.GROWNCATOBJECT_BROWN,
          ItemID.GROWNCATOBJECT_BLACK,
          ItemID.GROWNCATOBJECT_BROWNGREY,
          ItemID.GROWNCATOBJECT_BLUEGREY,
          ItemID.OVERGROWNCATOBJECT,
          ItemID.OVERGROWNCATOBJECT_LIGHT,
          ItemID.OVERGROWNCATOBJECT_BROWN,
          ItemID.OVERGROWNCATOBJECT_BLACK,
          ItemID.OVERGROWNCATOBJECT_BROWNGREY,
          ItemID.OVERGROWNCATOBJECT_BLUEGREY,
          ItemID.WILEYCATOBJECT_LIGHT,
          ItemID.WILEYCATOBJECT,
          ItemID.WILEYCATOBJECT_BROWN,
          ItemID.WILEYCATOBJECT_BLACK,
          ItemID.WILEYCATOBJECT_BROWNGREY,
          ItemID.WILEYCATOBJECT_BLUEGREY,
          ItemID.LAZYCATOBJECT_LIGHT,
          ItemID.LAZYCATOBJECT,
          ItemID.LAZYCATOBJECT_BROWN,
          ItemID.LAZYCATOBJECT_BLACK,
          ItemID.LAZYCATOBJECT_BROWNGREY,
          ItemID.LAZYCATOBJECT_BLUEGREY,
          ItemID.KITTENOBJECT_HELL,
          ItemID.GROWNCATOBJECT_HELL,
          ItemID.OVERGROWNCATOBJECT_HELL,
          ItemID.WILEYCATOBJECT_HELL,
          ItemID.LAZYCATOBJECT_HELL,
          ItemID.FISHBOWL_BLUEFISH,
          ItemID.FISHBOWL_GREENFISH,
          ItemID.FISHBOWL_SPINEFISH,
          ItemID.POH_TOY_CAT,
          ItemID.WGS_BROAV);

  private int petBits1;
  private int petBits2;
  private int petBits3;
  private final List<ItemStack> compiledItems = new ArrayList<>();
  private final List<ItemStack> varplayerItems = new ArrayList<>();
  private boolean wasBeingFollowedLastTick = false;

  protected Menagerie(DudeWheresMyStuffPlugin plugin) {
    super(PlayerOwnedHouseStorageType.MENAGERIE, plugin);
  }

  @Override
  protected ArrayList<String> getSaveValues() {
    ArrayList<String> saveValues = super.getSaveValues();

    saveValues.add(SaveFieldFormatter.format(petBits1));
    saveValues.add(SaveFieldFormatter.format(petBits2));
    saveValues.add(SaveFieldFormatter.format(petBits3));

    return saveValues;
  }

  @Override
  protected void loadValues(ArrayList<String> values) {
    super.loadValues(values);

    petBits1 = SaveFieldLoader.loadInt(values, petBits1);
    petBits2 = SaveFieldLoader.loadInt(values, petBits2);
    petBits3 = SaveFieldLoader.loadInt(values, petBits3);
  }

  private void updateItems() {
    compiledItems.clear();
    compiledItems.addAll(items);
    compiledItems.addAll(varplayerItems);
  }

  private boolean updateRemovedInventoryItems() {
    boolean updated = false;
    ItemContainerWatcher inventoryWatcher = ItemContainerWatcher.getInventoryWatcher();

    // Remove items that were added to the inventory
    for (ItemStack itemStack : inventoryWatcher.getItemsAddedLastTick()) {
      if (ITEM_CONTAINER_ITEM_IDS.contains(itemStack.getId())) {
        ListIterator<ItemStack> listIterator = items.listIterator();

        while (listIterator.hasNext()) {
          ItemStack item = listIterator.next();
          if (item.getId() == itemStack.getId()) {
            listIterator.remove();
            updated = true;
            break;
          }
        }
      }
    }

    return updated;
  }

  private boolean updateFromInventoryWatcher(boolean isBeingFollowed) {
    boolean updated = false;
    ItemContainerWatcher inventoryWatcher = ItemContainerWatcher.getInventoryWatcher();

    // Add items that disappeared from the inventory if the player is not being followed or was
    // already being followed last tick (pet not dropped this tick)
    if (!isBeingFollowed || wasBeingFollowedLastTick) {
      for (ItemStack itemStack : inventoryWatcher.getItemsRemovedLastTick()) {
        if (ITEM_CONTAINER_ITEM_IDS.contains(itemStack.getId())) {
          items.add(itemStack);
          updated = true;
        }
      }
    }

    if (updateRemovedInventoryItems()) {
      updated = true;
    }

    return updated;
  }

  @Override
  public boolean onGameTick() {
    WorldPoint worldPoint =
        WorldPoint.fromLocalInstance(
            plugin.getClient(), plugin.getClient().getLocalPlayer().getLocalLocation());

    if (Region.get(worldPoint.getRegionID()) != Region.REGION_POH) {
      return false;
    }

    boolean isBeingFollowed = plugin.getClient().getVarpValue(VarPlayerID.FOLLOWER_NPC) != -1;
    boolean updated = updateFromInventoryWatcher(isBeingFollowed);

    if (updated) {
      updateLastUpdated();
      updateItems();
    }

    wasBeingFollowedLastTick = isBeingFollowed;

    return updated;
  }

  @Override
  public boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    if (itemContainerChanged.getContainerId() != type.getItemContainerId()) {
      return false;
    }

    updateLastUpdated();
    items.clear();
    for (Item item : itemContainerChanged.getItemContainer().getItems()) {
      if (item.getId() != -1) {
        items.add(new ItemStack(item.getId(), 1, plugin));
      }
    }
    updateItems();

    return true;
  }

  void rebuildPetsFromBits() {
    varplayerItems.clear();

    var client = plugin.getClient();
    var petEnum = client.getEnum(985);

    for (var i = 0; i < petEnum.getIntVals().length; i++) {
      if (i < 32 && (petBits1 & (1 << i)) != 0) {
        varplayerItems.add(new ItemStack(petEnum.getIntValue(i), 1, plugin));
      } else if (i >= 32 && i < 63 && (petBits2 & (1 << (i - 32))) != 0) {
        varplayerItems.add(new ItemStack(petEnum.getIntValue(i), 1, plugin));
      } else if (i >= 63 && i < 94 && (petBits3 & (1 << (i - 63))) != 0) {
        varplayerItems.add(new ItemStack(petEnum.getIntValue(i), 1, plugin));
      }
    }

    updateItems();
  }

  @Override
  public boolean onVarbitChanged(VarbitChanged varbitChanged) {
    var petVar1 = Var.player(varbitChanged, VarPlayerID.PRAYER20);
    var petVar2 = Var.player(varbitChanged, VarPlayerID.MENAGERIE_CONTENTS2);
    var petVar3 = Var.player(varbitChanged, VarPlayerID.MENAGERIE_CONTENTS3);
    if (!petVar1.wasChanged() && !petVar2.wasChanged() && !petVar3.wasChanged()) {
      return false;
    }

    final var oldPetBits1 = petBits1;
    final var oldPetBits2 = petBits2;
    final var oldPetBits3 = petBits3;
    var client = plugin.getClient();
    petBits1 = petVar1.getValue(client);
    petBits2 = petVar2.getValue(client);
    petBits3 = petVar3.getValue(client);

    if (petBits1 != oldPetBits1 || petBits2 != oldPetBits2 || petBits3 != oldPetBits3) {
      rebuildPetsFromBits();
      updateLastUpdated();
      return true;
    }

    return false;
  }

  @Override
  public List<ItemStack> getItems() {
    return compiledItems;
  }

  @Override
  public void reset() {
    super.reset();

    items.clear();
    compiledItems.clear();
    varplayerItems.clear();
    petBits1 = 0;
    petBits2 = 0;
    petBits3 = 0;
  }

  @Override
  public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
    super.load(configManager, managerConfigKey, profileKey);

    rebuildPetsFromBits();
  }
}
