package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.client.config.ConfigManager;
import org.apache.commons.lang3.math.NumberUtils;

public class BlastFurnace extends WorldStorage {
  private final Map<Integer, ItemStack> varbits = new HashMap<>();

  protected BlastFurnace(WorldStorageType type, DudeWheresMyStuffPlugin plugin) {
    super(type, plugin);

    ItemStack copperOre = new ItemStack(ItemID.COPPER_ORE, plugin);
    ItemStack tinOre = new ItemStack(ItemID.TIN_ORE, plugin);
    ItemStack ironOre = new ItemStack(ItemID.IRON_ORE, plugin);
    ItemStack coal = new ItemStack(ItemID.COAL, plugin);
    ItemStack mithrilOre = new ItemStack(ItemID.MITHRIL_ORE, plugin);
    ItemStack adamantiteOre = new ItemStack(ItemID.ADAMANTITE_ORE, plugin);
    ItemStack runiteOre = new ItemStack(ItemID.RUNITE_ORE, plugin);
    ItemStack silverOre = new ItemStack(ItemID.SILVER_ORE, plugin);
    ItemStack goldOre = new ItemStack(ItemID.GOLD_ORE, plugin);
    ItemStack bronzeBar = new ItemStack(ItemID.BRONZE_BAR, plugin);
    ItemStack ironBar = new ItemStack(ItemID.IRON_BAR, plugin);
    ItemStack steelBar = new ItemStack(ItemID.STEEL_BAR, plugin);
    ItemStack mithrilBar = new ItemStack(ItemID.MITHRIL_BAR, plugin);
    ItemStack adamantiteBar = new ItemStack(ItemID.ADAMANTITE_BAR, plugin);
    ItemStack runiteBar = new ItemStack(ItemID.RUNITE_BAR, plugin);
    ItemStack silverBar = new ItemStack(ItemID.SILVER_BAR, plugin);
    ItemStack goldBar = new ItemStack(ItemID.GOLD_BAR, plugin);

    varbits.put(Varbits.BLAST_FURNACE_COPPER_ORE, copperOre);
    varbits.put(Varbits.BLAST_FURNACE_TIN_ORE, tinOre);
    varbits.put(Varbits.BLAST_FURNACE_IRON_ORE, ironOre);
    varbits.put(Varbits.BLAST_FURNACE_COAL, coal);
    varbits.put(Varbits.BLAST_FURNACE_MITHRIL_ORE, mithrilOre);
    varbits.put(Varbits.BLAST_FURNACE_ADAMANTITE_ORE, adamantiteOre);
    varbits.put(Varbits.BLAST_FURNACE_RUNITE_ORE, runiteOre);
    varbits.put(Varbits.BLAST_FURNACE_SILVER_ORE, silverOre);
    varbits.put(Varbits.BLAST_FURNACE_GOLD_ORE, goldOre);
    varbits.put(Varbits.BLAST_FURNACE_BRONZE_BAR, bronzeBar);
    varbits.put(Varbits.BLAST_FURNACE_IRON_BAR, ironBar);
    varbits.put(Varbits.BLAST_FURNACE_STEEL_BAR, steelBar);
    varbits.put(Varbits.BLAST_FURNACE_MITHRIL_BAR, mithrilBar);
    varbits.put(Varbits.BLAST_FURNACE_ADAMANTITE_BAR, adamantiteBar);
    varbits.put(Varbits.BLAST_FURNACE_RUNITE_BAR, runiteBar);
    varbits.put(Varbits.BLAST_FURNACE_SILVER_BAR, silverBar);
    varbits.put(Varbits.BLAST_FURNACE_GOLD_BAR, goldBar);

    items.addAll(varbits.values());
  }

  @Override
  public boolean onVarbitChanged() {
    boolean updated = false;

    for (Entry<Integer, ItemStack> entry : varbits.entrySet()) {
      Integer varbit = entry.getKey();
      ItemStack itemStack = entry.getValue();

      if (plugin.getClient().getVarbitValue(varbit) != itemStack.getQuantity()) {
        itemStack.setQuantity(plugin.getClient().getVarbitValue(varbit));
        updated = true;
      }
    }

    return updated;
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

  @Override
  public void reset() {
    for (ItemStack item : items) {
      item.setQuantity(0);
    }
    lastUpdated = -1;
    enable();
  }
}
