package dev.thource.runelite.dudewheresmystuff;

import com.google.inject.Provides;
import com.google.inject.name.Named;
import dev.thource.runelite.dudewheresmystuff.carryable.CarryableStorageManager;
import dev.thource.runelite.dudewheresmystuff.coins.CoinsStorageManager;
import dev.thource.runelite.dudewheresmystuff.death.DeathStorageManager;
import dev.thource.runelite.dudewheresmystuff.death.ExpiringDeathStorageTextOverlay;
import dev.thource.runelite.dudewheresmystuff.death.ExpiringDeathStorageTilesOverlay;
import dev.thource.runelite.dudewheresmystuff.minigames.MinigamesStorageManager;
import dev.thource.runelite.dudewheresmystuff.playerownedhouse.PlayerOwnedHouseStorageManager;
import dev.thource.runelite.dudewheresmystuff.stash.StashStorageManager;
import dev.thource.runelite.dudewheresmystuff.world.WorldStorageManager;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemContainer;
import net.runelite.api.VarClientInt;
import net.runelite.api.Varbits;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneScapeProfile;
import net.runelite.client.config.RuneScapeProfileType;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.ConfigSync;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.itemidentification.ItemIdentificationConfig;
import net.runelite.client.plugins.itemidentification.ItemIdentificationPlugin;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

/**
 * DudeWheresMyStuffPlugin is a RuneLite plugin designed to help accounts of all types find their
 * items, coins and minigame points.
 */
@Slf4j
@PluginDescriptor(
    name = "Dude, Where's My Stuff?",
    description = "Helps you keep track of your stuff (items, gp, minigame points) by recording "
        + "and showing you where they are in an easy to view way.",
    tags = {"uim", "storage", "deathbank", "deathstorage", "death", "deathpile", "coins", "poh",
        "stash", "minigames", "leprechaun", "fossils"}
)
@PluginDependency(ItemIdentificationPlugin.class)
public class DudeWheresMyStuffPlugin extends Plugin {

  private static final String CONFIG_KEY_IS_MEMBER = "isMember";
  private static final String CONFIG_KEY_SAVE_MIGRATED = "saveMigrated";

  @Getter @Inject protected PluginManager pluginManager;
  @Getter @Inject protected ItemIdentificationPlugin itemIdentificationPlugin;
  @Getter @Inject protected ItemIdentificationConfig itemIdentificationConfig;

  @Inject @Getter @Named("developerMode") boolean developerMode;

  @Inject private ClientToolbar clientToolbar;
  @Getter @Inject private Notifier notifier;
  @Getter @Inject private Client client;
  @Getter @Inject private ClientThread clientThread;
  @Getter @Inject private InfoBoxManager infoBoxManager;
  @Getter @Inject private ItemManager itemManager;
  @Getter @Inject private DudeWheresMyStuffConfig config;
  @Inject private ConfigManager configManager;
  @Inject private OverlayManager overlayManager;
  @Inject private KeyManager keyManager;
  @Getter @Inject private ChatMessageManager chatMessageManager;

  private ExpiringDeathStorageTilesOverlay expiringDeathStorageTilesOverlay;
  private ExpiringDeathStorageTextOverlay expiringDeathStorageTextOverlay;
  @Inject private ItemCountOverlay itemCountOverlay;
  @Inject private ItemCountInputListener itemCountInputListener;
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
  private String profileKey;
  @Getter private String previewProfileKey;

  /**
   * Displays a confirmation popup to the user and returns true if they confirmed it.
   *
   * @param parentComponent the calling component
   * @param text            the description shown to the user
   * @param confirmText     the text displayed on the confirmation button
   * @return true if they clicked the confirmation button
   */
  public static boolean getConfirmation(Component parentComponent, String text,
      String confirmText) {
    int result = JOptionPane.CANCEL_OPTION;

    try {
      result =
          JOptionPane.showConfirmDialog(parentComponent, text, confirmText,
              JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
    } catch (Exception err) {
      log.warn("Unexpected exception occurred while check for confirm required", err);
    }

    return result == JOptionPane.OK_OPTION;
  }

  Stream<RuneScapeProfile> getProfilesWithData() {
    return configManager
        .getRSProfiles()
        .stream()
        .filter(profile -> configManager.getConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP,
            profile.getKey(), CONFIG_KEY_IS_MEMBER) != null);
  }

  @Override
  protected void startUp() {
    if (panelContainer == null) {
      expiringDeathStorageTilesOverlay = new ExpiringDeathStorageTilesOverlay(config, client,
          deathStorageManager);
      expiringDeathStorageTextOverlay = new ExpiringDeathStorageTextOverlay(config,
          deathStorageManager, client);
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
                  this, configManager, storageManagerManager, false),
              new DudeWheresMyStuffPanel(
                  this,
                  configManager,
                  previewStorageManagerManager,
                  true
              ));

      SwingUtilities.invokeLater(
          () -> {
            storageManagerManager
                .getStorageManagers()
                .forEach(
                    storageManager ->
                        storageManager.getStorages()
                            .forEach(o -> o.createStoragePanel(storageManager)));

            previewStorageManagerManager
                .getStorageManagers()
                .forEach(
                    storageManager ->
                        storageManager.getStorages()
                            .forEach(o -> o.createStoragePanel(storageManager)));
          });

      clientThread.invoke(() -> navButton = buildNavigationButton());

      ItemContainerWatcher.init(client);
    }

    reset();

    clientThread.invoke(() -> clientToolbar.addNavigation(navButton));

    AtomicBoolean anyProfilesMigrated = new AtomicBoolean(false);
    getProfilesWithData().forEach(runeScapeProfile -> {
      if (configManager.getConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP,
          runeScapeProfile.getKey(), CONFIG_KEY_SAVE_MIGRATED) == null) {
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

    deathStorageManager.refreshInfoBoxes();

    overlayManager.add(expiringDeathStorageTextOverlay);
    overlayManager.add(expiringDeathStorageTilesOverlay);
    overlayManager.add(itemCountOverlay);
    itemCountInputListener.setItemCountOverlay(itemCountOverlay);
    keyManager.registerKeyListener(itemCountInputListener);
  }

  private void reset() {
    clientState = ClientState.LOGGED_OUT;

    ItemContainerWatcher.reset();
    storageManagerManager.reset();
    panelContainer.reset();
  }

  @Override
  protected void shutDown() {
    save();

    clientToolbar.removeNavigation(navButton);

    infoBoxManager.removeIf(
        infoBox -> infoBox.getName().startsWith(this.getClass().getSimpleName()));

    overlayManager.remove(expiringDeathStorageTextOverlay);
    overlayManager.remove(expiringDeathStorageTilesOverlay);
    overlayManager.remove(itemCountOverlay);
    keyManager.unregisterKeyListener(itemCountInputListener);
  }

  @Subscribe
  public void onFocusChanged(FocusChanged focusChanged) {
    if (!focusChanged.isFocused()) {
      itemCountOverlay.setKeybindPressed(false);
    }
  }

  @Subscribe
  public void onConfigSync(ConfigSync configSync) {
    save();
  }

  @Subscribe
  public void onClientShutdown(ClientShutdown clientShutdown) {
    save();
  }

  private void load(String profileKey) {
    this.profileKey = profileKey;

    clientThread.invokeLater(
        () -> {
          storageManagerManager.reset();
          storageManagerManager.load(profileKey);
          SwingUtilities.invokeLater(panelContainer.getPanel()::softUpdate);
        });
  }

  private void save() {
    if (profileKey == null) {
      return;
    }

    storageManagerManager.save(profileKey);
  }

  @Subscribe
  void onConfigChanged(ConfigChanged configChanged) {
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
      case "deathbankInfoBox":
      case "deathpileInfoBox":
        deathStorageManager.refreshInfoBoxes();
        break;
      case "showDeathStorageRiskWarning":
        SwingUtilities.invokeLater(
            () -> deathStorageManager.getStorageTabPanel().reorderStoragePanels());
        break;
      case "deathpileColorScheme":
        deathStorageManager.refreshDeathpileColors();
        break;
      default:
        // do nothing
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

  private String toTitleCase(String str) {
    if (str == null) {
      return null;
    }

    Pattern pattern = Pattern.compile("(_|^)(\\w)([^_]*)");
    Matcher matcher = pattern.matcher(str.toLowerCase());

    StringBuilder builder = new StringBuilder();
    while (matcher.find()) {
      builder.append(matcher.group(1)).append(matcher.group(2).toUpperCase())
          .append(matcher.group(3));
    }
    return builder.toString().replace("_", " ");
  }

  /**
   * Gets the display name for the supplied profileKey and appends the account type if not
   * standard.
   *
   * @param profileKey the profile key
   * @return display name, potentially with a suffix
   */
  public String getDisplayName(String profileKey) {
    RuneScapeProfile profile = configManager.getRSProfiles().stream()
        .filter(p -> p.getKey().equals(profileKey))
        .findFirst().orElse(null);

    return getDisplayName(profile);
  }

  /**
   * Gets the display name for the supplied profile and appends the account type if not standard.
   *
   * @param profile the profile
   * @return display name, potentially with a suffix
   */
  public String getDisplayName(RuneScapeProfile profile) {
    if (profile == null) {
      return "Unknown";
    }

    String displayName = profile.getDisplayName();
    if (profile.getType() != RuneScapeProfileType.STANDARD) {
      displayName += " - " + toTitleCase(profile.getType().toString());
    }

    return displayName;
  }

  @Subscribe
  void onRuneScapeProfileChanged(RuneScapeProfileChanged e) {
    save();
    load(configManager.getRSProfileKey());

    String displayName = getDisplayName(configManager.getRSProfileKey());
    if (Objects.equals(displayName, panelContainer.getPreviewPanel().getDisplayName())) {
      disablePreviewMode(false);
    }

    panelContainer.getPanel().setDisplayName(displayName);
  }

  @Subscribe
  void onGameStateChanged(GameStateChanged gameStateChanged) {
    storageManagerManager.onGameStateChanged(gameStateChanged);

    if (gameStateChanged.getGameState() == GameState.LOGGING_IN) {
      clientState = ClientState.LOGGING_IN;
    }
  }

  @Subscribe
  void onGameTick(GameTick gameTick) {
    if (clientState == ClientState.LOGGED_OUT) {
      return;
    }

    if (clientState == ClientState.LOGGING_IN) {
      final boolean isMember = client.getVarcIntValue(VarClientInt.MEMBERSHIP_STATUS) == 1;
      final int accountType = client.getVarbitValue(Varbits.ACCOUNT_TYPE);
      final String displayName = getDisplayName(configManager.getRSProfileKey());

      // All saves should be migrated on plugin start, so this must be a new account
      if (configManager.getRSProfileConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP,
          CONFIG_KEY_SAVE_MIGRATED) == null) {
        configManager.setRSProfileConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP,
            CONFIG_KEY_SAVE_MIGRATED, true);
      }

      configManager.setRSProfileConfiguration(
          DudeWheresMyStuffConfig.CONFIG_GROUP, CONFIG_KEY_IS_MEMBER, isMember);
      configManager.setRSProfileConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, "accountType",
          accountType);

      panelContainer.getPanel().logIn(isMember, accountType, displayName);
      clientState = ClientState.LOGGED_IN;

      if (pluginStartedAlreadyLoggedIn) {
        load(configManager.getRSProfileKey());

        for (ItemContainer itemContainer : client.getItemContainers()) {
          onItemContainerChanged(new ItemContainerChanged(itemContainer.getId(), itemContainer));
        }

        onVarbitChanged(new VarbitChanged());

        panelContainer.getPanel().setDisplayName(getDisplayName(configManager.getRSProfileKey()));

        pluginStartedAlreadyLoggedIn = false;
      }

      SwingUtilities.invokeLater(panelContainer.getPanel()::softUpdate);

      return;
    }

    expiringDeathStorageTextOverlay.updateSoonestExpiringDeathStorage();

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

        storageManagerManager
            .getStorageManagers()
            .forEach(
                storageManager -> {
                  storageManager
                      .getStorages()
                      .forEach(storage -> {
                        if (storage.getStoragePanel() != null) {
                          storage.getStoragePanel().refreshItems();
                          SwingUtilities.invokeLater(() -> storage.getStoragePanel().update());
                        }
                      });

                  SwingUtilities.invokeLater(
                      () -> storageManager.getStorageTabPanel().reorderStoragePanels());
                });
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

    Integer playedMinutes = configManager.getConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP,
        profileKey, "minutesPlayed", int.class);
    previewDeathStorageManager.setStartPlayedMinutes(playedMinutes == null ? 0 : playedMinutes);
    clientThread.invoke(
        () -> {
          previewStorageManagerManager.load(profileKey);

          panelContainer
              .getPreviewPanel()
              .logIn(
                  configManager.getConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, profileKey,
                      CONFIG_KEY_IS_MEMBER, boolean.class),
                  configManager.getConfiguration(
                      DudeWheresMyStuffConfig.CONFIG_GROUP,
                      profileKey,
                      "accountType",
                      int.class),
                  displayName
              );

          panelContainer.enablePreviewMode();
        });
  }

  public void setItemSortMode(ItemSortMode itemSortMode) {
    panelContainer.setItemSortMode(itemSortMode);
  }

  void deleteAllData() {
    getProfilesWithData().forEach(runeScapeProfile -> {
      for (String configKey : configManager.getRSProfileConfigurationKeys(
          DudeWheresMyStuffConfig.CONFIG_GROUP, runeScapeProfile.getKey(),
          "")) {
        configManager.unsetConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP,
            runeScapeProfile.getKey(), configKey);
      }
    });
    configManager.sendConfig();
  }

  public long getWithdrawableItemCount(int id) {
    int canonicalId = itemManager.canonicalize(id);

    return storageManagerManager.getStorages()
        .filter(Storage::isWithdrawable)
        .mapToLong(storage -> storage.getItemCount(canonicalId)).sum();
  }

  public Map<Storage<?>, Long> getDetailedWithdrawableItemCount(int id) {
    int canonicalId = itemManager.canonicalize(id);

    HashMap<Storage<?>, Long> map = new HashMap<>();

    storageManagerManager.getStorages()
        .filter(Storage::isWithdrawable).forEach(storage -> {
          long count = storage.getItemCount(canonicalId);

          if (count > 0) {
            map.put(storage, count);
          }
        });

    return map;
  }
}
