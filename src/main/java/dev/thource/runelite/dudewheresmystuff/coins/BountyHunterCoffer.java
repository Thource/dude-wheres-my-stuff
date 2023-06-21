package dev.thource.runelite.dudewheresmystuff.coins;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.api.widgets.Widget;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * BountyHunterCoffer is responsible for tracking how many coins the player has stored in the bounty
 * hunter coffer.
 */
public class BountyHunterCoffer extends CoinsStorage {

  private static final Pattern depositWithdrawPattern =

      Pattern.compile(
          "You (?:withdrew|added) \\d+ coins (?:from|to) your coffer. There are now (\\d+) coins in it. You need to "
              + "have at least \\d+ coins in your coffer to participate.");

  BountyHunterCoffer(DudeWheresMyStuffPlugin plugin) {
    super(CoinsStorageType.BOUNTY_HUNTER, plugin);
  }

  public boolean updateFromMain() {
    Widget widget = plugin.getClient().getWidget(219, 1);
    if (widget == null) {
      return false;
    }

    Widget textWidget = widget.getChild(0);
    if (textWidget == null) {
      return false;
    }
    if (!textWidget.getText().startsWith("Current coffer: ")) {
      return false;
    }

    int parsedCoins = NumberUtils.toInt(textWidget.getText().replaceAll("\\D+", ""), 0);
    this.getCoinStack().setQuantity(parsedCoins);
    this.lastUpdated = System.currentTimeMillis();
    return true;
  }

  public boolean updateFromDepositWithdraw() {
    Widget widget = plugin.getClient().getWidget(193, 2);
    if (widget == null) {
      return false;
    }

    String text = widget.getText().replace(",", "").replace("<br>", " ");
    Matcher matcher = depositWithdrawPattern.matcher(text);
    if (!matcher.matches()) {
      return false;
    }

    int parsedCoins = NumberUtils.toInt(matcher.group(1), 0);
    this.getCoinStack().setQuantity(parsedCoins);
    this.lastUpdated = System.currentTimeMillis();
    return true;
  }

  @Override
  public boolean onGameTick() {
    return updateFromMain() || updateFromDepositWithdraw();
  }
}
