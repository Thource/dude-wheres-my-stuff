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
