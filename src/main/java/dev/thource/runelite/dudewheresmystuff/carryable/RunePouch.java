package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.CarryableStorage;
import dev.thource.runelite.dudewheresmystuff.CarryableStorageType;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
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

    public RunePouch(Client client, ItemManager itemManager) {
        super(CarryableStorageType.RUNE_POUCH, client, itemManager);
    }

    public boolean onVarbitChanged() {
        int newRune1Type = client.getVarbitValue(Varbits.RUNE_POUCH_RUNE1);
        int newRune2Type = client.getVarbitValue(Varbits.RUNE_POUCH_RUNE2);
        int newRune3Type = client.getVarbitValue(Varbits.RUNE_POUCH_RUNE3);
        int newRune1Quantity = client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT1);
        int newRune2Quantity = client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT2);
        int newRune3Quantity = client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT3);

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
            items.add(new ItemStack(rune1.getItemId(), itemComposition.getName(), rune1Quantity, itemManager.getItemPrice(rune1.getItemId()), itemComposition.getHaPrice(), true));
        } else {
            items.add(new ItemStack(-1, "empty", 1, 0, 0, false));
        }

        if (rune2 != null) {
            ItemComposition itemComposition = itemManager.getItemComposition(rune2.getItemId());
            items.add(new ItemStack(rune2.getItemId(), itemComposition.getName(), rune2Quantity, itemManager.getItemPrice(rune2.getItemId()), itemComposition.getHaPrice(), true));
        } else {
            items.add(new ItemStack(-1, "empty", 1, 0, 0, false));
        }

        if (rune3 != null) {
            ItemComposition itemComposition = itemManager.getItemComposition(rune3.getItemId());
            items.add(new ItemStack(rune3.getItemId(), itemComposition.getName(), rune3Quantity, itemManager.getItemPrice(rune3.getItemId()), itemComposition.getHaPrice(), true));
        } else {
            items.add(new ItemStack(-1, "empty", 1, 0, 0, false));
        }
    }

    @Override
    public void reset() {
        super.reset();

        rune1Type = 0;
        rune2Type = 0;
        rune3Type = 0;

        rune1Quantity = 0;
        rune2Quantity = 0;
        rune3Quantity = 0;
    }
}
