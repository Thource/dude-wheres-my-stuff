package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.Widget;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Sandstorm is responsible for tracking how many buckets and sand the player has stored in the
 * Sandstorm machine.
 */
public class Sandstorm extends WorldStorage {

  private final Pattern checkPattern =
      Pattern.compile(
          "I have (\\d+) of your buckets and "
              + "you've ground enough sandstone for (\\d+) buckets of sand.");

  private final Pattern sandstoneDepositPattern =
      Pattern.compile("sandstone (?:equivalent to|for) (\\d+|one) buckets? of sand");

  private final Pattern bucketDepositPattern =
      Pattern.compile("holding onto (\\d+|one) buckets? for ya");
  private final ItemStack buckets;
  private final ItemStack sand;
  private boolean justWithdrawn;

  protected Sandstorm(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.SANDSTORM, plugin);

    hasStaticItems = true;

    buckets = new ItemStack(ItemID.BUCKET, plugin);
    sand = new ItemStack(ItemID.SAND, plugin);

    items.add(buckets);
    items.add(sand);

    plugin.getClientThread().invokeLater(() -> items.forEach(ItemStack::stripPrices));
  }

  @Override
  public boolean onGameTick() {
    Widget widget = plugin.getClient().getWidget(231, 6);

    if (!justWithdrawn) {
      if (widget != null && widget.getText().startsWith("If ya need any more sand")) {
        justWithdrawn = true;

        Optional<ItemStack> addedBuckets =
            ItemContainerWatcher.getInventoryWatcher().getItemsAddedLastTick().stream()
                .filter(itemStack -> itemStack.getId() == 1784)
                .findFirst();

        if (addedBuckets.isPresent()) {
          buckets.setQuantity(buckets.getQuantity() - addedBuckets.get().getQuantity());
          sand.setQuantity(sand.getQuantity() - addedBuckets.get().getQuantity());
          lastUpdated = System.currentTimeMillis();

          return true;
        }
      }
    } else if (widget == null || !widget.getText().startsWith("If ya need any more sand")) {
      justWithdrawn = false;
    }

    return checkForCheck() || checkForSandstoneDeposit() || checkForBucketDeposit();
  }

  private Matcher checkWidget(Pattern pattern) {
    Widget widget = plugin.getClient().getWidget(231, 6);
    if (widget == null) {
      return null;
    }

    Matcher matcher = pattern.matcher(widget.getText().replace("<br>", " ").replace(",", ""));
    if (!matcher.find()) {
      return null;
    }

    return matcher;
  }

  private boolean checkForCheck() {
    Matcher matcher = checkWidget(checkPattern);
    if (matcher == null) {
      return false;
    }

    boolean updated = false;

    int newBuckets = NumberUtils.toInt(matcher.group(1), 0);
    if (newBuckets != buckets.getQuantity()) {
      buckets.setQuantity(newBuckets);
      updated = true;
    }

    int newSand = NumberUtils.toInt(matcher.group(2), 0);
    if (newSand != sand.getQuantity()) {
      sand.setQuantity(newSand);
      updated = true;
    }

    if (updated) {
      this.lastUpdated = System.currentTimeMillis();
    }

    return updated;
  }

  private boolean checkForSandstoneDeposit() {
    Matcher matcher = checkWidget(sandstoneDepositPattern);
    if (matcher == null) {
      return false;
    }

    String quantityString = matcher.group(1);
    int quantity =
        Objects.equals(quantityString, "one") ? 1 : NumberUtils.toInt(matcher.group(1), 0);
    if (sand.getQuantity() == quantity) {
      return false;
    }

    sand.setQuantity(quantity);
    this.lastUpdated = System.currentTimeMillis();
    return true;
  }

  private boolean checkForBucketDeposit() {
    Matcher matcher = checkWidget(bucketDepositPattern);
    if (matcher == null) {
      return false;
    }

    String quantityString = matcher.group(1);
    int quantity =
        Objects.equals(quantityString, "one") ? 1 : NumberUtils.toInt(matcher.group(1), 0);
    if (buckets.getQuantity() == quantity) {
      return false;
    }

    buckets.setQuantity(quantity);
    this.lastUpdated = System.currentTimeMillis();
    return true;
  }

  @Override
  public boolean isWithdrawable() {
    return false;
  }
}
