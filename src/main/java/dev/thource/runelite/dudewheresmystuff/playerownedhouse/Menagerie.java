package dev.thource.runelite.dudewheresmystuff.playerownedhouse;

import com.google.common.collect.ImmutableList;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Region;
import dev.thource.runelite.dudewheresmystuff.SaveFieldFormatter;
import dev.thource.runelite.dudewheresmystuff.SaveFieldLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;

/** Menagerie is responsible for tracking which pets the player has in their POH menagerie. */
public class Menagerie extends PlayerOwnedHouseStorage {

  private static final List<Integer> ITEM_CONTAINER_ITEM_IDS =
      Arrays.asList(
          ItemID.PET_ROCK,
          ItemID.PET_KITTEN,
          ItemID.PET_KITTEN_1556,
          ItemID.PET_KITTEN_1557,
          ItemID.PET_KITTEN_1558,
          ItemID.PET_KITTEN_1559,
          ItemID.PET_KITTEN_1560,
          ItemID.PET_CAT,
          ItemID.PET_CAT_1562,
          ItemID.PET_CAT_1563,
          ItemID.PET_CAT_1564,
          ItemID.PET_CAT_1565,
          ItemID.PET_CAT_1566,
          ItemID.PET_CAT_1567,
          ItemID.PET_CAT_1568,
          ItemID.PET_CAT_1569,
          ItemID.PET_CAT_1570,
          ItemID.PET_CAT_1571,
          ItemID.PET_CAT_1572,
          ItemID.WILY_CAT,
          ItemID.WILY_CAT_6556,
          ItemID.WILY_CAT_6557,
          ItemID.WILY_CAT_6558,
          ItemID.WILY_CAT_6559,
          ItemID.WILY_CAT_6560,
          ItemID.LAZY_CAT,
          ItemID.LAZY_CAT_6550,
          ItemID.LAZY_CAT_6551,
          ItemID.LAZY_CAT_6552,
          ItemID.LAZY_CAT_6553,
          ItemID.LAZY_CAT_6554,
          ItemID.HELLKITTEN,
          ItemID.HELL_CAT,
          ItemID.OVERGROWN_HELLCAT,
          ItemID.WILY_HELLCAT,
          ItemID.LAZY_HELL_CAT,
          ItemID.FISHBOWL_6670,
          ItemID.FISHBOWL_6671,
          ItemID.FISHBOWL_6672,
          ItemID.TOY_CAT,
          ItemID.BROAV);
  public static final List<List<Integer>> VARPLAYER_BITS_TO_ITEM_IDS_LIST;

  static {
    ImmutableList.Builder<List<Integer>> builder = new ImmutableList.Builder<>();

    builder.add(
        Arrays.asList(
            ItemID.PET_CHAOS_ELEMENTAL,
            ItemID.PET_DAGANNOTH_SUPREME,
            ItemID.PET_DAGANNOTH_PRIME,
            ItemID.PET_DAGANNOTH_REX,
            ItemID.PET_PENANCE_QUEEN,
            ItemID.PET_KREEARRA,
            ItemID.PET_GENERAL_GRAARDOR,
            ItemID.PET_ZILYANA,
            ItemID.PET_KRIL_TSUTSAROTH,
            ItemID.BABY_MOLE,
            ItemID.PRINCE_BLACK_DRAGON,
            ItemID.KALPHITE_PRINCESS,
            ItemID.PET_SMOKE_DEVIL,
            ItemID.PET_KRAKEN,
            ItemID.PET_DARK_CORE,
            ItemID.PET_SNAKELING,
            ItemID.CHOMPY_CHICK,
            ItemID.VENENATIS_SPIDERLING,
            ItemID.CALLISTO_CUB,
            ItemID.VETION_JR,
            ItemID.SCORPIAS_OFFSPRING,
            ItemID.TZREKJAD,
            ItemID.HELLPUPPY,
            ItemID.ABYSSAL_ORPHAN,
            ItemID.HERON,
            ItemID.ROCK_GOLEM,
            ItemID.BEAVER,
            ItemID.BABY_CHINCHOMPA,
            ItemID.BLOODHOUND,
            ItemID.GIANT_SQUIRREL,
            ItemID.TANGLEROOT,
            ItemID.RIFT_GUARDIAN));
    builder.add(
        Arrays.asList(
            ItemID.ROCKY,
            ItemID.PHOENIX,
            ItemID.OLMLET,
            ItemID.SKOTOS,
            ItemID.JALNIBREK,
            ItemID.HERBI,
            ItemID.NOON,
            ItemID.VORKI,
            ItemID.LIL_ZIK,
            ItemID.IKKLE_HYDRA,
            ItemID.SRARACHA,
            ItemID.YOUNGLLEF,
            ItemID.SMOLCANO,
            ItemID.LITTLE_NIGHTMARE,
            ItemID.LIL_CREATOR,
            ItemID.TINY_TEMPOR,
            ItemID.NEXLING,
            ItemID.ABYSSAL_PROTECTOR,
            ItemID.TUMEKENS_GUARDIAN,
            ItemID.MUPHIN,
            ItemID.BARON,
            ItemID.BUTCH,
            ItemID.LILVIATHAN,
            ItemID.WISP,
            ItemID.SCURRY,
            ItemID.SMOL_HEREDIT,
            ItemID.QUETZIN));

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

    boolean isBeingFollowed = plugin.getClient().getVarpValue(447) != -1;
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
  public boolean onVarbitChanged() {
    int oldPetBits1 = petBits1;
    int oldPetBits2 = petBits2;

    petBits1 = plugin.getClient().getVarpValue(864);
    petBits2 = plugin.getClient().getVarpValue(1416);

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
