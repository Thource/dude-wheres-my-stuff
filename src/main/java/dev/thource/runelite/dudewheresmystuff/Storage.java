package dev.thource.runelite.dudewheresmystuff;

import dev.thource.runelite.dudewheresmystuff.death.DeathbankType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.vars.AccountType;
import net.runelite.client.config.ConfigManager;

/** Storage serves as a base class for all trackable data in the plugin. */
@Slf4j
@Getter
public abstract class Storage<T extends StorageType> {
  protected final DudeWheresMyStuffPlugin plugin;
  protected T type;
  protected boolean enabled = true;
  @Nullable protected StoragePanel storagePanel;
  @Nullable protected String lastSaveString;
  @Setter protected long lastUpdated = -1L;

  protected Storage(T type, DudeWheresMyStuffPlugin plugin) {
    this.type = type;
    this.plugin = plugin;
  }

  protected void createComponentPopupMenu(StorageManager<?, ?> storageManager) {
    if (type.isAutomatic()) {
      return;
    }

    final JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
    storagePanel.setComponentPopupMenu(popupMenu);

    final JMenuItem reset = new JMenuItem("Reset");
    reset.addActionListener(
        e -> {
          int result = JOptionPane.CANCEL_OPTION;

          try {
            result =
                JOptionPane.showConfirmDialog(
                    storagePanel,
                    "Are you sure you want to reset your " + type.getName() + " data?\nThis cannot be undone.",
                    "Confirm reset",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
          } catch (Exception err) {
            log.warn("Unexpected exception occurred while check for confirm required", err);
          }

          if (result == JOptionPane.OK_OPTION) {
            deleteData(storageManager);
            storagePanel.update();
            softUpdate();
            storageManager.getStorageTabPanel().reorderStoragePanels();
          }
        });
    popupMenu.add(reset);
  }

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

  public boolean onWidgetLoaded(WidgetLoaded widgetLoaded) {
    return false;
  }

  public boolean onWidgetClosed(WidgetClosed widgetClosed) {
    return false;
  }

  public boolean onChatMessage(ChatMessage chatMessage) {
    return false;
  }

  public boolean onVarbitChanged() {
    return false;
  }

  public boolean onMenuOptionClicked(MenuOptionClicked menuOption) {
    return false;
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

  public void disable() {
    enabled = false;

    if (storagePanel != null) {
      SwingUtilities.invokeLater(() -> storagePanel.setVisible(false));
    }
  }

  public void disable(boolean isMember, AccountType accountType) {
    if ((type.isMembersOnly() && !isMember)
        || (type.getAccountTypeBlacklist() != null
            && type.getAccountTypeBlacklist().contains(accountType))) {
      disable();
    }
  }

  public void enable() {
    enabled = true;

    if (storagePanel != null) {
      SwingUtilities.invokeLater(() -> storagePanel.setVisible(true));
    }
  }

  public String getName() {
    return type.getName();
  }

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
}
