package me.frep.vulcan.spigot.check.impl.player.improbable;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Improbable", type = 'A', complexType = "Combat", description = "Too many combined combat violations.")
public class ImprobableA extends AbstractCheck {

    private long lastViolationAtMillis;

    private static final String VIOLATION_DETAIL_PREFIX = "combatViolations=";

    public ImprobableA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isAttackPacket(packetEvent)) {
            WrapperPlayClientInteractEntity lastInteractionPacket = this.playerData.getLastInteractionPacket();
            if (lastInteractionPacket.getAction() == InteractAction.ATTACK && System.currentTimeMillis() - this.lastViolationAtMillis >= 60000L) {
                int combatViolations = this.playerData.getCombatViolations();
                if (combatViolations > VulcanSettings.checksPlayerImprobableAMaxCombatViolations) {
                    this.fail(VIOLATION_DETAIL_PREFIX + combatViolations);
                    this.lastViolationAtMillis = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    public void update() {
    }
}
