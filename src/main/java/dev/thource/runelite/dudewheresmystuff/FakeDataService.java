package dev.thource.runelite.dudewheresmystuff;

import net.runelite.api.vars.AccountType;
import net.runelite.client.config.ConfigManager;

class FakeDataService {

  private static final String PROFILE = "rsprofile.ZZZ";

  static void createData(ConfigManager configManager) {
    // Create a fake profile, real profiles have 8 character keys, so this has no chance of being a duplicate
    configManager.setConfiguration("rsprofile", PROFILE, "displayName", "Thource");

    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "minutesPlayed",
        600);
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "isMember", true);
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "accountType",
        AccountType.ULTIMATE_IRONMAN.ordinal());

    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "death.deathbank",
        "hespori;true;" + (System.currentTimeMillis() - (1000 * 60 * 14)) + ";4214,1=11802,1=");
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "death.lostdeathbanks",
        "hespori;" + (System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 28))
            + ";4214,1=19675,1=2434,1=19564,1=148,6582=160,3548=24482,1=5295,79=5297,121=5296,588=5302,212=5298,92=5301,20=561,1135=560,4804=11865,1=6570,1=6585,1=12508,1=12954,1=10388,1=7462,1=11773,1=12851,1");
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "death.deathpiles",
        "589;3222,3218,0;1523=150,4838=142,1697=3031,3789=7937,144000=2362,1030=1618,155$555;2205,3212,0;562,7756=556,12148=560,5336=554,10885=3031,3789=168,642=2459,936=2999,272$400;3203,3824,0;995,26000000=5295,185=22875,3=5296,278=5343,19=952,15=7409,1=13353,1");

    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "coins.nightmarezone", "-1;995,73000000");
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "coins.lastmanstanding", "-1;995,188979000");
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "coins.servantsmoneybag",
        System.currentTimeMillis() - (1000 * 60 * 60 * 4) + ";995,737300");
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "coins.blastfurnace", "-1;995,173");
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE, "coins.inventory",
        "-1;995,26000000");
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "coins.lootingbag", System.currentTimeMillis() - (1000 * 60 * 60 * 18) + ";995,666000");
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "coins.grandexchange", System.currentTimeMillis() - (1000 * 60 * 60 * 50) + ";995,69420");
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "coins.shilofurnace", System.currentTimeMillis() - (1000 * 60 * 30) + ";995,2020");

    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "carryable.inventory",
        "-1;2434,1=2434,1=-1,1=892,10593=143,1=-1,1=-1,1=9433,1=7937,144000=2362,1030=1618,155=1392,470=1514,12499=441,8800=1620,300=568,2700=566,60=563,3439=565,1075=555,6450=562,7756=556,12148=560,5336=554,10885=995,26000000=24482,1=12791,1=11941,1");
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "carryable.equipment",
        "-1;-1,1=3749,1=-1,1=-1,1=6570,1=1708,1=892,255=-1,1=13576,1=4940,1=-1,1=-1,1=-1,1=12502,1=-1,1=-1,1=7462,1=22951,1=6737,1");
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "carryable.lootingbag", System.currentTimeMillis() - (1000 * 60 * 60 * 18)
            + ";12632,2091=162,1523=150,4838=142,1697=3031,3789=168,642=2459,936=2999,272=262,448=445,2632=2354,1080=13391,294=4736,1=4728,1=4722,1=4716,1=810,19069=6914,1=11905,1=20736,1=995,666000");
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "carryable.seedbox", System.currentTimeMillis() - (1000 * 60 * 60 * 14)
            + ";5295,185=22875,3=5296,278=5300,35=5304,11");
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "carryable.runepouch", "-1;563,580=4696,853=554,429");

    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "world.leprechaun",
        "-1;5341,10=5343,19=952,15=7409,1=13353,1=5325,5=6036,8=1925,940=6032,65=6034,17=21483,647");

    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "minigames.magetrainingarena",
        System.currentTimeMillis() - (1000 * 60 * 60 * 2) + ";84=30=1020=489");
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "minigames.tithefarm", "843");
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "minigames.lastmanstanding", System.currentTimeMillis() - (1000 * 60 * 60 * 5) + ";43");
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "minigames.nightmarezone", "435645");
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, PROFILE,
        "minigames.barbarianassault", "194=294=40=64");
  }
}
