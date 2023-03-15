package dev.thource.runelite.dudewheresmystuff;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

/** DudeWheresMyStuffConfig manages the config for the plugin. */
@ConfigGroup("dudewheresmystuff")
public interface DudeWheresMyStuffConfig extends Config {

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
          + "non cross-client tracking mode.")
  default boolean deathpilesUseAccountPlayTime() {
    return false;
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
