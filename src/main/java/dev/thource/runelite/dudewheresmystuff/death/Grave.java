package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.VarbitID;

/** Grave is responsible for tracking the player's items that are in a grave. */
@Getter
@Slf4j
public class Grave extends ExpiringDeathStorage {

  Grave(DudeWheresMyStuffPlugin plugin, WorldArea worldArea,
      DeathStorageManager deathStorageManager, List<ItemStack> deathItems) {
    super(plugin, false, worldArea, deathStorageManager, deathItems,
        DeathStorageType.GRAVE);
  }

  static Grave load(DudeWheresMyStuffPlugin plugin, DeathStorageManager deathStorageManager,
      String profileKey, String uuid) {
    Grave grave = new Grave(
        plugin,
        null,
        deathStorageManager,
        new ArrayList<>()
    );

    grave.uuid = UUID.fromString(uuid);
    grave.load(deathStorageManager.getConfigManager(), deathStorageManager.getConfigKey(),
        profileKey);

    return grave;
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (!hasExpired() && (chatMessage.getType() == ChatMessageType.SPAM
        || chatMessage.getType() == ChatMessageType.GAMEMESSAGE) && (Objects.equals(
        chatMessage.getMessage(),
        "You successfully retrieved everything from your gravestone."))) {
      // invoke this later, so that it doesn't cause concurrent modification
      deathStorageManager.getClientThread()
          .invokeLater(() -> deathStorageManager.deleteStorage(this));

    }

    return false;
  }

  @Override
  public boolean onGameTick() {
    if (hasExpired()) {
      return false;
    }

    int newExpiryTime = plugin.getClient().getVarbitValue(VarbitID.GRAVESTONE_DURATION);
    if (newExpiryTime <= 0) {
      if (expiryTime >= 6) {
        // invoke this later, so that it doesn't cause concurrent modification
        deathStorageManager.getClientThread()
            .invokeLater(() -> deathStorageManager.deleteStorage(this));

        return false;
      }

      expire();
      return true;
    }

    expiryTime = newExpiryTime;

    if (worldPoint == null) {
      locate();
    }

    return true;
  }

  private void locate() {
    Optional<NPC> graveObject = plugin.getClient().getNpcs().stream()
        .filter(
            n -> n.getId() == NpcID.GRAVESTONE_DEFAULT || n.getId() == NpcID.GRAVESTONE_ANGEL)
        .findFirst();
    if (graveObject.isEmpty()) {
      return;
    }

    if (worldArea != null && !worldArea.contains(graveObject.get().getWorldLocation())) {
      return;
    }

    worldPoint = graveObject.get().getWorldLocation();
    worldArea = null;
    deathStorageManager.refreshMapPoints();
    SwingUtilities.invokeLater(this::setSubTitle);
  }

  void expire() {
    expiredAt = System.currentTimeMillis();

    deathStorageManager.getDeathsOffice().getItems().addAll(items);
    deathStorageManager.updateStorages(
        Collections.singletonList(deathStorageManager.getDeathsOffice()));

    SwingUtilities.invokeLater(() -> {
      if (storagePanel == null) {
        return;
      }

      JLabel footerLabel = storagePanel.getFooterLabel();
      footerLabel.setIcon(null);
      footerLabel.setToolTipText(null);
    });
  }

  @Override
  public int getTotalLifeInMinutes() {
    return 15;
  }
}
