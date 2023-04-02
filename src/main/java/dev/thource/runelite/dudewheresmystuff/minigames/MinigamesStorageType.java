package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.StorageType;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.vars.AccountType;

/** MinigamesStorageType is used to identify MinigameStorages. */
@RequiredArgsConstructor
@Getter
public enum MinigamesStorageType implements StorageType {
  MAGE_TRAINING_ARENA("Mage Training Arena", false, "magetrainingarena"),
  TITHE_FARM("Tithe Farm", true, "tithefarm"),
  LAST_MAN_STANDING("Last Man Standing", false, "lastmanstanding"),
  NIGHTMARE_ZONE("Nightmare Zone", true, "nightmarezone"),
  PEST_CONTROL("Pest Control", false, "pestcontrol"),
  BARBARIAN_ASSAULT("Barbarian Assault", true, "barbarianassault"),
  GUARDIANS_OF_THE_RIFT("Guardians of the Rift", false, "guardiansoftherift"),
  TEMPOROSS("Tempoross", true, "tempoross"),
  SLAYER("Slayer", true, "slayer"),
  SOUL_WARS("Soul Wars", true, "soulwars"),
  MAHOGANY_HOMES("Mahogany Homes", false, "mahoganyhomes"),
  GIANTS_FOUNDRY("Giants' Foundry", false, "giantsfoundry");

  private final String name;
  private final int itemContainerId = -1;
  // Whether the storage can be updated with no action required by the player
  private final boolean automatic;
  private final String configKey;
  private final boolean membersOnly = true;
  private final List<AccountType> accountTypeBlacklist = null;
}
