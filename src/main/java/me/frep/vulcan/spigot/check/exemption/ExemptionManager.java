package me.frep.vulcan.spigot.check.exemption;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.util.scheduling.MainThreadExecutor;
import me.frep.vulcan.spigot.util.minecraft.ChatColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public final class ExemptionManager {

    private static final boolean newerThan1_7_10 = getServerVersion().isNewerThan(ServerVersion.V_1_7_10);

    private static final boolean atLeast1_8 = getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_8);

    private static final boolean atLeast1_9 = getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_9);

    private static final boolean before1_13 = getServerVersion().isOlderThan(ServerVersion.V_1_13);

    private static final boolean before1_8 = getServerVersion().isOlderThan(ServerVersion.V_1_8);

    private static final boolean before1_9 = getServerVersion().isOlderThan(ServerVersion.V_1_9);

    private static final boolean newerThan1_13 = getServerVersion().isNewerThan(ServerVersion.V_1_13);

    private static final boolean atLeast1_16 = getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_16);

    private static final boolean atLeast1_12 = getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_12);

    private static final boolean atLeast1_11 = getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_11);

    private static final boolean atLeast1_17 = getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_17);

    private static final boolean atLeast1_14 = getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_14);

    private static final boolean atLeast1_18 = getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_18);

    private static final boolean atLeast1_19 = getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_19);

    private static final boolean atLeast1_20 = getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20);

    private static final boolean atLeast1_20_2 = getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20_2);

    private static final boolean before1_21 = getServerVersion().isOlderThan(ServerVersion.V_1_21);

    private static final boolean atLeast1_12_2 = getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_12_2);

    private static final boolean atLeast1_21_2 = getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2);

    private static final boolean atLeast26_1 = getServerVersion().isNewerThanOrEquals(ServerVersion.V_26_1);

    public static double getTps() {
        return PluginServices.INSTANCE.isFoliaServer() ? 20.0 : Math.min(20.0, SpigotReflectionUtil.getTPS());
    }

    public static void logInfo(String message) {
        PluginServices.INSTANCE.getLogger().info(ChatColorUtils.colorize(message));
    }

    public static void logColored(String message) {
        PluginServices.INSTANCE.getLogger().info(ChatColorUtils.colorize(message));
    }

    public static ServerVersion getServerVersion() {
        return PacketEvents.getAPI().getServerManager().getVersion();
    }

    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer", false, ExemptionManager.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException exception) {
            return false;
        }
    }

    public static boolean isAtLeast26_1() {
        return atLeast26_1;
    }

    public static boolean isAtLeast1_17() {
        return atLeast1_17;
    }

    public static boolean isAtLeast1_19() {
        return atLeast1_19;
    }

    public static boolean isAtLeast1_14() {
        return atLeast1_14;
    }

    public static boolean isAtLeast1_11() {
        return atLeast1_11;
    }

    public static boolean isNewerThan1_7_10() {
        return newerThan1_7_10;
    }

    public static boolean isBefore1_9() {
        return before1_9;
    }

    public static boolean isAtLeast1_8() {
        return atLeast1_8;
    }

    public static boolean isNewerThan1_13() {
        return newerThan1_13;
    }

    public static boolean isAtLeast1_16() {
        return atLeast1_16;
    }

    public static boolean isAtLeast1_9() {
        return atLeast1_9;
    }

    public static boolean isBefore1_13() {
        return before1_13;
    }

    public static boolean isBefore1_8() {
        return before1_8;
    }

    public static boolean isAtLeast1_18() {
        return atLeast1_18;
    }

    public static boolean isBefore1_21() {
        return before1_21;
    }

    public static boolean isAtLeast1_20() {
        return atLeast1_20;
    }

    public static boolean isAtLeast1_20_2() {
        return atLeast1_20_2;
    }

    public static void debug(Object value) {
        if (VulcanSettings.settingsDebug) {
            broadcast("&c[!] &7" + value);
        }
    }

    public static void dispatchConsoleCommand(String command) {
        MainThreadExecutor.execute(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColorUtils.colorize(command)));
    }

    public static void broadcast(String message) {
        MainThreadExecutor.execute(() -> Bukkit.broadcastMessage(ChatColorUtils.colorize(message)));
    }

    public static boolean isAtLeast1_12() {
        return atLeast1_12;
    }

    public static boolean isBefore1_17() {
        return getServerVersion().isOlderThan(ServerVersion.V_1_17);
    }

    public static boolean isAtLeast1_12_2() {
        return atLeast1_12_2;
    }

    public static boolean isAtLeast1_21_2() {
        return atLeast1_21_2;
    }

    private ExemptionManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static void executeConsoleCommand(String text) {
        ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
        Bukkit.dispatchCommand(consoleSender, ChatColorUtils.colorize(text));
    }
}
