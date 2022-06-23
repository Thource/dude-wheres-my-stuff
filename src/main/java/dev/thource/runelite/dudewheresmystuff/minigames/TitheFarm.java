package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.client.config.ConfigManager;
import org.apache.commons.lang3.math.NumberUtils;

/** TitheFarm is responsible for tracking the player's Tithe Farm points. */
@Getter
public class TitheFarm extends MinigamesStorage {

  private final ItemStack points = new ItemStack(ItemID.GRICOLLERS_CAN, "Points", 0, 0, 0, true);

  TitheFarm(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.TITHE_FARM, plugin);

    items.add(points);
  }

  @Override
  public boolean onVarbitChanged() {
    int newPoints = plugin.getClient().getVarbitValue(Varbits.TITHE_FARM_POINTS);
    if (newPoints == points.getQuantity()) {
      return false;
    }

    points.setQuantity(newPoints);
    return true;
  }

  @Override
  public void save(ConfigManager configManager, String managerConfigKey) {
    String data = String.valueOf(points.getQuantity());

    configManager.setRSProfileConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, managerConfigKey + "." + type.getConfigKey(), data);
  }

  @Override
  public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
    String data =
        configManager.getConfiguration(
            DudeWheresMyStuffConfig.CONFIG_GROUP,
            profileKey,
            managerConfigKey + "." + type.getConfigKey(),
            String.class);
    if (data == null) {
      return;
    }

    points.setQuantity(NumberUtils.toInt(data, 0));
  }
}
