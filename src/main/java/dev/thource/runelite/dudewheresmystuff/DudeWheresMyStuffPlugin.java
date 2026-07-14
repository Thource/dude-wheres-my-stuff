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
import dev.thource.runelite.dudewheresmystuff.sailing.SailingStorageManager;
import dev.thource.runelite.dudewheresmystuff.stash.StashStorageManager;
import dev.thource.runelite.dudewheresmystuff.world.WorldStorageManager;
import java.awt.Component;
import java.util.HashMap;
import java.util.List;
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
import net.runelite.api.KeyCode;
import net.runelite.api.MenuAction;
import net.runelite.api.VarClientInt;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneScapeProfile;
import net.runelite.client.config.RuneScapeProfileType;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.ConfigSync;
import net.runelite.client.events.PluginMessage;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
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
  private static final String CONFIG_KEY_VERSION = "version";
  private static final String PLUGIN_MESSAGE_STORAGES_REQUEST = "storages-request";
  private static final String PLUGIN_MESSAGE_STORAGES_RESPONSE = "storages-response";
  private static final int PLUGIN_MESSAGE_VERSION = 1;
  private static final String PLUGIN_MESSAGE_KEY_SOURCE = "source";
  private static final String PLUGIN_MESSAGE_KEY_TARGET = "target";
  private static final String PLUGIN_MESSAGE_KEY_VERSION = "version";
  private static final String PLUGIN_MESSAGE_KEY_STORAGES = "storages";
  private static final String PLUGIN_NAME = "Dude, Where's My Stuff?";
  private static final String LOADOUT_LAB_NAMESPACE = "loadoutlab";
  private static final String LOADOUT_LAB_STASH_CATEGORY = "collection";
  private static final String LOADOUT_LAB_STASH_NAME = "stash";

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
  @Getter @Inject private SpriteManager spriteManager;
  @Getter @Inject private DudeWheresMyStuffConfig config;
  @Getter @Inject private ConfigManager configManager;
  @Inject private OverlayManager overlayManager;
  @Inject private KeyManager keyManager;
  @Getter @Inject private ChatMessageManager chatMessageManager;
  @Inject private EventBus eventBus;

  private ExpiringDeathStorageTilesOverlay expiringDeathStorageTilesOverlay;
  private ExpiringDeathStorageTextOverlay expiringDeathStorageTextOverlay;
  @Inject private ItemCountOverlay itemCountOverlay;
  @Inject private ItemCountInputListener itemCountInputListener;
  @Inject private DeathStorageManager deathStorageManager;
  @Inject private DeathStorageManager previewDeathStorageManager;
  @Inject private CoinsStorageManager coinsStorageManager;
  @Inject private CoinsStorageManager previewCoinsStorageManager;
  @Getter @Inject private SailingStorageManager sailingStorageManager;
  @Inject private SailingStorageManager previewSailingStorageManager;
  @Inject private CarryableStorageManager carryableStorageManager;
  @Inject private CarryableStorageManager previewCarryableStorageManager;
  @Getter @Inject private WorldStorageManager worldStorageManager;
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
          deathStorageManager, this);
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
              sailingStorageManager,
              carryableStorageManager,
              coinsStorageManager,
              deathStorageManager,
              minigamesStorageManager,
              stashStorageManager,
              playerOwnedHouseStorageManager,
              worldStorageManager);

      previewSailingStorageManager.setPreviewManager(true);
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
              previewSailingStorageManager,
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

      var lastVersion = configManager.getConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP,
          CONFIG_KEY_VERSION);
      configManager.setConfiguration(
          DudeWheresMyStuffConfig.CONFIG_GROUP, CONFIG_KEY_VERSION, "2.11.4");
      // Delete all lost boats from v2.11.1 and before
      if (lastVersion == null) {
        getProfilesWithData()
            .forEach(profile ->
                configManager.getRSProfileConfigurationKeys(DudeWheresMyStuffConfig.CONFIG_GROUP,
                        profile.getKey(), "sailing.lostBoat")
                    .forEach(key -> configManager.unsetConfiguration(
                        DudeWheresMyStuffConfig.CONFIG_GROUP, profile.getKey(), key)));
      }

      ItemContainerWatcher.init(client);
    }

    reset();

    clientThread.invoke(() -> clientToolbar.addNavigation(navButton));

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
          requestLoadoutLabStorages();
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
        ItemSortMode newValue = configChanged.getNewValue() != null ?
            ItemSortMode.valueOf(configChanged.getNewValue()):
            ItemSortMode.UNSORTED;
        setItemSortMode(newValue);
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
        .tooltip(PLUGIN_NAME)
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
      final int accountType = client.getVarbitValue(VarbitID.IRONMAN);
      final String displayName = getDisplayName(configManager.getRSProfileKey());

      configManager.setRSProfileConfiguration(
          DudeWheresMyStuffConfig.CONFIG_GROUP, CONFIG_KEY_IS_MEMBER, isMember);
      configManager.setRSProfileConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, "accountType",
          accountType);

      panelContainer.getPanel().logIn(isMember, accountType, displayName);
      clientState = ClientState.LOGGED_IN;

      if (pluginStartedAlreadyLoggedIn) {
        load(configManager.getRSProfileKey());

        clientThread.invokeLater(() -> {
          for (ItemContainer itemContainer : client.getItemContainers()) {
            onItemContainerChanged(new ItemContainerChanged(itemContainer.getId(), itemContainer));
          }

          var varbitChanged = new VarbitChanged();
          varbitChanged.setVarbitId(-999);
          onVarbitChanged(varbitChanged);
        });

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
  void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned) {
    if (clientState == ClientState.LOGGED_OUT) {
      return;
    }

    storageManagerManager.onGameObjectSpawned(gameObjectSpawned);
  }

  @Subscribe
  void onMenuOptionClicked(MenuOptionClicked menuOption) {
    if (clientState == ClientState.LOGGED_OUT) {
      return;
    }

    storageManagerManager.onMenuOptionClicked(menuOption);
  }

  @Subscribe
  void onMenuEntryAdded(MenuEntryAdded menuEntryAdded) {
    final boolean hotKeyPressed = client.isKeyPressed(KeyCode.KC_SHIFT);
    if (!isDeveloperMode() || !menuEntryAdded.getOption().equals("Walk here") || !hotKeyPressed) {
      return;
    }

    if (Objects.equals(configManager.getConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, "debug.menu.createDeathpile"), "true")) {
      client
          .getMenu()
          .createMenuEntry(-1)
          .setOption("Create deathpile")
          .setType(MenuAction.RUNELITE)
          .onClick(
              e -> {
                var target = client.getTopLevelWorldView().getSelectedSceneTile();
                if (target != null) {
                  SwingUtilities.invokeLater(
                      () ->
                          deathStorageManager
                              .getDeathItemsStorage()
                              .createDebugDeathpile(
                                  WorldPoint.fromLocalInstance(client, target.getLocalLocation())));
                }
              });
    }

    if (Objects.equals(configManager.getConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP, "debug.menu.logCoords"), "true")) {
      client
          .getMenu()
          .createMenuEntry(-1)
          .setOption("Log co-ords")
          .setType(MenuAction.RUNELITE)
          .onClick(
              e -> {
                var target = client.getTopLevelWorldView().getSelectedSceneTile();
                if (target != null) {
                  var worldPoint = WorldPoint.fromLocalInstance(client, target.getLocalLocation());
                  log.info(
                      "Co-ords: {}, {}, {}",
                      worldPoint.getX(),
                      worldPoint.getY(),
                      worldPoint.getPlane());
                }
              });
    }
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

    storageManagerManager.onVarbitChanged(varbitChanged);
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

  /**
   * Replies to "storages-request" PluginMessages with a "storages-response" PluginMessage, so
   * that other plugins can use the tracked storage data of the logged in profile.
   *
   * <p>Request: namespace "dudewheresmystuff", name "storages-request", data: "source" (String,
   * required) - the display name of the requesting plugin. Requests without a source are ignored.
   *
   * <p>Response: namespace "dudewheresmystuff", name "storages-response", data: "source" (String,
   * "Dude, Where's My Stuff?"), "target" (String, the requester's "source", so that requesters
   * can filter out responses meant for other plugins), "version" (Integer, 1), "storages"
   * (List&lt;Map&gt;, one per non-empty enabled storage, with keys "category" (String, the
   * storage manager's config key), "name" (String, the storage's display name), "lastUpdated"
   * (Long, unix epoch ms, -1 if unknown) and "items" (List&lt;Map&gt; with keys "id" (Integer,
   * canonical item id) and "quantity" (Long))). The response is posted on the client thread.
   */
  @Subscribe
  void onPluginMessage(PluginMessage pluginMessage) {
    if (pluginMessage.getData() == null) {
      return;
    }

    if (Objects.equals(pluginMessage.getNamespace(), LOADOUT_LAB_NAMESPACE)
        && Objects.equals(pluginMessage.getName(), PLUGIN_MESSAGE_STORAGES_RESPONSE)) {
      handleLoadoutLabStorages(pluginMessage.getData());
      return;
    }

    if (!Objects.equals(pluginMessage.getNamespace(), DudeWheresMyStuffConfig.CONFIG_GROUP)
        || !Objects.equals(pluginMessage.getName(), PLUGIN_MESSAGE_STORAGES_REQUEST)) {
      return;
    }

    Object requestSource = pluginMessage.getData().get(PLUGIN_MESSAGE_KEY_SOURCE);
    if (!(requestSource instanceof String) || ((String) requestSource).isEmpty()) {
      return;
    }

    clientThread.invokeLater(
        () -> {
          Map<String, Object> data = new HashMap<>();
          data.put(PLUGIN_MESSAGE_KEY_SOURCE, PLUGIN_NAME);
          data.put(PLUGIN_MESSAGE_KEY_TARGET, requestSource);
          data.put(PLUGIN_MESSAGE_KEY_VERSION, PLUGIN_MESSAGE_VERSION);
          data.put(PLUGIN_MESSAGE_KEY_STORAGES, storageManagerManager.getPluginMessageStorages());

          eventBus.post(
              new PluginMessage(
                  DudeWheresMyStuffConfig.CONFIG_GROUP, PLUGIN_MESSAGE_STORAGES_RESPONSE, data));
        });
  }

  /**
   * Asks Loadout Lab (if installed) for its tracked storages, so a fresh DWMS install can seed
   * STASH units the player already recorded there. Fire-and-forget: an absent plugin simply never
   * replies, and the reply (if any) is handled by {@link #handleLoadoutLabStorages}.
   */
  private void requestLoadoutLabStorages() {
    if (!storageManagerManager.hasUnobservedStash()) {
      return;
    }

    Map<String, Object> data = new HashMap<>();
    data.put(PLUGIN_MESSAGE_KEY_SOURCE, PLUGIN_NAME);
    eventBus.post(new PluginMessage(LOADOUT_LAB_NAMESPACE, PLUGIN_MESSAGE_STORAGES_REQUEST, data));
  }

  /**
   * Consumes a Loadout Lab "storages-response" and seeds never-observed STASH units from the
   * "stash" collection it reports (see {@link StorageManagerManager#seedStashFromImport}). Only
   * the STASH category is used; live observations always take precedence.
   */
  private void handleLoadoutLabStorages(Map<String, Object> data) {
    if (!PLUGIN_NAME.equals(data.get(PLUGIN_MESSAGE_KEY_TARGET))
        || !(data.get(PLUGIN_MESSAGE_KEY_VERSION) instanceof Number)
        || ((Number) data.get(PLUGIN_MESSAGE_KEY_VERSION)).intValue() != PLUGIN_MESSAGE_VERSION
        || !(data.get(PLUGIN_MESSAGE_KEY_STORAGES) instanceof List)) {
      return;
    }

    Map<Integer, Integer> stashItems = new HashMap<>();
    for (Object storage : (List<?>) data.get(PLUGIN_MESSAGE_KEY_STORAGES)) {
      collectLoadoutLabStashItems(storage, stashItems);
    }

    if (stashItems.isEmpty()) {
      return;
    }

    clientThread.invokeLater(
        () -> {
          if (storageManagerManager.seedStashFromImport(stashItems)) {
            SwingUtilities.invokeLater(panelContainer.getPanel()::softUpdate);
          }
        });
  }

  /** Merges one Loadout Lab storage entry into {@code stashItems} if it is the STASH collection. */
  private void collectLoadoutLabStashItems(Object storage, Map<Integer, Integer> stashItems) {
    if (!(storage instanceof Map)) {
      return;
    }

    Map<?, ?> storageMap = (Map<?, ?>) storage;
    if (!LOADOUT_LAB_STASH_CATEGORY.equals(storageMap.get("category"))
        || !LOADOUT_LAB_STASH_NAME.equals(storageMap.get("name"))
        || !(storageMap.get("items") instanceof List)) {
      return;
    }

    for (Object item : (List<?>) storageMap.get("items")) {
      if (!(item instanceof Map)) {
        continue;
      }

      Object id = ((Map<?, ?>) item).get("id");
      Object quantity = ((Map<?, ?>) item).get("quantity");
      if (id instanceof Number && quantity instanceof Number) {
        stashItems.merge(((Number) id).intValue(),
            (int) Math.min(((Number) quantity).longValue(), Integer.MAX_VALUE), Integer::sum);
      }
    }
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

    return storageManagerManager.getStoredItemCountStorages()
        .mapToLong(storage -> storage.getItemCount(canonicalId)).sum();
  }

  public Map<Storage<?>, Long> getDetailedWithdrawableItemCount(int id) {
    int canonicalId = itemManager.canonicalize(id);

    HashMap<Storage<?>, Long> map = new HashMap<>();

    storageManagerManager.getStoredItemCountStorages()
        .forEach(storage -> {
          long count = storage.getItemCount(canonicalId);

          if (count > 0) {
            map.put(storage, count);
          }
        });

    return map;
  }
}
