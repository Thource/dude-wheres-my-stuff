package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemBox;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.annotation.Nullable;
import lombok.Getter;

@Getter
public class DeathpileItemBox extends ItemBox {

  private boolean prioritized;
  private int priority;

  DeathpileItemBox(
      DudeWheresMyStuffPlugin plugin, @Nullable ItemStack itemStack, boolean displayEmptyStacks,
      Deathpile deathpile) {
    super(plugin, itemStack, displayEmptyStacks);

    addMouseListener(
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent mouseEvent) {
            if (!deathpile.isSettingPickupOrder() || mouseEvent.getButton() != MouseEvent.BUTTON1) {
              return;
            }

            if (prioritized) {
              deathpile.resetPriority(itemStack);
              resetPriority();
              return;
            }

            setPriority(deathpile.prioritizeItem(itemStack));
          }
        });
  }

  void resetPriority() {
    prioritized = false;
    priority = 0;

    if (imageLabel != null) {
      imageLabel.repaint();
    }
  }

  void setPriority(int priority) {
    prioritized = true;
    this.priority = priority;

    if (imageLabel != null) {
      imageLabel.repaint();
    }
  }
}
