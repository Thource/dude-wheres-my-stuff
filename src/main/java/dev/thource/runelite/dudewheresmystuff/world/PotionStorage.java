package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.HashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;

/** PotionStorage is responsible for tracking what the player has stored in their potion storage. */
@Slf4j
public class PotionStorage extends WorldStorage {

  @RequiredArgsConstructor
  @Getter
  private static class PotionDoseInfo {
    private final int doses;
    private final ItemStack itemStack;
  }

  private final ItemStack vials;
  private final HashMap<String, ItemStack> potionMap = new HashMap<>();
  @Getter private final HashMap<Integer, PotionDoseInfo> doseMap = new HashMap<>();

  protected PotionStorage(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.POTION_STORAGE, plugin);

    hasStaticItems = true;

    vials = new ItemStack(ItemID.VIAL_EMPTY, plugin);
    items.add(vials);
    doseMap.put(ItemID.VIAL_EMPTY, new PotionDoseInfo(0, vials));

    // regular potions
    addPotion(
        "Agility potion",
        ItemID._1DOSE1AGILITY,
        ItemID._2DOSE1AGILITY,
        ItemID._3DOSE1AGILITY,
        ItemID._4DOSE1AGILITY);
    addPotion(
        "Ancient brew",
        ItemID._1DOSEANCIENTBREW,
        ItemID._2DOSEANCIENTBREW,
        ItemID._3DOSEANCIENTBREW,
        ItemID._4DOSEANCIENTBREW);
    addPotion(
        "Anti-venom", ItemID.ANTIVENOM1, ItemID.ANTIVENOM2, ItemID.ANTIVENOM3, ItemID.ANTIVENOM4);
    addPotion(
        "Anti-venom+",
        ItemID.ANTIVENOM_1,
        ItemID.ANTIVENOM_2,
        ItemID.ANTIVENOM_3,
        ItemID.ANTIVENOM_4);
    addPotion(
        "Antidote+", ItemID.ANTIDOTE_1, ItemID.ANTIDOTE_2, ItemID.ANTIDOTE_3, ItemID.ANTIDOTE_4);
    addPotion(
        "Antidote++",
        ItemID.ANTIDOTE__1,
        ItemID.ANTIDOTE__2,
        ItemID.ANTIDOTE__3,
        ItemID.ANTIDOTE__4);
    addPotion(
        "Antifire potion",
        ItemID._1DOSE1ANTIDRAGON,
        ItemID._2DOSE1ANTIDRAGON,
        ItemID._3DOSE1ANTIDRAGON,
        ItemID._4DOSE1ANTIDRAGON);
    addPotion(
        "Antipoison",
        ItemID._1DOSEANTIPOISON,
        ItemID._2DOSEANTIPOISON,
        ItemID._3DOSEANTIPOISON,
        ItemID._4DOSEANTIPOISON);
    addPotion(
        "Attack potion",
        ItemID._1DOSE1ATTACK,
        ItemID._2DOSE1ATTACK,
        ItemID._3DOSE1ATTACK,
        ItemID._4DOSE1ATTACK);
    addPotion(
        "Battlemage potion",
        ItemID._1DOSEBATTLEMAGE,
        ItemID._2DOSEBATTLEMAGE,
        ItemID._3DOSEBATTLEMAGE,
        ItemID._4DOSEBATTLEMAGE);
    addPotion(
        "Bastion potion",
        ItemID._1DOSEBASTION,
        ItemID._2DOSEBASTION,
        ItemID._3DOSEBASTION,
        ItemID._4DOSEBASTION);
    addPotion(
        "Blighted super restore",
        ItemID.BLIGHTED_1DOSE2RESTORE,
        ItemID.BLIGHTED_2DOSE2RESTORE,
        ItemID.BLIGHTED_3DOSE2RESTORE,
        ItemID.BLIGHTED_4DOSE2RESTORE);
    addPotion(
        "Combat potion",
        ItemID._1DOSECOMBAT,
        ItemID._2DOSECOMBAT,
        ItemID._3DOSECOMBAT,
        ItemID._4DOSECOMBAT);
    addPotion(
        "Compost potion",
        ItemID.SUPERCOMPOST_POTION_1,
        ItemID.SUPERCOMPOST_POTION_2,
        ItemID.SUPERCOMPOST_POTION_3,
        ItemID.SUPERCOMPOST_POTION_4);
    addPotion(
        "Defence potion",
        ItemID._1DOSE1DEFENSE,
        ItemID._2DOSE1DEFENSE,
        ItemID._3DOSE1DEFENSE,
        ItemID._4DOSE1DEFENSE);
    addPotion(
        "Divine bastion",
        ItemID._1DOSEDIVINEBASTION,
        ItemID._2DOSEDIVINEBASTION,
        ItemID._3DOSEDIVINEBASTION,
        ItemID._4DOSEDIVINEBASTION);
    addPotion(
        "Divine battlemage",
        ItemID._1DOSEDIVINEBATTLEMAGE,
        ItemID._2DOSEDIVINEBATTLEMAGE,
        ItemID._3DOSEDIVINEBATTLEMAGE,
        ItemID._4DOSEDIVINEBATTLEMAGE);
    addPotion(
        "Divine magic",
        ItemID._1DOSEDIVINEMAGIC,
        ItemID._2DOSEDIVINEMAGIC,
        ItemID._3DOSEDIVINEMAGIC,
        ItemID._4DOSEDIVINEMAGIC);
    addPotion(
        "Divine ranging",
        ItemID._1DOSEDIVINERANGE,
        ItemID._2DOSEDIVINERANGE,
        ItemID._3DOSEDIVINERANGE,
        ItemID._4DOSEDIVINERANGE);
    addPotion(
        "Divine super attack",
        ItemID._1DOSEDIVINEATTACK,
        ItemID._2DOSEDIVINEATTACK,
        ItemID._3DOSEDIVINEATTACK,
        ItemID._4DOSEDIVINEATTACK);
    addPotion(
        "Divine super combat",
        ItemID._1DOSEDIVINECOMBAT,
        ItemID._2DOSEDIVINECOMBAT,
        ItemID._3DOSEDIVINECOMBAT,
        ItemID._4DOSEDIVINECOMBAT);
    addPotion(
        "Divine super defence",
        ItemID._1DOSEDIVINEDEFENCE,
        ItemID._2DOSEDIVINEDEFENCE,
        ItemID._3DOSEDIVINEDEFENCE,
        ItemID._4DOSEDIVINEDEFENCE);
    addPotion(
        "Divine super strength",
        ItemID._1DOSEDIVINESTRENGTH,
        ItemID._2DOSEDIVINESTRENGTH,
        ItemID._3DOSEDIVINESTRENGTH,
        ItemID._4DOSEDIVINESTRENGTH);
    addPotion(
        "Energy potion",
        ItemID._1DOSE1ENERGY,
        ItemID._2DOSE1ENERGY,
        ItemID._3DOSE1ENERGY,
        ItemID._4DOSE1ENERGY);
    addPotion(
        "Extended anti-venom+",
        ItemID.EXTENDED_ANTIVENOM_1,
        ItemID.EXTENDED_ANTIVENOM_2,
        ItemID.EXTENDED_ANTIVENOM_3,
        ItemID.EXTENDED_ANTIVENOM_4);
    addPotion(
        "Extended antifire",
        ItemID._1DOSE2ANTIDRAGON,
        ItemID._2DOSE2ANTIDRAGON,
        ItemID._3DOSE2ANTIDRAGON,
        ItemID._4DOSE2ANTIDRAGON);
    addPotion(
        "Extended super antifire",
        ItemID._1DOSE4ANTIDRAGON,
        ItemID._2DOSE4ANTIDRAGON,
        ItemID._3DOSE4ANTIDRAGON,
        ItemID._4DOSE4ANTIDRAGON);
    addPotion(
        "Fishing potion",
        ItemID._1DOSEFISHERSPOTION,
        ItemID._2DOSEFISHERSPOTION,
        ItemID._3DOSEFISHERSPOTION,
        ItemID._4DOSEFISHERSPOTION);
    addPotion(
        "Forgotten brew",
        ItemID._1DOSEFORGOTTENBREW,
        ItemID._2DOSEFORGOTTENBREW,
        ItemID._3DOSEFORGOTTENBREW,
        ItemID._4DOSEFORGOTTENBREW);
    addPotion(
        "Goading potion",
        ItemID._1DOSEGOADING,
        ItemID._2DOSEGOADING,
        ItemID._3DOSEGOADING,
        ItemID._4DOSEGOADING);
    addPotion(
        "Guthix balance",
        ItemID.BURGH_GUTHIX_BALANCE_1,
        ItemID.BURGH_GUTHIX_BALANCE_2,
        ItemID.BURGH_GUTHIX_BALANCE_3,
        ItemID.BURGH_GUTHIX_BALANCE_4);
    addPotion(
        "Hunter potion",
        ItemID._1DOSEHUNTING,
        ItemID._2DOSEHUNTING,
        ItemID._3DOSEHUNTING,
        ItemID._4DOSEHUNTING);
    addPotion(
        "Magic essence",
        ItemID._1DOSEMAGICESS,
        ItemID._2DOSEMAGICESS,
        ItemID._3DOSEMAGICESS,
        ItemID._4DOSEMAGICESS);
    addPotion(
        "Magic potion",
        ItemID._1DOSE1MAGIC,
        ItemID._2DOSE1MAGIC,
        ItemID._3DOSE1MAGIC,
        ItemID._4DOSE1MAGIC);
    addPotion(
        "Menaphite remedy",
        ItemID._1DOSESTATRENEWAL,
        ItemID._2DOSESTATRENEWAL,
        ItemID._3DOSESTATRENEWAL,
        ItemID._4DOSESTATRENEWAL);
    addPotion(
        "Prayer potion",
        ItemID._1DOSEPRAYERRESTORE,
        ItemID._2DOSEPRAYERRESTORE,
        ItemID._3DOSEPRAYERRESTORE,
        ItemID._4DOSEPRAYERRESTORE);
    addPotion(
        "Prayer regeneration",
        ItemID._1DOSE1PRAYER_REGENERATION,
        ItemID._2DOSE1PRAYER_REGENERATION,
        ItemID._3DOSE1PRAYER_REGENERATION,
        ItemID._4DOSE1PRAYER_REGENERATION);
    addPotion(
        "Ranging potion",
        ItemID._1DOSERANGERSPOTION,
        ItemID._2DOSERANGERSPOTION,
        ItemID._3DOSERANGERSPOTION,
        ItemID._4DOSERANGERSPOTION);
    addPotion(
        "Relicym's balm",
        ItemID.RELICYMS_BALM1,
        ItemID.RELICYMS_BALM2,
        ItemID.RELICYMS_BALM3,
        ItemID.RELICYMS_BALM4);
    addPotion(
        "Restore potion",
        ItemID._1DOSESTATRESTORE,
        ItemID._2DOSESTATRESTORE,
        ItemID._3DOSESTATRESTORE,
        ItemID._4DOSESTATRESTORE);
    addPotion(
        "Sanfew serum",
        ItemID.SANFEW_SALVE_1_DOSE,
        ItemID.SANFEW_SALVE_2_DOSE,
        ItemID.SANFEW_SALVE_3_DOSE,
        ItemID.SANFEW_SALVE_4_DOSE);
    addPotion(
        "Saradomin brew",
        ItemID._1DOSEPOTIONOFSARADOMIN,
        ItemID._2DOSEPOTIONOFSARADOMIN,
        ItemID._3DOSEPOTIONOFSARADOMIN,
        ItemID._4DOSEPOTIONOFSARADOMIN);
    addPotion(
        "Serum 207",
        ItemID.MORT_SERUM1,
        ItemID.MORT_SERUM2,
        ItemID.MORT_SERUM3,
        ItemID.MORT_SERUM4);
    addPotion(
        "Stamina potion",
        ItemID._1DOSESTAMINA,
        ItemID._2DOSESTAMINA,
        ItemID._3DOSESTAMINA,
        ItemID._4DOSESTAMINA);
    addPotion(
        "Strength potion",
        ItemID._1DOSE1STRENGTH,
        ItemID._2DOSE1STRENGTH,
        ItemID._3DOSE1STRENGTH,
        ItemID.STRENGTH4);
    addPotion(
        "Super antifire potion",
        ItemID._1DOSE3ANTIDRAGON,
        ItemID._2DOSE3ANTIDRAGON,
        ItemID._3DOSE3ANTIDRAGON,
        ItemID._4DOSE3ANTIDRAGON);
    addPotion(
        "Super attack",
        ItemID._1DOSE2ATTACK,
        ItemID._2DOSE2ATTACK,
        ItemID._3DOSE2ATTACK,
        ItemID._4DOSE2ATTACK);
    addPotion(
        "Super combat potion",
        ItemID._1DOSE2COMBAT,
        ItemID._2DOSE2COMBAT,
        ItemID._3DOSE2COMBAT,
        ItemID._4DOSE2COMBAT);
    addPotion(
        "Super defence",
        ItemID._1DOSE2DEFENSE,
        ItemID._2DOSE2DEFENSE,
        ItemID._3DOSE2DEFENSE,
        ItemID._4DOSE2DEFENSE);
    addPotion(
        "Super energy",
        ItemID._1DOSE2ENERGY,
        ItemID._2DOSE2ENERGY,
        ItemID._3DOSE2ENERGY,
        ItemID._4DOSE2ENERGY);
    addPotion(
        "Super restore",
        ItemID._1DOSE2RESTORE,
        ItemID._2DOSE2RESTORE,
        ItemID._3DOSE2RESTORE,
        ItemID._4DOSE2RESTORE);
    addPotion(
        "Super strength",
        ItemID._1DOSE2STRENGTH,
        ItemID._2DOSE2STRENGTH,
        ItemID._3DOSE2STRENGTH,
        ItemID._4DOSE2STRENGTH);
    addPotion(
        "Superantipoison",
        ItemID._1DOSE2ANTIPOISON,
        ItemID._2DOSE2ANTIPOISON,
        ItemID._3DOSE2ANTIPOISON,
        ItemID._4DOSE2ANTIPOISON);
    addPotion("Weapon poison", ItemID.WEAPON_POISON);
    addPotion("Weapon poison(+)", ItemID.WEAPON_POISON_);
    addPotion("Weapon poison(++)", ItemID.WEAPON_POISON__);
    addPotion(
        "Zamorak brew",
        ItemID._1DOSEPOTIONOFZAMORAK,
        ItemID._2DOSEPOTIONOFZAMORAK,
        ItemID._3DOSEPOTIONOFZAMORAK,
        ItemID._4DOSEPOTIONOFZAMORAK);

    // brutal potions
    addPotion("Agility mix", ItemID.BRUTAL_1DOSE1AGILITY, ItemID.BRUTAL_2DOSE1AGILITY);
    addPotion("Ancient mix", ItemID.BRUTAL_1DOSEANCIENTBREW, ItemID.BRUTAL_2DOSEANCIENTBREW);
    addPotion("Antifire mix", ItemID.BRUTAL_1DOSE1ANTIDRAGON, ItemID.BRUTAL_2DOSE1ANTIDRAGON);
    addPotion("Antipoison mix", ItemID.BRUTAL_1DOSEANTIPOISON, ItemID.BRUTAL_2DOSEANTIPOISON);
    addPotion("Antidote+ mix", ItemID.BRUTAL_ANTIDOTE_1, ItemID.BRUTAL_ANTIDOTE_2);
    addPotion("Attack mix", ItemID.BRUTAL_1DOSE1ATTACK, ItemID.BRUTAL_2DOSE1ATTACK);
    addPotion("Combat mix", ItemID.BRUTAL_1DOSECOMBAT, ItemID.BRUTAL_2DOSECOMBAT);
    addPotion("Defence mix", ItemID.BRUTAL_1DOSE1DEFENSE, ItemID.BRUTAL_2DOSE1DEFENSE);
    addPotion("Energy mix", ItemID.BRUTAL_1DOSE1ENERGY, ItemID.BRUTAL_2DOSE1ENERGY);
    addPotion(
        "Extended antifire mix", ItemID.BRUTAL_1DOSE2ANTIDRAGON, ItemID.BRUTAL_2DOSE2ANTIDRAGON);
    addPotion(
        "Ext. super antifire mix", ItemID.BRUTAL_1DOSE4ANTIDRAGON, ItemID.BRUTAL_2DOSE4ANTIDRAGON);
    addPotion("Fishing mix", ItemID.BRUTAL_1DOSEFISHERSPOTION, ItemID.BRUTAL_2DOSEFISHERSPOTION);
    addPotion("Hunting mix", ItemID.BRUTAL_1DOSE1HUNTING, ItemID.BRUTAL_2DOSE1HUNTING);
    addPotion("Magic essence mix", ItemID.BRUTAL_1DOSEMAGICESS, ItemID.BRUTAL_2DOSEMAGICESS);
    addPotion("Magic mix", ItemID.BRUTAL_1DOSE1MAGIC, ItemID.BRUTAL_2DOSE1MAGIC);
    addPotion("Prayer mix", ItemID.BRUTAL_1DOSEPRAYERRESTORE, ItemID.BRUTAL_2DOSEPRAYERRESTORE);
    addPotion("Ranging mix", ItemID.BRUTAL_1DOSERANGERSPOTION, ItemID.BRUTAL_2DOSERANGERSPOTION);
    addPotion("Restore mix", ItemID.BRUTAL_1DOSESTATRESTORE, ItemID.BRUTAL_2DOSESTATRESTORE);
    addPotion("Stamina mix", ItemID.BRUTAL_1DOSESTAMINA, ItemID.BRUTAL_2DOSESTAMINA);
    addPotion("Strength mix", ItemID.BRUTAL_1DOSE1STRENGTH, ItemID.BRUTAL_2DOSE1STRENGTH);
    addPotion("Super antifire mix", ItemID.BRUTAL_1DOSE3ANTIDRAGON, ItemID.BRUTAL_2DOSE3ANTIDRAGON);
    addPotion("Superattack mix", ItemID.BRUTAL_1DOSE2ATTACK, ItemID.BRUTAL_2DOSE2ATTACK);
    addPotion("Super def. mix", ItemID.BRUTAL_1DOSE2DEFENSE, ItemID.BRUTAL_2DOSE2DEFENSE);
    addPotion("Super energy mix", ItemID.BRUTAL_1DOSE2ENERGY, ItemID.BRUTAL_2DOSE2ENERGY);
    addPotion("Super restore mix", ItemID.BRUTAL_1DOSE2RESTORE, ItemID.BRUTAL_2DOSE2RESTORE);
    addPotion("Super str. mix", ItemID.BRUTAL_1DOSE2STRENGTH, ItemID.BRUTAL_2DOSE2STRENGTH);
    addPotion(
        "Anti-poison supermix", ItemID.BRUTAL_1DOSE2ANTIPOISON, ItemID.BRUTAL_2DOSE2ANTIPOISON);
    addPotion(
        "Zamorak mix", ItemID.BRUTAL_1DOSEPOTIONOFZAMORAK, ItemID.BRUTAL_2DOSEPOTIONOFZAMORAK);
    addPotion("Relicym's mix", ItemID.BRUTAL_RELICYMS_BALM1, ItemID.BRUTAL_RELICYMS_BALM2);

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

  private void addPotion(String storageText, int oneDoseId, int... otherDoseIds) {
    var itemStack = new ItemStack(oneDoseId, plugin);
    items.add(itemStack);
    potionMap.put(storageText, itemStack);

    doseMap.put(oneDoseId, new PotionDoseInfo(1, itemStack));
    var doses = 1;
    for (int otherDoseId : otherDoseIds) {
      doseMap.put(otherDoseId, new PotionDoseInfo(++doses, itemStack));
    }
  }

  private boolean updateFromPotionStorageWidget() {
    var potionStorageWidget = plugin.getClient().getWidget(InterfaceID.Bankmain.POTIONSTORE_ITEMS);

    if (potionStorageWidget == null
        || potionStorageWidget.isHidden()
        || potionStorageWidget.getChildren() == null) {
      return false;
    }

    var doseMap = new HashMap<String, Integer>();
    var updated = false;
    String currentPotionString = null;
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

      if (widgetText.startsWith("Doses: ") || widgetText.startsWith("Quantity: ")) {
        if (currentPotionString != null) {
          String qtyText = widgetText.replace("Doses: ", "").replace("Quantity: ", "");
          doseMap.put(currentPotionString, Integer.parseInt(qtyText));
          currentPotionString = null;
        }

        continue;
      }

      if (widgetText.contains("(")) {
        currentPotionString = widgetText;
        if (!potionMap.containsKey(currentPotionString)) {
          var textSplit = widgetText.split("\\s*\\(");
          currentPotionString = textSplit[0];
        }
      }
    }

    for (var entry : potionMap.entrySet()) {
      var potionString = entry.getKey();
      var potionStack = entry.getValue();

      var newQuantity = doseMap.getOrDefault(potionString, 0);
      if (newQuantity != potionStack.getQuantity()) {
        updated = true;
        potionStack.setQuantity(newQuantity);
      }
    }

    updateLastUpdated();
    return updated;
  }

  private boolean updateFromDepositBoxOrBank() {
    var client = plugin.getClient();
    if (client.getVarbitValue(VarbitID.BANK_DEPOSITPOTION) != 1) {
      return false;
    }

    var depositBoxWidget = client.getWidget(InterfaceID.BankDepositbox.FRAME);
    var depositBoxOpen = depositBoxWidget != null && !depositBoxWidget.isHidden();
    var bankWidget = client.getWidget(InterfaceID.Bankmain.ITEMS);
    var bankOpen = bankWidget != null && !bankWidget.isHidden();
    if (bankOpen) {
      var potionStorageWidget =
          plugin.getClient().getWidget(InterfaceID.Bankmain.POTIONSTORE_ITEMS);
      var potionStorageOpen =
          potionStorageWidget != null
              && !potionStorageWidget.isHidden()
              && potionStorageWidget.getChildren() != null;
      if (potionStorageOpen) {
        return false;
      }
    } else if (!depositBoxOpen) {
      return false;
    }

    var updated = false;
    for (ItemStack itemStack :
        ItemContainerWatcher.getInventoryWatcher().getItemsRemovedLastTick()) {

      var potionDoseInfo = doseMap.get(itemStack.getCanonicalId());
      if (potionDoseInfo != null) {
        var potionStack = potionDoseInfo.getItemStack();
        potionStack.setQuantity(
            potionStack.getQuantity() + itemStack.getQuantity() * potionDoseInfo.getDoses());

        vials.setQuantity(vials.getQuantity() + itemStack.getQuantity());

        updated = true;
      }
    }

    if (updated) {
      updateLastUpdated();
    }

    return updated;
  }

  @Override
  public boolean onGameTick() {
    var updated = updateFromPotionStorageWidget();

    if (updateFromDepositBoxOrBank()) {
      updated = true;
    }

    return updated;
  }
}
