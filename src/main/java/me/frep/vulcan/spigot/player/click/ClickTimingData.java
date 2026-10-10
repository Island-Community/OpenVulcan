package me.frep.vulcan.spigot.player.click;

import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.collection.list.EvictingList;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.util.numbers.MathUtils;

public class ClickTimingData {

    private final PlayerData playerData;

    private long lastClickAt = -1L;

    private long clickInterval;

    private long lastDigAt = System.currentTimeMillis();

    private long lastSwingAt = System.currentTimeMillis();

    private long lastPlaceAt = System.currentTimeMillis();

    private final EvictingList<Long> clickIntervals = new EvictingList<>(20);

    private double clicksPerSecond;

    private double kurtosis;

    public ClickTimingData(PlayerData playerData2) {
        this.playerData = playerData2;
    }

    public void recordClick() {
        if (playerData.getPlayerToggles().isExempt(ExemptType.AUTOCLICKER_NON_DIG))
            return;
        long now = System.currentTimeMillis();
        if (lastClickAt > 0) {
            clickInterval = now - lastClickAt;
            clickIntervals.add(clickInterval);
            if (clickIntervals.isFull())
                clicksPerSecond = MathUtils.clicksPerSecond(clickIntervals);
        }
        lastClickAt = now;
    }

    public void recordDig() {
        lastDigAt = System.currentTimeMillis();
    }

    public void recordPlacement() {
        lastPlaceAt = System.currentTimeMillis();
    }

    public void recordSwing() {
        lastSwingAt = System.currentTimeMillis();
        recordClick();
    }

    public long getClickInterval() {
        return this.clickInterval;
    }

    public long getLastDigAt() {
        return this.lastDigAt;
    }

    public long getLastSwingAt() {
        return this.lastSwingAt;
    }

    public long getLastPlaceAt() {
        return this.lastPlaceAt;
    }

    public double getClicksPerSecond() {
        return this.clicksPerSecond;
    }

    public double getKurtosis() {
        return this.kurtosis;
    }

    public void setKurtosis(double kurtosis) {
        this.kurtosis = kurtosis;
    }

    public void resetClickTime() {
        this.lastClickAt = 0L;
    }

    public void resetClickInterval() {
        this.clickInterval = 0L;
    }

    public void resetDigTime() {
        this.lastDigAt = 0L;
    }

    public void resetSwingTime() {
        this.lastSwingAt = 0L;
    }

    public void resetPlacementTime() {
        this.lastPlaceAt = 0L;
    }

    public void setClicksPerSecond(double clicksPerSecond2) {
        this.clicksPerSecond = clicksPerSecond2;
    }
}
