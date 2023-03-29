package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStorage;
import dev.thource.runelite.dudewheresmystuff.SaveFieldFormatter;
import dev.thource.runelite.dudewheresmystuff.SaveFieldLoader;
import java.util.ArrayList;
import java.util.UUID;
import lombok.Getter;

/**
 * DeathStorage is responsible for tracking death storages that hold the players items (deathpiles,
 * deathbanks).
 */
@Getter
public class DeathStorage extends ItemStorage<DeathStorageType> {

  protected UUID uuid = UUID.randomUUID();

  protected DeathStorage(DeathStorageType type, DudeWheresMyStuffPlugin plugin) {
    super(type, plugin);
  }

  @Override
  protected ArrayList<String> getSaveValues() {
    ArrayList<String> saveValues = super.getSaveValues();

    saveValues.add(SaveFieldFormatter.format(uuid));

    return saveValues;
  }

  @Override
  protected void loadValues(ArrayList<String> values) {
    super.loadValues(values);

    uuid = SaveFieldLoader.loadUUID(values, uuid);
  }

  @Override
  protected String getConfigKey(String managerConfigKey) {
    return super.getConfigKey(managerConfigKey) + "." + uuid;
  }
}
