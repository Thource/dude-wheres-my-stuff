/*
 * Copyright (c) 2018, Danny <DannysPVM@gmail.com>
 * Copyright (c) 2022, Thource <https://github.com/Thource>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package dev.thource.runelite.dudewheresmystuff.death;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import net.runelite.api.ItemID;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.util.ImageUtil;

public class DeathWorldMapPoint extends WorldMapPoint {

  private final ItemManager itemManager;
  private final BufferedImage worldmapHintArrow;
  private final Point worldmapHintArrowPoint;
  private BufferedImage mapArrow;

  DeathWorldMapPoint(final WorldPoint worldPoint, ItemManager itemManager, int index) {
    super(worldPoint, null);
    this.itemManager = itemManager;

    worldmapHintArrow = new BufferedImage(getMapArrow().getWidth(), getMapArrow().getHeight(),
        BufferedImage.TYPE_INT_ARGB);
    Graphics graphics = worldmapHintArrow.getGraphics();
    graphics.drawImage(getMapArrow(), 0, 0, null);
    graphics.drawImage(itemManager.getImage(ItemID.BONES), 0, 0, null);
    worldmapHintArrowPoint = new Point(worldmapHintArrow.getWidth() / 2,
        worldmapHintArrow.getHeight());

    this.setSnapToEdge(true);
    this.setJumpOnClick(true);
    this.setImage(worldmapHintArrow);
    this.setImagePoint(worldmapHintArrowPoint);
    this.setTooltip("Deathpile");
    this.setName("Deathpile " + index);
  }

  BufferedImage getMapArrow() {
    if (mapArrow != null) {
      return mapArrow;
    }

    mapArrow = ImageUtil.loadImageResource(getClass(), "/util/clue_arrow.png");

    return mapArrow;
  }

  @Override
  public void onEdgeSnap() {
    this.setImage(itemManager.getImage(ItemID.BONES));
    this.setImagePoint(null);
    this.setTooltip(null);
  }

  @Override
  public void onEdgeUnsnap() {
    this.setImage(worldmapHintArrow);
    this.setImagePoint(worldmapHintArrowPoint);
    this.setTooltip("Deathpile");
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
