package dev.thource.runelite.dudewheresmystuff;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.VarClientInt;
import net.runelite.api.events.*;
import net.runelite.api.vars.AccountType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
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
    private DeathStorageManager deathStorageManager;

    @Inject
    private CoinsStorageManager coinsStorageManager;

    @Inject
    private CarryableStorageManager carryableStorageManager;

    @Inject
    private WorldStorageManager worldStorageManager;

    @Inject
    private MinigamesStorageManager minigamesStorageManager;

    @Inject
    private ConfigManager configManager;

    private final List<StorageManager<?, ?>> storageManagers = new ArrayList<>();

    private DudeWheresMyStuffPanel panel;

    private NavigationButton navButton;

    private ClientState clientState = ClientState.LOGGED_OUT;

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

        storageManagers.add(deathStorageManager);
        storageManagers.add(coinsStorageManager);
        storageManagers.add(carryableStorageManager);
        storageManagers.add(worldStorageManager);
        storageManagers.add(minigamesStorageManager);
    }

    @Override
    protected void shutDown() throws Exception {
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    public void onActorDeath(ActorDeath actorDeath) {
        storageManagers.forEach(storageManager -> storageManager.onActorDeath(actorDeath));
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        storageManagers.forEach(storageManager -> storageManager.onGameStateChanged(gameStateChanged));

        if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN) {
            clientState = ClientState.LOGGED_OUT;

            for (StorageManager<?, ?> storageManager : storageManagers) {
                storageManager.reset();

                MaterialTab tab = panel.uiTabs.get(storageManager.getTab());
                OverviewItemPanel overviewItemPanel = panel.overviewTab.overviews.get(storageManager.getTab());

                if (tab != null) tab.setVisible(false);
                if (overviewItemPanel != null) overviewItemPanel.setVisible(false);

                panel.switchTab(Tab.OVERVIEW);
            }
            panel.uiTabs.get(Tab.SEARCH).setVisible(false);
            panel.setDisplayName("");
            panel.update();
        } else if (gameStateChanged.getGameState() == GameState.LOGGING_IN) {
            clientState = ClientState.LOGGING_IN;
        } else if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
            if (clientState != ClientState.LOGGING_IN) return;

            storageManagers.forEach(StorageManager::load);
            panel.update();
        }
    }

    @Subscribe
    private void onPlayerChanged(PlayerChanged ev) {
        if (ev.getPlayer() != client.getLocalPlayer()) return;

        panel.setDisplayName(ev.getPlayer().getName());
        panel.update();
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (clientState == ClientState.LOGGED_OUT) return;

        if (clientState == ClientState.LOGGING_IN) {
            boolean isMember = client.getVar(VarClientInt.MEMBERSHIP_STATUS) == 1;
            ((DeathStorageTabPanel) panel.storageTabPanelMap.get(Tab.DEATH)).accountType = client.getAccountType();

            for (StorageManager<?, ?> storageManager : storageManagers) {
                if (storageManager.getTab() != Tab.DEATH && storageManager.isMembersOnly() && !isMember) {
                    storageManager.disable();
                    continue;
                }

                for (Storage<?> storage : storageManager.storages) {
                    if (storage.getType().isMembersOnly() && !isMember) storage.disable();
                }

                MaterialTab tab = panel.uiTabs.get(storageManager.getTab());
                OverviewItemPanel overviewItemPanel = panel.overviewTab.overviews.get(storageManager.getTab());

                if (tab != null) tab.setVisible(true);
                if (overviewItemPanel != null) overviewItemPanel.setVisible(true);
            }
            panel.uiTabs.get(Tab.SEARCH).setVisible(true);
            panel.update();
            clientState = ClientState.LOGGED_IN;

            return;
        }

        AtomicBoolean isPanelDirty = new AtomicBoolean(false);

        storageManagers.forEach(storageManager -> {
            if (storageManager.onGameTick()) {
                isPanelDirty.set(true);

                // don't save before loading is complete, to avoid deleting save data
                if (clientState == ClientState.LOGGED_IN) storageManager.save();
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
        if (clientState == ClientState.LOGGED_OUT) return;

        AtomicBoolean isPanelDirty = new AtomicBoolean(false);

        storageManagers.forEach(storageManager -> {
            if (storageManager.onWidgetLoaded(widgetLoaded)) {
                isPanelDirty.set(true);

                // don't save before loading is complete, to avoid deleting save data
                if (clientState == ClientState.LOGGED_IN) storageManager.save();
            }
        });

        if (isPanelDirty.get()) panel.update();
    }

    @Subscribe
    public void onWidgetClosed(WidgetClosed widgetClosed) {
        if (clientState == ClientState.LOGGED_OUT) return;

        AtomicBoolean isPanelDirty = new AtomicBoolean(false);

        storageManagers.forEach(storageManager -> {
            if (storageManager.onWidgetClosed(widgetClosed)) {
                isPanelDirty.set(true);

                // don't save before loading is complete, to avoid deleting save data
                if (clientState == ClientState.LOGGED_IN) storageManager.save();
            }
        });

        if (isPanelDirty.get()) panel.update();
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged) {
        if (clientState == ClientState.LOGGED_OUT) return;

        AtomicBoolean isPanelDirty = new AtomicBoolean(false);

        storageManagers.forEach(storageManager -> {
            if (storageManager.onVarbitChanged()) {
                isPanelDirty.set(true);

                // don't save before loading is complete, to avoid deleting save data
                if (clientState == ClientState.LOGGED_IN) storageManager.save();
            }
        });

        if (isPanelDirty.get()) panel.update();
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
        if (clientState == ClientState.LOGGED_OUT) return;

        AtomicBoolean isPanelDirty = new AtomicBoolean(false);

        storageManagers.forEach(storageManager -> {
            if (storageManager.onItemContainerChanged(itemContainerChanged)) {
                isPanelDirty.set(true);

                // don't save before loading is complete, to avoid deleting save data
                if (clientState == ClientState.LOGGED_IN) storageManager.save();
            }
        });

        if (isPanelDirty.get()) panel.update();
    }

    @Subscribe
    public void onItemDespawned(ItemDespawned itemDespawned)
    {
        if (clientState == ClientState.LOGGED_OUT) return;

        AtomicBoolean isPanelDirty = new AtomicBoolean(false);

        storageManagers.forEach(storageManager -> {
            if (storageManager.onItemDespawned(itemDespawned)) {
                isPanelDirty.set(true);

                // don't save before loading is complete, to avoid deleting save data
                if (clientState == ClientState.LOGGED_IN) storageManager.save();
            }
        });

        if (isPanelDirty.get()) panel.update();
    }

    @Provides
    DudeWheresMyStuffConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(DudeWheresMyStuffConfig.class);
    }
}
