package dev.thource.runelite.dudewheresmystuff;

import java.awt.image.BufferedImage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.ImageUtil;

/** Represents which icon to show in the runelite sidebar. */
@Getter
@RequiredArgsConstructor
public enum SidebarIcon {
  DEFAULT(null),
  OVERVIEW(Tab.OVERVIEW),
  DEATH(Tab.DEATH),
  COINS(Tab.COINS),
  CARRYABLE_STORAGE(Tab.CARRYABLE_STORAGE),
  STASH_UNITS(Tab.STASH_UNITS),
  POH_STORAGE(Tab.POH_STORAGE),
  WORLD(Tab.WORLD),
  MINIGAMES(Tab.MINIGAMES);

  private final Tab tab;

  @Override
  public String toString() {
    if (tab == null) {
      return "Default";
    }

    return tab.getName();
  }

  /**
   * Gets the icon to be used in the sidebar.
   *
   * @param itemManager item manager
   * @return sidebar icon
   */
  public BufferedImage getIcon(ItemManager itemManager) {
    if (tab == null) {
      return ImageUtil.loadImageResource(getClass(), "icon.png");
    }

    return itemManager.getImage(tab.getItemId(), tab.getItemQuantity(), false);
  }
}
