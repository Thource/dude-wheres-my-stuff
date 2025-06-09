package dev.thource.runelite.dudewheresmystuff.playerownedhouse;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;

/**
 * SpiceRack is responsible for tracking how many of each spice the player has in their POH spice
 * rack.
 */
public class SpiceRack extends PlayerOwnedHouseStorage {

  @RequiredArgsConstructor
  @Getter
  private static class SpiceData {

    private final int doses;
    private final ItemStack itemStack;
  }

  private static final Pattern CHECK_PATTERN =
      Pattern.compile("(\\d+) x Red Spice.<br>(\\d+) x Brown Spice.<br>(\\d+) x Yellow Spice."
          + "<br>(\\d+) x Orange Spice.");

  private final ItemStack redSpice;
  private final ItemStack brownSpice;
  private final ItemStack yellowSpice;
  private final ItemStack orangeSpice;
  private final Map<Integer, SpiceData> spiceMap = new HashMap<>();

  private State state = State.NONE;

  private enum State {
    NONE,
    WITHDRAWING,
    CHECK_WITHDRAW,
    CHECK_WITHDRAW_TWICE,
    CHECKED_DEPOSIT,
  }

  protected SpiceRack(DudeWheresMyStuffPlugin plugin) {
    super(PlayerOwnedHouseStorageType.SPICE_RACK, plugin);

    hasStaticItems = true;

    redSpice = new ItemStack(ItemID.HUNDRED_DAVE_SPICE_RED_1, plugin);
    spiceMap.put(ItemID.HUNDRED_DAVE_SPICE_RED_1, new SpiceData(1, redSpice));
    spiceMap.put(ItemID.HUNDRED_DAVE_SPICE_RED_2, new SpiceData(2, redSpice));
    spiceMap.put(ItemID.HUNDRED_DAVE_SPICE_RED_3, new SpiceData(3, redSpice));
    spiceMap.put(ItemID.HUNDRED_DAVE_SPICE_RED_4, new SpiceData(4, redSpice));
    items.add(redSpice);

    brownSpice = new ItemStack(ItemID.HUNDRED_DAVE_SPICE_BROWN_1, plugin);
    spiceMap.put(ItemID.HUNDRED_DAVE_SPICE_BROWN_1, new SpiceData(1, brownSpice));
    spiceMap.put(ItemID.HUNDRED_DAVE_SPICE_BROWN_2, new SpiceData(2, brownSpice));
    spiceMap.put(ItemID.HUNDRED_DAVE_SPICE_BROWN_3, new SpiceData(3, brownSpice));
    spiceMap.put(ItemID.HUNDRED_DAVE_SPICE_BROWN_4, new SpiceData(4, brownSpice));
    items.add(brownSpice);

    yellowSpice = new ItemStack(ItemID.HUNDRED_DAVE_SPICE_YELLOW_1, plugin);
    spiceMap.put(ItemID.HUNDRED_DAVE_SPICE_YELLOW_1, new SpiceData(1, yellowSpice));
    spiceMap.put(ItemID.HUNDRED_DAVE_SPICE_YELLOW_2, new SpiceData(2, yellowSpice));
    spiceMap.put(ItemID.HUNDRED_DAVE_SPICE_YELLOW_3, new SpiceData(3, yellowSpice));
    spiceMap.put(ItemID.HUNDRED_DAVE_SPICE_YELLOW_4, new SpiceData(4, yellowSpice));
    items.add(yellowSpice);

    orangeSpice = new ItemStack(ItemID.HUNDRED_DAVE_SPICE_ORANGE_1, plugin);
    spiceMap.put(ItemID.HUNDRED_DAVE_SPICE_ORANGE_1, new SpiceData(1, orangeSpice));
    spiceMap.put(ItemID.HUNDRED_DAVE_SPICE_ORANGE_2, new SpiceData(2, orangeSpice));
    spiceMap.put(ItemID.HUNDRED_DAVE_SPICE_ORANGE_3, new SpiceData(3, orangeSpice));
    spiceMap.put(ItemID.HUNDRED_DAVE_SPICE_ORANGE_4, new SpiceData(4, orangeSpice));
    items.add(orangeSpice);
  }

  @Override
  public boolean onGameTick() {
    var updated = false;

    var client = plugin.getClient();
    var withdrawWidget = client.getWidget(InterfaceID.Chatbox.MES_TEXT);
    if (withdrawWidget != null && !withdrawWidget.isHidden() && Objects.equals(
        withdrawWidget.getText(),
        "How much spice would you like to take?:")) {
      state = State.WITHDRAWING;
    } else if (state == State.WITHDRAWING) {
      state = State.CHECK_WITHDRAW;
    }

    if (state == State.CHECK_WITHDRAW || state == State.CHECK_WITHDRAW_TWICE) {
      for (ItemStack itemStack : ItemContainerWatcher.getInventoryWatcher()
          .getItemsAddedLastTick()) {
        var spiceData = spiceMap.get(itemStack.getId());
        if (spiceData != null) {
          var spiceStack = spiceData.getItemStack();
          spiceStack.setQuantity(
              spiceStack.getQuantity() - spiceData.getDoses() * itemStack.getQuantity());
        }
      }
      updated = true;
      updateLastUpdated();

      if (state == State.CHECK_WITHDRAW) {
        state = State.CHECK_WITHDRAW_TWICE;
      } else {
        state = State.NONE;
      }
    }

    var messageBoxTextWidget = client.getWidget(InterfaceID.Messagebox.TEXT);
    if (messageBoxTextWidget != null && Objects.equals(messageBoxTextWidget.getText(),
        "Your spices have been stored.")) {
      if (state != State.CHECKED_DEPOSIT) {
        for (ItemStack itemStack : ItemContainerWatcher.getInventoryWatcher()
            .getItemsRemovedLastTick()) {
          var spiceData = spiceMap.get(itemStack.getId());
          if (spiceData != null) {
            var spiceStack = spiceData.getItemStack();
            spiceStack.setQuantity(
                spiceStack.getQuantity() + spiceData.getDoses() * itemStack.getQuantity());
          }
        }
        updated = true;
        updateLastUpdated();

        state = State.CHECKED_DEPOSIT;
      }
    } else if (state == State.CHECKED_DEPOSIT) {
      state = State.NONE;
    }

    if (messageBoxTextWidget != null) {
      var checkMatcher = CHECK_PATTERN.matcher(messageBoxTextWidget.getText());
      if (checkMatcher.matches()) {
        var redQuantity = Integer.parseInt(checkMatcher.group(1));
        var brownQuantity = Integer.parseInt(checkMatcher.group(2));
        var yellowQuantity = Integer.parseInt(checkMatcher.group(3));
        var orangeQuantity = Integer.parseInt(checkMatcher.group(4));

        updateLastUpdated();
        if (redQuantity != redSpice.getQuantity() || brownQuantity != brownSpice.getQuantity()
            || yellowQuantity != yellowSpice.getQuantity()
            || orangeQuantity != orangeSpice.getQuantity()) {
          redSpice.setQuantity(redQuantity);
          brownSpice.setQuantity(brownQuantity);
          yellowSpice.setQuantity(yellowQuantity);
          orangeSpice.setQuantity(orangeQuantity);

          updated = true;
        }
      }
    }

    return updated;
  }
}
