package me.frep.vulcan.spigot.check.data;

import java.util.List;
import me.frep.vulcan.api.check.ICheckData;

public class CheckData implements ICheckData {

    private boolean enabled;

    private boolean punishable;

    private boolean broadcastPunishment;

    private boolean hotbarShuffle;

    private boolean randomRotation;

    private int maxViolations;

    private int alertInterval;

    private int minimumVlToNotify;

    private int maxPing;

    private int minimumVlToRandomlyRotate;

    private int minimumVlToShuffleHotbar;

    private int randomRotationInterval;

    private int hotbarShuffleInterval;

    private double minimumTps;

    private double maxBuffer;

    private double bufferDecay;

    private double bufferMultiple;

    private List<String> punishmentCommands;

    private String checkName;

    public CheckData(String checkName2) {
        this.checkName = checkName2;
    }

    @Override
    public String toString() {
        return "{checkName=" + this.checkName + " enabled=" + this.enabled + " punishable=" + this.punishable + " broadcastPunishment=" + this.broadcastPunishment + "}";
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isPunishable() {
        return this.punishable;
    }

    @Override
    public boolean isBroadcastPunishment() {
        return this.broadcastPunishment;
    }

    @Override
    public boolean isHotbarShuffle() {
        return this.hotbarShuffle;
    }

    @Override
    public boolean isRandomRotation() {
        return this.randomRotation;
    }

    @Override
    public int getMaxViolations() {
        return this.maxViolations;
    }

    @Override
    public int getAlertInterval() {
        return this.alertInterval;
    }

    @Override
    public int getMinimumVlToNotify() {
        return this.minimumVlToNotify;
    }

    @Override
    public int getMaxPing() {
        return this.maxPing;
    }

    @Override
    public int getMinimumVlToRandomlyRotate() {
        return this.minimumVlToRandomlyRotate;
    }

    @Override
    public int getMinimumVlToShuffleHotbar() {
        return this.minimumVlToShuffleHotbar;
    }

    @Override
    public double getMinimumTps() {
        return this.minimumTps;
    }

    @Override
    public double getMaxBuffer() {
        return this.maxBuffer;
    }

    @Override
    public double getBufferDecay() {
        return this.bufferDecay;
    }

    @Override
    public double getBufferMultiple() {
        return this.bufferMultiple;
    }

    @Override
    public List<String> getPunishmentCommands() {
        return this.punishmentCommands;
    }

    @Override
    public int getRandomRotationInterval() {
        return this.randomRotationInterval;
    }

    public int getMaxPing2() {
        return this.maxPing;
    }

    public int getMinimumVlToRandomlyRotate2() {
        return this.minimumVlToRandomlyRotate;
    }

    public int getMinimumVlToShuffleHotbar2() {
        return this.minimumVlToShuffleHotbar;
    }

    public int getHotbarShuffleInterval() {
        return this.hotbarShuffleInterval;
    }

    public String getCheckName() {
        return this.checkName;
    }

    public void setEnabled(boolean enabled2) {
        this.enabled = enabled2;
    }

    public void setPunishable(boolean punishable2) {
        this.punishable = punishable2;
    }

    public void setBroadcastPunishment(boolean broadcastPunishment2) {
        this.broadcastPunishment = broadcastPunishment2;
    }

    public void setHotbarShuffle(boolean hotbarShuffle2) {
        this.hotbarShuffle = hotbarShuffle2;
    }

    public void setRandomRotation(boolean randomRotation2) {
        this.randomRotation = randomRotation2;
    }

    public void setMaxViolations(int maxViolations2) {
        this.maxViolations = maxViolations2;
    }

    public void setAlertInterval(int alertInterval2) {
        this.alertInterval = alertInterval2;
    }

    public void setMinimumVlToNotify(int minimumVlToNotify2) {
        this.minimumVlToNotify = minimumVlToNotify2;
    }

    public void setMaxPing(int maxPing2) {
        this.maxPing = maxPing2;
    }

    public void setMinimumVlToRandomlyRotate(int minimumVlToRandomlyRotate2) {
        this.minimumVlToRandomlyRotate = minimumVlToRandomlyRotate2;
    }

    public void setMinimumVlToShuffleHotbar(int minimumVlToShuffleHotbar2) {
        this.minimumVlToShuffleHotbar = minimumVlToShuffleHotbar2;
    }

    public void setRandomRotationInterval(int randomRotationInterval2) {
        this.randomRotationInterval = randomRotationInterval2;
    }

    public void setHotbarShuffleInterval(int hotbarShuffleInterval2) {
        this.hotbarShuffleInterval = hotbarShuffleInterval2;
    }

    public void setMinimumTps(double minimumTps2) {
        this.minimumTps = minimumTps2;
    }

    public void setMaxBuffer(double maxBuffer2) {
        this.maxBuffer = maxBuffer2;
    }

    public void setBufferDecay(double bufferDecay2) {
        this.bufferDecay = bufferDecay2;
    }

    public void setBufferMultiple(double bufferMultiple2) {
        this.bufferMultiple = bufferMultiple2;
    }

    public void setPunishmentCommands(List<String> punishmentCommands2) {
        this.punishmentCommands = punishmentCommands2;
    }

    public void setCheckName(String checkName2) {
        this.checkName = checkName2;
    }
}
