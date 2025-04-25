package dev.thource.runelite.dudewheresmystuff.playerownedhouse;

import com.google.common.collect.ImmutableList;
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
  public static final List<List<Integer>> VARPLAYER_BITS_TO_ITEM_IDS_LIST;

  static {
    ImmutableList.Builder<List<Integer>> builder = new ImmutableList.Builder<>();

    builder.add(
        Arrays.asList(
            ItemID.CHAOSELEPET,
            ItemID.SUPREMEPET,
            ItemID.PRIMEPET,
            ItemID.REXPET,
            ItemID.PENANCEPET,
            ItemID.ARMADYLPET,
            ItemID.BANDOSPET,
            ItemID.SARADOMINPET,
            ItemID.ZAMORAKPET,
            ItemID.MOLEPET,
            ItemID.KBDPET,
            ItemID.KQPET_WALKING,
            ItemID.SMOKEPET,
            ItemID.KRAKENPET,
            ItemID.COREPET,
            ItemID.SNAKEPET,
            ItemID.CHOMPYBIRD_PET,
            ItemID.VENENATIS_PET,
            ItemID.CALLISTO_PET,
            ItemID.VETION_PET,
            ItemID.SCORPIA_PET,
            ItemID.JAD_PET,
            ItemID.HELL_PET,
            ItemID.ABYSSALSIRE_PET,
            ItemID.SKILLPETFISH,
            ItemID.SKILLPETMINING,
            ItemID.SKILLPETWC,
            ItemID.SKILLPETHUNTER_RED,
            ItemID.BLOODHOUND_PET,
            ItemID.SKILLPETAGILITY,
            ItemID.SKILLPETFARMING,
            ItemID.SKILLPETRUNECRAFTING_FIRE));
    builder.add(
        Arrays.asList(
            ItemID.SKILLPETTHIEVING,
            ItemID.PHOENIXPET,
            ItemID.OLMPET,
            ItemID.SKOTIZOPET,
            ItemID.INFERNOPET,
            ItemID.HERBIBOARPET,
            ItemID.DAWNPET,
            ItemID.VORKATHPET,
            ItemID.VERZIKPET,
            ItemID.HYDRAPET,
            ItemID.SARACHNISPET,
            ItemID.GAUNTLETPET,
            ItemID.ZALCANOPET,
            ItemID.NIGHTMAREPET,
            ItemID.SOULWARSPET_BLUE,
            ItemID.TEMPOROSSPET,
            ItemID.NEXPET,
            ItemID.ABYSSALPET,
            ItemID.WARDENPET_TUMEKEN,
            ItemID.MUSPAHPET,
            ItemID.DUKESUCELLUSPET,
            ItemID.VARDORVISPET,
            ItemID.LEVIATHANPET,
            ItemID.WHISPERERPET,
            ItemID.SCURRIUSPET,
            ItemID.SOLHEREDITPET,
            ItemID.QUETZALPET));

    VARPLAYER_BITS_TO_ITEM_IDS_LIST = builder.build();
  }

  private int petBits1;
  private int petBits2;
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

    return saveValues;
  }

  @Override
  protected void loadValues(ArrayList<String> values) {
    super.loadValues(values);

    petBits1 = SaveFieldLoader.loadInt(values, petBits1);
    petBits2 = SaveFieldLoader.loadInt(values, petBits2);
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
    int varpIndex = 0;
    for (List<Integer> itemIds : VARPLAYER_BITS_TO_ITEM_IDS_LIST) {
      int value = varpIndex == 0 ? petBits1 : petBits2;
      for (int i = 0; i < itemIds.size(); i++) {
        if ((value & (1L << i)) == 0) {
          continue;
        }

        varplayerItems.add(new ItemStack(itemIds.get(i), 1, plugin));
      }

      varpIndex++;
    }

    updateItems();
  }

  @Override
  public boolean onVarbitChanged(VarbitChanged varbitChanged) {
    var petVar1 = Var.player(varbitChanged, VarPlayerID.PRAYER20);
    var petVar2 = Var.player(varbitChanged, VarPlayerID.MENAGERIE_CONTENTS2);
    if (!petVar1.wasChanged() && !petVar2.wasChanged()) {
      return false;
    }

    final var oldPetBits1 = petBits1;
    final var oldPetBits2 = petBits2;
    var client = plugin.getClient();
    petBits1 = petVar1.getValue(client);
    petBits2 = petVar2.getValue(client);

    if (petBits1 != oldPetBits1 || petBits2 != oldPetBits2) {
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
  }

  @Override
  public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
    super.load(configManager, managerConfigKey, profileKey);

    rebuildPetsFromBits();
  }
}
