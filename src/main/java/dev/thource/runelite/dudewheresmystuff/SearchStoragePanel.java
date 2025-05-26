package dev.thource.runelite.dudewheresmystuff;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.Setter;

class SearchStoragePanel extends StoragePanel {

  @Setter private String searchText = "";

  public SearchStoragePanel(DudeWheresMyStuffPlugin plugin, Storage<?> storage) {
    super(plugin, storage, false, false, true);
  }

  @Override
  protected List<ItemStack> getNewItems() {
    return super.getNewItems().stream()
        .filter(
            item ->
                searchText == null
                    || item.getName()
                    .toLowerCase(Locale.ROOT)
                    .contains(searchText.toLowerCase(Locale.ROOT)))
        .collect(Collectors.toList());
  }
}
