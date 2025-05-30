package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import net.runelite.api.gameval.InterfaceID.Objectbox;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;

/** Nest is responsible for tracking what the player has stored in the Nest (nice). */
public class Nest extends WorldStorage {
  protected Nest(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.NEST, plugin);
  }

  private int getConvertedItemId(int itemId) {
    switch (itemId) {
      case ItemID.TOA_LOOT_POO:
        return ItemID.VARLAMORE_NASTY_TOKEN_1;
      case ItemID.RAW_CHICKEN:
        return ItemID.COOKED_CHICKEN;
      case ItemID.GNOME_SPICE:
        return ItemID.IRON_PICKAXE;
      case ItemID.ROPE:
        return ItemID.LEATHER_GLOVES;
      case ItemID.DRAGON_CLAWS:
        return ItemID.EGG;
      case ItemID.RUNE_PLATELEGS:
        return ItemID.RUNITE_BAR;
      case ItemID.ADAMANT_SCIMITAR:
        return ItemID.CAKE;
      case ItemID.XBOWS_CROSSBOW_LIMBS_MITHRIL:
        return ItemID.XBOWS_CROSSBOW_MITHRIL;
      case ItemID.STEEL_DAGGER:
        return ItemID.SNELM_ROUND_YELLOW;
      case ItemID.KWUARMVIAL:
        return ItemID.WEAPON_POISON;
      default:
    }

    return itemId;
  }

  @Override
  public boolean onGameTick() {
    Widget textWidget = plugin.getClient().getWidget(Objectbox.TEXT);
    if (textWidget == null) {
      return false;
    }

    var isDeposit = textWidget.getText().equals("You place your item in the nest.");
    var isRetrieval = textWidget.getText().equals("You retrieve your item from the nest.");
    if (!isDeposit && !isRetrieval) {
      return false;
    }

    Widget itemWidget = plugin.getClient().getWidget(Objectbox.ITEM);
    if (itemWidget == null) {
      return false;
    }

    updateLastUpdated();

    var itemId = getConvertedItemId(itemWidget.getItemId());
    if (isDeposit) {
      if (items.isEmpty() || items.get(0).getId() != itemId) {
        items.clear();
        items.add(new ItemStack(itemId, 1, plugin));

        return true;
      }
    } else if (!items.isEmpty()) {
      items.clear();

      return true;
    }

    return false;
  }
}
