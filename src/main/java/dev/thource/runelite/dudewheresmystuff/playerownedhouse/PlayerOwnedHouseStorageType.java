package dev.thource.runelite.dudewheresmystuff.playerownedhouse;

import com.google.gson.Gson;
import dev.thource.runelite.dudewheresmystuff.JsonEnumData;
import dev.thource.runelite.dudewheresmystuff.JsonStorageType;
import dev.thource.runelite.dudewheresmystuff.StorageTypeData;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Getter;

/**
 * PlayerOwnedHouseStorageType is used to identify PlayerOwnedHouseStorages.
 *
 * <p>The data for each type is loaded from {@code data/PlayerOwnedHouseStorageTypeData.json}, which
 * is generated from {@code PlayerOwnedHouseStorageTypeData} in the test source set by {@code
 * EnumJsonGenerator}. Do not edit the JSON by hand.
 *
 * <p>Call {@link #load(Gson)} with the plugin's injected Gson before using any type.
 */
@Getter
public enum PlayerOwnedHouseStorageType implements JsonStorageType<PlayerOwnedHouseStorageType.Data> {
  TREASURE_CHEST_BEGINNER,
  TREASURE_CHEST_EASY,
  TREASURE_CHEST_MEDIUM,
  TREASURE_CHEST_HARD,
  TREASURE_CHEST_ELITE,
  TREASURE_CHEST_MASTER,
  ARMOUR_CASE,
  MAGIC_WARDROBE,
  FANCY_DRESS_BOX,
  CAPE_RACK,
  MENAGERIE,
  BOSS_LAIR_DISPLAY,
  CAPE_HANGER,
  SPICE_RACK,
  TOY_BOX,
  UNCATEGORISED;

  private static final JsonEnumData<PlayerOwnedHouseStorageType, Data> DATA =
      new JsonEnumData<>(values(), Data.class, "PlayerOwnedHouseStorageTypeData");

  // Whether the storage can be updated with no action required by the player
  private final boolean automatic = false;
  private final boolean membersOnly = true;
  private final List<Integer> accountTypeBlacklist = null;

  /** The base storage type data plus the items each player owned house storage can hold. */
  public static class Data extends StorageTypeData {
    @Nullable private final List<Integer> storableItemIds;

    // Cached read-only view; transient so Gson ignores it in both directions.
    private transient volatile List<Integer> readOnlyStorableItemIds;

    public Data(
        String name,
        int itemContainerId,
        String configKey,
        @Nullable List<Integer> storableItemIds) {
      super(name, itemContainerId, configKey);
      this.storableItemIds = storableItemIds;
    }

    @Nullable
    public List<Integer> getStorableItemIds() {
      if (storableItemIds == null) {
        return null;
      }

      List<Integer> view = readOnlyStorableItemIds;
      if (view == null) {
        // benign race: worst case two equivalent views get created
        view = Collections.unmodifiableList(storableItemIds);
        readOnlyStorableItemIds = view;
      }

      return view;
    }
  }

  public static void load(Gson gson) {
    DATA.load(gson);
  }

  @Override
  public Data getData() {
    return DATA.get(this);
  }

  @Nullable
  public List<Integer> getStorableItemIds() {
    return getData().getStorableItemIds();
  }
}
