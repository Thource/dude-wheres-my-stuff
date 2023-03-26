package dev.thource.runelite.dudewheresmystuff;

import static net.runelite.client.RuneLite.RUNELITE_DIR;

import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageManager;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageManager;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageType;
import dev.thource.runelite.dudewheresmystuff.death.DeathItems;
import dev.thource.runelite.dudewheresmystuff.death.DeathStorageManager;
import dev.thource.runelite.dudewheresmystuff.death.Deathbank;
import dev.thource.runelite.dudewheresmystuff.death.Deathpile;
import dev.thource.runelite.dudewheresmystuff.minigames.MinigamesStorageManager;
import dev.thource.runelite.dudewheresmystuff.playerownedhouse.PlayerOwnedHouseStorageManager;
import dev.thource.runelite.dudewheresmystuff.stash.StashStorageManager;
import dev.thource.runelite.dudewheresmystuff.world.WorldStorageManager;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.config.ConfigManager;

@Slf4j
@Getter
public class StorageManagerManager {

  public static final File EXPORT_DIR = new File(RUNELITE_DIR, "dudewheresmystuff");

  private final CarryableStorageManager carryableStorageManager;
  private final CoinsStorageManager coinsStorageManager;
  private final DeathStorageManager deathStorageManager;
  private final MinigamesStorageManager minigamesStorageManager;
  private final StashStorageManager stashStorageManager;
  private final PlayerOwnedHouseStorageManager playerOwnedHouseStorageManager;
  private final WorldStorageManager worldStorageManager;
  @Setter private String displayName;
  @Getter() private final List<StorageManager<?, ?>> storageManagers;

  private final DudeWheresMyStuffPlugin plugin;
  private final ConfigManager configManager;

  @SuppressWarnings("java:S107")
  StorageManagerManager(
      DudeWheresMyStuffPlugin plugin,
      CarryableStorageManager carryableStorageManager,
      CoinsStorageManager coinsStorageManager,
      DeathStorageManager deathStorageManager,
      MinigamesStorageManager minigamesStorageManager,
      StashStorageManager stashStorageManager,
      PlayerOwnedHouseStorageManager playerOwnedHouseStorageManager,
      WorldStorageManager worldStorageManager) {
    this.plugin = plugin;
    this.configManager = carryableStorageManager.getConfigManager();
    this.carryableStorageManager = carryableStorageManager;
    this.coinsStorageManager = coinsStorageManager;
    this.deathStorageManager = deathStorageManager;
    this.minigamesStorageManager = minigamesStorageManager;
    this.stashStorageManager = stashStorageManager;
    this.playerOwnedHouseStorageManager = playerOwnedHouseStorageManager;
    this.worldStorageManager = worldStorageManager;
    this.displayName = "";

    storageManagers =
        Arrays.asList(
            carryableStorageManager,
            coinsStorageManager,
            deathStorageManager,
            minigamesStorageManager,
            stashStorageManager,
            playerOwnedHouseStorageManager,
            worldStorageManager);
  }

  void reset() {
    storageManagers.forEach(StorageManager::reset);
  }

  public void onActorDeath(ActorDeath actorDeath) {
    storageManagers.forEach(storageManager -> storageManager.onActorDeath(actorDeath));
  }

  public void onGameStateChanged(GameStateChanged gameStateChanged) {
    storageManagers.forEach(storageManager -> storageManager.onGameStateChanged(gameStateChanged));
  }

  public void load(String profileKey) {
    for (StorageManager<?, ?> storageManager : storageManagers) {
      storageManager.load(profileKey);

      SwingUtilities.invokeLater(
          () -> {
            storageManager.getStorages().forEach(storage -> storage.getStoragePanel().update());
            storageManager.getStorageTabPanel().reorderStoragePanels();
          });
    }
  }

  public void save(String profileKey) {
    for (StorageManager<?, ?> storageManager : storageManagers) {
      storageManager.save(profileKey);
    }
  }

  public void onGameTick() {
    storageManagers.forEach(StorageManager::onGameTick);
  }

  public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
    storageManagers.forEach(manager -> manager.onWidgetLoaded(widgetLoaded));
  }

  public void onWidgetClosed(WidgetClosed widgetClosed) {
    storageManagers.forEach(manager -> manager.onWidgetClosed(widgetClosed));
  }

  public void onVarbitChanged() {
    storageManagers.forEach(StorageManager::onVarbitChanged);
  }

  public void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    storageManagers.forEach(manager -> manager.onItemContainerChanged(itemContainerChanged));
  }

  public void onItemDespawned(ItemDespawned itemDespawned) {
    storageManagers.forEach(manager -> manager.onItemDespawned(itemDespawned));
  }

  public void onChatMessage(ChatMessage chatMessage) {
    storageManagers.forEach(manager -> manager.onChatMessage(chatMessage));
  }

  @SuppressWarnings("java:S1452")
  public Stream<? extends Storage<? extends Enum<? extends Enum<?>>>> getStorages() {
    return Stream.of(
            getDeathStorageManager().storages.stream()
                .filter(s -> !(s instanceof DeathItems))
                .filter(
                    s ->
                        (s instanceof Deathpile && !((Deathpile) s).hasExpired())
                            || (s instanceof Deathbank && ((Deathbank) s).getLostAt() == -1L)),
            getCoinsStorageManager().storages.stream()
                .filter(
                    storage ->
                        storage.getType() != CoinsStorageType.INVENTORY
                            && storage.getType() != CoinsStorageType.LOOTING_BAG),
            getCarryableStorageManager().storages.stream(),
            getStashStorageManager().storages.stream(),
            getPlayerOwnedHouseStorageManager().storages.stream(),
            getWorldStorageManager().storages.stream())
        .flatMap(i -> i);
  }

  public List<ItemStack> getItems() {
    return getStorages().filter(Storage::isEnabled).map(Storage::getItems).flatMap(List::stream)
        .collect(Collectors.toList());
  }

  /**
   * Gets all known withdrawable items
   * <p>
   * If the same item is in multiple storages, the item stacks are combined. "Same item" refers to
   * items with the same canonical ID, but note that the actual ID of the stack will be set to the
   * ID of one of the items arbitrarily. It is therefore recommended that callers do not use the
   * IDs, only the canonical IDs.
   *
   * @return The item stacks
   */
  public Collection<ItemStack> getWithdrawableItems() {
    // We need to deduplicate and combine item stacks if they're in multiple
    // storages. This is a map from the stack's canonical (unnoted,
    // un-placeholdered) ID to its stack.
    TreeMap<Integer, ItemStack> items = new TreeMap<>();

    getStorages()
        .filter(Storage::isWithdrawable)
        .map(Storage::getItems)
        .flatMap(List::stream)
        .forEach((ItemStack stack) -> {
          if (stack.getQuantity() == 0 || stack.getId() == -1) {
            return;
          }

          int id = stack.getCanonicalId();

          ItemStack existing = items.get(id);
          if (existing == null) {
            // No item yet, insert a copy so that we can modify their quantities later if necessary
            items.put(id, new ItemStack(stack));
          } else {
            // This item was already in there. Update the quantity to include the new stack.
            existing.setQuantity(stack.getQuantity() + existing.getQuantity());
          }
        });

    return items.values();
  }

  public void setItemSortMode(ItemSortMode itemSortMode) {
    storageManagers.forEach(
        storageManager -> {
          storageManager.getStorages().stream()
              .map(Storage::getStoragePanel)
              .forEach(storagePanel -> storagePanel.setSortMode(itemSortMode));

          JComboBox<ItemSortMode> sortDropdown =
              storageManager.getStorageTabPanel().getSortItemsDropdown();

          final ItemListener[] itemListeners = sortDropdown.getItemListeners();

          // We need to remove and re-add the item listeners to avoid recursion
          Arrays.stream(itemListeners).forEach(sortDropdown::removeItemListener);
          sortDropdown.setSelectedItem(itemSortMode);
          Arrays.stream(itemListeners).forEach(sortDropdown::addItemListener);
        });
  }

  public void onMenuOptionClicked(MenuOptionClicked menuOption) {
    storageManagers.forEach(manager -> manager.onMenuOptionClicked(menuOption));
  }

  // Creates a CSV file containing all the items in any exportable storage
  public void exportItems() {
    if (displayName.equals("")) {
      log.info("Can't export: no display name");
      return;
    }
    File user_dir = new File(EXPORT_DIR, displayName);
    String fileName = new SimpleDateFormat("'dudewheresmystuff-'yyyyMMdd'T'HHmmss'.csv'").format(
        new Date());
    String filePath = user_dir + File.separator + fileName;

    Collection<ItemStack> items = getWithdrawableItems();

    try {
      user_dir.mkdirs();

      BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
      // Include a CSV header describing the columns
      writer.write("ID,Name,Quantity\n");

      for (ItemStack stack : items) {
        String escaped_name = stack.getName().replace(",", "").replace("\n", "");
        writer.write(
            String.format("%d,%s,%d\n", stack.getCanonicalId(), escaped_name, stack.getQuantity()));
      }
      writer.close();
    } catch (IOException e) {
      log.error("Unable to export: " + e.getMessage());
    }
  }
}
