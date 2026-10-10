package me.frep.vulcan.spigot.check;

import java.util.List;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.data.CheckData;
import me.frep.vulcan.spigot.check.manager.CheckManager;
import me.frep.vulcan.spigot.config.VulcanSettings;

public final class CheckSettingsLoader {

    public static void clearCheckSettings() {
        VulcanSettings.checkDataByName.clear();
        VulcanSettings.checkEnabled.clear();
        VulcanSettings.maximumViolations.clear();
        VulcanSettings.alertIntervals.clear();
        VulcanSettings.minimumAlertViolations.clear();
        VulcanSettings.punishmentCommands.clear();
        VulcanSettings.checkPunishable.clear();
        VulcanSettings.broadcastPunishments.clear();
        VulcanSettings.setbackEnabled.clear();
        VulcanSettings.maximumBuffers.clear();
        VulcanSettings.bufferDecays.clear();
        VulcanSettings.bufferMultipliers.clear();
        VulcanSettings.hotbarShuffleEnabled.clear();
        VulcanSettings.minimumShuffleViolations.clear();
        VulcanSettings.shuffleIntervals.clear();
        VulcanSettings.randomRotationEnabled.clear();
        VulcanSettings.minimumRotationViolations.clear();
        VulcanSettings.rotationIntervals.clear();
        VulcanSettings.discordAlertIntervals.clear();
        VulcanSettings.minimumSetbackViolations.clear();
    }

    public static void loadCheckSettings() {
        Class<?>[] checkTypes = CheckManager.checkTypes;
        int checkCount = checkTypes.length;
        for (int checkIndex = 0; checkIndex < checkCount; ++checkIndex) {
            Class<?> checkType = checkTypes[checkIndex];
            CheckInfo information = checkType.getAnnotation(CheckInfo.class);
            String category = "";
            if (checkType.getName().contains("combat")) {
                category = "combat";
            } else if (checkType.getName().contains("movement")) {
                category = "movement";
            } else if (checkType.getName().contains("player")) {
                category = "player";
            }
            String checkName = information.name().replaceAll(" ", "").toLowerCase();
            char checkVariant = Character.toLowerCase(information.type());
            String path = "checks." + category + "." + checkName + "." + checkVariant + ".";
            boolean enabled = VulcanSettings.getBoolean(path + "enabled");
            int maximumViolations = VulcanSettings.getInt(path + "max-violations");
            int minimumAlertViolations = VulcanSettings.getInt(path + "dont-alert-until");
            int alertInterval = VulcanSettings.getInt(path + "alert-interval");
            int maximumPing = VulcanSettings.getInt(path + "maximum-ping");
            double minimumTps = VulcanSettings.getDouble(path + "minimum-tps");
            List<String> punishmentCommands = VulcanSettings.getStringList(path + "punishment-commands");
            boolean punishable = VulcanSettings.getBoolean(path + "punishable");
            boolean broadcastPunishment = VulcanSettings.getBoolean(path + "broadcast-punishment");
            boolean setback = VulcanSettings.getBoolean(path + "setback");
            int discordAlertInterval = VulcanSettings.getInt(path + "discord-alert-interval");
            int minimumSetbackViolations = VulcanSettings.getInt(path + "min-vl-to-setback");
            VulcanSettings.checkEnabled.put(checkType.getSimpleName(), enabled);
            VulcanSettings.maximumViolations.put(checkType.getSimpleName(), maximumViolations);
            VulcanSettings.minimumAlertViolations.put(checkType.getSimpleName(), minimumAlertViolations);
            VulcanSettings.alertIntervals.put(checkType.getSimpleName(), alertInterval);
            VulcanSettings.maximumPing.put(checkType.getSimpleName(), maximumPing);
            VulcanSettings.minimumTps.put(checkType.getSimpleName(), minimumTps);
            VulcanSettings.punishmentCommands.put(checkType.getSimpleName(), punishmentCommands);
            VulcanSettings.checkPunishable.put(checkType.getSimpleName(), punishable);
            VulcanSettings.broadcastPunishments.put(checkType.getSimpleName(), broadcastPunishment);
            VulcanSettings.setbackEnabled.put(checkType.getSimpleName(), setback);
            VulcanSettings.discordAlertIntervals.put(checkType.getSimpleName(), discordAlertInterval);
            VulcanSettings.minimumSetbackViolations.put(checkType.getSimpleName(), minimumSetbackViolations);
            int maximumBuffer = VulcanSettings.getInt(path + "buffer.max");
            double bufferMultiplier = VulcanSettings.getDouble(path + "buffer.multiple");
            double bufferDecay = VulcanSettings.getDouble(path + "buffer.decay");
            VulcanSettings.maximumBuffers.put(checkType.getSimpleName(), maximumBuffer);
            VulcanSettings.bufferMultipliers.put(checkType.getSimpleName(), bufferMultiplier);
            VulcanSettings.bufferDecays.put(checkType.getSimpleName(), bufferDecay);
            boolean hotbarShuffle = VulcanSettings.getBoolean(path + "hotbar-shuffle.enabled");
            int minimumShuffleViolations = VulcanSettings.getInt(path + "hotbar-shuffle.minimum-vl-to-shuffle");
            int shuffleInterval = VulcanSettings.getInt(path + "hotbar-shuffle.shuffle-every");
            VulcanSettings.hotbarShuffleEnabled.put(checkType.getSimpleName(), hotbarShuffle);
            VulcanSettings.minimumShuffleViolations.put(checkType.getSimpleName(), minimumShuffleViolations);
            VulcanSettings.shuffleIntervals.put(checkType.getSimpleName(), shuffleInterval);
            boolean randomRotation = VulcanSettings.getBoolean(path + "random-rotation.enabled");
            int minimumRotationViolations = VulcanSettings.getInt(path + "random-rotation.minimum-vl-to-randomly-rotate");
            int rotationInterval = VulcanSettings.getInt(path + "random-rotation.rotate-every");
            VulcanSettings.randomRotationEnabled.put(checkType.getSimpleName(), randomRotation);
            VulcanSettings.minimumRotationViolations.put(checkType.getSimpleName(), minimumRotationViolations);
            VulcanSettings.rotationIntervals.put(checkType.getSimpleName(), rotationInterval);
            if (!VulcanSettings.settingsEnableApi)
                continue;
            CheckData checkData = new CheckData(checkType.getSimpleName());
            checkData.setEnabled(enabled);
            checkData.setPunishable(punishable);
            checkData.setBroadcastPunishment(broadcastPunishment);
            checkData.setHotbarShuffle(hotbarShuffle);
            checkData.setRandomRotation(randomRotation);
            checkData.setMaxViolations(maximumViolations);
            checkData.setAlertInterval(alertInterval);
            checkData.setMinimumVlToNotify(minimumAlertViolations);
            checkData.setMaxPing(maximumPing);
            checkData.setRandomRotationInterval(rotationInterval);
            checkData.setMinimumVlToRandomlyRotate(minimumRotationViolations);
            checkData.setMinimumVlToShuffleHotbar(minimumShuffleViolations);
            checkData.setHotbarShuffleInterval(shuffleInterval);
            checkData.setMinimumTps(minimumTps);
            checkData.setMaxBuffer((double) maximumBuffer);
            checkData.setBufferDecay(bufferDecay);
            checkData.setBufferMultiple(bufferMultiplier);
            checkData.setPunishmentCommands(punishmentCommands);
            VulcanSettings.checkDataByName.put(checkType.getSimpleName(), checkData);
        }
    }

    private CheckSettingsLoader() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
