package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.Storage;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

@Getter
abstract class MinigamesStorage extends Storage<MinigamesStorageType> {

  protected MinigamesStorage(
      MinigamesStorageType type,
      Client client,
      ClientThread clientThread,
      ItemManager itemManager) {
    super(type, client, clientThread, itemManager);
  }

  @Override
  public void reset() {
    items.forEach(item -> item.setQuantity(0));
    lastUpdated = -1L;
    enable();
  }
}
