package me.frep.vulcan.spigot.message;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.util.numbers.MathUtils;
import org.bukkit.entity.Player;

public final class ViolationFormatter {

    public static final String capitalize(String text2) {
        return text2 != null && text2.length() != 0 ? text2.substring(0, 1).toUpperCase() + text2.substring(1) : text2;
    }

    public static synchronized void writeViolationLog(AbstractCheck check, PlayerData data, String info) {
        var annotation = check.getCheck();
        CollisionState position = data.getCollisionState();
        String message = VulcanSettings.logFileAlertMessage.replace("%player%", data.getPlayerName()).replace("%max-vl%", String.valueOf(VulcanSettings.maximumViolations.get(check.getClass().getSimpleName()))).replace("%cps%", MathUtils.formatNumber(data.getClickTimingData().getClicksPerSecond())).replace("%check%", annotation.name()).replace("%type%", Character.toString(annotation.type())).replace("%world%", data.getPlayer().getWorld().getName()).replace("%x%", Double.toString(position.getX())).replace("%y%", Double.toString(position.getY())).replace("%z%", Double.toString(position.getZ())).replace("%client-brand%", data.getClientBrand()).replace("%complex-type%", annotation.complexType()).replace("%tps%", new DecimalFormat("##.##").format(ExemptionManager.getTps())).replace("%ping%", Integer.toString(CheckUtils.getPing(data.getPlayer()))).replace("%description%", annotation.description()).replace("%version%", data.getClientVersion().getReleaseName()).replace("%dev%", annotation.experimental() ? "*" : "").replace("%vl%", Integer.toString(check.getVl()));
        java.nio.file.Path folder = PluginServices.INSTANCE.getVulcanPlugin().getDataFolder().toPath();
        java.nio.file.Path log = folder.resolve("violations.txt");
        try {
            java.nio.file.Files.createDirectories(folder);
            String time = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("[MM-dd-yyyy] [HH:mm:ss] "));
            java.nio.file.Files.writeString(log, time + message + " " + info + System.lineSeparator(), java.nio.charset.StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            if (java.nio.file.Files.size(log) / 1000L > VulcanSettings.settingsMaxLogsFileSize)
                archiveLog(log, folder.resolve("logs"));
        } catch (java.io.IOException exception) {
            PluginServices.INSTANCE.getVulcanPlugin().getLogger().log(java.util.logging.Level.WARNING, "Cannot write violation log", exception);
        }
    }

    public static void archiveLog(java.nio.file.Path log, java.nio.file.Path archiveFolder) throws java.io.IOException {
        java.nio.file.Files.createDirectories(archiveFolder);
        String date = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
        java.nio.file.Path archive = java.nio.file.Files.createTempFile(archiveFolder, date + "-", ".zip");
        try (ZipOutputStream output = new ZipOutputStream(java.nio.file.Files.newOutputStream(archive))) {
            output.putNextEntry(new ZipEntry(log.getFileName().toString()));
            java.nio.file.Files.copy(log, output);
            output.closeEntry();
        } catch (java.io.IOException exception) {
            java.nio.file.Files.deleteIfExists(archive);
            throw exception;
        }
        java.nio.file.Files.delete(log);
    }

    public static void writePunishmentLog(AbstractCheck abstractCheck, PlayerData playerData) {
        try {
            java.nio.file.Files.createDirectories(PluginServices.INSTANCE.getVulcanPlugin().getDataFolder().toPath());
            File file2 = new File(PluginServices.INSTANCE.getVulcanPlugin().getDataFolder(), "punishments.txt");
            if (!file2.exists()) {
                java.nio.file.Files.createFile(file2.toPath());
            }
            FileWriter fileWriter = new FileWriter(file2, java.nio.charset.StandardCharsets.UTF_8, true);
            try (PrintWriter printWriter = new PrintWriter(fileWriter)) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM-dd-yyyy");
                String displayName = abstractCheck.getDisplayName();
                printWriter.println("--------------[ " + playerData.getPlayerName() + " was punished for " + ViolationFormatter.capitalize(displayName) + " (Type " + Character.toUpperCase(abstractCheck.getDisplayType()) + ") ]--------------");
                printWriter.println("");
                printWriter.println("Date & Time:");
                printWriter.println(" - Date: " + simpleDateFormat2.format(new Date()));
                printWriter.println(" - Time: " + simpleDateFormat.format(calendar.getTime()));
                printWriter.println("");
                printWriter.println("Location:");
                printWriter.println(" - World: " + playerData.getPlayer().getWorld().getName());
                printWriter.println(" - X: " + playerData.getCollisionState().getX());
                printWriter.println(" - Y: " + playerData.getCollisionState().getY());
                printWriter.println(" - Z: " + playerData.getCollisionState().getZ());
                printWriter.println("");
                printWriter.println("Information:");
                printWriter.println(" - UUID: " + playerData.getPlayer().getUniqueId());
                printWriter.println(" - Keep Alive Ping: " + playerData.getConnectionTracker().getKeepAlivePing());
                printWriter.println(" - Transaction Ping: " + playerData.getConfirmationTracker().getTransactionPingMillis());
                printWriter.println(" - Server TPS: " + new DecimalFormat("##.##").format(ExemptionManager.getTps()));
                printWriter.println(" - Sensitivity: " + playerData.getRotationTracker().getSensitivity());
                printWriter.println(" - GameMode: " + playerData.getPlayer().getGameMode());
                printWriter.println(" - Client Brand: " + playerData.getClientBrand());
                printWriter.println(" - Vehicle: " + playerData.getPlayer().getVehicle());
                printWriter.println(" - Version: " + playerData.getClientVersion().getReleaseName());
                printWriter.println(" - Last Damage: " + (double) playerData.getVelocityTracker().getTicksSinceVelocity() / 20.0 + " seconds ago");
                printWriter.println("");
                printWriter.println("Total Violations: " + playerData.getTotalViolations());
                printWriter.println(" - Combat Violations: " + playerData.getCombatViolations());
                printWriter.println(" - Movement Violations: " + playerData.getMovementViolations());
                printWriter.println(" - Player Violations: " + playerData.getPlayerViolations());
                printWriter.println("");
                printWriter.println("Misc Information:");
                printWriter.println(" - Since Teleport Ticks: " + playerData.getMotionState().getTicksSinceServerPosition());
                printWriter.println(" - Since Riptide Ticks: " + playerData.getCollisionState().getTicksSinceRiptide());
                printWriter.println(" - Flying Delay: " + playerData.getConnectionTracker().getMovementDelayMillis());
                printWriter.println(" - Last Client Lag Spike: " + (System.currentTimeMillis() - playerData.getConfirmationTracker().getLastClientLagSpikeAtMillis()) + "ms ago");
                printWriter.println(" - Last Server Lag Spike: " + (System.currentTimeMillis() - PluginServices.INSTANCE.getTickProcessor().getLastLagSpikeAtMillis()) + "ms ago");
                printWriter.println(" - Since Velocity Ticks: " + playerData.getVelocityTracker().getTicksSinceVelocity());
                printWriter.println(" - Since Firework Ticks: " + playerData.getCollisionState().getTicksSinceFirework());
                printWriter.println(" - Ticks Existed: " + playerData.getMotionState().getTicksExisted());
                printWriter.println(" - Since Explosion Ticks: " + playerData.getVelocityTracker().getTicksSinceExplosion());
                printWriter.println(" - Since Vehicle Ticks: " + playerData.getCollisionState().getTicksSinceVehicle());
                printWriter.println(" - Since Liquid Ticks: " + playerData.getCollisionState().getTicksSinceLiquid());
                printWriter.println(" - Since Gliding Ticks: " + playerData.getMotionState().getTicksSinceGliding());
                printWriter.println(" - Since Wearing Elytra Ticks: " + playerData.getCollisionState().getTicksSinceWearingElytra());
                printWriter.println(" - Queued Transactions: " + playerData.getQueuedTransactionCount());
                printWriter.println(" - Joined: " + (System.currentTimeMillis() - playerData.getJoinTime()) / 1000L + " seconds ago");
                if (ExemptionManager.isAtLeast1_9()) {
                    printWriter.println(" - Holding Item (Main Hand): " + playerData.getPlayer().getInventory().getItemInMainHand());
                    printWriter.println(" - Holding Item (Off Hand): " + playerData.getPlayer().getInventory().getItemInOffHand());
                } else {
                    printWriter.println(" - Holding Item: " + playerData.getPlayer().getInventory().getItemInHand());
                }
                printWriter.println("");
                printWriter.println("Armor:");
                printWriter.println(" - Helmet: " + playerData.getPlayer().getInventory().getHelmet());
                printWriter.println(" - Chestplate: " + playerData.getPlayer().getInventory().getChestplate());
                printWriter.println(" - Leggings: " + playerData.getPlayer().getInventory().getLeggings());
                printWriter.println(" - Boots: " + playerData.getPlayer().getInventory().getBoots());
                printWriter.println("");
                printWriter.println("All Violations:");
                for (AbstractCheck abstractCheck2 : playerData.getChecks()) {
                    if (abstractCheck2.getVl() <= 0)
                        continue;
                    String displayName2 = abstractCheck2.getDisplayName();
                    printWriter.println(" - " + ViolationFormatter.capitalize(displayName2) + " (Type " + Character.toUpperCase(abstractCheck2.getType()) + ") [VL: " + abstractCheck2.getVl() + "] (" + abstractCheck2.getCheck().description() + ")");
                }
                printWriter.println("");
                if (ExemptionManager.isNewerThan1_13()) {
                    if (playerData.getCollisionState().getSurroundingMaterials() != null) {
                        printWriter.println("Blocks Nearby: " + playerData.getCollisionState().getSurroundingMaterials());
                    }
                } else if (playerData.getCollisionState().getSurroundingMaterialsLegacy() != null) {
                    printWriter.println("Blocks Nearby: " + playerData.getCollisionState().getSurroundingMaterialsLegacy());
                }
                if (playerData.getCollisionState().getNearbyEntities() != null) {
                    printWriter.println("Entities Nearby: " + playerData.getCollisionState().getNearbyEntities());
                }
                printWriter.println("Potion Effects: " + playerData.getPlayer().getActivePotionEffects());
                printWriter.println("---------------------------------------------------------------------------");
                printWriter.println("");
                printWriter.println("");
                printWriter.flush();
                if (printWriter.checkError())
                    throw new java.io.IOException("Cannot write punishment log");
            }
        } catch (java.io.IOException exception) {
            PluginServices.INSTANCE.getVulcanPlugin().getLogger().log(java.util.logging.Level.WARNING, "Cannot write punishment log", exception);
        }
    }

    private ViolationFormatter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
