package dev.thource.runelite.dudewheresmystuff.death;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.SaveFieldFormatter;
import dev.thource.runelite.dudewheresmystuff.SaveFieldLoader;
import dev.thource.runelite.dudewheresmystuff.StoragePanel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.UUID;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.eventbus.Subscribe;

/** Deathpile is responsible for tracking the player's deathpiled items. */
@Getter
@Slf4j
public class Deathpile extends ExpiringDeathStorage {

  private boolean settingPickupOrder;
  @Getter private final List<ItemStack> pickupOrder = new ArrayList<>();

  Deathpile(
      DudeWheresMyStuffPlugin plugin,
      boolean useAccountPlayTime,
      WorldPoint worldPoint,
      DeathStorageManager deathStorageManager,
      List<ItemStack> deathItems) {
    super(plugin, useAccountPlayTime, worldPoint, deathStorageManager, deathItems,
        DeathStorageType.DEATHPILE);
    refreshColor();
  }

  @Override
  protected StoragePanel newStoragePanel() {
    return new DeathpileStoragePanel(plugin, this, true, false);
  }

  @Override
  protected void createMenuOptions(JPopupMenu popupMenu) {
    if (!deathStorageManager.isPreviewManager()) {
      final JMenuItem prioritize = new JMenuItem("Set pickup order");
      prioritize.addActionListener(
          e -> {
            settingPickupOrder = !settingPickupOrder;

            prioritize.setText(
                settingPickupOrder ? "Stop setting pickup order" : "Set pickup order");
          });
      popupMenu.add(prioritize);

      final JMenuItem clearPickupOrder = new JMenuItem("Clear pickup order");
      clearPickupOrder.addActionListener(e -> resetPickupOrder());
      popupMenu.add(clearPickupOrder);
    }

    super.createMenuOptions(popupMenu);
  }

  static Deathpile load(DudeWheresMyStuffPlugin plugin, DeathStorageManager deathStorageManager,
      String profileKey, String uuid) {
    Deathpile deathpile = new Deathpile(
        plugin,
        true,
        null,
        deathStorageManager,
        new ArrayList<>()
    );

    deathpile.uuid = UUID.fromString(uuid);
    deathpile.load(deathStorageManager.getConfigManager(), deathStorageManager.getConfigKey(),
        profileKey);

    return deathpile;
  }

  private Color generateColor() {
    if (worldPoint == null) {
      return Color.WHITE;
    }

    Random rand = new Random(
        worldPoint.getX() * 200L + worldPoint.getY() * 354L + worldPoint.getPlane() * 42L);

    return plugin.getConfig().deathpileColorScheme().generateColor(rand);
  }

  @Override
  protected void loadValues(ArrayList<String> values) {
    super.loadValues(values);
    pickupOrder.clear();
    pickupOrder.addAll(SaveFieldLoader.loadItems(values, pickupOrder, plugin));

    refreshColor();
  }

  @Override
  protected ArrayList<String> getSaveValues() {
    ArrayList<String> saveValues = super.getSaveValues();

    saveValues.add(SaveFieldFormatter.format(pickupOrder, false));

    return saveValues;
  }

  void refreshColor() {
    color = generateColor();
  }

  @Override
  public int getTotalLifeInMinutes() {
    return 60;
  }

  public void resetPriority(ItemStack itemStack) {
    pickupOrder.remove(itemStack);

    if (storagePanel != null) {
      ((DeathpileStoragePanel) storagePanel).setItemBoxesPriorities();
    }
  }

  public int prioritizeItem(ItemStack itemStack) {
    pickupOrder.add(itemStack);

    return pickupOrder.size();
  }

  private void resetPickupOrder() {
    pickupOrder.clear();

    if (storagePanel != null) {
      storagePanel.getItemBoxes().forEach(ib -> ((DeathpileItemBox) ib).resetPriority(true));
    }
  }

  @Subscribe(priority = -0.9f)
  public void onPostMenuSort() {
    Client client = plugin.getClient();
    // The menu is not rebuilt when it is open, so don't swap or else it will
    // repeatedly swap entries
    if (client.isMenuOpen()) {
      return;
    }

    if (pickupOrder.isEmpty() || !worldPoint.isInScene(client)) {
      return;
    }

    MenuEntry[] clientEntries = client.getMenuEntries();

    int sceneX = worldPoint.getX() - client.getScene().getBaseX();
    int sceneY = worldPoint.getY() - client.getScene().getBaseY();

    boolean hasPileTakeEntries = Arrays.stream(clientEntries).anyMatch(
        e -> e.getOption().equals("Take") && e.getParam0() == sceneX && e.getParam1() == sceneY);

    if (!hasPileTakeEntries) {
      return;
    }

    List<MenuEntry> pileEntries = new ArrayList<>();
    List<MenuEntry> newClientEntries = new ArrayList<>(Arrays.asList(clientEntries));

    for (int i = pickupOrder.size() - 1; i >= 0; i--) {
      ItemStack orderStack = pickupOrder.get(i);
      ListIterator<MenuEntry> listIterator = newClientEntries.listIterator();
      while (listIterator.hasNext()) {
        MenuEntry clientEntry = listIterator.next();
        if (clientEntry.getOption().equals("Take")
            && clientEntry.getIdentifier() == orderStack.getId()) {
          pileEntries.add(clientEntry);
          listIterator.remove();
          break;
        }
      }
    }

    newClientEntries.addAll(pileEntries);

    client.setMenuEntries(newClientEntries.toArray(new MenuEntry[]{}));
  }

  @Override
  public long remove(int id, long quantity) {
    long removed = super.remove(id, quantity);

    if (removed > 0) {
      pickupOrder.stream()
          .filter(i -> i.getId() == id)
          .findFirst()
          .ifPresent(pickupOrder::remove);

      if (storagePanel != null) {
        ((DeathpileStoragePanel) storagePanel).setItemBoxesPriorities();
      }
    }

    return removed;
  }
}
