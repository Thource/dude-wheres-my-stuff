package dev.thource.runelite.dudewheresmystuff;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.events.VarbitChanged;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Var {
  private final int varbitId;
  private final int varpId;
  private final VarbitChanged varbitChanged;

  public static Var bit(VarbitChanged varbitChanged, int id) {
    return new Var(id, -1, varbitChanged);
  }

  public static Var player(VarbitChanged varbitChanged, int id) {
    return new Var(-1, id, varbitChanged);
  }

  public int getValue(Client client) {
    if (wasChanged() && varbitChanged.getVarbitId() != -999) {
      return varbitChanged.getValue();
    }

    if (varbitId != -1) {
      return client.getVarbitValue(varbitId);
    }

    return client.getVarpValue(varpId);
  }

  public boolean wasChanged() {
    return varbitChanged.getVarbitId() == -999
        || (varbitId != -1 && varbitChanged.getVarbitId() == varbitId)
        || (varpId != -1 && varbitChanged.getVarpId() == varpId);
  }
}
