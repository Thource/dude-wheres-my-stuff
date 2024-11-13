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
  private boolean lastPrioritized;
  private int priority;
  private int lastPriority;

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
              resetPriority(true);
              return;
            }

            setPriority(deathpile.prioritizeItem(itemStack), true);
          }
        });
  }

  void repaintLabel() {
    if (imageLabel != null && (lastPrioritized != prioritized || lastPriority != priority)) {
      lastPrioritized = prioritized;
      lastPriority = priority;

      imageLabel.repaint();
    }
  }

  void resetPriority(boolean repaint) {
    if (!prioritized) {
      return;
    }

    prioritized = false;
    priority = 0;

    if (repaint) {
      repaintLabel();
    }
  }

  void setPriority(int priority, boolean repaint) {
    if (prioritized && priority != this.priority) {
      return;
    }

    prioritized = true;
    this.priority = priority;

    if (repaint) {
      repaintLabel();
    }
  }
}
