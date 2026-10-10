package me.frep.vulcan.spigot.check.impl.player.scaffold;

import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;
import me.frep.vulcan.spigot.util.numbers.MathUtils;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

@CheckInfo(name = "Scaffold", type = 'J', complexType = "Acceleration [2]", experimental = false, description = "Invalid pitch change.")
public class ScaffoldJ extends AbstractCheck {

    public ScaffoldJ(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void update() {
    }

    public void handleEvent(Event event2) {
        if ((event2 instanceof BlockPlaceEvent) && (this.playerData.getMotionState().isLastPlacementValid())) {
            double deltaXZChange = this.playerData.getCollisionState().getDeltaXZChange();
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            float deltaPitch = rotationTracker.getDeltaPitch();
            boolean condition = MathUtils.isSmallScientificNotation(deltaXZChange) && deltaPitch > 18.0F;
            if (condition) {
                this.fail("acceleration=" + deltaXZChange + " deltaPitch=" + deltaPitch);
            }
        }
    }
}
