package dev.thource.runelite.dudewheresmystuff.stash;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirements;

/**
 * Serialisable description of a RuneLite {@link ItemRequirement}. One class covers every kind; only
 * the fields relevant to {@code type} are set, the rest are omitted from the json.
 *
 * <ul>
 *   <li>{@code item}: {@code id}
 *   <li>{@code range}: {@code min} and {@code max} item ids (inclusive)
 *   <li>{@code any}: a {@code name} and the {@code requirements} it can be satisfied by
 *   <li>{@code all}: the {@code requirements} that must all be satisfied
 * </ul>
 *
 * <p>The static factories mirror RuneLite's {@link ItemRequirements} so the data can be written the
 * same way. {@link #toItemRequirement()} builds the real requirement from them.
 */
public class ItemRequirementData {
  private static final String ITEM = "item";
  private static final String RANGE = "range";
  private static final String ANY = "any";
  private static final String ALL = "all";

  private final String type;
  @Nullable private final String name;
  @Nullable private final Integer id;
  @Nullable private final Integer min;
  @Nullable private final Integer max;
  @Nullable private final List<ItemRequirementData> requirements;

  private ItemRequirementData(
      String type,
      @Nullable String name,
      @Nullable Integer id,
      @Nullable Integer min,
      @Nullable Integer max,
      @Nullable List<ItemRequirementData> requirements) {
    this.type = type;
    this.name = name;
    this.id = id;
    this.min = min;
    this.max = max;
    this.requirements = requirements;
  }

  public static ItemRequirementData item(int id) {
    return new ItemRequirementData(ITEM, null, id, null, null, null);
  }

  public static ItemRequirementData range(int min, int max) {
    return new ItemRequirementData(RANGE, null, null, min, max, null);
  }

  public static ItemRequirementData any(String name, ItemRequirementData... requirements) {
    return new ItemRequirementData(ANY, name, null, null, null, Arrays.asList(requirements));
  }

  public static ItemRequirementData all(ItemRequirementData... requirements) {
    return new ItemRequirementData(ALL, null, null, null, null, Arrays.asList(requirements));
  }

  /** Builds the RuneLite requirement this data describes. */
  public ItemRequirement toItemRequirement() {
    switch (type) {
      case ITEM:
        return ItemRequirements.item(id);
      case RANGE:
        return ItemRequirements.range(min, max);
      case ANY:
        return ItemRequirements.any(name, convertRequirements());
      case ALL:
        return ItemRequirements.all(convertRequirements());
      default:
        throw new IllegalStateException("Unknown item requirement type " + type);
    }
  }

  private ItemRequirement[] convertRequirements() {
    if (requirements == null) {
      throw new IllegalStateException(type + " item requirement has no requirements");
    }

    ItemRequirement[] converted = new ItemRequirement[requirements.size()];
    for (int i = 0; i < converted.length; i++) {
      converted[i] = requirements.get(i).toItemRequirement();
    }

    return converted;
  }
}
