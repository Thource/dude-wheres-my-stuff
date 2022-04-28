package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;

@Getter
public class WorldStorage extends Storage<WorldStorageType> {

  protected WorldStorage(WorldStorageType type, Client client, ItemManager itemManager) {
    super(type, client, itemManager);
  }
}