package me.frep.vulcan.api;

import java.util.List;
import java.util.Map;
import java.util.Set;
import me.frep.vulcan.api.check.Check;
import me.frep.vulcan.api.data.IPlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface VulcanAPI {

    class Factory {

        private static VulcanAPI api;

        @Nullable
        public static VulcanAPI getApi() {
            return api;
        }

        public static void setApi(VulcanAPI instance) {
            api = instance;
        }
    }

    void toggleAlerts(Player player);

    void toggleVerbose(Player player);

    IPlayerData getPlayerData(Player player);

    int getPing(Player player);

    double getKurtosis(Player player);

    int getTransactionPing(Player player);

    int getSensitivity(Player player);

    void executeBanWave();

    void setFrozen(Player player, boolean condition);

    double getCps(Player player);

    int getTotalViolations(Player player);

    boolean isFrozen(Player player);

    int getMovementViolations(Player player);

    int getPlayerViolations(Player player);

    int getCombatViolations(Player player);

    double getTps();

    String getServerVersion();

    Map getCheckData();

    int getJoinTicks(Player player);

    boolean hasAlertsEnabled(Player player);

    Check getCheck(Player player, String text, char character);

    String getVulcanVersion();

    boolean isCheckEnabled(String text);

    int getTicks();

    List getChecks(Player player);

    Map getEnabledChecks();

    Map getMaxViolations();

    Map getAlertIntervals();

    Map getMinimumViolationsToNotify();

    Map getPunishmentCommands();

    Map getPunishableChecks();

    Map getBroadcastPunishments();

    Map getMaximumPings();

    Map getMinimumTps();

    Map getMaxBuffers();

    Map getBufferDecays();

    Map getBufferMultiples();

    Map getHotbarShuffle();

    Map getHotbarShuffleMinimums();

    Map getHotbarShuffleIntervals();

    Map getRandomRotation();

    Map getRandomRotationMinimums();

    Map getRandomRotationIntervals();

    Set getChecks();

    void flag(Player player, String text, String text2, String text3);
}
