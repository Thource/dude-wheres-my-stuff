package dev.thource.runelite.dudewheresmystuff;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

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

    private DudeWheresMyStuffPanel panel;

    private NavigationButton navButton;

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
    }

    @Override
    protected void shutDown() throws Exception {
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    public void onActorDeath(ActorDeath actorDeath) {
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
            coinsManager.reset();
            carryableManager.reset();
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged) {
        boolean isPanelDirty = coinsManager.updateVarbits();
        if (carryableManager.updateVarbits()) isPanelDirty = true;

        if (isPanelDirty) panel.update();
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged itemContainerChanged)
    {
        boolean isPanelDirty = coinsManager.updateItemContainer(itemContainerChanged);
        if (carryableManager.updateItemContainer(itemContainerChanged)) isPanelDirty = true;

        if (isPanelDirty) panel.update();
    }

    @Provides
    DudeWheresMyStuffConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(DudeWheresMyStuffConfig.class);
    }
}
