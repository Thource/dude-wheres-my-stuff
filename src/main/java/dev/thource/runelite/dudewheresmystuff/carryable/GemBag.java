package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.death.SuspendedGroundItem;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.ItemID;

/** GemBag is responsible for tracking the player's gems in their gem bag. */
@Getter
public class GemBag extends CarryableStorage implements Emptyable, Fillable, PatternCheckable,
    PatternEmptyable, PickupDepositable, UseDepositable {

  private static final List<Integer> ITEM_WHITELIST = List.of(ItemID.UNCUT_SAPPHIRE,
      ItemID.UNCUT_EMERALD, ItemID.UNCUT_RUBY, ItemID.UNCUT_DIAMOND, ItemID.UNCUT_DRAGONSTONE);
  private static final Pattern CHECK_PATTERN =
      Pattern.compile("(?:Left in bag: )?"
          + "Sapphires: (\\d+) / Emeralds: (\\d+) / Rubies: (\\d+)<br>"
          + "Diamonds: (\\d+) / Dragonstones: (\\d+)");
  private static final Pattern EMPTY_PATTERN = Pattern.compile("The gem bag is (?:now )?empty.");

  @Getter private final List<SuspendedItem> itemsUsed = new ArrayList<>();
  @Getter private final List<SuspendedGroundItem> itemsPickedUp = new ArrayList<>();
  @Getter @Setter private EmptyingState emptyingState = EmptyingState.NONE;
  @Getter @Setter private FillingState fillingState = FillingState.NONE;

  public GemBag(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.GEM_BAG, plugin);

    hasStaticItems = true;

    for (Integer itemId : ITEM_WHITELIST) {
      items.add(new ItemStack(itemId, plugin));
    }

    plugin
        .getClientThread()
        .invokeLater(() -> items.forEach(itemStack -> itemStack.setStackable(true)));
  }

  // the use of | instead of || is not an accident, each function should be executed
  @SuppressWarnings("java:S2178")
  @Override
  public boolean onGameTick() {
    boolean updated = super.onGameTick();

    if (Emptyable.super.onGameTick() | Fillable.super.onGameTick()
        | PickupDepositable.super.onGameTick() | UseDepositable.super.onGameTick()) {
      updated = true;
    }

    return updated;
  }

  // This is required because the gem bag will throw everything in, regardless of what was used on
  //   it
  @Override
  public boolean wasItemUsed(int itemId) {
    return !itemsUsed.isEmpty() && ITEM_WHITELIST.contains(itemId);
  }

  @Override
  public int getOpenContainerId() {
    return ItemID.GEM_BAG_OPEN;
  }

  @Override
  public List<Integer> getItemWhitelist() {
    return ITEM_WHITELIST;
  }

  @Override
  public Pattern getEmptyPattern() {
    return EMPTY_PATTERN;
  }

  @Override
  public Pattern getCheckPattern() {
    return CHECK_PATTERN;
  }

  @Override
  public boolean onItemDespawned(ItemDespawned itemDespawned) {
    return PickupDepositable.super.onItemDespawned(itemDespawned);
  }

  @Override
  public boolean onMenuOptionClicked(MenuOptionClicked menuOption) {
    Emptyable.super.onMenuOptionClicked(menuOption);
    Fillable.super.onMenuOptionClicked(menuOption);
    PickupDepositable.super.onMenuOptionClicked(menuOption);
    UseDepositable.super.onMenuOptionClicked(menuOption);

    return false;
  }

  // the use of | instead of || is not an accident, each function should be executed
  @SuppressWarnings("java:S2178")
  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    return PatternCheckable.super.onChatMessage(chatMessage)
        | PatternEmptyable.super.onChatMessage(chatMessage);
  }
}
