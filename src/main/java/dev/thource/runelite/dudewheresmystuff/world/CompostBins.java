package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.StoragePanel;
import dev.thource.runelite.dudewheresmystuff.Var;
import java.util.HashMap;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

public class CompostBins extends WorldStorage {
  @RequiredArgsConstructor
  @Getter
  private static class CompostBinData {
    private final String areaName;
    private final boolean bigCompost;
    private final int varbitId;
    @Setter private ItemStack compostStack;
    @Setter private ItemStack superCompostStack;
    @Setter private ItemStack ultraCompostStack;
    @Setter private ItemStack rottenTomatoStack;

    boolean setStackQuantities(int binValue) {
      final var oldCompostQuantity = compostStack.getQuantity();
      final var oldSuperCompostQuantity = superCompostStack.getQuantity();
      final var oldUltraCompostQuantity = ultraCompostStack.getQuantity();
      final var oldRottenTomatoQuantity = rottenTomatoStack.getQuantity();
      compostStack.setQuantity(0);
      superCompostStack.setQuantity(0);
      ultraCompostStack.setQuantity(0);
      rottenTomatoStack.setQuantity(0);

      if (bigCompost) {
        if (binValue >= 16 && binValue <= 30) {
          compostStack.setQuantity(binValue - 15);
        } else if (binValue >= 48 && binValue <= 62) {
          superCompostStack.setQuantity(binValue - 47);
        } else if (binValue >= 78 && binValue <= 92) {
          compostStack.setQuantity(15 + binValue - 77);
        } else if (binValue == 93) {
          compostStack.setQuantity(30);
        } else if (binValue == 99) {
          superCompostStack.setQuantity(30);
        } else if (binValue >= 100 && binValue <= 114) {
          superCompostStack.setQuantity(15 + binValue - 99);
        } else if (binValue >= 144 && binValue <= 158) {
          rottenTomatoStack.setQuantity(binValue - 143);
        } else if (binValue >= 176 && binValue <= 205) {
          ultraCompostStack.setQuantity(binValue - 175);
        } else if (binValue >= 207 && binValue <= 221) {
          rottenTomatoStack.setQuantity(15 + binValue - 206);
        } else if (binValue == 222) {
          rottenTomatoStack.setQuantity(30);
        }
      } else {
        if (binValue >= 16 && binValue <= 30) {
          compostStack.setQuantity(binValue - 15);
        } else if (binValue >= 48 && binValue <= 62) {
          superCompostStack.setQuantity(binValue - 47);
        } else if (binValue == 94) {
          compostStack.setQuantity(15);
        } else if (binValue == 126) {
          superCompostStack.setQuantity(15);
        } else if (binValue >= 144 && binValue <= 158) {
          rottenTomatoStack.setQuantity(binValue - 143);
        } else if (binValue == 160) {
          rottenTomatoStack.setQuantity(15);
        } else if (binValue >= 176 && binValue <= 190) {
          ultraCompostStack.setQuantity(binValue - 175);
        }
      }

      return oldCompostQuantity != compostStack.getQuantity()
          || oldSuperCompostQuantity != superCompostStack.getQuantity()
          || oldUltraCompostQuantity != ultraCompostStack.getQuantity()
          || oldRottenTomatoQuantity != rottenTomatoStack.getQuantity();
    }
  }

  private final HashMap<Integer, CompostBinData> regionBinMap = new HashMap<>();

  private int lastRegionId = -1;

  public CompostBins(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.COMPOST_BINS, plugin);

    hasStaticItems = true;

    addBin(new CompostBinData("Ardougne", false, VarbitID.FARMING_TRANSMIT_E), 10548);
    addBin(new CompostBinData("Catherby", false, VarbitID.FARMING_TRANSMIT_E), 11062);
    addBin(new CompostBinData("Falador", false, VarbitID.FARMING_TRANSMIT_E), 12083);
    addBin(new CompostBinData("Kourend", false, VarbitID.FARMING_TRANSMIT_E), 6967, 6711);
    addBin(new CompostBinData("Morytania", false, VarbitID.FARMING_TRANSMIT_E), 14391, 14390);
    addBin(
        new CompostBinData("Farming Guild", true, VarbitID.FARMING_TRANSMIT_N),
        4922,
        5177,
        5178,
        5179,
        4921,
        4923,
        4665,
        4666,
        4667);
    addBin(
        new CompostBinData("Prifddinas", false, VarbitID.FARMING_TRANSMIT_D),
        13151,
        12895,
        12894,
        13150,
        /* Underground */ 12994,
        12993,
        12737,
        12738,
        12126,
        12127,
        13250);
  }

  private void addBin(CompostBinData compostBinData, int... regionIds) {
    var compostStack = new ItemStack(ItemID.BUCKET_COMPOST, plugin);
    items.add(compostStack);
    var superCompostStack = new ItemStack(ItemID.BUCKET_SUPERCOMPOST, plugin);
    items.add(superCompostStack);
    var ultraCompostStack = new ItemStack(ItemID.BUCKET_ULTRACOMPOST, plugin);
    items.add(ultraCompostStack);
    var rottenTomatoStack = new ItemStack(ItemID.ROTTEN_TOMATO, plugin);
    items.add(rottenTomatoStack);

    compostBinData.setCompostStack(compostStack);
    compostBinData.setSuperCompostStack(superCompostStack);
    compostBinData.setUltraCompostStack(ultraCompostStack);
    compostBinData.setRottenTomatoStack(rottenTomatoStack);

    for (var regionId : regionIds) {
      regionBinMap.put(regionId, compostBinData);
    }

    plugin
        .getClientThread()
        .invokeLater(
            () -> {
              if (Objects.equals(compostStack.getName(), "Loading")) {
                return false;
              }

              compostStack.setName(
                  compostStack.getName() + " (" + compostBinData.getAreaName() + ")");
              superCompostStack.setName(
                  superCompostStack.getName() + " (" + compostBinData.getAreaName() + ")");
              ultraCompostStack.setName(
                  ultraCompostStack.getName() + " (" + compostBinData.getAreaName() + ")");
              rottenTomatoStack.setName(
                  rottenTomatoStack.getName() + " (" + compostBinData.getAreaName() + ")");

              return true;
            });
  }

  @Override
  public boolean onGameTick() {
    var client = plugin.getClient();
    var location = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());

    if (location.getRegionID() == lastRegionId) {
      return false;
    }

    lastRegionId = location.getRegionID();

    var bin = regionBinMap.get(location.getRegionID());
    if (bin != null) {
      var compostValue = client.getVarbitValue(bin.getVarbitId());
      if (bin.setStackQuantities(compostValue)) {
        updateLastUpdated();
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean onVarbitChanged(VarbitChanged varbitChanged) {
    if (varbitChanged.getVarbitId() != -999
        && varbitChanged.getVarbitId() != VarbitID.FARMING_TRANSMIT_E
        && varbitChanged.getVarbitId() != VarbitID.FARMING_TRANSMIT_N
        && varbitChanged.getVarbitId() != VarbitID.FARMING_TRANSMIT_D) {
      return false;
    }

    var client = plugin.getClient();
    var location = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
    var bin = regionBinMap.get(location.getRegionID());
    if (bin != null) {
      var compostValue = Var.bit(varbitChanged, bin.getVarbitId()).getValue(client);
      if (bin.setStackQuantities(compostValue)) {
        updateLastUpdated();
        return true;
      }
    }

    return true;
  }

  @Override
  public void reset() {
    super.reset();

    lastRegionId = -1;
  }

  @Override
  protected void createStoragePanel(StorageManager<?, ?> storageManager) {
    storagePanel = new StoragePanel(plugin, this, true, false, false);

    createComponentPopupMenu(storageManager);
  }
}
