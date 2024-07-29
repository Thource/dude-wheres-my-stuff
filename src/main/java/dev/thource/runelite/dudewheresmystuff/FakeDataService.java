package dev.thource.runelite.dudewheresmystuff;

import dev.thource.runelite.dudewheresmystuff.playerownedhouse.PlayerOwnedHouseStorageType;
import dev.thource.runelite.dudewheresmystuff.stash.StashUnit;
import java.util.stream.Collectors;
import net.runelite.client.config.ConfigManager;

class FakeDataService {

  public static final String PROFILE = "rsprofile.ZZZ";

  private FakeDataService() {}

  static void createData(ConfigManager configManager) {
    // Wipe the fake profile data
    for (String configKey :
        configManager.getRSProfileConfigurationKeys(
            DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "")) {
      configManager.unsetConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, configKey);
    }

    // Create a fake profile, real profiles have 8 character keys, so this has no chance of being
    // a duplicate
    configManager.setConfiguration("rsprofile", PROFILE, "displayName", "Thource");
    configManager.setConfiguration("rsprofile", PROFILE, "type", "STANDARD");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "saveMigrated", true);
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "minutesPlayed", 600);
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "isMember", true);
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "accountType", 2); // uim

    createDeathData(configManager);
    createCoinsData(configManager);
    createCarryableData(configManager);
    createWorldData(configManager);
    createMinigamesData(configManager);
    createStashData(configManager);
    createPlayerOwnedHouseData(configManager);
  }

  private static void createPlayerOwnedHouseData(ConfigManager configManager) {
    for (PlayerOwnedHouseStorageType type : PlayerOwnedHouseStorageType.values()) {
      if (type.getStorableItemIds() == null) {
        if (type == PlayerOwnedHouseStorageType.MENAGERIE) {
          configManager.setConfiguration(
              DudeWheresMyStuffConfig.CONFIG_GROUP,
              PROFILE,
              "poh." + type.getConfigKey(),
              (System.currentTimeMillis() - (1000 * 60 * 4)) + ";6555x1;482534;2343");
        }
      } else {
        configManager.setConfiguration(
            DudeWheresMyStuffConfig.CONFIG_GROUP,
            PROFILE,
            "poh." + type.getConfigKey(),
            (System.currentTimeMillis() - (1000 * 60 * 4))
                + ";"
                + type.getStorableItemIds().stream()
                    .map(id -> id + "x" + 1)
                    .collect(Collectors.joining(",")));
      }
    }
  }

  private static void createStashData(ConfigManager configManager) {
    for (StashUnit stashUnit : StashUnit.values()) {
      StringBuilder itemsBuilder = new StringBuilder();
      for (int itemId : stashUnit.getDefaultItemIds()) {
        itemsBuilder.append(itemId).append("x1,");
      }

      configManager.setConfiguration(
          DudeWheresMyStuffConfig.CONFIG_GROUP,
          PROFILE,
          "stash." + stashUnit.getStashUnitData().getObjectId(),
          (System.currentTimeMillis() - (1000 * 60 * 9)) + ";" + itemsBuilder);
    }
  }

  private static void createDeathData(ConfigManager configManager) {
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "death.deathpile.5d7f983f-3501-4010-8522-a45c3173ffb3",
        "995x26000000,5295x185,22875x3,5296x278,5343x19,952x15,7409x1,13353x1;"
            + "5d7f983f-3501-4010-8522-a45c3173ffb3;3203,3824,0;true;0;"
            + (System.currentTimeMillis() - (1000 * 60 * 25)));
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "death.deathpile.5a5d74b6-d279-4fa9-95e7-ee0c14f957d2",
        "562x7756,556x12148,560x5336,554x10885,3031x3789,168x642,2459x936,2999x272;"
            + "5a5d74b6-d279-4fa9-95e7-ee0c14f957d2;2205,3212,0;false;500");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "death.deathpile.cebe31f5-0047-40c2-bddf-9f2f37a3664b",
        "1523,150x4838,142x1697,3031x3789,7937x144000,2362x1030,1618x155;"
            + "cebe31f5-0047-40c2-bddf-9f2f37a3664b;3222,3218,0;true;619");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "death.deathbank.9267fafd-517d-45db-8d88-ede39393f176",
        "1678391322175;4214x1,11802x1,;9267fafd-517d-45db-8d88-ede39393f176;true;-1;HESPORI");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "death.deathbank.b6dcb979-ad92-4641-8540-6efee365c629",
        "1675972962178;4214x1,19675x1,2434x1,19564x1,148x6582,160x3548,24482x1,5295x79,"
            + "5297x121,5296x588,5302x212,5298x92,5301x20,561x1135,560x4804,11865x1,6570x1,"
            + "6585x1,12508x1,12954x1,10388x1,7462x1,11773x1,12851x1;"
            + "b6dcb979-ad92-4641-8540-6efee365c629;false;"
            + (System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 9))
            + ";HESPORI");
  }

  private static void createCoinsData(ConfigManager configManager) {
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "coins.nightmarezone", "73000000");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "coins.lastmanstanding", "188979000");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "coins.servantsmoneybag",
        System.currentTimeMillis() - (1000 * 60 * 60 * 4) + ";737300");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "coins.blastfurnace", "173");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "coins.inventory", "26000000");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "coins.lootingbag",
        System.currentTimeMillis() - (1000 * 60 * 60 * 18) + ";666000");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "coins.grandexchange",
        System.currentTimeMillis() - (1000 * 60 * 60 * 50) + ";69420");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "coins.shilofurnace",
        System.currentTimeMillis() - (1000 * 60 * 30) + ";2020");
  }

  private static void createCarryableData(ConfigManager configManager) {
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "carryable.inventory",
        "2434x1,2434x1,-1x1,892x10593,143x1,-1x1,-1x1,9433x1,7937x144000,2362x1030,"
            + "1618x155,1392x470,1514x12499,441x8800,1620x300,568x2700,566x60,563x3439,565x1075,"
            + "555x6450,562x7756,556x12148,560x5336,554x10885,995x26000000,24482x1,12791x1,"
            + "11941x1");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "carryable.equipment",
        "-1x1,3749x1,-1x1,-1x1,6570x1,1708x1,892x255,-1x1,13576x1,4940x1,-1x1,-1x1,-1x1,"
            + "12502x1,-1x1,-1x1,7462x1,22951x1,6737x1");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "carryable.lootingbag",
        System.currentTimeMillis()
            - (1000 * 60 * 60 * 18)
            + ";12632x2091,162x1523,150x4838,142x1697,3031x3789,168x642,2459x936,2999x272,262x448,"
            + "445x2632,2354x1080,13391x294,4736x1,4728x1,4722x1,4716x1,810x19069,6914x1,11905x1,"
            + "20736x1,995x666000");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "carryable.seedbox",
        System.currentTimeMillis()
            - (1000 * 60 * 60 * 14)
            + ";5295x185,22875x3,5296x278,5300x35,5304x11");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "carryable.runepouch",
        "563x580,4696x853,554x429");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "carryable.bottomlessbucket",
        System.currentTimeMillis() - (1000 * 60 * 60 * 2) + ";0,0,1423");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "carryable.planksack",
        System.currentTimeMillis() - (1000 * 60 * 43) + ";0,0,0,21");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "carryable.boltpouch",
        "4740x301,21950x10000,8882x8080,9245x99");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "carryable.gnomishfirelighter",
        System.currentTimeMillis() - (1000 * 60 * 60 * 3) + ";0,0,730,0,0");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "carryable.masterscrollbook",
        "1,12,32,40,2,32,43,5,9,8,4,24,57,100,28");
  }

  private static void createWorldData(ConfigManager configManager) {
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "world.leprechaun",
        "10,19,15,1,1,5,8,1,10,65,17,647;1;10");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "world.blastfurnace",
        "0,0,0,4,0,0,0,0,0,0,0,0,0,0,22,0,0");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "world.logstorage",
        System.currentTimeMillis() - (1000 * 60 * 60 * 2) + ";10,12,8,5,24");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "world.fossilstorage",
        "10,4,5,6,4,3,2,34,23,12,5,4,6,45,3,43,23,21,12,4,6,4,11,16,1,2,7,9,4,8");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "world.vyrewell", "10000,3000000");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "world.annette", "82");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "world.elnock", "0,1,1,948");
  }

  private static void createMinigamesData(ConfigManager configManager) {
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "minigames.magetrainingarena",
        System.currentTimeMillis() - (1000 * 60 * 60 * 2) + ";84,30,1020,489");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "minigames.tithefarm", "843");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "minigames.lastmanstanding",
        System.currentTimeMillis() - (1000 * 60 * 60 * 5) + ";43");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "minigames.nightmarezone",
        "435645,12,8,120,30");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "minigames.barbarianassault",
        "194,294,40,64");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "minigames.guardiansoftherift",
        System.currentTimeMillis() - (1000 * 60 * 60) + ";1200,1200");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "minigames.mahoganyhomes",
        System.currentTimeMillis() - (1000 * 60 * 23) + ";782");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "minigames.slayer", "1043");
    configManager.setConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        PROFILE,
        "minigames.pestcontrol",
        System.currentTimeMillis() - (1000 * 60 * 4) + ";1050");
  }
}
