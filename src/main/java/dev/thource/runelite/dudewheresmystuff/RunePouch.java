package dev.thource.runelite.dudewheresmystuff;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.Varbits;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.RunepouchRune;

@Getter
public class RunePouch extends CarryableStorage {
    private int rune1Type;
    private int rune2Type;
    private int rune3Type;

    private int rune1Quantity;
    private int rune2Quantity;
    private int rune3Quantity;

    RunePouch(CarryableStorageType type, Client client, ItemManager itemManager) {
        super(type, client, itemManager);
    }

    boolean updateVarbits() {
        int newRune1Type = client.getVar(Varbits.RUNE_POUCH_RUNE1);
        int newRune2Type = client.getVar(Varbits.RUNE_POUCH_RUNE2);
        int newRune3Type = client.getVar(Varbits.RUNE_POUCH_RUNE3);
        int newRune1Quantity = client.getVar(Varbits.RUNE_POUCH_AMOUNT1);
        int newRune2Quantity = client.getVar(Varbits.RUNE_POUCH_AMOUNT2);
        int newRune3Quantity = client.getVar(Varbits.RUNE_POUCH_AMOUNT3);

        if (newRune1Type == rune1Type && newRune2Type == rune2Type && newRune3Type == rune3Type
                && newRune1Quantity == rune1Quantity && newRune2Quantity == rune2Quantity && newRune3Quantity == rune3Quantity)
            return false;

        rune1Type = newRune1Type;
        rune2Type = newRune2Type;
        rune3Type = newRune3Type;

        rune1Quantity = newRune1Quantity;
        rune2Quantity = newRune2Quantity;
        rune3Quantity = newRune3Quantity;

        refreshItems();

        return true;
    }

    private void refreshItems() {
        items.clear();

        RunepouchRune rune1 = RunepouchRune.getRune(rune1Type);
        RunepouchRune rune2 = RunepouchRune.getRune(rune2Type);
        RunepouchRune rune3 = RunepouchRune.getRune(rune3Type);

        if (rune1 != null) {
            ItemComposition itemComposition = itemManager.getItemComposition(rune1.getItemId());
            items.add(new ItemStack(rune1.getItemId(), rune1.getName(), rune1Quantity, itemManager.getItemPrice(rune1.getItemId()), itemComposition.getHaPrice(), true));
        }

        if (rune2 != null) {
            ItemComposition itemComposition = itemManager.getItemComposition(rune2.getItemId());
            items.add(new ItemStack(rune2.getItemId(), rune2.getName(), rune2Quantity, itemManager.getItemPrice(rune2.getItemId()), itemComposition.getHaPrice(), true));
        }

        if (rune3 != null) {
            ItemComposition itemComposition = itemManager.getItemComposition(rune3.getItemId());
            items.add(new ItemStack(rune3.getItemId(), rune3.getName(), rune3Quantity, itemManager.getItemPrice(rune3.getItemId()), itemComposition.getHaPrice(), true));
        }
    }
}
