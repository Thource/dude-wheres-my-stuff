package dev.thource.runelite.dudewheresmystuff.stash;

import com.google.inject.Inject;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.Tab;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

/** StashStorageManager is responsible for managing all StashStorages. */
@Slf4j
public class StashStorageManager extends StorageManager<StashStorageType, StashStorage> {

  private final ClientThread clientThread;

  @Inject
  private StashStorageManager(
      Client client,
      ClientThread clientThread,
      ItemManager itemManager,
      ConfigManager configManager,
      DudeWheresMyStuffConfig config,
      Notifier notifier,
      DudeWheresMyStuffPlugin plugin) {
    super(client, itemManager, configManager, config, notifier, plugin);

    this.clientThread = clientThread;

    for (StashUnit stashUnit : StashUnit.values()) {
      storages.add(new StashStorage(client, clientThread, itemManager, stashUnit));
    }
  }

  @Override
  public boolean onGameTick() {
    super.onGameTick();

    Widget stashChartWidget = client.getWidget(493, 2);
    if (stashChartWidget == null) {
      return false;
    }

    boolean updated = updateBeginnerStashChartItems();
    if (updateEasyStashChartItems()) {
      updated = true;
    }
    if (updateMediumStashChartItems()) {
      updated = true;
    }
    if (updateHardStashChartItems()) {
      updated = true;
    }
    if (updateEliteStashChartItems()) {
      updated = true;
    }
    if (updateMasterStashChartItems()) {
      updated = true;
    }

    return updated;
  }

  private boolean updateBeginnerStashChartItems() {
    return updateStashChartItems(4);
  }

  private boolean updateEasyStashChartItems() {
    return updateStashChartItems(6);
  }

  private boolean updateMediumStashChartItems() {
    return updateStashChartItems(8);
  }

  private boolean updateHardStashChartItems() {
    return updateStashChartItems(10);
  }

  private boolean updateEliteStashChartItems() {
    return updateStashChartItems(12);
  }

  private boolean updateMasterStashChartItems() {
    return updateStashChartItems(14);
  }

  private boolean updateStashChartItems(int childId) {
    Widget stashWidget = client.getWidget(493, childId);
    if (stashWidget == null) {
      return false;
    }

    Widget[] widgetChildren = stashWidget.getChildren();
    if (widgetChildren == null) {
      return false;
    }

    Optional<StashStorage> stashStorage = Optional.empty();
    boolean built = false;
    boolean filled = false;
    for (Widget widget : widgetChildren) {
      if (widget.getType() == 4) {
        if (stashStorage.isPresent()) {
          updateStashChartItem(stashStorage.get(), filled);
        }

        stashStorage = findStashStorageFromChartText(getChartText(widget, childId));
        built = false;
        filled = false;
      } else if (widget.getType() == 5 && stashStorage.isPresent()) {
        if (built) {
          filled = true;
        } else {
          built = true;
        }
      }
    }
    if (stashStorage.isPresent()) {
      updateStashChartItem(stashStorage.get(), filled);
    }

    return true;
  }

  private String getChartText(Widget widget, int childId) {
    if (Objects.equals(widget.getText(), "Warriors' Guild bank") && childId == 14) {
      return "Warriors' Guild bank (master)";
    }

    return widget.getText();
  }

  private void updateStashChartItem(StashStorage stashStorage, boolean filled) {
    stashStorage.setLastUpdated(System.currentTimeMillis());

    if (filled && stashStorage.getItems().isEmpty()) {
      for (int defaultItemId : stashStorage.getStashUnit().getDefaultItemIds()) {
        stashStorage
            .getItems()
            .add(new ItemStack(defaultItemId, 1, client, clientThread, itemManager));
      }
    } else if (!filled && !stashStorage.getItems().isEmpty()) {
      stashStorage.getItems().clear();
    }
  }

  private Optional<StashStorage> findStashStorageFromChartText(String text) {
    return storages.stream()
        .filter(s -> Objects.equals(s.getStashUnit().getChartText(), text))
        .findFirst();
  }

  @Override
  public String getConfigKey() {
    return "stash";
  }

  @Override
  public Tab getTab() {
    return Tab.STASH_UNITS;
  }
}
