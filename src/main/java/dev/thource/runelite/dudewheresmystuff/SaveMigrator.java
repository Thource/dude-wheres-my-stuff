package dev.thource.runelite.dudewheresmystuff;

import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageType;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageType;
import dev.thource.runelite.dudewheresmystuff.death.DeathbankType;
import dev.thource.runelite.dudewheresmystuff.minigames.MinigamesStorageType;
import dev.thource.runelite.dudewheresmystuff.playerownedhouse.Menagerie;
import dev.thource.runelite.dudewheresmystuff.playerownedhouse.PlayerOwnedHouseStorageType;
import dev.thource.runelite.dudewheresmystuff.stash.StashUnit;
import dev.thource.runelite.dudewheresmystuff.world.Leprechaun;
import dev.thource.runelite.dudewheresmystuff.world.WorldStorageType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.client.config.ConfigManager;

/** SaveMigrator converts the player's data from the v1 format to the v2 format. */
@Slf4j
public class SaveMigrator {

  private static final String PREFIX_COINS = "coins.";
  private static final String PREFIX_CARRYABLE = "carryable.";
  private static final String PREFIX_POH = "poh.";
  private static final String PREFIX_MINIGAMES = "minigames.";
  private static final String PREFIX_WORLD = "world.";

  private final ConfigManager configManager;
  private final String rsProfileKey;

  public SaveMigrator(ConfigManager configManager, String rsProfileKey) {
    this.configManager = configManager;
    this.rsProfileKey = rsProfileKey;
  }

  void migrate() {
    log.info("Migrating data for profile: " + rsProfileKey);

    Map<String, String> map = new HashMap<>();
    migrateCoins(map);
    migrateCarryables(map);
    migrateDeaths(map);
    migrateMinigames(map);
    migratePlayerOwnedHouse(map);
    migrateStashes(map);
    migrateWorld(map);

    map.forEach((key, value) -> configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP,
        rsProfileKey, key, value));
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, rsProfileKey,
        "saveMigrated", true);
    log.info("Profile data migrated!");
  }

  private void migrateCoins(Map<String, String> map) {
    stripLastUpdated(map, PREFIX_COINS + CoinsStorageType.NMZ.getConfigKey());
    stripLastUpdated(map, PREFIX_COINS + CoinsStorageType.LMS.getConfigKey());
    stripLastUpdated(map, PREFIX_COINS + CoinsStorageType.BF.getConfigKey());
    stripLastUpdated(map, PREFIX_COINS + CoinsStorageType.INVENTORY.getConfigKey());
  }

  private void migrateCarryables(Map<String, String> map) {
    simpleItemsMigration(map, PREFIX_CARRYABLE + CarryableStorageType.INVENTORY.getConfigKey(),
        true);
    simpleItemsMigration(map, PREFIX_CARRYABLE + CarryableStorageType.EQUIPMENT.getConfigKey(),
        true);
    simpleItemsMigration(map, PREFIX_CARRYABLE + CarryableStorageType.LOOTING_BAG.getConfigKey(),
        false);
    simpleItemsMigration(map, PREFIX_CARRYABLE + CarryableStorageType.SEED_BOX.getConfigKey(),
        false);
    simpleItemsMigration(map, PREFIX_CARRYABLE + CarryableStorageType.RUNE_POUCH.getConfigKey(),
        true);
    staticItemsMigration(map,
        PREFIX_CARRYABLE + CarryableStorageType.BOTTOMLESS_BUCKET.getConfigKey());
    staticItemsMigration(map, PREFIX_CARRYABLE + CarryableStorageType.PLANK_SACK.getConfigKey()
    );
  }

  private void migrateDeaths(Map<String, String> map) {
    migrateDeathpiles(map);
    migrateDeathbanks(map);
  }

  private void migrateDeathpiles(Map<String, String> map) {
    String oldValue = configManager.getConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        rsProfileKey,
        "death.deathpiles"
    );
    if (oldValue == null) {
      return;
    }

    for (String deathpileData : oldValue.split("\\$")) {
      String[] deathpileDatas = deathpileData.split(";");
      if (deathpileDatas.length < 3) {
        continue;
      }

      UUID uuid = UUID.randomUUID();

      map.put(
          "death.deathpile." + uuid,
          deathpileDatas[2].replace(",", "x").replace("=", ",") + ";"
              + uuid + ";"
              + deathpileDatas[1] + ";"
              + "true;"
              + deathpileDatas[0]
      );
    }
  }

  private void migrateDeathbanks(Map<String, String> map) {
    String currentDeathbankData = configManager.getConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        rsProfileKey,
        "death.deathbank"
    );
    if (currentDeathbankData != null) {
      String[] deathbankDatas = currentDeathbankData.split(";");
      if (deathbankDatas.length >= 4) {
        UUID uuid = UUID.randomUUID();

        map.put(
            "death.deathbank." + uuid,
            deathbankDatas[2] + ";"
                + deathbankDatas[3].replace(",", "x").replace("=", ",") + ";"
                + uuid + ";"
                + deathbankDatas[1] + ";"
                + "-1;"
                + getDeathbankType(deathbankDatas[0])
        );
      }
    }

    String lostDeathbanksData = configManager.getConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        rsProfileKey,
        "death.lostdeathbanks"
    );

    if (lostDeathbanksData != null) {
      for (String lostDeathbankData : lostDeathbanksData.split("\\$")) {
        String[] deathbankDatas = lostDeathbankData.split(";");
        if (deathbankDatas.length >= 3) {
          UUID uuid = UUID.randomUUID();

          map.put(
              "death.deathbank." + uuid,
              deathbankDatas[1] + ";"
                  + deathbankDatas[2].replace(",", "x").replace("=", ",") + ";"
                  + uuid + ";"
                  + "false;"
                  + deathbankDatas[1] + ";"
                  + getDeathbankType(deathbankDatas[0])
          );
        }
      }
    }
  }

  private void migrateMinigames(Map<String, String> map) {
    minigamePointsMigration(map,
        PREFIX_MINIGAMES + MinigamesStorageType.MAGE_TRAINING_ARENA.getConfigKey());
    minigamePointsMigration(map, PREFIX_MINIGAMES + MinigamesStorageType.TITHE_FARM.getConfigKey());
    minigamePointsMigration(map,
        PREFIX_MINIGAMES + MinigamesStorageType.NIGHTMARE_ZONE.getConfigKey());
    minigamePointsMigration(map,
        PREFIX_MINIGAMES + MinigamesStorageType.BARBARIAN_ASSAULT.getConfigKey());
    minigamePointsMigration(map,
        PREFIX_MINIGAMES + MinigamesStorageType.GUARDIANS_OF_THE_RIFT.getConfigKey());
  }

  private void migratePlayerOwnedHouse(Map<String, String> map) {
    simpleItemsMigration(map, PREFIX_POH + PlayerOwnedHouseStorageType.ARMOUR_CASE.getConfigKey(),
        false);
    simpleItemsMigration(map, PREFIX_POH + PlayerOwnedHouseStorageType.CAPE_RACK.getConfigKey(),
        false);
    simpleItemsMigration(map,
        PREFIX_POH + PlayerOwnedHouseStorageType.FANCY_DRESS_BOX.getConfigKey(), false);
    simpleItemsMigration(map,
        PREFIX_POH + PlayerOwnedHouseStorageType.MAGIC_WARDROBE.getConfigKey(), false);
    simpleItemsMigration(map,
        PREFIX_POH + PlayerOwnedHouseStorageType.TREASURE_CHEST_BEGINNER.getConfigKey(), false);
    simpleItemsMigration(map,
        PREFIX_POH + PlayerOwnedHouseStorageType.TREASURE_CHEST_EASY.getConfigKey(), false);
    simpleItemsMigration(map,
        PREFIX_POH + PlayerOwnedHouseStorageType.TREASURE_CHEST_MEDIUM.getConfigKey(), false);
    simpleItemsMigration(map,
        PREFIX_POH + PlayerOwnedHouseStorageType.TREASURE_CHEST_HARD.getConfigKey(), false);
    simpleItemsMigration(map,
        PREFIX_POH + PlayerOwnedHouseStorageType.TREASURE_CHEST_ELITE.getConfigKey(), false);
    simpleItemsMigration(map,
        PREFIX_POH + PlayerOwnedHouseStorageType.TREASURE_CHEST_MASTER.getConfigKey(), false);

    simpleItemsMigration(map, PREFIX_POH + PlayerOwnedHouseStorageType.MENAGERIE.getConfigKey(),
        false);
    String menagerieData = map.get(
        PREFIX_POH + PlayerOwnedHouseStorageType.MENAGERIE.getConfigKey());
    if (menagerieData == null) {
      return;
    }

    String[] menagerieDataSplit = menagerieData.split(";");
    if (menagerieDataSplit.length < 3) {
      return;
    }

    int petBits1 = 0;
    int petBits2 = 0;
    for (String itemStackData : menagerieDataSplit[2].split(",")) {
      int itemId = Integer.parseInt(itemStackData.split("x")[0]);

      int bitId = 0;
      for (List<Integer> itemIds : Menagerie.VARPLAYER_BITS_TO_ITEM_IDS_LIST) {
        int itemIndex = itemIds.indexOf(itemId);

        if (itemIndex != -1) {
          if (bitId == 0) {
            petBits1 |= 1 << itemIndex;
          } else {
            petBits2 |= 1 << itemIndex;
          }

          break;
        }

        bitId += 1;
      }
    }

    map.put(
        PREFIX_POH + PlayerOwnedHouseStorageType.MENAGERIE.getConfigKey(),
        menagerieDataSplit[0] + ";" + menagerieDataSplit[1] + ";" + petBits1 + ";" + petBits2
    );
  }

  private void migrateStashes(Map<String, String> map) {
    for (StashUnit stashUnit : StashUnit.values()) {
      simpleItemsMigration(map, "stash." + stashUnit.getStashUnitData().getObjectId(), false);
    }
  }

  private void migrateWorld(Map<String, String> map) {
    simpleItemsMigration(map, PREFIX_WORLD + WorldStorageType.LOG_STORAGE.getConfigKey(), false);
    simpleItemsMigration(map, PREFIX_WORLD + WorldStorageType.BLAST_FURNACE.getConfigKey(), false);
    simpleItemsMigration(map, PREFIX_WORLD + WorldStorageType.BANK.getConfigKey(), false);
    simpleItemsMigration(map, PREFIX_WORLD + WorldStorageType.GROUP_STORAGE.getConfigKey(), false);
    simpleItemsMigration(map, PREFIX_WORLD + WorldStorageType.LEPRECHAUN.getConfigKey(), true);

    String leprechaunData = map.get(PREFIX_WORLD + WorldStorageType.LEPRECHAUN.getConfigKey());
    if (leprechaunData == null) {
      return;
    }

    List<Integer> itemIds = Arrays.stream(leprechaunData.split(","))
        .map(is -> Integer.parseInt(is.split("x")[0])).collect(
            Collectors.toList());
    List<Long> itemQuantities = Arrays.stream(leprechaunData.split(","))
        .map(is -> Long.parseLong(is.split("x")[1])).collect(
            Collectors.toList());

    int wateringCanIndex = 0;
    if (itemQuantities.get(4) > 0) {
      wateringCanIndex = (Arrays.stream(Leprechaun.getWateringCanIds()).boxed()
          .collect(Collectors.toList()).indexOf(itemIds.get(4)));
    }
    map.put(
        PREFIX_WORLD + WorldStorageType.LEPRECHAUN.getConfigKey(),
        itemQuantities.stream().map(Object::toString).collect(Collectors.joining(","))
            + ";"
            + (itemIds.get(3) == ItemID.MAGIC_SECATEURS ? "1" : "0") + ";"
            + wateringCanIndex
    );
  }

  private String stripLastUpdated(String data) {
    return data.substring(data.indexOf(";") + 1);
  }

  private void stripLastUpdated(Map<String, String> map, String configKey) {
    String oldValue = configManager.getConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        rsProfileKey,
        configKey
    );
    if (oldValue == null) {
      return;
    }

    map.put(configKey, stripLastUpdated(oldValue));
  }

  private void simpleItemsMigration(Map<String, String> map, String configKey,
      boolean removeLastUpdated) {
    String oldValue = configManager.getConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        rsProfileKey,
        configKey
    );
    if (oldValue == null) {
      return;
    }

    if (removeLastUpdated) {
      oldValue = stripLastUpdated(oldValue);
    }

    map.put(configKey, oldValue.replace(",", "x").replace("=", ","));
  }

  private void staticItemsMigration(Map<String, String> map, String configKey) {
    String oldValue = configManager.getConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        rsProfileKey,
        configKey
    );
    if (oldValue == null) {
      return;
    }

    String[] parts = oldValue.split(";", 2);
    map.put(configKey, parts[0] + ";" + parts[1].replace(";", ","));
  }

  private void minigamePointsMigration(Map<String, String> map, String configKey) {
    String oldValue = configManager.getConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        rsProfileKey,
        configKey
    );
    if (oldValue == null) {
      return;
    }

    map.put(configKey, oldValue.replace("=", ","));
  }

  private DeathbankType getDeathbankType(String deathbankData) {
    switch (deathbankData) {
      case "hydra":
        return DeathbankType.HYDRA;
      case "guardians":
        return DeathbankType.GUARDIANS;
      case "sepulchre":
        return DeathbankType.SEPULCHRE;
      case "hespori":
        return DeathbankType.HESPORI;
      case "mimic":
        return DeathbankType.MIMIC;
      case "nex":
        return DeathbankType.NEX;
      case "nightmare":
        return DeathbankType.NIGHTMARE;
      case "phosani":
        return DeathbankType.PHOSANI;
      case "tob":
        return DeathbankType.TOB;
      case "volcanicmine":
        return DeathbankType.VOLCANIC_MINE;
      case "vorkath":
        return DeathbankType.VORKATH;
      case "zulrah":
        return DeathbankType.ZULRAH;
      case "questds2":
        return DeathbankType.QUEST_DS2;
      case "questatoh":
        return DeathbankType.QUEST_ATOH;
      case "questsotf":
        return DeathbankType.QUEST_SOTF;
      case "questsote":
        return DeathbankType.QUEST_SOTE;
      case "questtfe":
        return DeathbankType.QUEST_TFE;
      case "unknown":
      default:
        return DeathbankType.UNKNOWN;
    }
  }
}
