package me.frep.vulcan.api.check;

import java.util.List;

public interface ICheckData {

    boolean isEnabled();

    boolean isPunishable();

    boolean isBroadcastPunishment();

    boolean isHotbarShuffle();

    boolean isRandomRotation();

    int getMaxViolations();

    int getAlertInterval();

    int getMinimumVlToNotify();

    int getMaxPing();

    int getMinimumVlToRandomlyRotate();

    int getMinimumVlToShuffleHotbar();

    double getMinimumTps();

    double getMaxBuffer();

    double getBufferDecay();

    double getBufferMultiple();

    List<String> getPunishmentCommands();

    int getRandomRotationInterval();
}
