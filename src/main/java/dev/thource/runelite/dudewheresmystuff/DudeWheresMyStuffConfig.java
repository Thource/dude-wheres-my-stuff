package dev.thource.runelite.dudewheresmystuff;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

/** DudeWheresMyStuffConfig manages the config for the plugin. */
@SuppressWarnings("SameReturnValue")
@ConfigGroup("dudewheresmystuff")
public interface DudeWheresMyStuffConfig extends Config {

  @ConfigSection(
      name = "Deathpiles Options",
      description = "Settings deathpiles",
      position = 100
  )
  String deathpileOptionsSection = "Deathpiles Options";

  String CONFIG_GROUP = "dudewheresmystuff";

  @ConfigItem(
      keyName = "showEmptyStorages",
      name = "Show empty storages",
      description = "Whether storages with no items should be shown")
  default boolean showEmptyStorages() {
    return true;
  }

  @ConfigItem(
      keyName = "sidebarIcon",
      name = "Sidebar icon",
      description = "Which icon to display in the RuneLite sidebar")
  default SidebarIcon sidebarIcon() {
    return SidebarIcon.DEFAULT;
  }

  @ConfigItem(
      keyName = "deathpilesUseAccountPlayTime",
      name = "Cross-client deathpile tracking",
      description = "When enabled, deathpile expiry will be based on account play time, this means "
          + "that if you play for 5 minutes on another client (like mobile) your deathpile timer "
          + "will still be accurate.<br><br>Using this option requires you to set your quest tab "
          + "to the \"Character summary\" tab (brown star icon) once per login. With this option "
          + "enabled, an infobox will be displayed prompting you to swap to the \"Character "
          + "summary\" tab when the plugin doesn't know your play time.<br><br>If the plugin "
          + "doesn't know your play time at the time of your death, the deathpile will default to "
          + "non cross-client tracking mode.",
      section = deathpileOptionsSection,
      position = 101
  )

  default boolean deathpilesUseAccountPlayTime() {
    return false;
  }

  @ConfigItem(
      keyName = "deathpileInfoBox",
      name = "Show infoboxes for deathpiles",
      description = "When enabled, infoboxes will be displayed while you have active deathpiles.",
      section = deathpileOptionsSection,
      position = 102
  )

  default boolean deathpileInfoBox() {
    return true;
  }
  @ConfigItem(
      keyName = "deathbankInfoBox",
      name = "Show infobox for deathbank",
      description = "When enabled, an infobox will be displayed while you have an active "
          + "deathbank.")
  default boolean deathbankInfoBox() {
    return true;
  }

  @ConfigItem(
      keyName = "csvCombineItems",
      name = "Combine items in CSV",
      description = "When enabled, items from different storages will be combined into a single "
          + "row in the exported CSV file. This means that the CSV won't contain which item is in "
          + "which storage.")
  default boolean csvCombineItems() {
    return false;
  }

  @Range(
      min = 1,
      max = 59
  )
  @ConfigItem(
      keyName = "deathpileContingencyMinutes",
      name = "Deathpile contingency (minutes)",
      description = "This amount of minutes is removed from the deathpile timer. If set to 15, any "
          + "new deathpiles will start with 45 minutes until expiry.",
      section = deathpileOptionsSection,
      position = 102
  )
  default int deathpileContingencyMinutes() {
    return 1;
  }

  @ConfigItem(
      keyName = "storedItemCountTooltip",
      name = "Item Count Tooltip Mode",
      description = "Adds a tooltip when you hover items that tells you how many of those item you "
          + "have stored. \"Simple\" mode shows a sum, \"Detailed\" mode tells you where they are "
          + "stored."
  )
  default StoredItemCountTooltipMode storedItemCountTooltip() {
    return StoredItemCountTooltipMode.OFF;
  }

  @ConfigItem(
      keyName = "storedItemCountTooltipKeybind",
      name = "Item Count Tooltip Keybind",
      description = "Hold this key down to display the item count tooltip."
  )
  default Keybind storedItemCountTooltipKeybind() {
    return Keybind.NOT_SET;
  }

  @ConfigItem(
      keyName = "itemSortMode",
      name = "Item Sort Mode",
      description = "Which mode to use when sorting items",
      hidden = true)
  default ItemSortMode itemSortMode() {
    return ItemSortMode.UNSORTED;
  }

  @ConfigItem(
      keyName = "warnDeathpileExpiring",
      name = "Deathpile expiring soon warning",
      description = "Displays a text warning on screen when a deathpile is expiring soon.",
      section = deathpileOptionsSection,
      position = 103
  )
  default boolean warnDeathPileExpiring() {return false;}

  @ConfigItem(
      keyName = "warnDeathpileExpiringTime",
      name = "Deathpile expiry warning time",
      description = "The remaining on your oldest death pile before you start getting alerted.",
      section = deathpileOptionsSection,
      position = 104
  )
  default int timeUntilDeathpileExpires() {return 5;}

  @ConfigItem(
      keyName = "warnDeathpileExpiringFontSize",
      name = "Deathpile expiry font size",
      description = "Font size for the deathpile expiry warning.",
      section = deathpileOptionsSection,
      position = 105
  )
  default int warnDeathpileExpiringFontSize() {return 32;}

  @ConfigItem(keyName = "itemSortMode", name = "", description = "", hidden = true)
  void setItemSortMode(ItemSortMode itemSortMode);
}
