package dev.thource.runelite.dudewheresmystuff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.config.ConfigManager;

/** Storage serves as a base class for all trackable data in the plugin. */
@Slf4j
@Getter
public abstract class Storage<T extends StorageType> {

  protected final DudeWheresMyStuffPlugin plugin;
  protected final T type;
  protected boolean enabled = true;
  @Nullable protected StoragePanel storagePanel;
  @Nullable protected String lastSaveString;
  @Setter protected long lastUpdated = -1L;

  protected Storage(T type, DudeWheresMyStuffPlugin plugin) {
    this.type = type;
    this.plugin = plugin;
  }

  protected void createComponentPopupMenu(StorageManager<?, ?> storageManager) {
    if (type.isAutomatic() || storagePanel == null) {
      return;
    }

    final JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
    storagePanel.setComponentPopupMenu(popupMenu);

    final JMenuItem reset = new JMenuItem("Reset");
    reset.addActionListener(
        e -> {
          boolean confirmed = DudeWheresMyStuffPlugin.getConfirmation(storagePanel,
              "Are you sure you want to reset your " + type.getName()
                  + " data?\nThis cannot be undone.", "Confirm reset");

          if (confirmed) {
            deleteData(storageManager);
            plugin.getClientThread().invoke(() -> {
              storagePanel.refreshItems();

              SwingUtilities.invokeLater(() -> {
                storagePanel.update();
                softUpdate();
                storageManager.getStorageTabPanel().reorderStoragePanels();
              });
            });
          }
        });
    popupMenu.add(reset);
  }

  /**
   * Deletes the data for this storage from the config and resets the storage.
   *
   * @param storageManager the storage manager that relates to this storage
   */
  public void deleteData(StorageManager<?, ?> storageManager) {
    String profileKey = storageManager.isPreviewManager() ? plugin.getPreviewProfileKey()
        : storageManager.getConfigManager().getRSProfileKey();

    storageManager.getConfigManager().unsetConfiguration(
        DudeWheresMyStuffConfig.CONFIG_GROUP,
        profileKey,
        getConfigKey(storageManager.getConfigKey())
    );
    reset();
  }

  protected void createStoragePanel(StorageManager<?, ?> storageManager) {
    storagePanel = new StoragePanel(plugin, this, true, false);

    createComponentPopupMenu(storageManager);
  }

  public long getTotalValue() {
    return 0;
  }

  public boolean onGameTick() {
    return false;
  }

  public boolean onGameObjectSpawned(GameObjectSpawned gameObjectSpawned) {
    return false;
  }

  @SuppressWarnings("java:S1172") // the parameter is used in child classes
  public boolean onWidgetLoaded(WidgetLoaded widgetLoaded) {
    return false;
  }

  @SuppressWarnings("java:S1172") // the parameter is used in child classes
  public void onWidgetClosed(WidgetClosed widgetClosed) {
  }

  @SuppressWarnings("java:S1172") // the parameter is used in child classes
  public boolean onChatMessage(ChatMessage chatMessage) {
    return false;
  }

  public boolean onVarbitChanged() {
    return false;
  }

  @SuppressWarnings({"java:S1172", "unused"}) // the parameter is used in child classes
  public boolean onMenuOptionClicked(MenuOptionClicked menuOption) {
    return false;
  }

  /**
   * Can the items in this storage be withdrawn?
   *
   * <p>Should be overridden by subclasses. Should be false for things like minigame points,
   * expired deathbanks, or deposit-only storages such as balloon log storage.
   *
   * <p>NOTE: this abstraction does not work for storages where some items are real and others are
   * not. For example, the ores in blast furnace storage cannot be withdrawn but bars can. Also,
   * some items in the POH may be unable to be withdrawn by UIMs without getting the full set.
   *
   * @return true if the items are withdrawable, otherwise false
   */
  public boolean isWithdrawable() {
    return enabled;
  }

  /**
   * tells the Storage that the onItemContainerChanged event was called and that it should update
   * the player's trackable data.
   *
   * @param itemContainerChanged the ItemContainerChanged event
   * @return whether the data changed
   */
  public boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    return false;
  }

  /**
   * resets the Storage back to it's initial state, useful for when the player logs out, for
   * example.
   */
  public void reset() {
    lastUpdated = -1;
    lastSaveString = null;
    enable();
  }

  /** saves the Storage data to the player's RuneLite RS profile config. */
  public void save(ConfigManager configManager, String profileKey, String managerConfigKey) {
    if (!type.isAutomatic() && lastUpdated == -1L) {
      return;
    }

    String saveString = getSaveString();
    if (Objects.equals(lastSaveString, saveString)) {
      return;
    }

    this.lastSaveString = saveString;
    configManager.setConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, profileKey,
        getConfigKey(managerConfigKey), saveString);
  }

  public String getSaveString() {
    return String.join(";", getSaveValues());
  }

  protected ArrayList<String> getSaveValues() {
    ArrayList<String> saveValues = new ArrayList<>();

    if (!type.isAutomatic()) {
      saveValues.add(SaveFieldFormatter.format(lastUpdated));
    }

    return saveValues;
  }

  /** loads the Storage data from the specified RuneLite RS profile config. */
  public void load(ConfigManager configManager, String managerConfigKey, String profileKey) {
    String data =
        configManager.getConfiguration(
            DudeWheresMyStuffConfig.CONFIG_GROUP,
            profileKey,
            getConfigKey(managerConfigKey),
            String.class);

    if (data == null) {
      return;
    }

    this.lastSaveString = data;
    loadValues(new ArrayList<>(Arrays.asList(data.split(";"))));
  }

  protected void loadValues(ArrayList<String> values) {
    if (!type.isAutomatic()) {
      lastUpdated = SaveFieldLoader.loadLong(values, lastUpdated);
    }
  }

  protected String getConfigKey(String managerConfigKey) {
    return managerConfigKey + "." + type.getConfigKey();
  }

  /** Disables the storage. */
  public void disable() {
    enabled = false;

    if (storagePanel != null) {
      SwingUtilities.invokeLater(() -> storagePanel.setVisible(false));
    }
  }

  /** Disables the storage. */
  public void disable(boolean isMember, int accountType) {
    if ((type.isMembersOnly() && !isMember)
        || (type.getAccountTypeBlacklist() != null
        && type.getAccountTypeBlacklist().contains(accountType))) {
      disable();
    }
  }

  /** Enables the storage. */
  public void enable() {
    enabled = true;

    if (storagePanel != null) {
      SwingUtilities.invokeLater(() -> storagePanel.setVisible(true));
    }
  }

  public String getName() {
    return type.getName();
  }

  /** Updates the storage's panel. */
  public void softUpdate() {
    if (storagePanel == null) {
      return;
    }

    if (!type.isAutomatic()) {
      if (lastUpdated == -1) {
        storagePanel.setFooterText("No data");
      } else {
        long timeSinceLastUpdate = System.currentTimeMillis() - lastUpdated;
        storagePanel.setFooterText(
            "Updated " + DurationFormatter.format(Math.abs(timeSinceLastUpdate)) + " ago");
      }
    }
  }

  public List<ItemStack> getItems() {
    return new ArrayList<>();
  }

  public long getItemCount(int canonicalId) {
    return getItems().stream().filter(stack -> stack.getCanonicalId() == canonicalId)
        .mapToLong(ItemStack::getQuantity).sum();
  }

  protected void updateLastUpdated() {
    lastUpdated = System.currentTimeMillis();
  }
}
