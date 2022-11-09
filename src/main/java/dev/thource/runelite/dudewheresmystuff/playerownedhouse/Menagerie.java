package dev.thource.runelite.dudewheresmystuff.playerownedhouse;

import com.google.common.collect.ImmutableMap;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Region;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import org.apache.commons.lang3.math.NumberUtils;

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
          ItemID.TOY_CAT);
  private static final Map<Integer, List<Integer>> VARPLAYER_BITS_TO_ITEM_ID_MAP;

  static {
    ImmutableMap.Builder<Integer, List<Integer>> builder = new ImmutableMap.Builder<>();

    builder.put(
        864,
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
    builder.put(
        1416,
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
            ItemID.ABYSSAL_PROTECTOR));

    VARPLAYER_BITS_TO_ITEM_ID_MAP = builder.build();
  }

  private final int[] varplayerValues = new int[2];
  private final List<ItemStack> itemContainerItems = new ArrayList<>();
  private final List<ItemStack> varplayerItems = new ArrayList<>();
  private boolean wasBeingFollowedLastTick = false;

  protected Menagerie(DudeWheresMyStuffPlugin plugin) {
    super(PlayerOwnedHouseStorageType.MENAGERIE, plugin);
  }

  private void updateItems() {
    items.clear();
    items.addAll(itemContainerItems);
    items.addAll(varplayerItems);
  }

  private boolean updateRemovedInventoryItems() {
    boolean updated = false;
    ItemContainerWatcher inventoryWatcher = ItemContainerWatcher.getInventoryWatcher();

    // Remove items that were added to the inventory
    for (ItemStack itemStack : inventoryWatcher.getItemsAddedLastTick()) {
      if (ITEM_CONTAINER_ITEM_IDS.contains(itemStack.getId())) {
        ListIterator<ItemStack> listIterator = itemContainerItems.listIterator();

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
          itemContainerItems.add(itemStack);
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
      lastUpdated = System.currentTimeMillis();
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

    lastUpdated = System.currentTimeMillis();
    itemContainerItems.clear();
    for (Item item : itemContainerChanged.getItemContainer().getItems()) {
      if (item.getId() != -1) {
        itemContainerItems.add(new ItemStack(item.getId(), 1, plugin));
      }
    }
    updateItems();

    return true;
  }

  @Override
  public boolean onVarbitChanged() {
    boolean didChange = false;

    int index = 0;
    for (Integer varplayer : VARPLAYER_BITS_TO_ITEM_ID_MAP.keySet()) {
      int value = plugin.getClient().getVarpValue(varplayer);
      if (value != varplayerValues[index]) {
        didChange = true;
        varplayerValues[index] = value;
      }

      index++;
    }

    if (didChange) {
      lastUpdated = System.currentTimeMillis();

      varplayerItems.clear();
      int varpIndex = 0;
      for (List<Integer> itemIds : VARPLAYER_BITS_TO_ITEM_ID_MAP.values()) {
        int value = varplayerValues[varpIndex];
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

    return didChange;
  }

  @Override
  public void save(ConfigManager configManager, String managerConfigKey) {
    String data =
        lastUpdated
            + ";"
            + itemContainerItems.stream()
                .map(item -> item.getId() + "," + item.getQuantity())
                .collect(Collectors.joining("="))
            + ";"
            + varplayerItems.stream()
                .map(item -> item.getId() + "," + item.getQuantity())
                .collect(Collectors.joining("="));

    configManager.setRSProfileConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, getConfigKey(managerConfigKey), data);
  }

  @Override
  protected List<ItemStack> loadItems(
      ConfigManager configManager, String managerConfigKey, String profileKey) {
    String data =
        configManager.getConfiguration(
            DudeWheresMyStuffConfig.CONFIG_GROUP,
            profileKey,
            getConfigKey(managerConfigKey),
            String.class);
    if (data == null) {
      return Collections.emptyList();
    }

    String[] dataSplit = data.split(";");
    if (dataSplit.length < 1) {
      return Collections.emptyList();
    }

    this.lastUpdated = NumberUtils.toLong(dataSplit[0], -1);
    if (dataSplit.length < 2) {
      return Collections.emptyList();
    }

    for (String itemStackString : dataSplit[1].split("=")) {
      ItemStack itemStack = stringDataToItemStack(itemStackString);

      if (itemStack != null) {
        itemContainerItems.add(itemStack);
      }
    }
    List<ItemStack> loadedItems = new ArrayList<>(itemContainerItems);

    if (dataSplit.length == 3) {
      for (String itemStackString : dataSplit[2].split("=")) {
        ItemStack itemStack = stringDataToItemStack(itemStackString);

        if (itemStack != null) {
          varplayerItems.add(itemStack);
        }
      }
      loadedItems.addAll(varplayerItems);
    }

    return loadedItems;
  }

  private ItemStack stringDataToItemStack(String itemStackString) {
    String[] itemStackData = itemStackString.split(",");
    if (itemStackData.length != 2) {
      return null;
    }

    int itemId = NumberUtils.toInt(itemStackData[0]);
    int itemQuantity = NumberUtils.toInt(itemStackData[1]);

    return new ItemStack(itemId, itemQuantity, plugin);
  }

  @Override
  public void reset() {
    super.reset();

    itemContainerItems.clear();
    varplayerItems.clear();
    varplayerValues[0] = 0;
    varplayerValues[1] = 0;
  }
}
