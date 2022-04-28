package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.MinigamesStorage;
import dev.thource.runelite.dudewheresmystuff.MinigamesStorageType;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import org.apache.commons.lang3.math.NumberUtils;

@Getter
public class LastManStanding extends MinigamesStorage {

  ItemStack points = new ItemStack(ItemID.SKULL, "Points", 0, 0, 0, true);

  Widget shopWidget = null;

  public LastManStanding(Client client, ItemManager itemManager) {
    super(MinigamesStorageType.LAST_MAN_STANDING, client, itemManager);

    items.add(points);
  }

  @Override
  public boolean onGameTick() {
    return updateFromWidgets();
  }

  @Override
  public boolean onWidgetLoaded(WidgetLoaded widgetLoaded) {
    if (client.getLocalPlayer() == null) {
      return false;
    }

    if (widgetLoaded.getGroupId() == 645) {
      shopWidget = client.getWidget(645, 0);
    }

    return updateFromWidgets();
  }

  @Override
  public boolean onWidgetClosed(WidgetClosed widgetClosed) {
    if (client.getLocalPlayer() == null) {
      return false;
    }

    if (widgetClosed.getGroupId() == 645) {
      shopWidget = null;
    }

    return false;
  }

  boolean updateFromWidgets() {
    if (shopWidget == null) {
      return false;
    }

    lastUpdated = System.currentTimeMillis();
    int newPoints = client.getVarpValue(261);
    if (newPoints == points.getQuantity()) {
      return !this.getType().isAutomatic();
    }

    points.setQuantity(newPoints);
    return true;
  }

  @Override
  public void save(ConfigManager configManager, String managerConfigKey) {
    String data = lastUpdated + ";"
        + points.getQuantity();

    configManager.setRSProfileConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        managerConfigKey + "." + type.getConfigKey(),
        data
    );
  }

  @Override
  public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
    String data = configManager.getConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        profileKey,
        managerConfigKey + "." + type.getConfigKey(),
        String.class
    );
    if (data == null) {
      return;
    }

    String[] dataSplit = data.split(";");
    if (dataSplit.length != 2) {
      return;
    }

    this.lastUpdated = NumberUtils.toLong(dataSplit[0], -1);

    points.setQuantity(NumberUtils.toInt(dataSplit[1], 0));
  }
}
