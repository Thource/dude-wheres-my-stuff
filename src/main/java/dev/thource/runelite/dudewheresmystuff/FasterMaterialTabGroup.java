/*
 * Copyright (c) 2018, Psikoi <https://github.com/psikoi>
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

package dev.thource.runelite.dudewheresmystuff;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * This class will be a container (group) for the new Material Tabs. It will contain a list of tabs
 * and a display (JPanel). When a tab is selected, the JPanel "display" will display the content
 * associated with that tab.
 *
 * <p>How to use these tabs:
 *
 * <ol>
 *   <li>1 - Create displays (JPanels) for each tab
 *   <li>2 - Create an empty JPanel to serve as the group's display
 *   <li>3 - Create a new MaterialGroup, passing the panel in step 2 as a param
 *   <li>4 - Create new tabs, passing the group in step 3 and one of the panels in step 1 as params
 *   <li>5 - Add the tabs to the group using the MaterialTabGroup#addTab method
 *   <li>6 - Select one of the tabs using the MaterialTab#select method
 * </ol>
 *
 * @author Psikoi
 */
public class FasterMaterialTabGroup extends JPanel {

  /* The panel on which the content tab's content will be displayed on. */
  private final JPanel display;
  /* A list of all the tabs contained in this group. */
  private final List<FasterMaterialTab> tabs = new ArrayList<>();

  FasterMaterialTabGroup(JPanel display) {
    this.display = display;
    if (display != null) {
      this.display.setLayout(new BorderLayout());
    }
    setLayout(new FlowLayout(FlowLayout.CENTER, 8, 0));
    setOpaque(false);
  }

  public void addTab(FasterMaterialTab tab) {
    tabs.add(tab);
    add(tab, BorderLayout.NORTH);
  }

  /**
   * Selects a tab from the group, and sets the display's content to the tab's associated content.
   *
   * @param selectedTab - The tab to select
   */
  public void select(FasterMaterialTab selectedTab) {
    if (!tabs.contains(selectedTab)) {
      return;
    }

    // If the OnTabSelected returned false, exit the method to prevent tab switching
    if (!selectedTab.select()) {
      return;
    }

    // If the display is available, switch from the old to the new display
    if (display != null) {
      EnhancedSwingUtilities.fastRemoveAll(display);
      display.add(selectedTab.getContent());
      display.revalidate();
      display.repaint();
    }

    // Unselected all other tabs
    for (FasterMaterialTab tab : tabs) {
      if (!tab.equals(selectedTab)) {
        tab.unselect();
      }
    }
  }
}
