package me.frep.vulcan.spigot.check.impl.player.scaffold;

import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@CheckInfo(name = "Scaffold", type = 'A', complexType = "Interact", description = "Interacted with the bottom of block.")
public class ScaffoldA extends AbstractCheck {

    private static final String VIOLATION_DETAIL_PREFIX = "block=";

    public ScaffoldA(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void update() {
    }

    public void handleEvent(Event event2) {
        boolean condition;
        if (event2 instanceof PlayerInteractEvent playerInteractEvent && playerInteractEvent.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            long currentTimeMillis2 = System.currentTimeMillis();
            boolean belowCurrentTimeMillis2DifferenceLimit = currentTimeMillis2 - this.playerData.getClickTimingData().getLastSwingAt() < 10000L;
            if (belowCurrentTimeMillis2DifferenceLimit) {
                return;
            }
            if (this.playerData.getPlayer() == null) {
                return;
            }
            boolean exempt;
            Block block = this.playerData.getPlayer().getLocation().subtract(0.0, 1.0, 0.0).getBlock();
            exempt = this.isExempt(new ExemptType[] { ExemptType.TELEPORT, ExemptType.SERVER_POSITION });
            condition = ((playerInteractEvent.getClickedBlock() != null && playerInteractEvent.getClickedBlock() == block && playerInteractEvent.getBlockFace() == BlockFace.DOWN && playerInteractEvent.getClickedBlock().getType().isSolid()) && (this.playerData.getPlayer().getItemInHand().getType().isSolid()));
            boolean condition3 = condition;
            if (condition3 && !exempt) {
                this.fail(VIOLATION_DETAIL_PREFIX + playerInteractEvent.getClickedBlock().getType());
            }
        }
    }
}
