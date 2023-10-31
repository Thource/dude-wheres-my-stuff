package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffConfig;
import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.Region;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ItemID;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.util.QuantityFormatter;

class DeathpileInfoBox extends ExpiringDeathStorageInfoBox {

  public DeathpileInfoBox(@Nonnull DudeWheresMyStuffPlugin plugin, Deathpile deathpile) {
    super(plugin, deathpile, ItemID.BONES);
  }
}
