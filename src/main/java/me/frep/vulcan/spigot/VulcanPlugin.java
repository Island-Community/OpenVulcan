package me.frep.vulcan.spigot;

import me.frep.vulcan.spigot.lifecycle.PluginServices;
import org.bukkit.plugin.java.JavaPlugin;

public final class VulcanPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginServices.INSTANCE.start(this);
    }

    @Override
    public void onDisable() {
        PluginServices.INSTANCE.stop(this);
    }
}
