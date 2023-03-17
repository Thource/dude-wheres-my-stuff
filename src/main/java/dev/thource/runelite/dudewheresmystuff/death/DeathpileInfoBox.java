package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.Region;
import java.awt.Color;
import javax.annotation.Nonnull;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.client.ui.overlay.infobox.InfoBox;

public class DeathpileInfoBox extends InfoBox {

  @Getter private final Deathpile deathpile;
  public DeathpileInfoBox(@Nonnull DudeWheresMyStuffPlugin plugin, Deathpile deathpile) {
    super(plugin.getItemManager().getImage(ItemID.BONES), plugin);
    this.deathpile = deathpile;

    Region region = Region.get(deathpile.getWorldPoint().getRegionID());
    setTooltip((region == null ? "Unknown" : region.getName()) + " deathpile");
  }

  @Override
  public String getText() {
    if (deathpile.isUseAccountPlayTime()
        && deathpile.getDeathStorageManager().getStartPlayedMinutes() <= 0) {
      return "??";
    }

    int minutesLeft =
        (int) Math.floor((deathpile.getExpiryMs() - System.currentTimeMillis()) / 60000f);

    return (minutesLeft > 0 ? minutesLeft : "<1") + "m";
  }

  @Override
  public Color getTextColor() {
    if (deathpile.isUseAccountPlayTime()
        && deathpile.getDeathStorageManager().getStartPlayedMinutes() <= 0) {
      return Color.ORANGE;
    }

    int minutesLeft =
        (int) Math.floor((deathpile.getExpiryMs() - System.currentTimeMillis()) / 60000f);

    if (minutesLeft <= 5) {
      return Color.RED;
    } else if (minutesLeft <= 10) {
      return Color.ORANGE;
    }
    return Color.WHITE;
  }
}
