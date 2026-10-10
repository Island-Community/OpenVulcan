package me.frep.vulcan.spigot.check.impl.combat.fastbow;

import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.player.session.PlayerData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileLaunchEvent;

@CheckInfo(name = "Fast Bow", type = 'A', complexType = "Limit", description = "Shooting a bow too quickly")
public class FastBowA extends AbstractCheck {

    private long lastShotAtMillis;

    public FastBowA(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void update() {
    }

    @Override
    public void handleEvent(Event event) {
        if (!(event instanceof ProjectileLaunchEvent launch) || !(launch.getEntity() instanceof Arrow arrow))
            return;
        Player player = playerData.getPlayer();
        if (player == null)
            return;
        String mainHandType = player.getInventory().getItemInMainHand().getType().name();
        String offHandType = ExemptionManager.isAtLeast1_9() ? player.getInventory().getItemInOffHand().getType().name() : "";
        if (mainHandType.contains("CROSS") || offHandType.contains("CROSS"))
            return;
        if (!mainHandType.contains("BOW") && !offHandType.contains("BOW"))
            return;
        if (ExemptionManager.isAtLeast1_14() && (player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.MULTISHOT) > 0 || player.getInventory().getItemInOffHand().getEnchantmentLevel(Enchantment.MULTISHOT) > 0))
            return;
        double velocityLength = arrow.getVelocity().length();
        if (velocityLength <= 2.0)
            return;
        long now = System.currentTimeMillis();
        long shotDelay = now - lastShotAtMillis;
        if (shotDelay < 750L && !isExempt(new ExemptType[] { ExemptType.FAST, ExemptType.ELYTRA, ExemptType.GLIDING })) {
            if (increaseBuffer() > maximumBuffer)
                fail("length=" + velocityLength + " delay=" + shotDelay);
        } else
            decayBuffer();
        lastShotAtMillis = now;
    }
}
