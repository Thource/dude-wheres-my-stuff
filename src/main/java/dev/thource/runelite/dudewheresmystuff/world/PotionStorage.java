package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

/** PotionStorage is responsible for tracking what the player has stored in their potion storage. */
public class PotionStorage extends WorldStorage {

  protected PotionStorage(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.POTION_STORAGE, plugin);

    hasStaticItems = true;

    varbits =
        new int[] {
          VarbitID.POTIONSTORE_VILE_SIZE_AGILITY_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_ANCIENT_BREW,
          VarbitID.POTIONSTORE_VILE_SIZE_ANTI_VENOM,
          VarbitID.POTIONSTORE_VILE_SIZE_ANTI_VENOM_P,
          VarbitID.POTIONSTORE_VILE_SIZE_ANTIDOTE_P,
          VarbitID.POTIONSTORE_VILE_SIZE_ANTIDOTE_PP,
          VarbitID.POTIONSTORE_VILE_SIZE_ANTIFIRE_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_ANTIPOISON,
          VarbitID.POTIONSTORE_VILE_SIZE_ATTACK_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_BATTLEMAGE_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_BASTION_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_COMBAT_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_COMPOST_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_DEFENCE_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_DIVINE_BASTION_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_DIVINE_BATTLEMAGE_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_DIVINE_MAGIC_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_DIVINE_RANGING_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_DIVINE_SUPER_ATTACK_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_DIVINE_SUPER_COMBAT_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_DIVINE_SUPER_DEFENCE_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_DIVINE_SUPER_STRENGTH_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_ENERGY_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_EXTENDED_ANTIFIRE_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_EXTENDED_SUPER_ANTIFIRE_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_FISHING_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_FORGOTTENBREW_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_GUTHIX_BALANCE_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_HUNTER_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_MAGICESSENCE_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_MAGIC_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_MENAPHITEREMEDY_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_PRAYER_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_RANGING_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_RELICYMSBALM_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_RESTORE_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_SANFEWSERUM_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_SARADOMINBREW_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_SERUM207_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_STAMINA_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_STRENGTH_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_SUPER_ANTIFIRE_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_SUPER_ATTACK_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_SUPER_COMBAT_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_SUPER_DEFENCE_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_SUPER_ENERGY_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_SUPER_RESTORE_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_SUPER_STRENGTH_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_SUPER_ANTIPOISON_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_WEAPON_POISON_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_WEAPON_POISON_P_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_WEAPON_POISON_PP_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_ZAMORAK_BREW_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_AGILITY_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_ANCIENT_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_ANTIFIRE_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_ANTIPOISON_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_ANTIDOTE_P_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_ATTACK_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_DEFENCE_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_ENERGY_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_EXTENDED_ANTIFIRE_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_EXTENDED_SUPER_ANTIFIRE_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_FISHING_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_HUNTING_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_MAGIC_ESSENCE_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_MAGIC_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_PRAYER_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_RANGING_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_RESTORE_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_STAMINA_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_STRENGTH_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_SUPER_ANTIFIRE_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_SUPERATTACK_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_SUPER_DEFENCE_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_SUPER_ENERGY_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_SUPER_RESTORE_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_SUPER_STRENGTH_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_ANTIPOISON_SUPER_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_ZAMORAK_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_RELICYMS_MIX,
          VarbitID.POTIONSTORE_VILE_SIZE_PRAYER_REGENERATION_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_GOADING_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_BLIGHTED_2RESTORE_POTION,
          VarbitID.POTIONSTORE_VILE_SIZE_EXTENDED_ANTIVENOMPLUS_POTION,
        };

    items.add(new ItemStack(ItemID._1DOSE1AGILITY, plugin));
    items.add(new ItemStack(ItemID._1DOSEANCIENTBREW, plugin));
    items.add(new ItemStack(ItemID.ANTIVENOM1, plugin));
    items.add(new ItemStack(ItemID.ANTIVENOM_1, plugin));
    items.add(new ItemStack(ItemID.ANTIDOTE_1, plugin));
    items.add(new ItemStack(ItemID.ANTIDOTE__1, plugin));
    items.add(new ItemStack(ItemID._1DOSE1ANTIDRAGON, plugin));
    items.add(new ItemStack(ItemID._1DOSEANTIPOISON, plugin));
    items.add(new ItemStack(ItemID._1DOSE1ATTACK, plugin));
    items.add(new ItemStack(ItemID._1DOSEBATTLEMAGE, plugin));
    items.add(new ItemStack(ItemID._1DOSEBASTION, plugin));
    items.add(new ItemStack(ItemID._1DOSECOMBAT, plugin));
    items.add(new ItemStack(ItemID.SUPERCOMPOST_POTION_1, plugin));
    items.add(new ItemStack(ItemID._1DOSE1DEFENSE, plugin));
    items.add(new ItemStack(ItemID._1DOSEDIVINEBASTION, plugin));
    items.add(new ItemStack(ItemID._1DOSEDIVINEBATTLEMAGE, plugin));
    items.add(new ItemStack(ItemID._1DOSEDIVINEMAGIC, plugin));
    items.add(new ItemStack(ItemID._1DOSEDIVINERANGE, plugin));
    items.add(new ItemStack(ItemID._1DOSEDIVINEATTACK, plugin));
    items.add(new ItemStack(ItemID._1DOSEDIVINECOMBAT, plugin));
    items.add(new ItemStack(ItemID._1DOSEDIVINEDEFENCE, plugin));
    items.add(new ItemStack(ItemID._1DOSEDIVINESTRENGTH, plugin));
    items.add(new ItemStack(ItemID._1DOSE1ENERGY, plugin));
    items.add(new ItemStack(ItemID._1DOSE2ANTIDRAGON, plugin));
    items.add(new ItemStack(ItemID._1DOSE4ANTIDRAGON, plugin));
    items.add(new ItemStack(ItemID._1DOSEFISHERSPOTION, plugin));
    items.add(new ItemStack(ItemID._1DOSEFORGOTTENBREW, plugin));
    items.add(new ItemStack(ItemID.BURGH_GUTHIX_BALANCE_1, plugin));
    items.add(new ItemStack(ItemID._1DOSEHUNTING, plugin));
    items.add(new ItemStack(ItemID._1DOSEMAGICESS, plugin));
    items.add(new ItemStack(ItemID._1DOSE1MAGIC, plugin));
    items.add(new ItemStack(ItemID._1DOSESTATRENEWAL, plugin));
    items.add(new ItemStack(ItemID._1DOSEPRAYERRESTORE, plugin));
    items.add(new ItemStack(ItemID._1DOSERANGERSPOTION, plugin));
    items.add(new ItemStack(ItemID.RELICYMS_BALM1, plugin));
    items.add(new ItemStack(ItemID._1DOSESTATRESTORE, plugin));
    items.add(new ItemStack(ItemID.SANFEW_SALVE_1_DOSE, plugin));
    items.add(new ItemStack(ItemID._1DOSEPOTIONOFSARADOMIN, plugin));
    items.add(new ItemStack(ItemID.MORT_SERUM1, plugin));
    items.add(new ItemStack(ItemID._1DOSESTAMINA, plugin));
    items.add(new ItemStack(ItemID._1DOSE1STRENGTH, plugin));
    items.add(new ItemStack(ItemID._1DOSE3ANTIDRAGON, plugin));
    items.add(new ItemStack(ItemID._1DOSE2ATTACK, plugin));
    items.add(new ItemStack(ItemID._1DOSE2COMBAT, plugin));
    items.add(new ItemStack(ItemID._1DOSE2DEFENSE, plugin));
    items.add(new ItemStack(ItemID._1DOSE2ENERGY, plugin));
    items.add(new ItemStack(ItemID._1DOSE2RESTORE, plugin));
    items.add(new ItemStack(ItemID._1DOSE2STRENGTH, plugin));
    items.add(new ItemStack(ItemID._1DOSE2ANTIPOISON, plugin));
    items.add(new ItemStack(ItemID.WEAPON_POISON, plugin));
    items.add(new ItemStack(ItemID.WEAPON_POISON_, plugin));
    items.add(new ItemStack(ItemID.WEAPON_POISON__, plugin));
    items.add(new ItemStack(ItemID._1DOSEPOTIONOFZAMORAK, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSE1AGILITY, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSEANCIENTBREW, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSE1ANTIDRAGON, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSEANTIPOISON, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_ANTIDOTE_1, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSE1ATTACK, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSE1DEFENSE, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSE1ENERGY, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSE2ANTIDRAGON, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSE4ANTIDRAGON, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSEFISHERSPOTION, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSE1HUNTING, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSEMAGICESS, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSE1MAGIC, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSEPRAYERRESTORE, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSERANGERSPOTION, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSESTATRESTORE, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSESTAMINA, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSE1STRENGTH, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSE3ANTIDRAGON, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSE2ATTACK, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSE2DEFENSE, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSE2ENERGY, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSE2RESTORE, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSE2STRENGTH, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSE2ANTIPOISON, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_1DOSEPOTIONOFZAMORAK, plugin));
    items.add(new ItemStack(ItemID.BRUTAL_RELICYMS_BALM1, plugin));
    items.add(new ItemStack(ItemID._1DOSE1PRAYER_REGENERATION, plugin));
    items.add(new ItemStack(ItemID._1DOSEGOADING, plugin));
    items.add(new ItemStack(ItemID.BLIGHTED_1DOSE2RESTORE, plugin));
    items.add(new ItemStack(ItemID.EXTENDED_ANTIVENOM_1, plugin));
  }
}
