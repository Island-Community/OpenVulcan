package me.frep.vulcan.spigot.check.impl.player.fastbreak;

import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.world.block.BlockUtils;
import me.frep.vulcan.spigot.world.registry.BukkitRegistry;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@CheckInfo(name = "Fast Break", type = 'A', complexType = "Delay", description = "Breaking blocks too quickly.")
public class FastBreakA extends AbstractCheck {

    private long breakCompletionAtMillis;

    public FastBreakA(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void update() {
    }

    public void handleEvent(Event event) {
        if (playerData.getPlayer() == null)
            return;
        if (event instanceof BlockBreakEvent broken)
            checkBlockBreak(broken);
        else if (event instanceof PlayerInteractEvent interaction && interaction.getAction() == org.bukkit.event.block.Action.LEFT_CLICK_BLOCK)
            setTimestamp(interaction);
    }

    private void setTimestamp(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null && !BlockUtils.isLiquid(block.getType()))
            breakCompletionAtMillis = System.currentTimeMillis() + BukkitRegistry.getBlockBreakTime(block, event.getPlayer());
    }

    private void checkBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || event.getPlayer().getGameMode() != GameMode.SURVIVAL || isExempt(new ExemptType[] { ExemptType.VEHICLE }) || checkItems(event.getPlayer()))
            return;
        double remainingMillis = System.currentTimeMillis() - breakCompletionAtMillis;
        if (remainingMillis < VulcanSettings.checksPlayerFastbreakAMinDifference)
            fail("block=" + event.getBlock().getType() + " diff=" + remainingMillis + " tool=" + event.getPlayer().getItemInHand().getType());
        else
            decayBuffer();
    }

    private boolean checkItems(Player player) {
        return player.getInventory().getItemInHand().getType().name().contains("COPPER");
    }

    private boolean checkItems2(Material material) {
        return BlockUtils.isLiquid(material);
    }
}
