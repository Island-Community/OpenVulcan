package me.frep.vulcan.spigot.tracker.rotation;

import java.util.ArrayDeque;
import java.util.Collection;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.task.TickProcessor;
import me.frep.vulcan.spigot.util.minecraft.MaterialSets;
import me.frep.vulcan.spigot.util.numbers.MathUtils;
import org.bukkit.util.Vector;

public class RotationTracker {

    private final PlayerData playerData;

    private float yaw;

    private float pitch;

    private float previousYaw;

    private float previousPitch;

    private float deltaYaw;

    private float deltaPitch;

    private float previousDeltaYaw;

    private float previousDeltaPitch;

    private float yawAcceleration;

    private float pitchAcceleration;

    private float previousYawAcceleration;

    private float previousPitchAcceleration;

    private float yawMouseSteps;

    private float pitchMouseSteps;

    private float pitchSmoothing;

    private float yawSmoothing;

    private float previousPitchSmoothing;

    private float previousYawSmoothing;

    private boolean rotationSettled;

    private boolean recentAnomaly;

    private boolean cinematic;

    private double sensitivityEstimate;

    private double sensitivityStep;

    private final ArrayDeque sensitivitySamples = new ArrayDeque();

    private int sensitivity;

    private int lastSpikeTick;

    private int lastSensitivityAnomalyTick;

    private int lastCinematicTick;

    private int yawMouseStepCount;

    private int pitchMouseStepCount;

    public RotationTracker(PlayerData data) {
        this.playerData = data;
    }

    public void handleRotation(float yaw2, float pitch2) {
        this.previousYaw = this.yaw;
        this.previousPitch = this.pitch;
        this.yaw = yaw2;
        this.pitch = pitch2;
        this.previousDeltaYaw = this.deltaYaw;
        this.previousDeltaPitch = this.deltaPitch;
        this.deltaYaw = Math.abs(yaw2 - this.previousYaw);
        this.deltaPitch = Math.abs(pitch2 - this.previousPitch);
        this.previousPitchAcceleration = this.pitchAcceleration;
        this.previousYawAcceleration = this.yawAcceleration;
        this.yawAcceleration = Math.abs(this.deltaYaw - this.previousDeltaYaw);
        this.pitchAcceleration = Math.abs(this.deltaPitch - this.previousDeltaPitch);
        float adjustedScaledSensitivityStep = (float) this.sensitivityStep * 0.6F + 0.2F;
        float scaledScaledScaledAdjustedScaledSensitivityStep = adjustedScaledSensitivityStep * adjustedScaledSensitivityStep * adjustedScaledSensitivityStep * 1.2F;
        this.yawMouseSteps = this.deltaYaw / scaledScaledScaledAdjustedScaledSensitivityStep;
        this.pitchMouseSteps = this.deltaPitch / scaledScaledScaledAdjustedScaledSensitivityStep;
        this.yawMouseStepCount = (int) (this.deltaYaw / scaledScaledScaledAdjustedScaledSensitivityStep);
        this.pitchMouseStepCount = (int) (this.deltaPitch / scaledScaledScaledAdjustedScaledSensitivityStep);
        this.updateRotationFlags();
        float adjustedScaledDeltaYaw = this.deltaYaw * 1.073742F + (float) (this.deltaYaw + 0.15);
        float scaledDeltaPitchDifference = this.deltaPitch * 1.073742F - (float) (this.deltaPitch - 0.15);
        float deltaPitchDifference = Math.abs(this.deltaPitch - scaledDeltaPitchDifference);
        float deltaYawDifference = Math.abs(this.deltaYaw - adjustedScaledDeltaYaw);
        this.previousPitchSmoothing = this.pitchSmoothing;
        this.previousYawSmoothing = this.yawSmoothing;
        this.pitchSmoothing = Math.abs(this.deltaPitch - deltaPitchDifference);
        this.yawSmoothing = Math.abs(this.deltaYaw - deltaYawDifference);
        if (this.deltaPitch > 0.1 && this.deltaPitch < 25.0F) {
            this.sampleSensitivity();
        }
    }

    public boolean hasValidSensitivity() {
        return this.sensitivity > 0 && this.sensitivity < 200;
    }

    private void updateRotationFlags() {
        TickProcessor tickProcessor = PluginServices.INSTANCE.getTickProcessor();
        int lastSpikeTick2 = tickProcessor.getTotalTicks();
        float deltaYawDifference = Math.abs(this.deltaYaw - this.previousDeltaYaw);
        float deltaPitchDifference = Math.abs(this.deltaPitch - this.previousDeltaPitch);
        float deltaYawDifference2 = Math.abs(deltaYawDifference - this.deltaYaw);
        float deltaPitchDifference2 = Math.abs(deltaPitchDifference - this.deltaPitch);
        if (deltaYawDifference2 > 1.0 && deltaPitchDifference2 > 1.0) {
            this.lastSpikeTick = lastSpikeTick2;
        }
        if (this.deltaPitch < 20.0F && this.sensitivityEstimate < 0.0) {
            this.lastSensitivityAnomalyTick = lastSpikeTick2;
        }
        if (this.rotationSettled && this.recentAnomaly) {
            this.lastCinematicTick = lastSpikeTick2;
        }
        this.rotationSettled = lastSpikeTick2 - this.lastSpikeTick > 3;
        this.recentAnomaly = lastSpikeTick2 - this.lastSensitivityAnomalyTick < 3;
        this.cinematic = lastSpikeTick2 - this.lastCinematicTick < 8;
    }

    private void sampleSensitivity() {
        double deltaPitch2 = this.deltaPitch;
        float amount = (float) MathUtils.gcdWithTolerance(deltaPitch2, (double) this.previousDeltaPitch);
        double amount2 = Math.cbrt(0.8333 * amount);
        double amount3 = 1.666 * amount2 - 0.3333;
        double sensitivityEstimate2 = amount3 * 200.0;
        this.sensitivityEstimate = sensitivityEstimate2;
        this.sensitivitySamples.add((int) sensitivityEstimate2);
        if (this.sensitivitySamples.size() == 40) {
            this.sensitivity = MathUtils.integerMode((Collection) this.sensitivitySamples);
            if (this.hasValidSensitivity()) {
                this.sensitivityStep = (Double) MaterialSets.sensitivityTable.get(this.sensitivity);
            }
            this.sensitivitySamples.clear();
        }
    }

    public boolean hasKnownSensitivity() {
        return this.sensitivity >= 0 && this.sensitivity < 50;
    }

    public float getMovementLookAngle() {
        PlayerData playerData2 = this.playerData;
        double deltaX = playerData2.getCollisionState().getDeltaX();
        playerData2 = this.playerData;
        double deltaZ = playerData2.getCollisionState().getDeltaZ();
        Vector vector = new Vector(deltaX, 0.0, deltaZ);
        Vector vector2 = new Vector(-Math.sin(this.yaw * (float) Math.PI / 180.0F) * 1.0 * 0.5, 0.0, Math.cos(this.yaw * (float) Math.PI / 180.0F) * 1.0 * 0.5);
        return vector.angle(vector2);
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getPreviousPitch() {
        return this.previousPitch;
    }

    public float getDeltaYaw() {
        return this.deltaYaw;
    }

    public float getDeltaPitch() {
        return this.deltaPitch;
    }

    public float getPreviousDeltaYaw() {
        return this.previousDeltaYaw;
    }

    public float getPreviousDeltaPitch() {
        return this.previousDeltaPitch;
    }

    public float getYawAcceleration() {
        return this.yawAcceleration;
    }

    public float getPitchAcceleration() {
        return this.pitchAcceleration;
    }

    public float getYawSmoothing() {
        return this.yawSmoothing;
    }

    public float getPreviousYawSmoothing() {
        return this.previousYawSmoothing;
    }

    public boolean isCinematic() {
        return this.cinematic;
    }

    public double getSensitivityEstimate() {
        return this.sensitivityEstimate;
    }

    public double getSensitivityStep() {
        return this.sensitivityStep;
    }

    public int getSensitivity() {
        return this.sensitivity;
    }
}
