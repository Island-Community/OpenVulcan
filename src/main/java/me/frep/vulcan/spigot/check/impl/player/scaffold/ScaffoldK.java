package me.frep.vulcan.spigot.check.impl.player.scaffold;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction.Action;
import java.util.HashMap;
import java.util.Map;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.world.block.BlockUtils;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

@CheckInfo(name = "Scaffold", type = 'K', complexType = "Limit", description = "Bridging too quickly.")
public class ScaffoldK extends AbstractCheck {

    private int lastSneakTick;

    private final Map cache;

    private int placementCount;

    public ScaffoldK(PlayerData playerData) {
        super(playerData);
        int index = 0;
        this.cache = new HashMap();
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isEntityActionPacket(packetEvent)) {
            WrapperPlayClientEntityAction wrapperPlayClientEntityAction = new WrapperPlayClientEntityAction((PacketReceiveEvent) packetEvent);
            if (wrapperPlayClientEntityAction.getAction() == Action.START_SNEAKING || wrapperPlayClientEntityAction.getAction() == Action.STOP_SNEAKING) {
                this.lastSneakTick = this.getCurrentTick();
            }
        }
    }

    public void handleEvent(Event event2) {
        if (event2 instanceof BlockPlaceEvent blockPlaceEvent) {
            if (blockPlaceEvent.isCancelled() || BlockUtils.isSlab(blockPlaceEvent.getBlock().getType())) {
                return;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.SLAB })) {
                return;
            }
            if (ExemptionManager.isAtLeast1_19()) {
                MotionState motionState = this.playerData.getMotionState();
                if (motionState.isSwiftSneak()) {
                    return;
                }
            }
            if (CheckUtils.isValidBlockPlacement(blockPlaceEvent) && this.getCurrentTick() - this.lastSneakTick > 20) {
                this.placementCount++;
                this.cache.put(this.placementCount, System.currentTimeMillis());
                if (System.currentTimeMillis() - (Long) this.cache.get(1) > 1000L) {
                    int cacheSize = this.cache.size();
                    if (cacheSize >= VulcanSettings.checksPlayerScaffoldKMaxBlocks) {
                        if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("amount=" + cacheSize);
                        }
                    } else {
                        this.decayBuffer();
                    }
                    this.cache.clear();
                    this.placementCount = 0;
                }
            }
        }
    }
}
