package dev.thource.runelite.dudewheresmystuff.carryable;

import com.google.gson.Gson;
import dev.thource.runelite.dudewheresmystuff.JsonEnumData;
import dev.thource.runelite.dudewheresmystuff.JsonStorageType;
import dev.thource.runelite.dudewheresmystuff.StorageTypeData;
import java.util.List;
import lombok.Getter;

/**
 * CarryableStorageType is used to identify CarryableStorages.
 *
 * <p>The data for each type is loaded from {@code data/CarryableStorageTypeData.json}, which is
 * generated from {@code CarryableStorageTypeData} in the test source set by {@code
 * EnumJsonGenerator}. Do not edit the JSON by hand.
 *
 * <p>Call {@link #load(Gson)} with the plugin's injected Gson before using any type.
 */
@Getter
public enum CarryableStorageType implements JsonStorageType<CarryableStorageType.Data> {
  INVENTORY,
  EQUIPMENT,
  LOOTING_BAG,
  SEED_BOX,
  RUNE_POUCH,
  BOTTOMLESS_BUCKET,
  PLANK_SACK,
  BOLT_POUCH,
  GNOMISH_FIRELIGHTER,
  MASTER_SCROLL_BOOK,
  HUNTSMANS_KIT,
  FORESTRY_KIT,
  TACKLE_BOX,
  HERB_SACK,
  CHUGGING_BARREL,
  DIZANAS_QUIVER,
  BOW_STRING_SPOOL;

  private static final JsonEnumData<CarryableStorageType, Data> DATA =
      new JsonEnumData<>(values(), Data.class, "CarryableStorageTypeData");

  private final List<Integer> accountTypeBlacklist = null;

  /** The base storage type data plus the properties specific to carryable storages. */
  @Getter
  public static class Data extends StorageTypeData {
    // Whether the storage can be updated with no action required by the player
    private final boolean automatic;
    private final boolean membersOnly;
    // ids of container items (the id of the rune pouch item, for example)
    private final List<Integer> containerIds;
    private final int emptyOnDeathVarbit;

    public Data(
        String name,
        int itemContainerId,
        boolean automatic,
        String configKey,
        boolean membersOnly,
        List<Integer> containerIds,
        int emptyOnDeathVarbit) {
      super(name, itemContainerId, configKey);
      this.automatic = automatic;
      this.membersOnly = membersOnly;
      this.containerIds = containerIds;
      this.emptyOnDeathVarbit = emptyOnDeathVarbit;
    }
  }

  public static void load(Gson gson) {
    DATA.load(gson);
  }

  @Override
  public Data getData() {
    return DATA.get(this);
  }

  public boolean isAutomatic() {
    return getData().isAutomatic();
  }

  public boolean isMembersOnly() {
    return getData().isMembersOnly();
  }

  public List<Integer> getContainerIds() {
    return getData().getContainerIds();
  }

  public int getEmptyOnDeathVarbit() {
    return getData().getEmptyOnDeathVarbit();
  }
}