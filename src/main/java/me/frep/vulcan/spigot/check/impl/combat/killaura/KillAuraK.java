package me.frep.vulcan.spigot.check.impl.combat.killaura;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Kill Aura", type = 'K', complexType = "Pattern", description = "Attack pattern.")
public class KillAuraK extends AbstractCheck {

    private final List attackIntervals = Lists.newArrayList();

    private long lastAttackAtMillis;

    private static final String VIOLATION_DETAIL_PREFIX = "range=";

    public KillAuraK(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isAttackPacket(packetEvent)) {
            WrapperPlayClientInteractEntity lastInteractionPacket = this.playerData.getLastInteractionPacket();
            if (lastInteractionPacket.getAction() != InteractAction.ATTACK) {
                return;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.FAST })) {
                return;
            }
            long currentTimeMillisDifference = System.currentTimeMillis() - this.lastAttackAtMillis;
            this.attackIntervals.add(currentTimeMillisDifference);
            if (this.attackIntervals.size() == 10) {
                Collections.sort(this.attackIntervals);
                long longValue2 = (Long) this.attackIntervals.get(this.attackIntervals.size() - 1) - (Long) this.attackIntervals.get(0);
                if (longValue2 < 50L) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail(VIOLATION_DETAIL_PREFIX + longValue2);
                    }
                } else {
                    this.decayBuffer();
                }
                this.attackIntervals.clear();
            }
            this.lastAttackAtMillis = System.currentTimeMillis();
        }
    }

    @Override
    public void update() {
    }
}
