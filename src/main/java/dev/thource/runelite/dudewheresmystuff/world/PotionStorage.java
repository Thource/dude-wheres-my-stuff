package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;

/** PotionStorage is responsible for tracking what the player has stored in their potion storage. */
@Slf4j
public class PotionStorage extends WorldStorage {

  private final ItemStack vials;
  private final HashMap<String, ItemStack> potionMap = new HashMap<>();

  protected PotionStorage(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.POTION_STORAGE, plugin);

    hasStaticItems = true;

    vials = new ItemStack(ItemID.VIAL_EMPTY, plugin);
    items.add(vials);

    // regular potions
    addPotion("Agility potion", ItemID._1DOSE1AGILITY);
    addPotion("Ancient brew", ItemID._1DOSEANCIENTBREW);
    addPotion("Anti-venom", ItemID.ANTIVENOM1);
    addPotion("Anti-venom+", ItemID.ANTIVENOM_1);
    addPotion("Antidote+", ItemID.ANTIDOTE_1);
    addPotion("Antidote++", ItemID.ANTIDOTE__1);
    addPotion("Antifire potion", ItemID._1DOSE1ANTIDRAGON);
    addPotion("Antipoison", ItemID._1DOSEANTIPOISON);
    addPotion("Attack potion", ItemID._1DOSE1ATTACK);
    addPotion("Battlemage potion", ItemID._1DOSEBATTLEMAGE);
    addPotion("Bastion potion", ItemID._1DOSEBASTION);
    addPotion("Blighted super restore", ItemID.BLIGHTED_1DOSE2RESTORE);
    addPotion("Combat potion", ItemID._1DOSECOMBAT);
    addPotion("Compost potion", ItemID.SUPERCOMPOST_POTION_1);
    addPotion("Defence potion", ItemID._1DOSE1DEFENSE);
    addPotion("Divine bastion", ItemID._1DOSEDIVINEBASTION);
    addPotion("Divine battlemage", ItemID._1DOSEDIVINEBATTLEMAGE);
    addPotion("Divine magic", ItemID._1DOSEDIVINEMAGIC);
    addPotion("Divine ranging", ItemID._1DOSEDIVINERANGE);
    addPotion("Divine super attack", ItemID._1DOSEDIVINEATTACK);
    addPotion("Divine super combat", ItemID._1DOSEDIVINECOMBAT);
    addPotion("Divine super defence", ItemID._1DOSEDIVINEDEFENCE);
    addPotion("Divine super strength", ItemID._1DOSEDIVINESTRENGTH);
    addPotion("Energy potion", ItemID._1DOSE1ENERGY);
    addPotion("Extended anti-venom+", ItemID.EXTENDED_ANTIVENOM_1);
    addPotion("Extended antifire", ItemID._1DOSE2ANTIDRAGON);
    addPotion("Extended super antifire", ItemID._1DOSE4ANTIDRAGON);
    addPotion("Fishing potion", ItemID._1DOSEFISHERSPOTION);
    addPotion("Forgotten brew", ItemID._1DOSEFORGOTTENBREW);
    addPotion("Goading potion", ItemID._1DOSEGOADING);
    addPotion("Guthix balance", ItemID.BURGH_GUTHIX_BALANCE_1);
    addPotion("Hunter potion", ItemID._1DOSEHUNTING);
    addPotion("Magic essence", ItemID._1DOSEMAGICESS);
    addPotion("Magic potion", ItemID._1DOSE1MAGIC);
    addPotion("Menaphite remedy", ItemID._1DOSESTATRENEWAL);
    addPotion("Prayer potion", ItemID._1DOSEPRAYERRESTORE);
    addPotion("Prayer regeneration", ItemID._1DOSE1PRAYER_REGENERATION);
    addPotion("Ranging potion", ItemID._1DOSERANGERSPOTION);
    addPotion("Relicym's balm", ItemID.RELICYMS_BALM1);
    addPotion("Restore potion", ItemID._1DOSESTATRESTORE);
    addPotion("Sanfew serum", ItemID.SANFEW_SALVE_1_DOSE);
    addPotion("Saradomin brew", ItemID._1DOSEPOTIONOFSARADOMIN);
    addPotion("Serum 207", ItemID.MORT_SERUM1);
    addPotion("Stamina potion", ItemID._1DOSESTAMINA);
    addPotion("Strength potion", ItemID._1DOSE1STRENGTH);
    addPotion("Super antifire potion", ItemID._1DOSE3ANTIDRAGON);
    addPotion("Super attack", ItemID._1DOSE2ATTACK);
    addPotion("Super combat potion", ItemID._1DOSE2COMBAT);
    addPotion("Super defence", ItemID._1DOSE2DEFENSE);
    addPotion("Super energy", ItemID._1DOSE2ENERGY);
    addPotion("Super restore", ItemID._1DOSE2RESTORE);
    addPotion("Super strength", ItemID._1DOSE2STRENGTH);
    addPotion("Super antipoison", ItemID._1DOSE2ANTIPOISON);
    addPotion("Weapon poison", ItemID.WEAPON_POISON);
    addPotion("Weapon poison(+)", ItemID.WEAPON_POISON_);
    addPotion("Weapon poison(++)", ItemID.WEAPON_POISON__);
    addPotion("Zamorak brew", ItemID._1DOSEPOTIONOFZAMORAK);

    // brutal potions
    addPotion("Agility mix", ItemID.BRUTAL_1DOSE1AGILITY);
    addPotion("Ancient mix", ItemID.BRUTAL_1DOSEANCIENTBREW);
    addPotion("Antifire mix", ItemID.BRUTAL_1DOSE1ANTIDRAGON);
    addPotion("Antipoison mix", ItemID.BRUTAL_1DOSEANTIPOISON);
    addPotion("Antidote+ mix", ItemID.BRUTAL_ANTIDOTE_1);
    addPotion("Attack mix", ItemID.BRUTAL_1DOSE1ATTACK);
    addPotion("Combat mix", ItemID.BRUTAL_1DOSECOMBAT);
    addPotion("Defence mix", ItemID.BRUTAL_1DOSE1DEFENSE);
    addPotion("Energy mix", ItemID.BRUTAL_1DOSE1ENERGY);
    addPotion("Extended antifire mix", ItemID.BRUTAL_1DOSE2ANTIDRAGON);
    addPotion("Ext. super antifire mix", ItemID.BRUTAL_1DOSE4ANTIDRAGON);
    addPotion("Fishing mix", ItemID.BRUTAL_1DOSEFISHERSPOTION);
    addPotion("Hunting mix", ItemID.BRUTAL_1DOSE1HUNTING);
    addPotion("Magic essence mix", ItemID.BRUTAL_1DOSEMAGICESS);
    addPotion("Magic mix", ItemID.BRUTAL_1DOSE1MAGIC);
    addPotion("Prayer mix", ItemID.BRUTAL_1DOSEPRAYERRESTORE);
    addPotion("Ranging mix", ItemID.BRUTAL_1DOSERANGERSPOTION);
    addPotion("Restore mix", ItemID.BRUTAL_1DOSESTATRESTORE);
    addPotion("Stamina mix", ItemID.BRUTAL_1DOSESTAMINA);
    addPotion("Strength mix", ItemID.BRUTAL_1DOSE1STRENGTH);
    addPotion("Super antifire mix", ItemID.BRUTAL_1DOSE3ANTIDRAGON);
    addPotion("Superattack mix", ItemID.BRUTAL_1DOSE2ATTACK);
    addPotion("Super def. mix", ItemID.BRUTAL_1DOSE2DEFENSE);
    addPotion("Super energy mix", ItemID.BRUTAL_1DOSE2ENERGY);
    addPotion("Super restore mix", ItemID.BRUTAL_1DOSE2RESTORE);
    addPotion("Super str. mix", ItemID.BRUTAL_1DOSE2STRENGTH);
    addPotion("Anti-poison supermix", ItemID.BRUTAL_1DOSE2ANTIPOISON);
    addPotion("Zamorak mix", ItemID.BRUTAL_1DOSEPOTIONOFZAMORAK);
    addPotion("Relicym's mix", ItemID.BRUTAL_RELICYMS_BALM1);

    // unfinished potions
    addPotion("Avantoe potion (unf)", ItemID.AVANTOEVIAL);
    addPotion("Cadantine blood potion (unf)", ItemID.CADANTINE_BLOODVIAL);
    addPotion("Cadantine potion (unf)", ItemID.CADANTINEVIAL);
    addPotion("Dwarf weed potion (unf)", ItemID.DWARFWEEDVIAL);
    addPotion("Guam potion (unf)", ItemID.GUAMVIAL);
    addPotion("Harralander potion (unf)", ItemID.HARRALANDERVIAL);
    addPotion("Huasca potion (unf)", ItemID.HUASCAVIAL);
    addPotion("Irit potion (unf)", ItemID.IRITVIAL);
    addPotion("Kwuarm potion (unf)", ItemID.KWUARMVIAL);
    addPotion("Lantadyme potion (unf)", ItemID.LANTADYMEVIAL);
    addPotion("Marrentill potion (unf)", ItemID.MARRENTILLVIAL);
    addPotion("Ranarr potion (unf)", ItemID.RANARRVIAL);
    addPotion("Snapdragon potion (unf)", ItemID.SNAPDRAGONVIAL);
    addPotion("Tarromin potion (unf)", ItemID.TARROMINVIAL);
    addPotion("Toadflax potion (unf)", ItemID.TOADFLAXVIAL);
    addPotion("Torstol potion (unf)", ItemID.TORSTOLVIAL);
  }

  private void addPotion(String storageText, int oneDoseId) {
    var itemStack = new ItemStack(oneDoseId, plugin);
    items.add(itemStack);
    potionMap.put(storageText, itemStack);
  }

  @Override
  public boolean onGameTick() {
    var potionStorageWidget = plugin.getClient().getWidget(InterfaceID.Bankmain.POTIONSTORE_ITEMS);

    if (potionStorageWidget == null || potionStorageWidget.getChildren() == null) {
      return false;
    }

    var updated = false;
    ItemStack currentPotion = null;
    for (Widget widget : potionStorageWidget.getChildren()) {
      var widgetText = widget.getText();
      if (widgetText.startsWith("Vials:")) {
        var newVials = Integer.parseInt(widgetText.replace("Vials: ", ""));
        if (newVials != vials.getQuantity()) {
          updated = true;
          vials.setQuantity(newVials);
        }

        continue;
      }

      if (widgetText.startsWith("Doses:")) {
        if (currentPotion != null) {
          var newDoses = Integer.parseInt(widgetText.replace("Doses: ", ""));
          if (newDoses != currentPotion.getQuantity()) {
            updated = true;
            currentPotion.setQuantity(newDoses);
          }
        }

        continue;
      }

      if (widgetText.contains("(")) {
        currentPotion = potionMap.get(widgetText);
        if (currentPotion == null) {
          var textSplit = widgetText.split("\\s*\\(");
          currentPotion = potionMap.get(textSplit[0]);
        }

        if (currentPotion == null) {
          log.info("Unknown potion: '{}'", widgetText);
        }
      }
    }

    if (updated) {
      updateLastUpdated();
    }

    return updated;
  }
}
