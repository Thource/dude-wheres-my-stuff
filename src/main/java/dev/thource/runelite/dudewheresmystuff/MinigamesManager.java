package dev.thource.runelite.dudewheresmystuff;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.thource.runelite.dudewheresmystuff.minigames.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

@Slf4j
@Singleton
public class MinigamesManager extends StorageManager<MinigameStorageType, MinigameStorage> {
    @Inject
    private MinigamesManager(Client client, ItemManager itemManager, ConfigManager configManager, DudeWheresMyStuffConfig config, Notifier notifier) {
        super(client, itemManager, configManager, config, notifier);

        storages.add(new MageTrainingArena(client, itemManager));
        storages.add(new TitheFarm(client, itemManager));
        storages.add(new LastManStanding(client, itemManager));
        storages.add(new BarbarianAssault(client, itemManager));
        storages.add(new NightmareZone(client, itemManager));
    }

    @Override
    public String getConfigKey() {
        return "minigames";
    }

    @Override
    public Tab getTab() {
        return Tab.MINIGAMES;
    }
}
