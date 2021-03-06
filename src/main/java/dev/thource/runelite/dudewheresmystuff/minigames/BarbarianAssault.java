package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.client.config.ConfigManager;
import org.apache.commons.lang3.math.NumberUtils;

/** BarbarianAssault is responsible for tracking the player's Barbarian Assault points. */
@Getter
public class BarbarianAssault extends MinigamesStorage {

  private final ItemStack attackerPoints =
      new ItemStack(ItemID.ATTACKER_ICON, "Attacker Points", 0, 0, 0, true);
  private final ItemStack collectorPoints =
      new ItemStack(ItemID.COLLECTOR_ICON, "Collector Points", 0, 0, 0, true);
  private final ItemStack defenderPoints =
      new ItemStack(ItemID.DEFENDER_ICON, "Defender Points", 0, 0, 0, true);
  private final ItemStack healerPoints =
      new ItemStack(ItemID.HEALER_ICON, "Healer Points", 0, 0, 0, true);

  private final Map<Integer, ItemStack> varbits = new HashMap<>();

  BarbarianAssault(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.BARBARIAN_ASSAULT, plugin);

    items.add(attackerPoints);
    items.add(collectorPoints);
    items.add(defenderPoints);
    items.add(healerPoints);

    varbits.put(4759, attackerPoints);
    varbits.put(4760, collectorPoints);
    varbits.put(4762, defenderPoints);
    varbits.put(4761, healerPoints);
  }

  @Override
  public boolean onVarbitChanged() {
    AtomicBoolean updated = new AtomicBoolean(false);

    varbits.forEach(
        (varbit, itemStack) -> {
          int newPoints = plugin.getClient().getVarbitValue(varbit);
          if (newPoints == itemStack.getQuantity()) {
            return;
          }

          itemStack.setQuantity(newPoints);
          updated.set(true);
        });

    return updated.get();
  }

  @Override
  public void save(ConfigManager configManager, String managerConfigKey) {
    String data =
        items.stream().map(item -> "" + item.getQuantity()).collect(Collectors.joining("="));

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

    String[] dataSplit = data.split("=");
    if (dataSplit.length != items.size()) {
      return;
    }

    for (int i = 0; i < items.size(); i++) {
      items.get(i).setQuantity(NumberUtils.toInt(dataSplit[i]));
    }
  }
}
