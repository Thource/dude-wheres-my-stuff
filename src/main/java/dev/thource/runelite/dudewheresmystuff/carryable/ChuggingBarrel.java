package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;
import org.apache.commons.lang3.math.NumberUtils;

/** ChuggingBarrel is responsible for tracking the player's potions in their chugging barrel. */
public class ChuggingBarrel extends CarryableStorage {

  private static final Pattern DRINK_PATTERN =
      Pattern.compile("You drink (?:some of )?(?:the|your)? (.*)\\.");
  private static final Pattern DOSES_LEFT_PATTERN =
      Pattern.compile("You have (\\d+) doses? of (.*) left in your barrel\\.");
  private static final Pattern EMPTY_PATTERN =
      Pattern.compile("You have finished all doses of (.*) in your barrel\\.");
  private static final Pattern DEPOSIT_PATTERN =
      Pattern.compile("Your chugging barrel has been filled with (\\d+) doses? of (.*)\\.");

  private final HashMap<String, ItemStack> depositMap = new HashMap<>();
  private final HashMap<String, ArrayList<ItemStack>> drinkMap = new HashMap<>();

  private boolean drinking;

  ChuggingBarrel(DudeWheresMyStuffPlugin plugin) {
    super(CarryableStorageType.CHUGGING_BARREL, plugin);

    hasStaticItems = true;

    // regular potions
    addPotion("Agility potion(1)", "agility potion", ItemID._1DOSE1AGILITY);
    addPotion("Ancient brew(1)", "foul liquid", ItemID._1DOSEANCIENTBREW);
    addPotion("Anti-venom(1)", "antivenom potion", ItemID.ANTIVENOM1);
    addPotion("Anti-venom+(1)", "super antivenom+ potion", ItemID.ANTIVENOM_1);
    addPotion("Antidote+(1)", "antipoison potion", ItemID.ANTIDOTE_1);
    addPotion("Antidote++(1)", "antipoison potion", ItemID.ANTIDOTE__1);
    addPotion("Antifire potion(1)", "antifire potion", ItemID._1DOSE1ANTIDRAGON);
    addPotion("Antipoison(1)", "antipoison potion", ItemID._1DOSEANTIPOISON);
    addPotion("Attack potion(1)", "attack potion", ItemID._1DOSE1ATTACK);
    addPotion("Battlemage potion(1)", "battlemage potion", ItemID._1DOSEBATTLEMAGE);
    addPotion("Bastion potion(1)", "bastion potion", ItemID._1DOSEBASTION);
    addPotion(
        "Blighted super restore(1)",
        "blighted super restore potion",
        ItemID.BLIGHTED_1DOSE2RESTORE);
    addPotion("Combat potion(1)", "combat potion", ItemID._1DOSECOMBAT);
    addPotion("Defence potion(1)", "defence potion", ItemID._1DOSE1DEFENSE);
    addPotion("Divine bastion potion(1)", "divine bastion potion", ItemID._1DOSEDIVINEBASTION);
    addPotion(
        "Divine battlemage potion(1)", "divine battlemage potion", ItemID._1DOSEDIVINEBATTLEMAGE);
    addPotion("Divine magic potion(1)", "divine magic potion", ItemID._1DOSEDIVINEMAGIC);
    addPotion("Divine ranging potion(1)", "divine ranging potion", ItemID._1DOSEDIVINERANGE);
    addPotion(
        "Divine super attack potion(1)", "divine super attack potion", ItemID._1DOSEDIVINEATTACK);
    addPotion("Divine super combat potion(1)", "divine combat potion", ItemID._1DOSEDIVINECOMBAT);
    addPotion(
        "Divine super defence potion(1)",
        "divine super defence potion",
        ItemID._1DOSEDIVINEDEFENCE);
    addPotion(
        "Divine super strength potion(1)",
        "divine super strength potion",
        ItemID._1DOSEDIVINESTRENGTH);
    addPotion("Energy potion(1)", "energy potion", ItemID._1DOSE1ENERGY);
    addPotion(
        "Extended anti-venom+(1)", "extended super antivenom+ potion", ItemID.EXTENDED_ANTIVENOM_1);
    addPotion("Extended antifire(1)", "extended antifire potion", ItemID._1DOSE2ANTIDRAGON);
    addPotion(
        "Extended super antifire(1)", "extended super antifire potion", ItemID._1DOSE4ANTIDRAGON);
    addPotion("Fishing potion(1)", "fishing potion", ItemID._1DOSEFISHERSPOTION);
    addPotion("Forgotten brew(1)", "foul liquid", ItemID._1DOSEFORGOTTENBREW);
    addPotion("Goading potion(1)", "goading potion", ItemID._1DOSEGOADING);
    addPotion("Hunter potion(1)", "Hunter potion", ItemID._1DOSEHUNTING);
    addPotion("Magic essence(1)", "potion of magic essence", ItemID._1DOSEMAGICESS);
    addPotion("Magic potion(1)", "magic potion", ItemID._1DOSE1MAGIC);
    addPotion("Menaphite remedy(1)", "Menaphite remedy", ItemID._1DOSESTATRENEWAL);
    addPotion("Prayer potion(1)", "restore prayer potion", ItemID._1DOSEPRAYERRESTORE);
    addPotion(
        "Prayer regeneration potion(1)",
        "prayer regeneration potion",
        ItemID._1DOSE1PRAYER_REGENERATION);
    addPotion("Ranging potion(1)", "ranging potion", ItemID._1DOSERANGERSPOTION);
    addPotion("Relicym's balm(1)", "Relicym's Balm", ItemID.RELICYMS_BALM1);
    addPotion("Restore potion(1)", "stat restoration potion", ItemID._1DOSESTATRESTORE);
    addPotion("Sanfew serum(1)", "Sanfew Serum", ItemID.SANFEW_SALVE_1_DOSE);
    addPotion("Saradomin brew(1)", "foul liquid", ItemID._1DOSEPOTIONOFSARADOMIN);
    addPotion("Stamina potion(1)", "stamina potion", ItemID._1DOSESTAMINA);
    addPotion("Strength potion(1)", "strength potion", ItemID._1DOSE1STRENGTH);
    addPotion("Super antifire potion(1)", "super antifire potion", ItemID._1DOSE3ANTIDRAGON);
    addPotion("Super attack(1)", "attack potion", ItemID._1DOSE2ATTACK);
    addPotion("Super combat potion(1)", "super combat potion", ItemID._1DOSE2COMBAT);
    addPotion("Super defence(1)", "defence potion", ItemID._1DOSE2DEFENSE);
    addPotion("Super energy(1)", "super energy potion", ItemID._1DOSE2ENERGY);
    addPotion("Super restore(1)", "super restore potion", ItemID._1DOSE2RESTORE);
    addPotion("Super strength(1)", "strength potion", ItemID._1DOSE2STRENGTH);
    addPotion("Superantipoison(1)", "antipoison potion", ItemID._1DOSE2ANTIPOISON);
    addPotion("Zamorak brew(1)", "foul liquid", ItemID._1DOSEPOTIONOFZAMORAK);

    // brutal potions
    addPotion("Agility mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSE1AGILITY);
    addPotion("Ancient mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSEANCIENTBREW);
    addPotion("Antifire mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSE1ANTIDRAGON);
    addPotion("Antipoison mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSEANTIPOISON);
    addPotion("Antidote+ mix(1)", "lumpy potion", ItemID.BRUTAL_ANTIDOTE_1);
    addPotion("Attack mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSE1ATTACK);
    addPotion("Combat mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSECOMBAT);
    addPotion("Defence mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSE1DEFENSE);
    addPotion("Energy mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSE1ENERGY);
    addPotion("Extended antifire mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSE2ANTIDRAGON);
    addPotion("Extended super antifire mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSE4ANTIDRAGON);
    addPotion("Fishing mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSEFISHERSPOTION);
    addPotion("Hunting mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSE1HUNTING);
    addPotion("Magic essence mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSEMAGICESS);
    addPotion("Magic mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSE1MAGIC);
    addPotion("Prayer mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSEPRAYERRESTORE);
    addPotion("Ranging mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSERANGERSPOTION);
    addPotion("Restore mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSESTATRESTORE);
    addPotion("Stamina mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSESTAMINA);
    addPotion("Strength mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSE1STRENGTH);
    addPotion("Super antifire mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSE3ANTIDRAGON);
    addPotion("Superattack mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSE2ATTACK);
    addPotion("Super def. mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSE2DEFENSE);
    addPotion("Super energy mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSE2ENERGY);
    addPotion("Super restore mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSE2RESTORE);
    addPotion("Super str. mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSE2STRENGTH);
    addPotion("Anti-poison supermix(1)", "lumpy potion", ItemID.BRUTAL_1DOSE2ANTIPOISON);
    addPotion("Zamorak mix(1)", "lumpy potion", ItemID.BRUTAL_1DOSEPOTIONOFZAMORAK);
    addPotion("Relicym's mix(1)", "lumpy potion", ItemID.BRUTAL_RELICYMS_BALM1);
  }

  private void addPotion(String depositText, String drinkText, int oneDoseId) {
    var itemStack = new ItemStack(oneDoseId, plugin);
    items.add(itemStack);
    depositMap.put(depositText, itemStack);
    drinkMap.computeIfAbsent(drinkText, (t) -> new ArrayList<>()).add(itemStack);
  }

  @Override
  public boolean onMenuOptionClicked(MenuOptionClicked menuOption) {
    if (menuOption.getWidget() != null
        && type.getContainerIds().contains(menuOption.getWidget().getItemId())
        && menuOption.getMenuOption().equals("Drink")) {
      drinking = true;
    }

    return false;
  }

  @Override
  public boolean onGameTick() {
    boolean didUpdate = super.onGameTick();

    if (drinking) {
      drinking = false;
      updateLastUpdated();
      didUpdate = true;
    }

    Widget textWidget = plugin.getClient().getWidget(InterfaceID.ObjectboxDouble.TEXT);
    if (textWidget != null && textWidget.getText().equals("You disassemble the Chugging barrel.")) {
      resetItems();
      updateLastUpdated();
      didUpdate = true;
    }

    return didUpdate;
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return false;
    }

    if (drinking) {
      var drinkMatcher = DRINK_PATTERN.matcher(chatMessage.getMessage());
      if (drinkMatcher.find()) {
        var drinkText = drinkMatcher.group(1);

        var drankPotions = drinkMap.get(drinkText);
        if (drankPotions != null) {
          drankPotions.stream()
              .filter(is -> is.getQuantity() > 0)
              .forEach(is -> is.setQuantity(is.getQuantity() - 1));
        }
      }
    }

    var depositMatcher = DEPOSIT_PATTERN.matcher(chatMessage.getMessage());
    if (depositMatcher.find()) {
      var doses = NumberUtils.toInt(depositMatcher.group(1));
      var potionName = depositMatcher.group(2);

      var depositedPotion = depositMap.get(potionName);
      if (depositedPotion != null) {
        depositedPotion.setQuantity(depositedPotion.getQuantity() + doses);
        updateLastUpdated();
        return true;
      }
    }

    var dosesLeftMatcher = DOSES_LEFT_PATTERN.matcher(chatMessage.getMessage());
    if (dosesLeftMatcher.find()) {
      var doses = NumberUtils.toInt(dosesLeftMatcher.group(1));
      var potionName = dosesLeftMatcher.group(2);

      var potion = depositMap.get(potionName);
      if (potion != null) {
        potion.setQuantity(doses);
        updateLastUpdated();
        return true;
      }
    }

    var emptyMatcher = EMPTY_PATTERN.matcher(chatMessage.getMessage());
    if (emptyMatcher.find()) {
      var potionName = emptyMatcher.group(1);

      var emptiedPotion = depositMap.get(potionName);
      if (emptiedPotion != null) {
        emptiedPotion.setQuantity(0);
        updateLastUpdated();
        return true;
      }
    }

    return false;
  }
}
