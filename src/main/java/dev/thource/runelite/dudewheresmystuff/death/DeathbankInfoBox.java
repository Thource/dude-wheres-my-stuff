package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import java.awt.Color;
import javax.annotation.Nonnull;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.ui.overlay.infobox.InfoBox;

class DeathbankInfoBox extends InfoBox {

  @Getter private final Deathbank deathbank;

  public DeathbankInfoBox(@Nonnull DudeWheresMyStuffPlugin plugin, Deathbank deathbank) {
    super(plugin.getItemManager().getImage(ItemID.OSB7_CAT_HAIR), plugin);
    this.deathbank = deathbank;

    setTooltip("Active deathbank: " + deathbank.getDeathbankType().getName());
  }

  @Override
  public String getText() {
    return null;
  }

  @Override
  public Color getTextColor() {
    return null;
  }
}
