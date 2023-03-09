package dev.thource.runelite.dudewheresmystuff;

import dev.thource.runelite.dudewheresmystuff.death.DeathbankType;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.swing.SwingUtilities;
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
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import org.apache.commons.lang3.math.NumberUtils;

/** Storage serves as a base class for all trackable data in the plugin. */
@Slf4j
@Getter
public abstract class Storage<T extends StorageType> {
  protected final DudeWheresMyStuffPlugin plugin;
  protected T type;
  protected boolean enabled = true;
  @Nullable protected StoragePanel storagePanel;

  @Saved(index = 0) @Setter protected long lastUpdated = -1L;

  protected Storage(T type, DudeWheresMyStuffPlugin plugin) {
    this.type = type;
    this.plugin = plugin;
  }

  protected void createStoragePanel() {
    storagePanel = new StoragePanel(plugin, this, true, false);
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
    enable();
  }

  protected List<Field> getAllFields(Class<?> type) {
    List<Field> fields = new ArrayList<>();
    for (Class<?> c = type; c != null; c = c.getSuperclass()) {
      fields.addAll(Arrays.asList(c.getDeclaredFields()));
    }
    return fields;
  }

  protected Stream<Field> getSavedFields() {
    return getAllFields(this.getClass()).stream()
               .filter(field -> field.isAnnotationPresent(Saved.class))
               .sorted(Comparator.comparing(field -> field.getAnnotation(Saved.class).index()));
  }

  /** saves the Storage data to the player's RuneLite RS profile config. */
  public void save(ConfigManager configManager, String managerConfigKey) {
    String saveString = getSavedFields()
        .map(this::getSaveStringFromField)
        .collect(Collectors.joining(";"));

    configManager.setRSProfileConfiguration(DudeWheresMyStuffConfig.CONFIG_GROUP, getConfigKey(managerConfigKey), saveString);
  }

  protected String getItemStackListSaveString(List<ItemStack> list) {
    return list.stream()
        .map(item -> item.getId() + "x" + item.getQuantity())
        .collect(Collectors.joining(","));
  }

  protected String getSaveStringFromField(Field field) {
    try {
      if (field.getType() == List.class) {
        ParameterizedType listType = (ParameterizedType) field.getGenericType();
        Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];

        if (listClass == ItemStack.class) {
          @SuppressWarnings("unchecked") // at this point we know that the type is List<ItemStack>, regardless of what the compiler thinks
          List<ItemStack> list = (List<ItemStack>) field.get(this);
          return getItemStackListSaveString(list);
        } else {
          return ((List<?>) field.get(this)).stream().map(Object::toString).collect(Collectors.joining(","));
        }
      } else if (field.getType() == WorldPoint.class) {
        WorldPoint worldPoint = (WorldPoint) field.get(this);

        return worldPoint.getX() + "," + worldPoint.getY() + "," + worldPoint.getPlane();
      }

      if (field.getName().equals("lastUpdated")) {
        long lastUpdated = (long) field.get(this);

        lastUpdated = lastUpdated / 10000L * 10000L;

        return Long.toString(lastUpdated);
      }

      return field.get(this).toString();
    } catch (IllegalAccessException e) {
      log.error("Tried saving field " + field.getName() + " of " + this.getClass().getName() + " but received IllegalAccessException! ");
//      e.printStackTrace();
      return "";
    }
  }

  protected void loadItemStackListForField(Field field, String data) throws IllegalAccessException {
    List<ItemStack> list = new ArrayList<>();

    for (String stackData : data.split(",")) {
      String[] stackDataSplit = stackData.split("x");
      if (stackDataSplit.length == 2) {
        list.add(new ItemStack(Integer.parseInt(stackDataSplit[0]), Long.parseLong(stackDataSplit[1]), plugin));
      }
    }

    field.set(this, list);
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

    String[] splitData = data.split(";");
    List<Field> savedFields = getSavedFields().collect(Collectors.toList());

    for (int i = 0; i < Math.min(splitData.length, savedFields.size()); i++) {
      loadField(savedFields.get(i), splitData[i]);
    }
  }

  private void loadField(Field field, String data) {
    try {
      if (field.getType() == long.class) {
        field.setLong(this, Long.parseLong(data));
      } else if (field.getType() == int.class) {
        field.setInt(this, Integer.parseInt(data));
      } else if (field.getType() == boolean.class) {
        field.setBoolean(this, data.equals("true"));
      } else if (field.getType() == UUID.class) {
        field.set(this, UUID.fromString(data));
      } else if (field.getType() == WorldPoint.class) {
        String[] splitData = data.split(",");
        field.set(this, new WorldPoint(Integer.parseInt(splitData[0]), Integer.parseInt(splitData[1]), Integer.parseInt(splitData[2])));
      } else if (field.getType() == DeathbankType.class) {
        field.set(this, DeathbankType.valueOf(data));
      } else if (field.getType() == List.class) {
        ParameterizedType listType = (ParameterizedType) field.getGenericType();
        Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];

        if (listClass == ItemStack.class) {
          loadItemStackListForField(field, data);
        } else {
          log.warn("Unsupported list type in Storage.loadField: " + listClass.getName());
        }
      } else {
        log.warn("Unsupported type in Storage.loadField: " + field.getType().getName());
      }
    } catch (IllegalAccessException e) {
      log.error("Tried loading field " + field.getName() + " of " + this.getClass().getName() + " but received IllegalAccessException!");
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
