package dev.thource.runelite.dudewheresmystuff;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@PluginDescriptor(
        name = "Dude, Where's My Stuff?"
)
public class DudeWheresMyStuffPlugin extends Plugin {
    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private Client client;

    @Inject
    private DudeWheresMyStuffConfig config;

    @Inject
    private CoinsManager coinsManager;

    @Inject
    private CarryableManager carryableManager;

    @Inject
    private MinigamesManager minigamesManager;

    @Inject
    private ConfigManager configManager;

    private final List<StorageManager<?, ?>> storageManagers = new ArrayList<>();

    private DudeWheresMyStuffPanel panel;

    private NavigationButton navButton;
    private boolean loggedIn;


    @Override
    protected void startUp() throws Exception {
        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");

        panel = injector.getInstance(DudeWheresMyStuffPanel.class);

        navButton = NavigationButton.builder()
                .tooltip("Dude, Where's My Stuff?")
                .icon(icon)
                .panel(panel)
                .priority(4)
                .build();

        clientToolbar.addNavigation(navButton);

        storageManagers.add(coinsManager);
        storageManagers.add(carryableManager);
        storageManagers.add(minigamesManager);
    }

    @Override
    protected void shutDown() throws Exception {
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    public void onActorDeath(ActorDeath actorDeath) {
        if (client.getLocalPlayer() == null) return;

        if (actorDeath.getActor() == client.getLocalPlayer()) {
            log.info("OH NO, YOU HAVE DIED!");
            HashTable<ItemContainer> itemContainers = client.getItemContainers();

            log.info(client.getLocalPlayer().getWorldLocation().toString());

            log.info("Items:");
            for (ItemContainer itemContainer : itemContainers) {
                Item[] items = itemContainer.getItems();
                if (items.length == 0) continue;

                log.info("  Container " + itemContainer.getId() + ":");
                for (Item item : items) {
                    log.info("    " + item.getId() + " x " + item.getQuantity());
                }
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN) {
            loggedIn = false;

            storageManagers.forEach(StorageManager::reset);
            panel.update();
        } else if (gameStateChanged.getGameState() == GameState.LOGGED_IN && !loggedIn) {
            loggedIn = true;

            storageManagers.forEach(StorageManager::load);
            panel.update();
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (!loggedIn) return;

        AtomicBoolean isPanelDirty = new AtomicBoolean(false);

        storageManagers.forEach(storageManager -> {
            if (storageManager.onGameTick()) {
                isPanelDirty.set(true);
                storageManager.save();
            }
        });

        if (isPanelDirty.get()) {
            panel.update();
            return;
        }

        panel.softUpdate();
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
        if (!loggedIn) return;

        AtomicBoolean isPanelDirty = new AtomicBoolean(false);

        storageManagers.forEach(storageManager -> {
            if (storageManager.onWidgetLoaded(widgetLoaded)) {
                isPanelDirty.set(true);
                storageManager.save();
            }
        });

        if (isPanelDirty.get()) panel.update();
    }

    @Subscribe
    public void onWidgetClosed(WidgetClosed widgetClosed) {
        if (!loggedIn) return;

        AtomicBoolean isPanelDirty = new AtomicBoolean(false);

        storageManagers.forEach(storageManager -> {
            if (storageManager.onWidgetClosed(widgetClosed)) {
                isPanelDirty.set(true);
                storageManager.save();
            }
        });

        if (isPanelDirty.get()) panel.update();
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged) {
        if (!loggedIn) return;

        AtomicBoolean isPanelDirty = new AtomicBoolean(false);

        storageManagers.forEach(storageManager -> {
            if (storageManager.onVarbitChanged()) {
                isPanelDirty.set(true);
                storageManager.save();
            }
        });

        if (isPanelDirty.get()) panel.update();
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
        if (!loggedIn) return;

        AtomicBoolean isPanelDirty = new AtomicBoolean(false);

        storageManagers.forEach(storageManager -> {
            if (storageManager.onItemContainerChanged(itemContainerChanged)) {
                isPanelDirty.set(true);
                storageManager.save();
            }
        });

        if (isPanelDirty.get()) panel.update();
    }

    @Provides
    DudeWheresMyStuffConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(DudeWheresMyStuffConfig.class);
    }
}
