package dev.thource.runelite.dudewheresmystuff;

import com.google.inject.Provides;
import com.google.inject.name.Named;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemContainer;
import net.runelite.api.VarClientInt;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.PlayerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.vars.AccountType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

@Slf4j
@PluginDescriptor(
    name = "Dude, Where's My Stuff?"
)
public class DudeWheresMyStuffPlugin extends Plugin {

  @Inject
  @Named("developerMode")
  boolean developerMode;
  DudeWheresMyStuffPanelContainer panelContainer;
  @Inject
  private ClientToolbar clientToolbar;
  @Inject
  private Client client;
  @Inject
  private ClientThread clientThread;
  @Inject
  private ItemManager itemManager;
  @Inject
  private DudeWheresMyStuffConfig config;
  @Inject
  private ConfigManager configManager;
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
  private List<StorageManager<?, ?>> storageManagers;
  @Inject
  private DeathStorageManager previewDeathStorageManager;
  @Inject
  private CoinsStorageManager previewCoinsStorageManager;
  @Inject
  private CarryableStorageManager previewCarryableStorageManager;
  @Inject
  private WorldStorageManager previewWorldStorageManager;
  @Inject
  private MinigamesStorageManager previewMinigamesStorageManager;
  private List<StorageManager<?, ?>> previewStorageManagers;
  private NavigationButton navButton;

  private ClientState clientState = ClientState.LOGGED_OUT;
  private boolean pluginStartedAlreadyLoggedIn;
  private String previewProfileKey;

  @Override
  protected void startUp() {
    if (panelContainer == null) {
      deathStorageManager.carryableStorageManager = carryableStorageManager;
      deathStorageManager.coinsStorageManager = coinsStorageManager;
      storageManagers = Arrays.asList(
          deathStorageManager,
          coinsStorageManager,
          carryableStorageManager,
          worldStorageManager,
          minigamesStorageManager
      );

      previewDeathStorageManager.carryableStorageManager = previewCarryableStorageManager;
      previewDeathStorageManager.coinsStorageManager = previewCoinsStorageManager;
      previewStorageManagers = Arrays.asList(
          previewDeathStorageManager,
          previewCoinsStorageManager,
          previewCarryableStorageManager,
          previewWorldStorageManager,
          previewMinigamesStorageManager
      );

      panelContainer = new DudeWheresMyStuffPanelContainer(
          new DudeWheresMyStuffPanel(this, config, itemManager, configManager, deathStorageManager,
              coinsStorageManager,
              carryableStorageManager, worldStorageManager, minigamesStorageManager, developerMode,
              false, client),
          new DudeWheresMyStuffPanel(this, config, itemManager, configManager,
              previewDeathStorageManager,
              previewCoinsStorageManager, previewCarryableStorageManager,
              previewWorldStorageManager,
              previewMinigamesStorageManager, developerMode, true, client),
          configManager
      );

      final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");

      navButton = NavigationButton.builder()
          .tooltip("Dude, Where's My Stuff?")
          .icon(icon)
          .panel(panelContainer)
          .priority(4)
          .build();
    }

    reset(true);

    clientToolbar.addNavigation(navButton);

    if (client.getGameState() == GameState.LOGGED_IN) {
      clientState = ClientState.LOGGING_IN;
      pluginStartedAlreadyLoggedIn = true;
    } else if (client.getGameState() == GameState.LOGGING_IN) {
      clientState = ClientState.LOGGING_IN;
    }
  }

  private void reset(boolean fullReset) {
    clientState = ClientState.LOGGED_OUT;

    storageManagers.forEach(StorageManager::reset);
      if (fullReset) {
          panelContainer.reset();
      } else {
          panelContainer.panel.reset();
      }
  }

  @Override
  protected void shutDown() {
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
      reset(false);
    } else if (gameStateChanged.getGameState() == GameState.LOGGING_IN) {
      clientState = ClientState.LOGGING_IN;
    } else if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
        if (clientState != ClientState.LOGGING_IN) {
            return;
        }

      storageManagers.forEach(StorageManager::load);
      panelContainer.panel.update();
    }
  }

  @Subscribe
  private void onPlayerChanged(PlayerChanged ev) {
      if (ev.getPlayer() != client.getLocalPlayer()) {
          return;
      }

    panelContainer.panel.setDisplayName(ev.getPlayer().getName());
    panelContainer.panel.update();
  }

  @Subscribe
  public void onGameTick(GameTick gameTick) {
      if (clientState == ClientState.LOGGED_OUT) {
          return;
      }

    if (clientState == ClientState.LOGGING_IN) {
      boolean isMember = client.getVar(VarClientInt.MEMBERSHIP_STATUS) == 1;
      AccountType accountType = client.getAccountType();
      String displayName = Objects.requireNonNull(client.getLocalPlayer()).getName();

      configManager.setRSProfileConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, "isMember",
          isMember);
      configManager.setRSProfileConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, "accountType",
          accountType.ordinal());

      panelContainer.panel.logIn(isMember, accountType, displayName);
      clientState = ClientState.LOGGED_IN;

      if (pluginStartedAlreadyLoggedIn) {
        storageManagers.forEach(StorageManager::load);

        for (ItemContainer itemContainer : client.getItemContainers()) {
          onItemContainerChanged(new ItemContainerChanged(itemContainer.getId(), itemContainer));
        }

        onVarbitChanged(new VarbitChanged());

        pluginStartedAlreadyLoggedIn = false;
      }

      panelContainer.panel.update();

      storageManagers.forEach(StorageManager::save);

      return;
    }

    AtomicBoolean isPanelDirty = new AtomicBoolean(false);

    storageManagers.forEach(storageManager -> {
      if (storageManager.onGameTick()) {
        isPanelDirty.set(true);

        // don't save before loading is complete, to avoid deleting save data
          if (clientState == ClientState.LOGGED_IN) {
              storageManager.save();
          }
      }
    });

    if (isPanelDirty.get()) {
      panelContainer.panel.update();
      return;
    }

    panelContainer.softUpdate();
  }

  @Subscribe
  public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
      if (clientState == ClientState.LOGGED_OUT) {
          return;
      }

    AtomicBoolean isPanelDirty = new AtomicBoolean(false);

    storageManagers.forEach(storageManager -> {
      if (storageManager.onWidgetLoaded(widgetLoaded)) {
        isPanelDirty.set(true);

        // don't save before loading is complete, to avoid deleting save data
          if (clientState == ClientState.LOGGED_IN) {
              storageManager.save();
          }
      }
    });

      if (isPanelDirty.get()) {
          panelContainer.panel.update();
      }
  }

  @Subscribe
  public void onWidgetClosed(WidgetClosed widgetClosed) {
      if (clientState == ClientState.LOGGED_OUT) {
          return;
      }

    AtomicBoolean isPanelDirty = new AtomicBoolean(false);

    storageManagers.forEach(storageManager -> {
      if (storageManager.onWidgetClosed(widgetClosed)) {
        isPanelDirty.set(true);

        // don't save before loading is complete, to avoid deleting save data
          if (clientState == ClientState.LOGGED_IN) {
              storageManager.save();
          }
      }
    });

      if (isPanelDirty.get()) {
          panelContainer.panel.update();
      }
  }

  @Subscribe
  public void onVarbitChanged(VarbitChanged varbitChanged) {
      if (clientState == ClientState.LOGGED_OUT) {
          return;
      }

    AtomicBoolean isPanelDirty = new AtomicBoolean(false);

    storageManagers.forEach(storageManager -> {
      if (storageManager.onVarbitChanged()) {
        isPanelDirty.set(true);

        // don't save before loading is complete, to avoid deleting save data
          if (clientState == ClientState.LOGGED_IN) {
              storageManager.save();
          }
      }
    });

      if (isPanelDirty.get()) {
          panelContainer.panel.update();
      }
  }

  @Subscribe
  public void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
      if (clientState == ClientState.LOGGED_OUT) {
          return;
      }

    AtomicBoolean isPanelDirty = new AtomicBoolean(false);

    storageManagers.forEach(storageManager -> {
      if (storageManager.onItemContainerChanged(itemContainerChanged)) {
        isPanelDirty.set(true);

        // don't save before loading is complete, to avoid deleting save data
          if (clientState == ClientState.LOGGED_IN) {
              storageManager.save();
          }
      }
    });

      if (isPanelDirty.get()) {
          panelContainer.panel.update();
      }
  }

  @Subscribe
  public void onItemDespawned(ItemDespawned itemDespawned) {
      if (clientState == ClientState.LOGGED_OUT) {
          return;
      }

    AtomicBoolean isPanelDirty = new AtomicBoolean(false);

    storageManagers.forEach(storageManager -> {
      if (storageManager.onItemDespawned(itemDespawned)) {
        isPanelDirty.set(true);

        // don't save before loading is complete, to avoid deleting save data
          if (clientState == ClientState.LOGGED_IN) {
              storageManager.save();
          }
      }
    });

      if (isPanelDirty.get()) {
          panelContainer.panel.update();
      }
  }

  @Provides
  DudeWheresMyStuffConfig provideConfig(ConfigManager configManager) {
    return configManager.getConfig(DudeWheresMyStuffConfig.class);
  }

  void disablePreviewMode(boolean deleteData) {
    previewStorageManagers.forEach(StorageManager::reset);

    if (deleteData) {
      configManager.getRSProfileConfigurationKeys(DudeWheresMyStuffConfig.CONFIG_GROUP,
              previewProfileKey, "")
          .forEach(key -> {
            configManager.unsetConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP,
                previewProfileKey, key);
          });
    }

    panelContainer.previewPanel.logOut();

    panelContainer.disablePreviewMode();
    this.previewProfileKey = null;
  }

  void enablePreviewMode(String profileKey, String displayName) {
    this.previewProfileKey = profileKey;

    previewDeathStorageManager.startPlayedMinutes = configManager.getConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, profileKey, "minutesPlayed", int.class);
    clientThread.invoke(() -> {
      previewStorageManagers.forEach((storageManager -> storageManager.load(profileKey)));

      panelContainer.previewPanel.logIn(
          configManager.getConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, profileKey,
              "isMember", boolean.class),
          AccountType.values()[(int) configManager.getConfiguration(
              DudeWheresMyStuffConfig.CONFIG_GROUP, profileKey, "accountType", int.class)],
          displayName
      );

      panelContainer.enablePreviewMode();
    });
  }
}
