package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import java.awt.Color;
import javax.annotation.Nonnull;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.util.ImageUtil;

public class CheckPlayTimeInfoBox extends InfoBox {
  public CheckPlayTimeInfoBox(@Nonnull DudeWheresMyStuffPlugin plugin) {
    super(ImageUtil.loadImageResource(DudeWheresMyStuffPlugin.class, "icon-28.png"), plugin);
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
