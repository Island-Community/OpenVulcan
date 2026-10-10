package me.frep.vulcan.spigot.check.impl.player.scaffold;

import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

@CheckInfo(name = "Scaffold", type = 'C', complexType = "Sprint", description = "Sprinting while bridging.")
public class ScaffoldC extends AbstractCheck {

    public ScaffoldC(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void update() {
    }

    public void handleEvent(Event event2) {
        if (event2 instanceof BlockPlaceEvent blockPlaceEvent) {
            if (blockPlaceEvent.isCancelled()) {
                return;
            }
            boolean validPlacement = this.playerData.getMotionState().getTicksSinceValidPlacement() < 1;
            int sprintingTicks = this.playerData.getMotionState().getSprintingTicks();
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.LIQUID, ExemptType.CREATIVE, ExemptType.FLIGHT });
            if (validPlacement && sprintingTicks > 5 && !exempt) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("ticks=" + sprintingTicks);
                }
            } else {
                this.decayBuffer();
            }
        }
    }
}
