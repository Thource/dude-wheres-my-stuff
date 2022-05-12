package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import org.apache.commons.lang3.math.NumberUtils;

/** NightmareZone is responsible for tracking the player's Nightmare Zone points. */
@Getter
public class NightmareZone extends MinigamesStorage {

  private final ItemStack points = new ItemStack(ItemID.DREAM_POTION, "Points", 0, 0, 0, true);

  NightmareZone(Client client, ClientThread clientThread, ItemManager itemManager) {
    super(MinigamesStorageType.NIGHTMARE_ZONE, client, clientThread, itemManager);

    items.add(points);
  }

  @Override
  public boolean onVarbitChanged() {
    int newPoints =
        client.getVarbitValue(Varbits.NMZ_POINTS) + client.getVar(VarPlayer.NMZ_REWARD_POINTS);
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
