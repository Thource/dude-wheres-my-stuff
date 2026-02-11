package dev.thource.runelite.dudewheresmystuff.sailing;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.SaveFieldFormatter;
import dev.thource.runelite.dudewheresmystuff.SaveFieldLoader;
import dev.thource.runelite.dudewheresmystuff.StorageManager;
import dev.thource.runelite.dudewheresmystuff.Var;
import java.util.ArrayList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.DBTableID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarbitID;

/** ActiveBoatStorage is responsible for tracking the items in the cargo hold of a player's boat. */
@Getter
public class ActiveBoatStorage extends BoatStorage {

  protected int port;

  private ActiveBoatStorage.State state = ActiveBoatStorage.State.NONE;

  private enum State {
    NONE,
    DEPOSITING,
    CHECK_DEPOSIT,
  }

  private static class BoatVarbits {
    private final int name2Varbit;
    private final int name3Varbit;
    private final int portVarbit;
    private final int hpVarbit;

    private BoatVarbits(SailingStorageType type) {
      if (type == SailingStorageType.BOAT_1) {
        name2Varbit = VarbitID.SAILING_BOAT_1_NAME_2;
        name3Varbit = VarbitID.SAILING_BOAT_1_NAME_3;
        portVarbit = VarbitID.SAILING_BOAT_1_PORT;
        hpVarbit = VarbitID.SAILING_BOAT_1_STORED_HP;
      } else if (type == SailingStorageType.BOAT_2) {
        name2Varbit = VarbitID.SAILING_BOAT_2_NAME_2;
        name3Varbit = VarbitID.SAILING_BOAT_2_NAME_3;
        portVarbit = VarbitID.SAILING_BOAT_2_PORT;
        hpVarbit = VarbitID.SAILING_BOAT_2_STORED_HP;
      } else if (type == SailingStorageType.BOAT_3) {
        name2Varbit = VarbitID.SAILING_BOAT_3_NAME_2;
        name3Varbit = VarbitID.SAILING_BOAT_3_NAME_3;
        portVarbit = VarbitID.SAILING_BOAT_3_PORT;
        hpVarbit = VarbitID.SAILING_BOAT_3_STORED_HP;
      } else if (type == SailingStorageType.BOAT_4) {
        name2Varbit = VarbitID.SAILING_BOAT_4_NAME_2;
        name3Varbit = VarbitID.SAILING_BOAT_4_NAME_3;
        portVarbit = VarbitID.SAILING_BOAT_4_PORT;
        hpVarbit = VarbitID.SAILING_BOAT_4_STORED_HP;
      } else if (type == SailingStorageType.BOAT_5) {
        name2Varbit = VarbitID.SAILING_BOAT_5_NAME_2;
        name3Varbit = VarbitID.SAILING_BOAT_5_NAME_3;
        portVarbit = VarbitID.SAILING_BOAT_5_PORT;
        hpVarbit = VarbitID.SAILING_BOAT_5_STORED_HP;
      } else {
        throw new IllegalArgumentException("Invalid boat type: " + type);
      }
    }
  }

  protected ActiveBoatStorage(SailingStorageType type, DudeWheresMyStuffPlugin plugin) {
    super(type, plugin);
  }

  @Override
  protected void createComponentPopupMenu(StorageManager<?, ?> storageManager) {
    super.createComponentPopupMenu(storageManager);

    if (storagePanel != null) {
      createDebugMenuOptions(storageManager, storagePanel.getComponentPopupMenu());
    }
  }

  private void createDebugMenuOptions(StorageManager<?, ?> storageManager, JPopupMenu popupMenu) {
    if (plugin.isDeveloperMode() && !storageManager.isPreviewManager()) {
      var debugMenu = new JMenu("Debug");
      popupMenu.add(debugMenu);

      final JMenuItem createLostBoat = new JMenuItem("Create lost boat");
      createLostBoat.addActionListener(e -> plugin.getSailingStorageManager().createLostBoat(this));
      debugMenu.add(createLostBoat);
    }
  }

  @Override
  public boolean onVarbitChanged(VarbitChanged varbitChanged) {
    var updated = super.onVarbitChanged(varbitChanged);
    var varbits = new BoatVarbits(type);

    // Check if the boat's name changed
    var name2Var = Var.bit(varbitChanged, varbits.name2Varbit);
    var name3Var = Var.bit(varbitChanged, varbits.name3Varbit);
    var nameUpdated = false;
    if (name2Var.wasChanged()) {
      name2Id = name2Var.getValue(plugin.getClient());
      nameUpdated = true;
    }
    if (name3Var.wasChanged()) {
      name3Id = name3Var.getValue(plugin.getClient());
      nameUpdated = true;
    }

    if (nameUpdated) {
      updateName();
    }

    // Check if the boat's port changed
    var portVar = Var.bit(varbitChanged, varbits.portVarbit);
    if (portVar.wasChanged()) {
      port = portVar.getValue(plugin.getClient());
      updateLocationText();
      updated = true;
    }

    // Check if the boat was capsized
    var hpVar = Var.bit(varbitChanged, varbits.hpVarbit);
    if (hpVar.wasChanged() && hpVar.getValue(plugin.getClient()) == 0 && !items.isEmpty()) {
      // Only create a lost boat if actually capsized (port 254).
      // When crew banks cargo at a dock, HP also goes to 0 but the port remains
      // set to the dock — not capsized.
      if (plugin.getClient().getVarbitValue(varbits.portVarbit) == 254) {
        plugin.getSailingStorageManager().createLostBoat(this);
      }
      items.clear();
      updateLastUpdated();
      updated = true;
    }

    // Check if the player withdrew cargo from the cargo hold
    var carryingVar = Var.bit(varbitChanged, VarbitID.SAILING_CARRYING_CARGO);
    var client = plugin.getClient();
    if (carryingVar.wasChanged() && carryingVar.getValue(client) == 1 && currentlyBoarded()) {
      var wornItemContainer = client.getItemContainer(InventoryID.WORN);
      if (wornItemContainer != null) {
        var cargoItem = wornItemContainer.getItem(3);
        if (cargoItem != null) {
          for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == cargoItem.getId()) {
              items.remove(i);
              updateLastUpdated();
              updated = true;
              break;
            }
          }
        }
      }
    }

    return updated || nameUpdated;
  }

  @Override
  public boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    // check that the player is on their own boat before updating items
    if (itemContainerChanged.getContainerId() > 0x8000 && !currentlyBoarded()) {
      return false;
    }

    return super.onItemContainerChanged(itemContainerChanged);
  }

  private boolean currentlyBoarded() {
    if (plugin.getClient().getVarbitValue(VarbitID.SAILING_PLAYER_IS_ON_PLAYER_BOAT) != 1) {
      return false;
    }

    var currentBoatId = plugin.getClient().getVarbitValue(VarbitID.SAILING_BOAT_SPAWNED);
    if (currentBoatId == 1) {
      return type == SailingStorageType.BOAT_1;
    } else if (currentBoatId == 2) {
      return type == SailingStorageType.BOAT_2;
    } else if (currentBoatId == 3) {
      return type == SailingStorageType.BOAT_3;
    } else if (currentBoatId == 4) {
      return type == SailingStorageType.BOAT_4;
    } else if (currentBoatId == 5) {
      return type == SailingStorageType.BOAT_5;
    }

    return false;
  }

  @Override
  protected void updateLocationText() {
    if (storagePanel == null) {
      return;
    }

    if (lastUpdated == -1L) {
      SwingUtilities.invokeLater(() -> storagePanel.setSubTitle(""));
      return;
    }

    if (port == 254) {
      SwingUtilities.invokeLater(() -> storagePanel.setSubTitle("Capsized"));
      return;
    }

    var client = plugin.getClient();
    plugin
        .getClientThread()
        .invoke(
            () -> {
              var name =
                  client
                      .getDBTableField(
                          DBTableID.SailingDock.Row.SAILING_DOCK_PORT_SARIM + port, 1, 0)[0];
              SwingUtilities.invokeLater(() -> storagePanel.setSubTitle(name.toString()));
            });
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if ((chatMessage.getType() != ChatMessageType.SPAM
            && chatMessage.getType() != ChatMessageType.GAMEMESSAGE)
        || !chatMessage.getMessage().equals("You deposit some cargo into the cargo hold.")
        || !currentlyBoarded()) {
      return false;
    }

    var itemsDeposited = ItemContainerWatcher.getWornWatcher().getItemsRemovedLastTick();
    if (itemsDeposited.isEmpty()) {
      return false;
    }

    items.addAll(itemsDeposited);
    updateLastUpdated();
    return true;
  }

  @Override
  public boolean onMenuOptionClicked(MenuOptionClicked menuOption) {
    if (!currentlyBoarded()
        || !menuOption.getMenuOption().equals("Quick-deposit")
        || !menuOption.getMenuTarget().contains("cargo hold")) {
      return false;
    }

    state = State.DEPOSITING;

    return false;
  }

  @Override
  public boolean onGameTick() {
    var updated = super.onGameTick();

    if (state == State.DEPOSITING || state == State.CHECK_DEPOSIT) {
      var itemsDepositedFromWorn = ItemContainerWatcher.getWornWatcher().getItemsRemovedLastTick();
      var itemsDepositedFromInventory =
          ItemContainerWatcher.getInventoryWatcher().getItemsRemovedLastTick();
      if (!itemsDepositedFromWorn.isEmpty() || !itemsDepositedFromInventory.isEmpty()) {
        items.addAll(itemsDepositedFromWorn);
        items.addAll(itemsDepositedFromInventory);
        updateLastUpdated();
        state = State.NONE;
        return true;
      }

      if (state == State.DEPOSITING) {
        state = State.CHECK_DEPOSIT;
      } else {
        state = State.NONE;
      }
    }

    return updated;
  }

  @Override
  protected ArrayList<String> getSaveValues() {
    ArrayList<String> saveValues = super.getSaveValues();

    saveValues.add(SaveFieldFormatter.format(port));

    return saveValues;
  }

  @Override
  protected void loadValues(ArrayList<String> values) {
    super.loadValues(values);

    port = SaveFieldLoader.loadInt(values, port);
  }
}
