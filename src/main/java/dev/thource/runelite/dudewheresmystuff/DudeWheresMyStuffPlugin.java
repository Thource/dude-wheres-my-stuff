package dev.thource.runelite.dudewheresmystuff;

import com.google.inject.Provides;
import com.google.inject.name.Named;
import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageManager;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageManager;
import dev.thource.runelite.dudewheresmystuff.death.DeathStorageManager;
import dev.thource.runelite.dudewheresmystuff.minigames.MinigamesStorageManager;
import dev.thource.runelite.dudewheresmystuff.playerownedhouse.PlayerOwnedHouseStorageManager;
import dev.thource.runelite.dudewheresmystuff.stash.StashStorageManager;
import dev.thource.runelite.dudewheresmystuff.world.WorldStorageManager;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemContainer;
import net.runelite.api.VarClientInt;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.PlayerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.vars.AccountType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneScapeProfile;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.itemidentification.ItemIdentificationConfig;
import net.runelite.client.plugins.itemidentification.ItemIdentificationPlugin;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

/**
 * DudeWheresMyStuffPlugin is a RuneLite plugin designed to help accounts of all types find their
 * items, coins and minigame points.
 */
@Slf4j
@PluginDescriptor(name = "Dude, Where's My Stuff?")
@PluginDependency(ItemIdentificationPlugin.class)
public class DudeWheresMyStuffPlugin extends Plugin {

  @Getter @Inject protected PluginManager pluginManager;
  @Getter @Inject protected ItemIdentificationPlugin itemIdentificationPlugin;
  @Getter @Inject protected ItemIdentificationConfig itemIdentificationConfig;

  @Inject
  @Getter
  @Named("developerMode")
  boolean developerMode;

  @Inject private ClientToolbar clientToolbar;
  @Getter @Inject private Client client;
  @Getter @Inject private ClientThread clientThread;
  @Getter @Inject private InfoBoxManager infoBoxManager;
  @Getter @Inject private ItemManager itemManager;
  @Getter @Inject private DudeWheresMyStuffConfig config;
  @Inject private ConfigManager configManager;

  @Inject private DeathStorageManager deathStorageManager;
  @Inject private DeathStorageManager previewDeathStorageManager;
  @Inject private CoinsStorageManager coinsStorageManager;
  @Inject private CoinsStorageManager previewCoinsStorageManager;
  @Inject private CarryableStorageManager carryableStorageManager;
  @Inject private CarryableStorageManager previewCarryableStorageManager;
  @Inject private WorldStorageManager worldStorageManager;
  @Inject private WorldStorageManager previewWorldStorageManager;
  @Inject private StashStorageManager stashStorageManager;
  @Inject private StashStorageManager previewStashStorageManager;
  @Inject private PlayerOwnedHouseStorageManager playerOwnedHouseStorageManager;
  @Inject private PlayerOwnedHouseStorageManager previewPlayerOwnedHouseStorageManager;
  @Inject private MinigamesStorageManager minigamesStorageManager;
  @Inject private MinigamesStorageManager previewMinigamesStorageManager;
  private StorageManagerManager storageManagerManager;
  private StorageManagerManager previewStorageManagerManager;

  private DudeWheresMyStuffPanelContainer panelContainer;
  private NavigationButton navButton;
  private ClientState clientState = ClientState.LOGGED_OUT;
  private boolean pluginStartedAlreadyLoggedIn;
  @Getter private String previewProfileKey;

  public Stream<RuneScapeProfile> getProfilesWithData() {
    return configManager
        .getRSProfiles()
        .stream()
        .filter(profile -> configManager.getConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP,
            profile.getKey(), "isMember") != null);
  }

  @Override
  protected void startUp() {
    if (panelContainer == null) {
      deathStorageManager.setCarryableStorageManager(carryableStorageManager);
      deathStorageManager.setCoinsStorageManager(coinsStorageManager);
      worldStorageManager
          .getLeprechaun()
          .setBottomlessBucketStorage(carryableStorageManager.getBottomlessBucket());
      storageManagerManager =
          new StorageManagerManager(
              this,
              carryableStorageManager,
              coinsStorageManager,
              deathStorageManager,
              minigamesStorageManager,
              stashStorageManager,
              playerOwnedHouseStorageManager,
              worldStorageManager);

      previewCarryableStorageManager.setPreviewManager(true);
      previewCoinsStorageManager.setPreviewManager(true);
      previewDeathStorageManager.setPreviewManager(true);
      previewMinigamesStorageManager.setPreviewManager(true);
      previewStashStorageManager.setPreviewManager(true);
      previewPlayerOwnedHouseStorageManager.setPreviewManager(true);
      previewWorldStorageManager.setPreviewManager(true);

      previewDeathStorageManager.setCarryableStorageManager(previewCarryableStorageManager);
      previewDeathStorageManager.setCoinsStorageManager(previewCoinsStorageManager);
      previewWorldStorageManager
          .getLeprechaun()
          .setBottomlessBucketStorage(previewCarryableStorageManager.getBottomlessBucket());
      previewStorageManagerManager =
          new StorageManagerManager(
              this,
              previewCarryableStorageManager,
              previewCoinsStorageManager,
              previewDeathStorageManager,
              previewMinigamesStorageManager,
              previewStashStorageManager,
              previewPlayerOwnedHouseStorageManager,
              previewWorldStorageManager);

      panelContainer =
          new DudeWheresMyStuffPanelContainer(
              new DudeWheresMyStuffPanel(
                  this, config, itemManager, configManager, storageManagerManager, false, client),
              new DudeWheresMyStuffPanel(
                  this,
                  config,
                  itemManager,
                  configManager,
                  previewStorageManagerManager,
                  true,
                  client));

      SwingUtilities.invokeLater(
          () -> {
            storageManagerManager
                .getStorageManagers()
                .forEach(
                    storageManager ->
                        storageManager.getStorages().forEach(o -> o.createStoragePanel(storageManager)));

            previewStorageManagerManager
                .getStorageManagers()
                .forEach(
                    storageManager ->
                        storageManager.getStorages().forEach(o -> o.createStoragePanel(storageManager)));
          });

      clientThread.invoke(() -> {
        navButton = buildNavigationButton();
      });

      ItemContainerWatcher.init(client, clientThread, itemManager);
    }

    reset(true);

    clientThread.invoke(() -> {
      clientToolbar.addNavigation(navButton);
    });

    AtomicBoolean anyProfilesMigrated = new AtomicBoolean(false);
    getProfilesWithData().forEach(runeScapeProfile -> {
      if (configManager.getConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, runeScapeProfile.getKey(), "saveMigrated") == null) {
        new SaveMigrator(configManager, runeScapeProfile.getKey()).migrate();
        anyProfilesMigrated.set(true);
      }
    });
    if (anyProfilesMigrated.get()) {
      configManager.sendConfig();
    }

    if (client.getGameState() == GameState.LOGGED_IN) {
      clientState = ClientState.LOGGING_IN;
      pluginStartedAlreadyLoggedIn = true;
    } else if (client.getGameState() == GameState.LOGGING_IN) {
      clientState = ClientState.LOGGING_IN;
    }

    deathStorageManager.refreshInfoBox();
  }

  private void reset(boolean fullReset) {
    clientState = ClientState.LOGGED_OUT;

    ItemContainerWatcher.reset();
    if (fullReset) {
      panelContainer.reset();
    } else {
      panelContainer.getPanel().reset();
    }
  }

  @Override
  protected void shutDown() {
    clientToolbar.removeNavigation(navButton);

    infoBoxManager.removeIf(infoBox -> infoBox.getName().startsWith(this.getClass().getSimpleName()));
  }

  @Subscribe
  public void onConfigChanged(ConfigChanged configChanged) {
    if (!Objects.equals(configChanged.getGroup(), DudeWheresMyStuffConfig.CONFIG_GROUP)) {
      return;
    }

    switch (configChanged.getKey()) {
      case "showEmptyStorages":
        panelContainer.reorderStoragePanels();
        break;
      case "sidebarIcon":
        clientThread.invoke(() -> {
          clientToolbar.removeNavigation(navButton);

          navButton = buildNavigationButton();
          clientToolbar.addNavigation(navButton);
        });
        break;
      case "itemSortMode":
        setItemSortMode(ItemSortMode.valueOf(configChanged.getNewValue()));
        break;
      case "deathpilesUseAccountPlayTime":
        deathStorageManager.refreshInfoBox();
        break;
    }
  }

  private NavigationButton buildNavigationButton() {
    return NavigationButton.builder()
        .tooltip("Dude, Where's My Stuff?")
        .icon(config.sidebarIcon().getIcon(itemManager))
        .panel(panelContainer)
        .priority(4)
        .build();
  }

  @Subscribe
  void onActorDeath(ActorDeath actorDeath) {
    storageManagerManager.onActorDeath(actorDeath);
  }

  @Subscribe
  void onChatMessage(ChatMessage chatMessage) {
    if (clientState == ClientState.LOGGED_OUT) {
      return;
    }

    storageManagerManager.onChatMessage(chatMessage);
  }

  @Subscribe
  public void onRuneScapeProfileChanged(RuneScapeProfileChanged e) {
    clientThread.invokeLater(
        () -> {
          storageManagerManager.reset();
          storageManagerManager.load();
          SwingUtilities.invokeLater(panelContainer.getPanel()::softUpdate);
        });
  }

  @Subscribe
  void onGameStateChanged(GameStateChanged gameStateChanged) {
    storageManagerManager.onGameStateChanged(gameStateChanged);

    if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN) {
      reset(false);
    } else if (gameStateChanged.getGameState() == GameState.LOGGING_IN) {
      clientState = ClientState.LOGGING_IN;
    }
  }

  @Subscribe
  void onPlayerChanged(PlayerChanged ev) {
    if (ev.getPlayer() != client.getLocalPlayer()) {
      return;
    }

    panelContainer.getPanel().setDisplayName(ev.getPlayer().getName());
    SwingUtilities.invokeLater(panelContainer.getPanel()::softUpdate);

    if (Objects.equals(
        ev.getPlayer().getName(), panelContainer.getPreviewPanel().getDisplayName())) {
      disablePreviewMode(false);
    }
  }

  @Subscribe
  void onGameTick(GameTick gameTick) {
    if (clientState == ClientState.LOGGED_OUT) {
      return;
    }

    if (clientState == ClientState.LOGGING_IN) {
      boolean isMember = client.getVarcIntValue(VarClientInt.MEMBERSHIP_STATUS) == 1;
      AccountType accountType = client.getAccountType();
      String displayName = Objects.requireNonNull(client.getLocalPlayer()).getName();

      // All saves should be migrated on plugin start, so this must be a new account
      if (configManager.getRSProfileConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, "saveMigrated") == null) {
        configManager.setRSProfileConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, "saveMigrated", true);
      }

      configManager.setRSProfileConfiguration(
          DudeWheresMyStuffConfig.CONFIG_GROUP, "isMember", isMember);
      configManager.setRSProfileConfiguration(
          DudeWheresMyStuffConfig.CONFIG_GROUP, "accountType", accountType.ordinal());

      panelContainer.getPanel().logIn(isMember, accountType, displayName);
      clientState = ClientState.LOGGED_IN;

      if (pluginStartedAlreadyLoggedIn) {
        storageManagerManager.load();

        for (ItemContainer itemContainer : client.getItemContainers()) {
          onItemContainerChanged(new ItemContainerChanged(itemContainer.getId(), itemContainer));
        }

        onVarbitChanged(new VarbitChanged());

        panelContainer.getPanel().setDisplayName(client.getLocalPlayer().getName());

        pluginStartedAlreadyLoggedIn = false;
      }

      SwingUtilities.invokeLater(panelContainer.getPanel()::softUpdate);

      return;
    }

    ItemContainerWatcher.onGameTick(this);
    storageManagerManager.onGameTick();

    SwingUtilities.invokeLater(panelContainer::softUpdate);
  }

  @Subscribe
  void onMenuOptionClicked(MenuOptionClicked menuOption) {
    if (clientState == ClientState.LOGGED_OUT) {
      return;
    }

    storageManagerManager.onMenuOptionClicked(menuOption);
  }

  @Subscribe
  void onWidgetLoaded(WidgetLoaded widgetLoaded) {
    if (clientState == ClientState.LOGGED_OUT) {
      return;
    }

    storageManagerManager.onWidgetLoaded(widgetLoaded);
  }

  @Subscribe
  void onWidgetClosed(WidgetClosed widgetClosed) {
    if (clientState == ClientState.LOGGED_OUT) {
      return;
    }

    storageManagerManager.onWidgetClosed(widgetClosed);
  }

  @Subscribe
  void onVarbitChanged(VarbitChanged varbitChanged) {
    if (clientState == ClientState.LOGGED_OUT) {
      return;
    }

    storageManagerManager.onVarbitChanged();
  }

  @Subscribe
  void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    if (clientState == ClientState.LOGGED_OUT) {
      return;
    }

    storageManagerManager.onItemContainerChanged(itemContainerChanged);
  }

  @Subscribe
  void onItemDespawned(ItemDespawned itemDespawned) {
    if (clientState == ClientState.LOGGED_OUT) {
      return;
    }

    storageManagerManager.onItemDespawned(itemDespawned);
  }

  @Provides
  DudeWheresMyStuffConfig provideConfig(ConfigManager configManager) {
    return configManager.getConfig(DudeWheresMyStuffConfig.class);
  }

  void disablePreviewMode(boolean deleteData) {
    previewStorageManagerManager.reset();

    if (deleteData) {
      for (String key :
          configManager.getRSProfileConfigurationKeys(
              DudeWheresMyStuffConfig.CONFIG_GROUP, previewProfileKey, "")) {
        configManager.unsetConfiguration(
            DudeWheresMyStuffConfig.CONFIG_GROUP, previewProfileKey, key);
      }

      if (Objects.equals(previewProfileKey, configManager.getRSProfileKey())) {
        storageManagerManager.reset();

        SwingUtilities.invokeLater(
            () ->
                storageManagerManager
                    .getStorageManagers()
                    .forEach(
                        storageManager -> {
                          storageManager
                              .getStorages()
                              .forEach(storage -> storage.getStoragePanel().update());
                          storageManager.getStorageTabPanel().reorderStoragePanels();
                        }));
      }
    }

    SwingUtilities.invokeLater(
        () -> {
          panelContainer.getPreviewPanel().logOut();

          panelContainer.disablePreviewMode();
        });
    this.previewProfileKey = null;
  }

  void enablePreviewMode(String profileKey, String displayName) {
    this.previewProfileKey = profileKey;

    previewDeathStorageManager.setStartPlayedMinutes(
        configManager.getConfiguration(
            DudeWheresMyStuffConfig.CONFIG_GROUP, profileKey, "minutesPlayed", int.class));
    clientThread.invoke(
        () -> {
          previewStorageManagerManager.load(profileKey);

          panelContainer
              .getPreviewPanel()
              .logIn(
                  configManager.getConfiguration(
                      DudeWheresMyStuffConfig.CONFIG_GROUP, profileKey, "isMember", boolean.class),
                  AccountType.values()[
                      (int)
                          configManager.getConfiguration(
                              DudeWheresMyStuffConfig.CONFIG_GROUP,
                              profileKey,
                              "accountType",
                              int.class)],
                  displayName);

          panelContainer.enablePreviewMode();
        });
  }

  public void setItemSortMode(ItemSortMode itemSortMode) {
    storageManagerManager.setItemSortMode(itemSortMode);
    previewStorageManagerManager.setItemSortMode(itemSortMode);
  }
}
