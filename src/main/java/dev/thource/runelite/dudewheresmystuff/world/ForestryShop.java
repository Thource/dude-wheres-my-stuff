package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * ForestryShop is responsible for tracking how many logs the player has stored in the forestry
 * shop.
 */
public class ForestryShop extends WorldStorage {

  private final Pattern[] checkPatterns = {
      Pattern.compile("Oak\\s+logs:\\s+(\\d+)"),
      Pattern.compile("Willow\\s+logs:\\s+(\\d+)"),
      Pattern.compile("Yew\\s+logs:\\s+(\\d+)"),
      Pattern.compile("Maple\\s+logs:\\s+(\\d+)"),
      Pattern.compile("Magic\\s+logs:\\s+(\\d+)"),
      Pattern.compile("Teak\\s+logs:\\s+(\\d+)"),
      Pattern.compile("Mahogany\\s+logs:\\s+(\\d+)"),
      Pattern.compile("Redwood\\s+logs:\\s+(\\d+)"),
      Pattern.compile("Arctic\\s+pine\\s+logs:\\s+(\\d+)")
  };

  protected ForestryShop(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.FORESTRY_SHOP, plugin);

    hasStaticItems = true;

    items.add(new ItemStack(ItemID.OAK_LOGS, plugin));
    items.add(new ItemStack(ItemID.WILLOW_LOGS, plugin));
    items.add(new ItemStack(ItemID.YEW_LOGS, plugin));
    items.add(new ItemStack(ItemID.MAPLE_LOGS, plugin));
    items.add(new ItemStack(ItemID.MAGIC_LOGS, plugin));
    items.add(new ItemStack(ItemID.TEAK_LOGS, plugin));
    items.add(new ItemStack(ItemID.MAHOGANY_LOGS, plugin));
    items.add(new ItemStack(ItemID.REDWOOD_LOGS, plugin));
    items.add(new ItemStack(ItemID.ARCTIC_PINE_LOG, plugin));

    plugin.getClientThread().invokeLater(() -> items.forEach(ItemStack::stripPrices));
  }

  @Override
  public boolean onGameTick() {
    return checkForCheck();
  }

  private boolean checkForCheck() {
    Widget widget = plugin.getClient().getWidget(InterfaceID.Messagebox.TEXT);
    if (widget == null || !widget.getText().startsWith("Your log storage contains:")) {
      return false;
    }

    String widgetText = widget.getText().replace("<br>", " ").replace(",", "");

    boolean updated = false;
    for (int i = 0; i < checkPatterns.length; i++) {
      Pattern checkPattern = checkPatterns[i];
      Matcher matcher = checkPattern.matcher(widgetText);
      int quantity = 0;
      if (matcher.find()) {
        quantity = NumberUtils.toInt(matcher.group(1), 0);
      }

      ItemStack itemStack = items.get(i);
      if (itemStack.getQuantity() != quantity) {
        itemStack.setQuantity(quantity);
        updated = true;
      }
    }

    updateLastUpdated();

    return updated;
  }

  @Override
  public boolean isWithdrawable() {
    return false;
  }
}
