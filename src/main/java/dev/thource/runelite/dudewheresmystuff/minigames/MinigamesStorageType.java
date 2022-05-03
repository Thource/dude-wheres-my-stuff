package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.StorageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MinigamesStorageType implements StorageType {
  MAGE_TRAINING_ARENA("Mage Training Arena", -1, false, "magetrainingarena"),
  TITHE_FARM("Tithe Farm", -1, true, "tithefarm"),
  LAST_MAN_STANDING("Last Man Standing", -1, false, "lastmanstanding"),
  NIGHTMARE_ZONE("Nightmare Zone", -1, true, "nightmarezone"),
  PEST_CONTROL("Pest Control", -1, true, "pestcontrol"),
  BARBARIAN_ASSAULT("Barbarian Assault", -1, true, "barbarianassault"),
  GUARDIANS_OF_THE_RIFT("Guardians of the Rift", -1, true, "guardiansoftherift"),
  TEMPOROSS("Tempoross", -1, true, "tempoross"),
  SLAYER("Slayer", -1, true, "slayer"),
  SOUL_WARS("Soul Wars", -1, true, "soulwars"),
  MAHOGANY_HOMES("Mahogany Homes", -1, true, "mahoganyhomes");

  private final String name;
  private final int itemContainerId;
  // Whether the storage can be updated with no action required by the player
  private final boolean automatic;
  private final String configKey;

  public boolean isMembersOnly() {
    return true;
  }
}
