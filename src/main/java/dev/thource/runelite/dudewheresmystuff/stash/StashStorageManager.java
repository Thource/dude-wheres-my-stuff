package dev.thource.runelite.dudewheresmystuff.stash;

import com.google.inject.Inject;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.Widget;

/** StashStorageManager is responsible for managing all StashStorages. */
@Slf4j
public class StashStorageManager extends StorageManager<StashStorageType, StashStorage> {

  private boolean updatedThisOpening = false;

  @Inject
  private StashStorageManager(DudeWheresMyStuffPlugin plugin) {
    super(plugin);

    for (StashUnit stashUnit : StashUnit.values()) {
      storages.add(new StashStorage(plugin, stashUnit));
    }
  }

  @Override
  public void onGameTick() {
    super.onGameTick();

    Widget stashChartWidget = client.getWidget(493, 2);
    if (stashChartWidget == null) {
      updatedThisOpening = false;
      return;
    }
    if (updatedThisOpening) {
      return;
    }

    updatedThisOpening = true;

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

    if (updated) {
      updateStorages(storages);
    }
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
        stashStorage.getItems().add(new ItemStack(defaultItemId, 1, plugin));
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
}
