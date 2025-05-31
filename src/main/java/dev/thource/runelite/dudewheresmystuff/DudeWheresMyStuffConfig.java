package dev.thource.runelite.dudewheresmystuff;

import dev.thource.runelite.dudewheresmystuff.death.DeathpileColorScheme;
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

  String CONFIG_GROUP = "dudewheresmystuff";

  @ConfigSection(
      name = "Data Export",
      description = "Settings for data exports",
      position = 100)
  String DATA_EXPORT_OPTIONS = "Data Export";

  @ConfigSection(
      name = "Deathpiles / Graves",
      description = "Settings for deathpiles and graves",
      position = 100
  )
  String DEATHPILE_OPTIONS_SECTION = "Deathpiles / Graves";

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
      keyName = "exportCombineItems",
      name = "Combine items in Export",
      section = DATA_EXPORT_OPTIONS,
      position = 2,
      description =
          "When enabled, items from different storages will be combined into a single "
              + "row in the exported CSV file. This means that the CSV won't contain which item is in "
              + "which storage.")
  default boolean exportCombineItems() {
    return false;
  }

  @ConfigItem(
      keyName = "googleSpreadSheetId",
      name = "Google Spreadsheet ID",
      section = DATA_EXPORT_OPTIONS,
      position = 101,
      description =
          "Google spreadsheet ID if you have an existing sheet. The target sheet must have been created by the plugin. If you do not provide this, we will create one and populate this field.")
  default String googleSpreadSheetId() {
    return "";
  }

  @ConfigItem(keyName = "googleSpreadSheetId", name = "", description = "", hidden = true)
  void setGoogleSpreadSheetId(String spreadSheetId);

  @ConfigItem(
      keyName = "googleSpreadSheetUrl",
      name = "Google Spreadsheet URL",
      section = DATA_EXPORT_OPTIONS,
      position = 101,
      description = "Google spreadsheet URL to the sheet. Auto populated.")
  default String googleSpreadSheetUrl() {
    return "";
  }

  @ConfigItem(keyName = "googleSpreadSheetUrl", name = "", description = "", hidden = true)
  void setGoogleSpreadSheetUrl(String spreadSheetUrl);

  @ConfigSection(
      name = "Item Count Tooltip",
      description = "Settings for the item count tooltip",
      position = 100
  )
  String ITEM_COUNT_TOOLTIP_SECTION = "Item Count Tooltip";

  @ConfigItem(
      keyName = "storedItemCountTooltip",
      name = "Display mode",
      description = "Adds a tooltip when you hover items that tells you how many of those item you "
          + "have stored. \"Simple\" mode shows a sum, \"Detailed\" mode tells you where they are "
          + "stored.",
      section = ITEM_COUNT_TOOLTIP_SECTION)
  default StoredItemCountTooltipMode storedItemCountTooltip() {
    return StoredItemCountTooltipMode.OFF;
  }

  @ConfigItem(
      keyName = "storedItemCountTooltipKeybind",
      name = "Keybind",
      description = "Hold this key down to display the item count tooltip.",
      section = ITEM_COUNT_TOOLTIP_SECTION
  )
  default Keybind storedItemCountTooltipKeybind() {
    return Keybind.NOT_SET;
  }

  @ConfigItem(
      keyName = "storedItemCountInclude.carryable",
      name = "Include carry-able storages",
      description = "Whether carry-able storages should be included in the item count.",
      section = ITEM_COUNT_TOOLTIP_SECTION,
      position = 100
  )
  default boolean storedItemCountIncludeCarryable() {
    return true;
  }

  @ConfigItem(
      keyName = "storedItemCountInclude.coins",
      name = "Include coin storages",
      description = "Whether coin storages should be included in the item count.",
      section = ITEM_COUNT_TOOLTIP_SECTION,
      position = 101
  )
  default boolean storedItemCountIncludeCoins() {
    return true;
  }

  @ConfigItem(
      keyName = "storedItemCountInclude.death",
      name = "Include death storages",
      description = "Whether death storages should be included in the item count.",
      section = ITEM_COUNT_TOOLTIP_SECTION,
      position = 102
  )
  default boolean storedItemCountIncludeDeath() {
    return true;
  }

  @ConfigItem(
      keyName = "storedItemCountInclude.minigames",
      name = "Include minigame storages",
      description = "Whether minigame storages should be included in the item count.",
      section = ITEM_COUNT_TOOLTIP_SECTION,
      position = 103
  )
  default boolean storedItemCountIncludeMinigames() {
    return true;
  }

  @ConfigItem(
      keyName = "storedItemCountInclude.poh",
      name = "Include POH storages",
      description = "Whether POH storages should be included in the item count.",
      section = ITEM_COUNT_TOOLTIP_SECTION,
      position = 104
  )
  default boolean storedItemCountIncludePOH() {
    return true;
  }

  @ConfigItem(
      keyName = "storedItemCountInclude.stash",
      name = "Include stash storages",
      description = "Whether stash storages should be included in the item count.",
      section = ITEM_COUNT_TOOLTIP_SECTION,
      position = 105
  )
  default boolean storedItemCountIncludeStash() {
    return true;
  }

  @ConfigItem(
      keyName = "storedItemCountInclude.world",
      name = "Include world storages",
      description = "Whether world storages should be included in the item count.",
      section = ITEM_COUNT_TOOLTIP_SECTION,
      position = 106
  )
  default boolean storedItemCountIncludeWorld() {
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
      keyName = "showDeathStorageRiskWarning",
      name = "Display death storage risk warning",
      description = "When enabled, a banner is displayed at the top of the death storage tab, "
          + "warning against relying solely on the plugin for death storages."
  )
  default boolean showDeathStorageRiskWarning() {
    return true;
  }

  @ConfigItem(
      keyName = "deathpileColorScheme",
      name = "Color scheme",
      description = "Which color scheme should be used for deathpiles.",
      section = DEATHPILE_OPTIONS_SECTION,
      position = 100
  )
  default DeathpileColorScheme deathpileColorScheme() {
    return DeathpileColorScheme.FULL_COLOR;
  }

  @ConfigItem(
      keyName = "deathpilesUseAccountPlayTime",
      name = "Cross-client tracking",
      description = "When enabled, deathpile/grave expiry will be based on account play time, this "
          + "means that if you play for 5 minutes on another client (like mobile) your "
          + "deathpile/grave timer will still be accurate.<br><br>Using this option requires you "
          + "to set your quest tab to the \"Character summary\" tab (brown star icon) once per "
          + "login. With this option enabled, an infobox will be displayed prompting you to swap "
          + "to the \"Character summary\" tab when the plugin doesn't know your play time.<br><br>"
          + "If the plugin doesn't know your play time at the time of your death, the "
          + "deathpile/grave will default to non cross-client tracking mode.",
      section = DEATHPILE_OPTIONS_SECTION,
      position = 101
  )
  default boolean deathpilesUseAccountPlayTime() {
    return false;
  }

  @ConfigItem(
      keyName = "deathpileInfoBox",
      name = "Show infoboxes",
      description = "When enabled, infoboxes will be displayed while you have active deathpiles / "
          + "graves.",
      section = DEATHPILE_OPTIONS_SECTION,
      position = 102
  )
  default boolean deathpileInfoBox() {
    return true;
  }

  @Range(
      min = 1,
      max = 59
  )
  @ConfigItem(
      keyName = "deathpileContingencyMinutes",
      name = "Contingency (minutes)",
      description = "This amount of minutes is removed from the deathpile / grave timer. If set to "
          + "15, any new deathpiles / graves will start with 45 minutes until expiry.",
      section = DEATHPILE_OPTIONS_SECTION,
      position = 104
  )
  default int deathpileContingencyMinutes() {
    return 1;
  }

  @ConfigItem(
      keyName = "flashExpiringDeathpileInfoboxes",
      name = "Flash expiring infoboxes",
      description = "Flash the infoboxes of expiring deathpiles / graves red.",
      section = DEATHPILE_OPTIONS_SECTION,
      position = 105
  )
  default boolean flashExpiringDeathpileInfoboxes() {
    return true;
  }

  @ConfigItem(
      keyName = "flashExpiringDeathpileTiles",
      name = "Flash expiring tiles",
      description = "Flash the tile borders of expiring deathpiles / graves red.",
      section = DEATHPILE_OPTIONS_SECTION,
      position = 106
  )
  default boolean flashExpiringDeathpileTiles() {
    return true;
  }

  @ConfigItem(
      keyName = "showDeathpileExpiryText",
      name = "Show expiry text",
      description = "Show on-screen text when your oldest deathpile / grave is expiring.",
      section = DEATHPILE_OPTIONS_SECTION,
      position = 107
  )
  default boolean showDeathpileExpiryText() {
    return false;
  }

  @ConfigItem(
      keyName = "deathpileExpiryWarningTime",
      name = "Expiry warning time",
      description = "The minutes remaining on your oldest deathpile / grave before you start getting alerted.",
      section = DEATHPILE_OPTIONS_SECTION,
      position = 108
  )
  default int deathpileExpiryWarningTime() {
    return 5;
  }

  @ConfigItem(
      keyName = "deathpileExpiryWarningFontSize",
      name = "Expiry font size",
      description = "Font size for the deathpile / grave expiry text warning.",
      section = DEATHPILE_OPTIONS_SECTION,
      position = 109
  )
  default int deathpileExpiryWarningFontSize() {
    return 32;
  }

  @ConfigItem(
      keyName = "itemSortMode",
      name = "Item Sort Mode",
      description = "Which mode to use when sorting items",
      hidden = true)
  default ItemSortMode itemSortMode() {
    return ItemSortMode.UNSORTED;
  }

  @ConfigItem(keyName = "itemSortMode", name = "", description = "", hidden = true)
  void setItemSortMode(ItemSortMode itemSortMode);
}
