package dev.thource.runelite.dudewheresmystuff.minigames;

import dev.thource.runelite.dudewheresmystuff.DudeWheresMyStuffPlugin;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.math.NumberUtils;

/** MasteringMixology tracks paste/points at the Mastering Mixology minigame. */
public class MasteringMixology extends MinigamesStorage {

  private final ItemStack moxPaste;
  private final ItemStack agaPaste;
  private final ItemStack lyePaste;
  private final ItemStack moxResin;
  private final ItemStack agaResin;
  private final ItemStack lyeResin;

  private static final Pattern rewardPointsPattern =
      Pattern.compile("You are rewarded \\d+/\\d+/\\d+. You now have (\\d+)/(\\d+)/(\\d+).");

  protected MasteringMixology(DudeWheresMyStuffPlugin plugin) {
    super(MinigamesStorageType.MASTERING_MIXOLOGY, plugin);

    hasStaticItems = true;

    varbits =
        new int[] {VarbitID.MM_AVAILABLE_MOX, VarbitID.MM_AVAILABLE_AGA, VarbitID.MM_AVAILABLE_LYE};

    moxPaste = new ItemStack(ItemID.MM_MOX_PASTE, plugin);
    items.add(moxPaste);
    agaPaste = new ItemStack(ItemID.MM_AGA_PASTE, plugin);
    items.add(agaPaste);
    lyePaste = new ItemStack(ItemID.MM_LYE_PASTE, plugin);
    items.add(lyePaste);

    moxResin = new ItemStack(-2, "Mox resin", 0, 0, 0, true); // sprite 5666
    moxResin.setSpriteId(5666);
    agaResin = new ItemStack(-2, "Aga resin", 0, 0, 0, true); // sprite 5667
    agaResin.setSpriteId(5667);
    lyeResin = new ItemStack(-2, "Lye resin", 0, 0, 0, true); // sprite 5668
    lyeResin.setSpriteId(5668);

    items.add(moxResin);
    items.add(agaResin);
    items.add(lyeResin);
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if ((chatMessage.getType() != ChatMessageType.SPAM
            && chatMessage.getType() != ChatMessageType.GAMEMESSAGE)
        || !chatMessage.getMessage().startsWith("You are rewarded ")) {
      return false;
    }

    Matcher matcher =
        rewardPointsPattern.matcher(Text.removeTags(chatMessage.getMessage().replace(",", "")));
    if (!matcher.find()) {
      return false;
    }

    boolean updated = false;
    int newMoxResin = NumberUtils.toInt(matcher.group(1), 0);
    if (newMoxResin != moxResin.getQuantity()) {
      moxResin.setQuantity(newMoxResin);
      updated = true;
    }

    int newAgaResin = NumberUtils.toInt(matcher.group(2), 0);
    if (newAgaResin != agaResin.getQuantity()) {
      agaResin.setQuantity(newAgaResin);
      updated = true;
    }

    int newLyeResin = NumberUtils.toInt(matcher.group(3), 0);
    if (newLyeResin != lyeResin.getQuantity()) {
      lyeResin.setQuantity(newLyeResin);
      updated = true;
    }

    if (updated) {
      updateLastUpdated();
    }

    return updated;
  }

  @Override
  public boolean onGameTick() {
    return updateFromWidgets();
  }

  private boolean updateFromOverlay() {
    Widget widget = plugin.getClient().getWidget(InterfaceID.MmOverlay.CONTENT);
    if (widget == null) {
      return false;
    }

    Widget[] widgetChildren = widget.getChildren();
    if (widgetChildren == null || widgetChildren.length < 16) {
      return false;
    }

    updateLastUpdated();

    int newMoxPaste = Integer.parseInt(widgetChildren[8].getText());
    if (newMoxPaste != moxPaste.getQuantity()) {
      moxPaste.setQuantity(newMoxPaste);
    }

    int newAgaPaste = Integer.parseInt(widgetChildren[11].getText());
    if (newAgaPaste != agaPaste.getQuantity()) {
      agaPaste.setQuantity(newAgaPaste);
    }

    int newLyePaste = Integer.parseInt(widgetChildren[14].getText());
    if (newLyePaste != lyePaste.getQuantity()) {
      lyePaste.setQuantity(newLyePaste);
    }

    return true;
  }

  private boolean updateFromShop() {
    Widget widget = plugin.getClient().getWidget(InterfaceID.OmnishopMain.POINTS_LAYER);
    if (widget == null) {
      return false;
    }

    Widget[] widgetChildren = widget.getChildren();
    if (widgetChildren == null || widgetChildren.length < 9) {
      return false;
    }

    updateLastUpdated();

    int newMoxResin = Integer.parseInt(widgetChildren[2].getText().replace(",", ""));
    if (newMoxResin != moxResin.getQuantity()) {
      moxResin.setQuantity(newMoxResin);
    }

    int newAgaResin = Integer.parseInt(widgetChildren[5].getText().replace(",", ""));
    if (newAgaResin != agaResin.getQuantity()) {
      agaResin.setQuantity(newAgaResin);
    }

    int newLyeResin = Integer.parseInt(widgetChildren[8].getText().replace(",", ""));
    if (newLyeResin != lyeResin.getQuantity()) {
      lyeResin.setQuantity(newLyeResin);
    }

    return true;
  }

  private boolean updateFromWidgets() {
    return updateFromOverlay() | updateFromShop();
  }

  @Override
  public boolean isWithdrawable() {
    return false;
  }
}
