package dev.thource.runelite.dudewheresmystuff;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.Setter;

public class SearchStoragePanel extends StoragePanel {

  @Setter private String searchText = "";

  public SearchStoragePanel(DudeWheresMyStuffPlugin plugin, Storage<?> storage) {
    super(plugin, storage, false, false);
  }

  @Override
  protected List<ItemStack> getItems() {
    return super.getItems().stream()
        .filter(
            item ->
                searchText == null
                    || item.getName()
                        .toLowerCase(Locale.ROOT)
                        .contains(searchText.toLowerCase(Locale.ROOT)))
        .collect(Collectors.toList());
  }
}
