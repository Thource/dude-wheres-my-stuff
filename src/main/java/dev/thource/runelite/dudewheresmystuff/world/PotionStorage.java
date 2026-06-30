package dev.thource.runelite.dudewheresmystuff.world;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Var;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import org.intellij.lang.annotations.MagicConstant;

/** PotionStorage is responsible for tracking what the player has stored in their potion storage. */
@Slf4j
public class PotionStorage extends WorldStorage {

  private static class PotionDoseInfo {
    private final int qtyVarpId;
    private final int countVarbitId;
    private final ItemStack[] itemStacks;

    public static PotionDoseInfo multi(DudeWheresMyStuffPlugin plugin,
        @MagicConstant(valuesFromClass = VarPlayerID.class) int qtyVarpId,
        @MagicConstant(valuesFromClass = VarbitID.class) int countVarbitId,
        @MagicConstant(valuesFromClass = ItemID.class) int[] doseIds) {
      ItemStack[] itemStacks = Arrays.stream(doseIds).mapToObj(itemId ->
          new ItemStack(itemId, plugin)
      ).toArray(ItemStack[]::new);
      return new PotionDoseInfo(qtyVarpId, countVarbitId, itemStacks);
    }
    public static PotionDoseInfo single(DudeWheresMyStuffPlugin plugin, @MagicConstant(valuesFromClass = VarPlayerID.class) int qtyVarpId,  @MagicConstant(valuesFromClass = ItemID.class) int itemId) {
      ItemStack[] itemStacks = IntStream.of(itemId).mapToObj(id ->
          new ItemStack(id, plugin)
      ).toArray(ItemStack[]::new);
      return new PotionDoseInfo(qtyVarpId, -1, itemStacks);
    }

    private PotionDoseInfo( int qtyVarpId,  int countVarbitId, ItemStack[] itemStacks) {
      this.qtyVarpId = qtyVarpId;
      this.countVarbitId = countVarbitId;
      this.itemStacks = itemStacks;
    }

    public int getQtyVarpId() {
      return this.qtyVarpId;
    }

    public int getCountVarbitId() {
      return this.countVarbitId;
    }

    public ItemStack[] getItemStacks() {
      return this.itemStacks;
    }
  }

  private final ItemStack vials;
  private final List<PotionDoseInfo> potionInfos = new ArrayList<>();

  protected PotionStorage(DudeWheresMyStuffPlugin plugin) {
    super(WorldStorageType.POTION_STORAGE, plugin);

    hasStaticItems = true;

    vials = addPotionSingle(VarPlayerID.POTIONSTORE_VIALS, ItemID.VIAL_EMPTY).getItemStacks()[0];
    addPotion(
        VarPlayerID.POTIONSTORE_AGILITY_POTION, VarbitID.POTIONSTORE_VILE_SIZE_AGILITY_POTION,
        ItemID._1DOSE1AGILITY, ItemID._2DOSE1AGILITY, ItemID._3DOSE1AGILITY, ItemID._4DOSE1AGILITY);
    addPotion(
        VarPlayerID.POTIONSTORE_ANCIENT_BREW, VarbitID.POTIONSTORE_VILE_SIZE_ANCIENT_BREW,
        ItemID._1DOSEANCIENTBREW, ItemID._2DOSEANCIENTBREW, ItemID._3DOSEANCIENTBREW, ItemID._4DOSEANCIENTBREW);
    addPotion(
        VarPlayerID.POTIONSTORE_ANTI_VENOM, VarbitID.POTIONSTORE_VILE_SIZE_ANTI_VENOM,
        ItemID.ANTIVENOM1, ItemID.ANTIVENOM2, ItemID.ANTIVENOM3, ItemID.ANTIVENOM4);
    addPotion(
        VarPlayerID.POTIONSTORE_ANTI_VENOM_P, VarbitID.POTIONSTORE_VILE_SIZE_ANTI_VENOM_P,
        ItemID.ANTIVENOM_1, ItemID.ANTIVENOM_2, ItemID.ANTIVENOM_3, ItemID.ANTIVENOM_4);
    addPotion(
        VarPlayerID.POTIONSTORE_ANTIDOTE_P, VarbitID.POTIONSTORE_VILE_SIZE_ANTIDOTE_P,
        ItemID.ANTIDOTE_1, ItemID.ANTIDOTE_2, ItemID.ANTIDOTE_3, ItemID.ANTIDOTE_4);
    addPotion(
        VarPlayerID.POTIONSTORE_ANTIDOTE_PP, VarbitID.POTIONSTORE_VILE_SIZE_ANTIDOTE_PP,
        ItemID.ANTIDOTE__1, ItemID.ANTIDOTE__2, ItemID.ANTIDOTE__3, ItemID.ANTIDOTE__4);
    addPotion(
        VarPlayerID.POTIONSTORE_ANTIFIRE_POTION, VarbitID.POTIONSTORE_VILE_SIZE_ANTIFIRE_POTION,
        ItemID._1DOSE1ANTIDRAGON, ItemID._2DOSE1ANTIDRAGON, ItemID._3DOSE1ANTIDRAGON, ItemID._4DOSE1ANTIDRAGON);
    addPotion(
        VarPlayerID.POTIONSTORE_ANTIPOISON, VarbitID.POTIONSTORE_VILE_SIZE_ANTIPOISON,
        ItemID._1DOSEANTIPOISON, ItemID._2DOSEANTIPOISON, ItemID._3DOSEANTIPOISON, ItemID._4DOSEANTIPOISON);
    addPotion(
        VarPlayerID.POTIONSTORE_ATTACK_POTION,  VarbitID.POTIONSTORE_VILE_SIZE_ATTACK_POTION,
        ItemID._1DOSE1ATTACK, ItemID._2DOSE1ATTACK, ItemID._3DOSE1ATTACK, ItemID._4DOSE1ATTACK);
    addPotion(
        VarPlayerID.POTIONSTORE_BATTLEMAGE_POTION, VarbitID.POTIONSTORE_VILE_SIZE_BATTLEMAGE_POTION,
        ItemID._1DOSEBATTLEMAGE, ItemID._2DOSEBATTLEMAGE, ItemID._3DOSEBATTLEMAGE, ItemID._4DOSEBATTLEMAGE);
    addPotion(
        VarPlayerID.POTIONSTORE_BASTION_POTION,  VarbitID.POTIONSTORE_VILE_SIZE_BASTION_POTION,
        ItemID._1DOSEBASTION, ItemID._2DOSEBASTION, ItemID._3DOSEBASTION, ItemID._4DOSEBASTION);
    addPotion(
        VarPlayerID.POTIONSTORE_BLIGHTED_2RESTORE_POTION, VarbitID.POTIONSTORE_VILE_SIZE_BLIGHTED_2RESTORE_POTION,
        ItemID.BLIGHTED_1DOSE2RESTORE, ItemID.BLIGHTED_2DOSE2RESTORE, ItemID.BLIGHTED_3DOSE2RESTORE, ItemID.BLIGHTED_4DOSE2RESTORE);
    addPotion(
        VarPlayerID.POTIONSTORE_COMBAT_POTION, VarbitID.POTIONSTORE_VILE_SIZE_COMBAT_POTION,
        ItemID._1DOSECOMBAT, ItemID._2DOSECOMBAT, ItemID._3DOSECOMBAT, ItemID._4DOSECOMBAT);
    addPotion(
        VarPlayerID.POTIONSTORE_COMPOST_POTION,  VarbitID.POTIONSTORE_VILE_SIZE_COMPOST_POTION,
        ItemID.SUPERCOMPOST_POTION_1, ItemID.SUPERCOMPOST_POTION_2, ItemID.SUPERCOMPOST_POTION_3, ItemID.SUPERCOMPOST_POTION_4);
    addPotion(
        VarPlayerID.POTIONSTORE_DEFENCE_POTION, VarbitID.POTIONSTORE_VILE_SIZE_DEFENCE_POTION,
        ItemID._1DOSE1DEFENSE, ItemID._2DOSE1DEFENSE, ItemID._3DOSE1DEFENSE, ItemID._4DOSE1DEFENSE);
    addPotion(
        VarPlayerID.POTIONSTORE_DIVINE_BASTION_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_BASTION_POTION,
        ItemID._1DOSEDIVINEBASTION,
        ItemID._2DOSEDIVINEBASTION,
        ItemID._3DOSEDIVINEBASTION,
        ItemID._4DOSEDIVINEBASTION);
    addPotion(
        VarPlayerID.POTIONSTORE_DIVINE_BATTLEMAGE_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_BATTLEMAGE_POTION,
        ItemID._1DOSEDIVINEBATTLEMAGE,
        ItemID._2DOSEDIVINEBATTLEMAGE,
        ItemID._3DOSEDIVINEBATTLEMAGE,
        ItemID._4DOSEDIVINEBATTLEMAGE);
    addPotion(
        VarPlayerID.POTIONSTORE_DIVINE_MAGIC_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_DIVINE_MAGIC_POTION,
        ItemID._1DOSEDIVINEMAGIC,
        ItemID._2DOSEDIVINEMAGIC,
        ItemID._3DOSEDIVINEMAGIC,
        ItemID._4DOSEDIVINEMAGIC);
    addPotion(
        VarPlayerID.POTIONSTORE_DIVINE_RANGING_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_DIVINE_RANGING_POTION,
        ItemID._1DOSEDIVINERANGE,
        ItemID._2DOSEDIVINERANGE,
        ItemID._3DOSEDIVINERANGE,
        ItemID._4DOSEDIVINERANGE);
    addPotion(
        VarPlayerID.POTIONSTORE_DIVINE_SUPER_ATTACK_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_DIVINE_SUPER_ATTACK_POTION,
        ItemID._1DOSEDIVINEATTACK,
        ItemID._2DOSEDIVINEATTACK,
        ItemID._3DOSEDIVINEATTACK,
        ItemID._4DOSEDIVINEATTACK);
    addPotion(
        VarPlayerID.POTIONSTORE_DIVINE_SUPER_COMBAT_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_DIVINE_SUPER_COMBAT_POTION,
        ItemID._1DOSEDIVINECOMBAT,
        ItemID._2DOSEDIVINECOMBAT,
        ItemID._3DOSEDIVINECOMBAT,
        ItemID._4DOSEDIVINECOMBAT);
    addPotion(
        VarPlayerID.POTIONSTORE_DIVINE_SUPER_DEFENCE_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_DIVINE_SUPER_DEFENCE_POTION,
        ItemID._1DOSEDIVINEDEFENCE,
        ItemID._2DOSEDIVINEDEFENCE,
        ItemID._3DOSEDIVINEDEFENCE,
        ItemID._4DOSEDIVINEDEFENCE);
    addPotion(
        VarPlayerID.POTIONSTORE_DIVINE_SUPER_STRENGTH_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_DIVINE_SUPER_STRENGTH_POTION,
        ItemID._1DOSEDIVINESTRENGTH,
        ItemID._2DOSEDIVINESTRENGTH,
        ItemID._3DOSEDIVINESTRENGTH,
        ItemID._4DOSEDIVINESTRENGTH);
    addPotion(
        VarPlayerID.POTIONSTORE_ENERGY_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_ENERGY_POTION,
        ItemID._1DOSE1ENERGY,
        ItemID._2DOSE1ENERGY,
        ItemID._3DOSE1ENERGY,
        ItemID._4DOSE1ENERGY);
    addPotion(
        VarPlayerID.POTIONSTORE_EXTENDED_ANTIVENOMPLUS_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_EXTENDED_ANTIVENOMPLUS_POTION,
        ItemID.EXTENDED_ANTIVENOM_1,
        ItemID.EXTENDED_ANTIVENOM_2,
        ItemID.EXTENDED_ANTIVENOM_3,
        ItemID.EXTENDED_ANTIVENOM_4);
    addPotion(
        VarPlayerID.POTIONSTORE_EXTENDED_ANTIFIRE_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_EXTENDED_ANTIFIRE_POTION,
        ItemID._1DOSE2ANTIDRAGON,
        ItemID._2DOSE2ANTIDRAGON,
        ItemID._3DOSE2ANTIDRAGON,
        ItemID._4DOSE2ANTIDRAGON);
    addPotion(
        VarPlayerID.POTIONSTORE_EXTENDED_SUPER_ANTIFIRE_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_EXTENDED_SUPER_ANTIFIRE_POTION,
        ItemID._1DOSE4ANTIDRAGON,
        ItemID._2DOSE4ANTIDRAGON,
        ItemID._3DOSE4ANTIDRAGON,
        ItemID._4DOSE4ANTIDRAGON);
    addPotion(
        VarPlayerID.POTIONSTORE_FISHING_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_FISHING_POTION,
        ItemID._1DOSEFISHERSPOTION,
        ItemID._2DOSEFISHERSPOTION,
        ItemID._3DOSEFISHERSPOTION,
        ItemID._4DOSEFISHERSPOTION);
    addPotion(
        VarPlayerID.POTIONSTORE_FORGOTTENBREW_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_FORGOTTENBREW_POTION,
        ItemID._1DOSEFORGOTTENBREW,
        ItemID._2DOSEFORGOTTENBREW,
        ItemID._3DOSEFORGOTTENBREW,
        ItemID._4DOSEFORGOTTENBREW);
    addPotion(
        VarPlayerID.POTIONSTORE_GOADING_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_GOADING_POTION,
        ItemID._1DOSEGOADING,
        ItemID._2DOSEGOADING,
        ItemID._3DOSEGOADING,
        ItemID._4DOSEGOADING);
    addPotion(
        VarPlayerID.POTIONSTORE_GUTHIX_BALANCE_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_GUTHIX_BALANCE_POTION,
        ItemID.BURGH_GUTHIX_BALANCE_1,
        ItemID.BURGH_GUTHIX_BALANCE_2,
        ItemID.BURGH_GUTHIX_BALANCE_3,
        ItemID.BURGH_GUTHIX_BALANCE_4);
    addPotion(
        VarPlayerID.POTIONSTORE_HUNTER_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_HUNTER_POTION,
        ItemID._1DOSEHUNTING,
        ItemID._2DOSEHUNTING,
        ItemID._3DOSEHUNTING,
        ItemID._4DOSEHUNTING);
    addPotion(
        VarPlayerID.POTIONSTORE_MAGICESSENCE_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_MAGICESSENCE_POTION,
        ItemID._1DOSEMAGICESS,
        ItemID._2DOSEMAGICESS,
        ItemID._3DOSEMAGICESS,
        ItemID._4DOSEMAGICESS);
    addPotion(
        VarPlayerID.POTIONSTORE_MAGIC_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_MAGIC_POTION,
        ItemID._1DOSE1MAGIC,
        ItemID._2DOSE1MAGIC,
        ItemID._3DOSE1MAGIC,
        ItemID._4DOSE1MAGIC);
    addPotion(
        VarPlayerID.POTIONSTORE_MENAPHITEREMEDY_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_MENAPHITEREMEDY_POTION,
        ItemID._1DOSESTATRENEWAL,
        ItemID._2DOSESTATRENEWAL,
        ItemID._3DOSESTATRENEWAL,
        ItemID._4DOSESTATRENEWAL);
    addPotion(
        VarPlayerID.POTIONSTORE_PRAYER_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_PRAYER_POTION,
        ItemID._1DOSEPRAYERRESTORE,
        ItemID._2DOSEPRAYERRESTORE,
        ItemID._3DOSEPRAYERRESTORE,
        ItemID._4DOSEPRAYERRESTORE);
    addPotion(
        VarPlayerID.POTIONSTORE_PRAYER_REGENERATION_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_PRAYER_REGENERATION_POTION,
        ItemID._1DOSE1PRAYER_REGENERATION,
        ItemID._2DOSE1PRAYER_REGENERATION,
        ItemID._3DOSE1PRAYER_REGENERATION,
        ItemID._4DOSE1PRAYER_REGENERATION);
    addPotion(
        VarPlayerID.POTIONSTORE_RANGING_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_RANGING_POTION,
        ItemID._1DOSERANGERSPOTION,
        ItemID._2DOSERANGERSPOTION,
        ItemID._3DOSERANGERSPOTION,
        ItemID._4DOSERANGERSPOTION);
    addPotion(
        VarPlayerID.POTIONSTORE_RELICYMSBALM_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_RELICYMSBALM_POTION,
        ItemID.RELICYMS_BALM1,
        ItemID.RELICYMS_BALM2,
        ItemID.RELICYMS_BALM3,
        ItemID.RELICYMS_BALM4);
    addPotion(
        VarPlayerID.POTIONSTORE_RESTORE_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_RESTORE_POTION,
        ItemID._1DOSESTATRESTORE,
        ItemID._2DOSESTATRESTORE,
        ItemID._3DOSESTATRESTORE,
        ItemID._4DOSESTATRESTORE);
    addPotion(
        VarPlayerID.POTIONSTORE_SANFEWSERUM_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_SANFEWSERUM_POTION,
        ItemID.SANFEW_SALVE_1_DOSE,
        ItemID.SANFEW_SALVE_2_DOSE,
        ItemID.SANFEW_SALVE_3_DOSE,
        ItemID.SANFEW_SALVE_4_DOSE);
    addPotion(
        VarPlayerID.POTIONSTORE_SARADOMINBREW_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_SARADOMINBREW_POTION,
        ItemID._1DOSEPOTIONOFSARADOMIN,
        ItemID._2DOSEPOTIONOFSARADOMIN,
        ItemID._3DOSEPOTIONOFSARADOMIN,
        ItemID._4DOSEPOTIONOFSARADOMIN);
    addPotion(
        VarPlayerID.POTIONSTORE_SERUM207_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_SERUM207_POTION,
        ItemID.MORT_SERUM1,
        ItemID.MORT_SERUM2,
        ItemID.MORT_SERUM3,
        ItemID.MORT_SERUM4);
    addPotion(
        VarPlayerID.POTIONSTORE_STAMINA_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_STAMINA_POTION,
        ItemID._1DOSESTAMINA,
        ItemID._2DOSESTAMINA,
        ItemID._3DOSESTAMINA,
        ItemID._4DOSESTAMINA);
    addPotion(
        VarPlayerID.POTIONSTORE_STRENGTH_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_STRENGTH_POTION,
        ItemID._1DOSE1STRENGTH,
        ItemID._2DOSE1STRENGTH,
        ItemID._3DOSE1STRENGTH,
        ItemID.STRENGTH4);
    addPotion(
        VarPlayerID.POTIONSTORE_SUPER_ANTIFIRE_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_SUPER_ANTIFIRE_POTION,
        ItemID._1DOSE3ANTIDRAGON,
        ItemID._2DOSE3ANTIDRAGON,
        ItemID._3DOSE3ANTIDRAGON,
        ItemID._4DOSE3ANTIDRAGON);
    addPotion(
        VarPlayerID.POTIONSTORE_SUPER_ATTACK_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_SUPER_ATTACK_POTION,
        ItemID._1DOSE2ATTACK,
        ItemID._2DOSE2ATTACK,
        ItemID._3DOSE2ATTACK,
        ItemID._4DOSE2ATTACK);
    addPotion(
        VarPlayerID.POTIONSTORE_SUPER_COMBAT_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_SUPER_COMBAT_POTION,
        ItemID._1DOSE2COMBAT,
        ItemID._2DOSE2COMBAT,
        ItemID._3DOSE2COMBAT,
        ItemID._4DOSE2COMBAT);
    addPotion(
        VarPlayerID.POTIONSTORE_SUPER_DEFENCE_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_SUPER_DEFENCE_POTION,
        ItemID._1DOSE2DEFENSE,
        ItemID._2DOSE2DEFENSE,
        ItemID._3DOSE2DEFENSE,
        ItemID._4DOSE2DEFENSE);
    addPotion(
        VarPlayerID.POTIONSTORE_SUPER_ENERGY_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_SUPER_ENERGY_POTION,
        ItemID._1DOSE2ENERGY,
        ItemID._2DOSE2ENERGY,
        ItemID._3DOSE2ENERGY,
        ItemID._4DOSE2ENERGY);
    addPotion(
        VarPlayerID.POTIONSTORE_SUPER_RESTORE_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_SUPER_RESTORE_POTION,
        ItemID._1DOSE2RESTORE,
        ItemID._2DOSE2RESTORE,
        ItemID._3DOSE2RESTORE,
        ItemID._4DOSE2RESTORE);
    addPotion(
        VarPlayerID.POTIONSTORE_SUPER_STRENGTH_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_SUPER_STRENGTH_POTION,
        ItemID._1DOSE2STRENGTH,
        ItemID._2DOSE2STRENGTH,
        ItemID._3DOSE2STRENGTH,
        ItemID._4DOSE2STRENGTH);
    addPotion(
        VarPlayerID.POTIONSTORE_SUPER_ANTIPOISON_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_SUPER_ANTIPOISON_POTION,
        ItemID._1DOSE2ANTIPOISON,
        ItemID._2DOSE2ANTIPOISON,
        ItemID._3DOSE2ANTIPOISON,
        ItemID._4DOSE2ANTIPOISON);
    addPotionSingle(VarPlayerID.POTIONSTORE_WEAPON_POISON_POTION, ItemID.WEAPON_POISON);
    addPotionSingle(VarPlayerID.POTIONSTORE_WEAPON_POISON_P_POTION, ItemID.WEAPON_POISON_);
    addPotionSingle(VarPlayerID.POTIONSTORE_WEAPON_POISON_PP_POTION, ItemID.WEAPON_POISON__);
    addPotion(
        VarPlayerID.POTIONSTORE_ZAMORAK_BREW_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_ZAMORAK_BREW_POTION,
        ItemID._1DOSEPOTIONOFZAMORAK,
        ItemID._2DOSEPOTIONOFZAMORAK,
        ItemID._3DOSEPOTIONOFZAMORAK,
        ItemID._4DOSEPOTIONOFZAMORAK);
    addPotion(
        VarPlayerID.POTIONSTORE_ARMADYLBREW_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_ARMADYLBREW_POTION,
        ItemID._1DOSEARMADYLBREW,
        ItemID._2DOSEARMADYLBREW,
        ItemID._3DOSEARMADYLBREW,
        ItemID._4DOSEARMADYLBREW);

    addPotion(
        VarPlayerID.POTIONSTORE_EXTREME_ENERGY_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_EXTREMEENERGY_POTION,
        ItemID._1DOSE3ENERGY,
        ItemID._2DOSE3ENERGY,
        ItemID._3DOSE3ENERGY,
        ItemID._4DOSE3ENERGY);

    addPotion(
        VarPlayerID.POTIONSTORE_EXTENDED_STAMINA_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_EXTENDEDSTAMINA_POTION,
        ItemID._1DOSE2STAMINA,
        ItemID._2DOSE2STAMINA,
        ItemID._3DOSE2STAMINA,
        ItemID._4DOSE2STAMINA);

    addPotion(
        VarPlayerID.POTIONSTORE_SUPER_HUNTER_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_SUPERHUNTER_POTION,
        ItemID._1DOSE2HUNTING,
        ItemID._2DOSE2HUNTING,
        ItemID._3DOSE2HUNTING,
        ItemID._4DOSE2HUNTING);

    addPotion(
        VarPlayerID.POTIONSTORE_SUPER_FISHING_POTION,
        VarbitID.POTIONSTORE_VILE_SIZE_SUPERFISHING_POTION,
        ItemID._1DOSE2FISHERSPOTION,
        ItemID._2DOSE2FISHERSPOTION,
        ItemID._3DOSE2FISHERSPOTION,
        ItemID._4DOSE2FISHERSPOTION);

    // brutal potions
    addPotion(
        VarPlayerID.POTIONSTORE_AGILITY_MIX,  VarbitID.POTIONSTORE_VILE_SIZE_AGILITY_MIX,
        ItemID.BRUTAL_1DOSE1AGILITY, ItemID.BRUTAL_2DOSE1AGILITY);
    addPotion(
        VarPlayerID.POTIONSTORE_ANCIENT_MIX, VarbitID.POTIONSTORE_VILE_SIZE_ANCIENT_MIX,
        ItemID.BRUTAL_1DOSEANCIENTBREW, ItemID.BRUTAL_2DOSEANCIENTBREW);
    addPotion(
        VarPlayerID.POTIONSTORE_ANTIFIRE_MIX, VarbitID.POTIONSTORE_VILE_SIZE_ANTIFIRE_MIX,
        ItemID.BRUTAL_1DOSE1ANTIDRAGON, ItemID.BRUTAL_2DOSE1ANTIDRAGON);
    addPotion(
        VarPlayerID.POTIONSTORE_ANTIPOISON_MIX, VarbitID.POTIONSTORE_VILE_SIZE_ANTIPOISON_MIX,
        ItemID.BRUTAL_1DOSEANTIPOISON, ItemID.BRUTAL_2DOSEANTIPOISON);
    addPotion(
        VarPlayerID.POTIONSTORE_ANTIDOTE_P_MIX, VarbitID.POTIONSTORE_VILE_SIZE_ANTIDOTE_P_MIX,
        ItemID.BRUTAL_ANTIDOTE_1, ItemID.BRUTAL_ANTIDOTE_2);
    addPotion(
        VarPlayerID.POTIONSTORE_ATTACK_MIX, VarbitID.POTIONSTORE_VILE_SIZE_ATTACK_MIX,
        ItemID.BRUTAL_1DOSE1ATTACK, ItemID.BRUTAL_2DOSE1ATTACK);
    addPotion(
        VarPlayerID.POTIONSTORE_COMBAT_MIX, VarbitID.POTIONSTORE_VILE_SIZE_COMBAT_MIX,
        ItemID.BRUTAL_1DOSECOMBAT, ItemID.BRUTAL_2DOSECOMBAT);
    addPotion(
        VarPlayerID.POTIONSTORE_DEFENCE_MIX, VarbitID.POTIONSTORE_VILE_SIZE_DEFENCE_MIX,
        ItemID.BRUTAL_1DOSE1DEFENSE, ItemID.BRUTAL_2DOSE1DEFENSE);
    addPotion(
        VarPlayerID.POTIONSTORE_ENERGY_MIX, VarbitID.POTIONSTORE_VILE_SIZE_ENERGY_MIX,
        ItemID.BRUTAL_1DOSE1ENERGY, ItemID.BRUTAL_2DOSE1ENERGY);
    addPotion(
        VarPlayerID.POTIONSTORE_EXTENDED_ANTIFIRE_MIX, VarbitID.POTIONSTORE_VILE_SIZE_EXTENDED_ANTIFIRE_MIX,
        ItemID.BRUTAL_1DOSE2ANTIDRAGON, ItemID.BRUTAL_2DOSE2ANTIDRAGON);
    addPotion(
        VarPlayerID.POTIONSTORE_EXTENDED_SUPER_ANTIFIRE_MIX, VarbitID.POTIONSTORE_VILE_SIZE_EXTENDED_SUPER_ANTIFIRE_MIX,
        ItemID.BRUTAL_1DOSE4ANTIDRAGON, ItemID.BRUTAL_2DOSE4ANTIDRAGON);
    addPotion(
        VarPlayerID.POTIONSTORE_FISHING_MIX, VarbitID.POTIONSTORE_VILE_SIZE_FISHING_MIX,
        ItemID.BRUTAL_1DOSEFISHERSPOTION, ItemID.BRUTAL_2DOSEFISHERSPOTION);
    addPotion(
        VarPlayerID.POTIONSTORE_HUNTING_MIX, VarbitID.POTIONSTORE_VILE_SIZE_HUNTING_MIX,
        ItemID.BRUTAL_1DOSE1HUNTING, ItemID.BRUTAL_2DOSE1HUNTING);
    addPotion(
        VarPlayerID.POTIONSTORE_MAGIC_ESSENCE_MIX, VarbitID.POTIONSTORE_VILE_SIZE_MAGIC_ESSENCE_MIX,
        ItemID.BRUTAL_1DOSEMAGICESS, ItemID.BRUTAL_2DOSEMAGICESS);
    addPotion(
        VarPlayerID.POTIONSTORE_MAGIC_MIX, VarbitID.POTIONSTORE_VILE_SIZE_MAGIC_MIX,
        ItemID.BRUTAL_1DOSE1MAGIC, ItemID.BRUTAL_2DOSE1MAGIC);
    addPotion(
        VarPlayerID.POTIONSTORE_PRAYER_MIX, VarbitID.POTIONSTORE_VILE_SIZE_PRAYER_MIX,
        ItemID.BRUTAL_1DOSEPRAYERRESTORE, ItemID.BRUTAL_2DOSEPRAYERRESTORE);
    addPotion(
        VarPlayerID.POTIONSTORE_RANGING_MIX, VarbitID.POTIONSTORE_VILE_SIZE_RANGING_MIX,
        ItemID.BRUTAL_1DOSERANGERSPOTION, ItemID.BRUTAL_2DOSERANGERSPOTION);
    addPotion(
        VarPlayerID.POTIONSTORE_RESTORE_MIX, VarbitID.POTIONSTORE_VILE_SIZE_RESTORE_MIX,
        ItemID.BRUTAL_1DOSESTATRESTORE, ItemID.BRUTAL_2DOSESTATRESTORE);
    addPotion(
        VarPlayerID.POTIONSTORE_STAMINA_MIX, VarbitID.POTIONSTORE_VILE_SIZE_STAMINA_MIX,
        ItemID.BRUTAL_1DOSESTAMINA, ItemID.BRUTAL_2DOSESTAMINA);
    addPotion(
        VarPlayerID.POTIONSTORE_STRENGTH_MIX, VarbitID.POTIONSTORE_VILE_SIZE_STRENGTH_MIX,
        ItemID.BRUTAL_1DOSE1STRENGTH, ItemID.BRUTAL_2DOSE1STRENGTH);
    addPotion(
        VarPlayerID.POTIONSTORE_SUPER_ANTIFIRE_MIX, VarbitID.POTIONSTORE_VILE_SIZE_SUPER_ANTIFIRE_MIX,
        ItemID.BRUTAL_1DOSE3ANTIDRAGON, ItemID.BRUTAL_2DOSE3ANTIDRAGON);
    addPotion(
        VarPlayerID.POTIONSTORE_SUPERATTACK_MIX, VarbitID.POTIONSTORE_VILE_SIZE_SUPERATTACK_MIX,
        ItemID.BRUTAL_1DOSE2ATTACK, ItemID.BRUTAL_2DOSE2ATTACK);
    addPotion(
        VarPlayerID.POTIONSTORE_SUPER_DEFENCE_MIX, VarbitID.POTIONSTORE_VILE_SIZE_SUPER_DEFENCE_MIX,
        ItemID.BRUTAL_1DOSE2DEFENSE, ItemID.BRUTAL_2DOSE2DEFENSE);
    addPotion(
        VarPlayerID.POTIONSTORE_SUPER_ENERGY_MIX, VarbitID.POTIONSTORE_VILE_SIZE_SUPER_ENERGY_MIX,
        ItemID.BRUTAL_1DOSE2ENERGY, ItemID.BRUTAL_2DOSE2ENERGY);
    addPotion(
        VarPlayerID.POTIONSTORE_SUPER_RESTORE_MIX, VarbitID.POTIONSTORE_VILE_SIZE_SUPER_RESTORE_MIX,
        ItemID.BRUTAL_1DOSE2RESTORE, ItemID.BRUTAL_2DOSE2RESTORE);
    addPotion(
        VarPlayerID.POTIONSTORE_SUPER_STRENGTH_MIX, VarbitID.POTIONSTORE_VILE_SIZE_SUPER_STRENGTH_MIX,
        ItemID.BRUTAL_1DOSE2STRENGTH, ItemID.BRUTAL_2DOSE2STRENGTH);
    addPotion(
        VarPlayerID.POTIONSTORE_ANTIPOISON_SUPER_MIX, VarbitID.POTIONSTORE_VILE_SIZE_ANTIPOISON_SUPER_MIX,
        ItemID.BRUTAL_1DOSE2ANTIPOISON, ItemID.BRUTAL_2DOSE2ANTIPOISON);
    addPotion(
        VarPlayerID.POTIONSTORE_ZAMORAK_MIX, VarbitID.POTIONSTORE_VILE_SIZE_ZAMORAK_MIX,
        ItemID.BRUTAL_1DOSEPOTIONOFZAMORAK, ItemID.BRUTAL_2DOSEPOTIONOFZAMORAK);
    addPotion(
        VarPlayerID.POTIONSTORE_RELICYMS_MIX, VarbitID.POTIONSTORE_VILE_SIZE_RELICYMS_MIX,
        ItemID.BRUTAL_RELICYMS_BALM1, ItemID.BRUTAL_RELICYMS_BALM2);

    // unfinished potions
    addPotionSingle(VarPlayerID.POTIONSTORE_AVANTOEVIAL, ItemID.AVANTOEVIAL);
    addPotionSingle(VarPlayerID.POTIONSTORE_CADANTINE_BLOODVIAL, ItemID.CADANTINE_BLOODVIAL);
    addPotionSingle(VarPlayerID.POTIONSTORE_CADANTINEVIAL, ItemID.CADANTINEVIAL);
    addPotionSingle(VarPlayerID.POTIONSTORE_DWARFWEEDVIAL, ItemID.DWARFWEEDVIAL);
    addPotionSingle(VarPlayerID.POTIONSTORE_GUAMVIAL, ItemID.GUAMVIAL);
    addPotionSingle(VarPlayerID.POTIONSTORE_HARRALANDERVIAL, ItemID.HARRALANDERVIAL);
    addPotionSingle(VarPlayerID.POTIONSTORE_HUASCAVIAL, ItemID.HUASCAVIAL);
    addPotionSingle(VarPlayerID.POTIONSTORE_IRITVIAL, ItemID.IRITVIAL);
    addPotionSingle(VarPlayerID.POTIONSTORE_KWUARMVIAL, ItemID.KWUARMVIAL);
    addPotionSingle(VarPlayerID.POTIONSTORE_LANTADYMEVIAL, ItemID.LANTADYMEVIAL);
    addPotionSingle(VarPlayerID.POTIONSTORE_MARRENTILLVIAL, ItemID.MARRENTILLVIAL);
    addPotionSingle(VarPlayerID.POTIONSTORE_RANARRVIAL, ItemID.RANARRVIAL);
    addPotionSingle(VarPlayerID.POTIONSTORE_SNAPDRAGONVIAL, ItemID.SNAPDRAGONVIAL);
    addPotionSingle(VarPlayerID.POTIONSTORE_TARROMINVIAL, ItemID.TARROMINVIAL);
    addPotionSingle(VarPlayerID.POTIONSTORE_TOADFLAXVIAL, ItemID.TOADFLAXVIAL);
    addPotionSingle(VarPlayerID.POTIONSTORE_TORSTOLVIAL, ItemID.TORSTOLVIAL);

    addPotionSingle(VarPlayerID.POTIONSTORE_ELKHORNVIAL, ItemID.ELKHORNVIAL);
    addPotionSingle(VarPlayerID.POTIONSTORE_PILLARVIAL, ItemID.PILLARVIAL);
    addPotionSingle(VarPlayerID.POTIONSTORE_UMBRALVIAL, ItemID.UMBRALVIAL);
  }

  private PotionDoseInfo addPotion(
      @MagicConstant(valuesFromClass = VarPlayerID.class) int varpId,
      @MagicConstant(valuesFromClass = VarbitID.class) int varId,
      @MagicConstant(valuesFromClass = ItemID.class) int... doseIds) {
    PotionDoseInfo toRet = PotionDoseInfo.multi(plugin, varpId, varId, (int[]) doseIds);
    potionInfos.add(toRet);
    items.addAll(Arrays.asList(toRet.getItemStacks()));
    return toRet;
  }

  private PotionDoseInfo addPotionSingle(
      @MagicConstant(valuesFromClass = VarPlayerID.class) int varpId,
      @MagicConstant(valuesFromClass = ItemID.class) int itemId) {
    PotionDoseInfo toRet = PotionDoseInfo.single(plugin, varpId, itemId);
    potionInfos.add(toRet);
    items.addAll(Arrays.asList(toRet.getItemStacks()));
    return toRet;
  }

  @Override
  public boolean onVarbitChanged(VarbitChanged varbitChanged) {
    boolean updated = false;
    for(PotionDoseInfo potEntry: potionInfos) {
      Var qtyVar = Var.player(varbitChanged, potEntry.getQtyVarpId());
      int qty = qtyVar.getValue(plugin.getClient());
      ItemStack is1 = potEntry.getItemStacks()[0];
      if(qtyVar.wasChanged() || is1.getQuantity() != qty) {
        updated = true;
        int div = 0;
        for (ItemStack is : potEntry.getItemStacks()) {
          div+=1;
          is.setQuantity(qty/div);
        }
      }
//      Var qtyVar = Var.bit(varbitChanged, potionInfos.get(0).getQtyVarpId());
    }

//    potionInfos.get(0).getQtyVarpId()
//    for (var entry : varpIdMap.entrySet()) {
//      var potionStack = entry.getValue();
//      Var varp = Var.player(varbitChanged, entry.getKey());
//      int nQty = varp.getValue(plugin.getClient());
//      if(varp.wasChanged() || potionStack.getQuantity() != nQty) {
//        updated = true;
//        potionStack.setQuantity(nQty);
//      }
//    }
    return updated;
  }
}
