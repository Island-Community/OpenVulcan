package me.frep.vulcan.spigot.check.impl.player.fastplace;

import java.util.ArrayDeque;
import java.util.Deque;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.session.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

@CheckInfo(name = "Fast Place", type = 'A', complexType = "Delay", description = "Placing blocks too quickly.")
public class FastPlaceA extends AbstractCheck {

    private final Deque<Long> placementTimes = new ArrayDeque<>();

    public FastPlaceA(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void update() {
    }

    @Override
    public void handleEvent(Event event) {
        if (!(event instanceof BlockPlaceEvent placement) || placement.isCancelled() || playerData.getPlayer() == null)
            return;
        if (isExempt(new ExemptType[] { ExemptType.CANCELLED_BREAK, ExemptType.VEHICLE }))
            return;
        long now = System.currentTimeMillis();
        while (!placementTimes.isEmpty() && now - placementTimes.peekFirst() >= 1000L) placementTimes.removeFirst();
        placementTimes.addLast(now);
        if (placementTimes.size() > VulcanSettings.checksPlayerFastplaceAMaxBlocks) {
            fail("amount=" + placementTimes.size());
            placementTimes.clear();
        }
    }
}
