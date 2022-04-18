package dev.thource.runelite.dudewheresmystuff;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;

@ConfigGroup("dudewheresmystuff")
public interface DudeWheresMyStuffConfig extends Config {
    String CONFIG_GROUP = "dudewheresmystuff";
    String COINS = "coins";
    String CARRYABLES = "carryables";
    String MINIGAMES = "minigames";
}
