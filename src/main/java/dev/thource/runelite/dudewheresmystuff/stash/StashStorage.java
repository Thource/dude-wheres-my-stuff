package dev.thource.runelite.dudewheresmystuff.stash;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.ItemStorage;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;

/** StashStorage is responsible for tracking storages that the players STASH units. */
@Getter
public class StashStorage extends ItemStorage<StashStorageType> {

  private final StashUnit stashUnit;
  private boolean handleWithdrawOnTick = false;
  private boolean handleDepositOnTick = false;

  protected StashStorage(DudeWheresMyStuffPlugin plugin, StashUnit stashUnit) {
    super(StashStorageType.STASH, plugin);
    this.stashUnit = stashUnit;
  }

  @Override
  protected void createStoragePanel(StorageManager<?, ?> storageManager) {
    super.createStoragePanel(storageManager);
    assert storagePanel != null;

    storagePanel.setTitleToolTip(stashUnit.getChartText());

    createComponentPopupMenu(storageManager);
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return false;
    }

    if (stashUnit
        .getStashUnitData()
        .getWorldPoints()[0]
        .distanceTo(plugin.getClient().getLocalPlayer().getWorldLocation())
        > 3) {
      return false;
    }

    if (Objects.equals(chatMessage.getMessage(), "You withdraw your items from the STASH unit.")) {
      handleWithdrawOnTick = true;
    } else if (Objects.equals(
        chatMessage.getMessage(), "You deposit your items into the STASH unit.")) {
      handleDepositOnTick = true;
    }

    return true;
  }

  @Override
  public boolean onGameTick() {
    if (handleWithdrawOnTick && handleDepositOnTick) {
      return false;
    }

    final boolean updated = handleWithdrawOnTick || handleDepositOnTick;
    if (handleWithdrawOnTick) {
      handleWithdraw();
    }
    if (handleDepositOnTick) {
      handleDeposit();
    }

    handleWithdrawOnTick = false;
    handleDepositOnTick = false;
    return updated;
  }

  private void handleWithdraw() {
    updateLastUpdated();
    items.clear();
  }

  private void handleDeposit() {
    updateLastUpdated();
    items.clear();
    for (ItemStack itemStack :
        ItemContainerWatcher.getInventoryWatcher().getItemsRemovedLastTick()) {
      for (ItemRequirement itemRequirement : stashUnit.getItemRequirements()) {
        if (itemRequirement.fulfilledBy(itemStack.getId())) {
          items.add(itemStack);
          break;
        }
      }
    }

    for (ItemStack itemStack :
        ItemContainerWatcher.getWornWatcher().getItemsRemovedLastTick()) {
      for (ItemRequirement itemRequirement : stashUnit.getItemRequirements()) {
        if (itemRequirement.fulfilledBy(itemStack.getId())) {
          items.add(itemStack);
          break;
        }
      }
    }
  }

  /**
   * Seeds this unit from an external owned-item snapshot (see Loadout Lab import) when it has
   * never been observed ({@code lastUpdated == -1}). Adds the unit's default items that the
   * snapshot reports as owned and stamps {@code lastUpdated}; a no-op once the unit has real data,
   * so live observations always win and re-imports never clobber them.
   *
   * @return true if this unit was seeded
   */
  public boolean seedFromImport(Map<Integer, Integer> ownedItems) {
    if (lastUpdated != -1L || !isEnabled()) {
      return false;
    }

    boolean seeded = false;
    for (int defaultItemId : stashUnit.getDefaultItemIds()) {
      Integer quantity = ownedItems.get(defaultItemId);
      if (quantity != null && quantity > 0) {
        items.add(new ItemStack(defaultItemId, quantity, plugin));
        seeded = true;
      }
    }

    if (seeded) {
      updateLastUpdated();
    }
    return seeded;
  }

  @Override
  public String getName() {
    if (stashUnit == null) {
      return "";
    }

    return stashUnit.getLocationName();
  }

  @Override
  protected String getConfigKey(String managerConfigKey) {
    return managerConfigKey + "." + stashUnit.getStashUnitData().getObjectId();
  }
}
