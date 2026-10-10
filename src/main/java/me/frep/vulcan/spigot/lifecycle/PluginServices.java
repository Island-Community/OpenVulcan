package me.frep.vulcan.spigot.lifecycle;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.frep.vulcan.api.VulcanAPI.Factory;
import me.frep.vulcan.spigot.VulcanPlugin;
import me.frep.vulcan.spigot.alert.AlertManager;
import me.frep.vulcan.spigot.api.VulcanSpigotAPI;
import me.frep.vulcan.spigot.bukkit.Metrics;
import me.frep.vulcan.spigot.check.CheckSettingsLoader;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.check.manager.CheckManager;
import me.frep.vulcan.spigot.command.AlertsCommand;
import me.frep.vulcan.spigot.command.JudgementDayCommand;
import me.frep.vulcan.spigot.command.InfoCommand;
import me.frep.vulcan.spigot.command.PunishmentLogsCommand;
import me.frep.vulcan.spigot.command.ViolationLogsCommand;
import me.frep.vulcan.spigot.command.VulcanTabCompleter;
import me.frep.vulcan.spigot.config.StatisticsFile;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.data.PlayerDataStore;
import me.frep.vulcan.spigot.integration.placeholder.PlaceholderHook;
import me.frep.vulcan.spigot.listener.BlockInteractionListener;
import me.frep.vulcan.spigot.listener.CombatDamageListener;
import me.frep.vulcan.spigot.listener.McMmoAbilityListener;
import me.frep.vulcan.spigot.listener.PlayerSessionListener;
import me.frep.vulcan.spigot.listener.ResurrectListener;
import me.frep.vulcan.spigot.listener.RiptideListener;
import me.frep.vulcan.spigot.packet.UserDataInitializer;
import me.frep.vulcan.spigot.packet.listener.PacketListener;
import me.frep.vulcan.spigot.packet.listener.SendPacketListener;
import me.frep.vulcan.spigot.punishment.PunishmentHandler;
import me.frep.vulcan.spigot.task.TickProcessor;
import me.frep.vulcan.spigot.task.ViolationResetProcessor;
import me.frep.vulcan.spigot.tracker.entity.EntityMetadataTracker;
import me.frep.vulcan.spigot.util.scheduling.Scheduler;
import me.frep.vulcan.spigot.util.scheduling.UniversalScheduler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public enum PluginServices {

    INSTANCE;

    private VulcanPlugin vulcanPlugin;

    private long startedAtMillis;

    private final Logger logger = Bukkit.getLogger();

    private boolean legacyFloodgateEnabled;

    private boolean floodgateEnabled;

    private boolean placeholderApiEnabled;

    private boolean mcMmoEnabled;

    private boolean crackshotEnabled;

    private boolean disguisesEnabled;

    private boolean mythicMobsEnabled;

    private boolean gSitEnabled;

    private boolean breweryEnabled;

    private boolean mythicMobs5;

    private boolean foliaServer;

    private boolean grimEnabled;

    private final Map<Integer, Integer> hookedBoatTicks = new HashMap<>();

    private final TickProcessor tickProcessor = new TickProcessor();

    private final ViolationResetProcessor violationResetProcessor = new ViolationResetProcessor();

    private PluginManager pluginManager;

    private final PacketListener packetListener = new PacketListener();

    private final EntityMetadataTracker entityMetadataTracker = new EntityMetadataTracker();

    private final UserDataInitializer userDataInitializer = new UserDataInitializer();

    private final PlayerDataStore playerDataStore = new PlayerDataStore();

    private final AlertManager alertManager = new AlertManager();

    private final PunishmentHandler punishmentHandler = new PunishmentHandler();

    private ExecutorService judgementExecutor = Executors.newSingleThreadExecutor();

    private ExecutorService violationExecutor = Executors.newSingleThreadExecutor();

    private ExecutorService alertExecutor = Executors.newSingleThreadExecutor();

    private ExecutorService punishmentExecutor = Executors.newSingleThreadExecutor();

    private static Scheduler scheduler;

    private final Map<UUID, String> clientBrands = new HashMap<>();

    private int punishmentsInWindow = 0;

    private boolean started;

    public synchronized void start(VulcanPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        if (started) {
            return;
        }
        if (judgementExecutor.isShutdown())
            judgementExecutor = Executors.newSingleThreadExecutor();
        if (violationExecutor.isShutdown())
            violationExecutor = Executors.newSingleThreadExecutor();
        if (alertExecutor.isShutdown())
            alertExecutor = Executors.newSingleThreadExecutor();
        if (punishmentExecutor.isShutdown())
            punishmentExecutor = Executors.newSingleThreadExecutor();
        this.vulcanPlugin = plugin;
        this.pluginManager = plugin.getServer().getPluginManager();
        this.foliaServer = ExemptionManager.isFolia();
        scheduler = UniversalScheduler.getScheduler(plugin);
        plugin.saveDefaultConfig();
        VulcanSettings.initialize();
        VulcanSettings.loadSettings();
        StatisticsFile.initialize();
        CheckManager.loadEnabledChecks();
        this.tickProcessor.start();
        this.violationResetProcessor.start();
        logInfo("Starting Vulcan... Server Version: " + ExemptionManager.getServerVersion().getReleaseName() + " detected!");
        PacketEvents.getAPI().getEventManager().registerListener(new SendPacketListener(), PacketListenerPriority.HIGHEST);
        detectIntegrations();
        Factory.setApi(new VulcanSpigotAPI());
        registerCommands(plugin);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "vulcan:bungee");
        if (VulcanSettings.settingsBstats && ExemptionManager.isNewerThan1_7_10()) {
            new Metrics(plugin, 10297);
            logInfo("BStats enabled!");
        }
        this.startedAtMillis = System.currentTimeMillis();
        registerListeners(plugin);
        System.setProperty("com.viaversion.handlePingsAsInvAcknowledgements", "true");
        System.setProperty("paper.explicit-flush", "true");
        started = true;
    }

    private void detectIntegrations() {
        grimEnabled = pluginManager.getPlugin("GrimAC") != null;
        legacyFloodgateEnabled = pluginManager.getPlugin("floodgate-bukkit") != null;
        floodgateEnabled = pluginManager.getPlugin("floodgate") != null;
        mcMmoEnabled = pluginManager.getPlugin("mcMMO") != null;
        crackshotEnabled = pluginManager.getPlugin("Crackshot") != null;
        disguisesEnabled = pluginManager.getPlugin("LibsDisguises") != null;
        Plugin mythicMobs = pluginManager.getPlugin("MythicMobs");
        mythicMobsEnabled = mythicMobs != null;
        mythicMobs5 = mythicMobsEnabled && mythicMobs.getDescription().getVersion().startsWith("5");
        gSitEnabled = pluginManager.getPlugin("GSit") != null || pluginManager.getPlugin("GSitPro") != null;
        breweryEnabled = pluginManager.getPlugin("Brewery") != null;
        if (legacyFloodgateEnabled)
            logInfo("Floodgate 1.0 found. Enabling hook!");
        if (floodgateEnabled)
            logInfo("Floodgate 2.0 found. Enabling hook!");
        if (mcMmoEnabled)
            logInfo("mcMMO found. Enabling hook!");
        if (disguisesEnabled)
            logInfo("LibsDisguises found. Enabling hook!");
        if (mythicMobsEnabled)
            logInfo("MythicMobs found. Enabling hook!");
        if (breweryEnabled)
            logInfo("Brewery found. Enabling hook!");
        placeholderApiEnabled = pluginManager.getPlugin("PlaceholderAPI") != null;
        if (placeholderApiEnabled) {
            new PlaceholderHook().register();
            logInfo("PlaceholderAPI found. Enabling hook!");
        }
    }

    private void registerCommands(VulcanPlugin plugin) {
        registerCommand(plugin, "punishlogs", new PunishmentLogsCommand());
        registerCommand(plugin, "alerts", new InfoCommand());
        registerCommand(plugin, "vulcan", new AlertsCommand());
        registerCommand(plugin, "jday", new JudgementDayCommand());
        registerCommand(plugin, "logs", new ViolationLogsCommand());
        PluginCommand command = plugin.getCommand("vulcan");
        if (command != null) {
            command.setTabCompleter(new VulcanTabCompleter());
        }
    }

    private void registerCommand(VulcanPlugin plugin, String name, CommandExecutor executor) {
        PluginCommand command = plugin.getCommand(name);
        if (command == null) {
            logger.warning("Missing command in plugin.yml: " + name);
            return;
        }
        command.setExecutor(executor);
    }

    private void registerListeners(VulcanPlugin plugin) {
        pluginManager.registerEvents(new PlayerSessionListener(), plugin);
        pluginManager.registerEvents(new BlockInteractionListener(), plugin);
        if (mcMmoEnabled && VulcanSettings.settingsHookMcmmo) {
            pluginManager.registerEvents(new McMmoAbilityListener(), plugin);
            logInfo("Registered mcMMO listener!");
        }
        if (mythicMobsEnabled && VulcanSettings.settingsHookMythicmobs) {
            pluginManager.registerEvents(new CombatDamageListener(), plugin);
            logInfo("Registered MythicMobs listener!");
        }
        if (ExemptionManager.isAtLeast1_11()) {
            pluginManager.registerEvents(new ResurrectListener(), plugin);
        }
        if (ExemptionManager.isNewerThan1_13()) {
            pluginManager.registerEvents(new RiptideListener(), plugin);
        }
    }

    public synchronized void stop(VulcanPlugin plugin) {
        if (!started) {
            return;
        }
        this.tickProcessor.stop();
        this.violationResetProcessor.stop();
        this.judgementExecutor.shutdownNow();
        this.violationExecutor.shutdownNow();
        this.alertExecutor.shutdownNow();
        this.punishmentExecutor.shutdownNow();
        if (scheduler != null) {
            scheduler.cancelTasks(plugin);
        }
        started = false;
    }

    public void reload() {
        if (vulcanPlugin == null) {
            throw new IllegalStateException("Vulcan has not been initialized");
        }
        VulcanSettings.reload();
        VulcanSettings.loadSettings();
        CheckSettingsLoader.clearCheckSettings();
        CheckSettingsLoader.loadCheckSettings();
        vulcanPlugin.reloadConfig();
    }

    public void logInfo(String text) {
        Bukkit.getLogger().log(Level.INFO, "[Vulcan] " + text);
    }

    public long getStartedAtMillis() {
        return this.startedAtMillis;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public boolean isLegacyFloodgateEnabled() {
        return this.legacyFloodgateEnabled;
    }

    public boolean isFloodgateEnabled() {
        return this.floodgateEnabled;
    }

    public boolean isPlaceholderApiEnabled() {
        return this.placeholderApiEnabled;
    }

    public boolean isMcMmoEnabled() {
        return this.mcMmoEnabled;
    }

    public boolean isCrackshotEnabled() {
        return this.crackshotEnabled;
    }

    public boolean isDisguisesEnabled() {
        return this.disguisesEnabled;
    }

    public boolean isMythicMobsEnabled() {
        return this.mythicMobsEnabled;
    }

    public boolean isGSitEnabled() {
        return this.gSitEnabled;
    }

    public boolean isBreweryEnabled() {
        return this.breweryEnabled;
    }

    public boolean isMythicMobs5() {
        return this.mythicMobs5;
    }

    public boolean isFoliaServer() {
        return this.foliaServer;
    }

    public boolean isGrimEnabled() {
        return this.grimEnabled;
    }

    public Map<Integer, Integer> getHookedBoatTicks() {
        return this.hookedBoatTicks;
    }

    public TickProcessor getTickProcessor() {
        return this.tickProcessor;
    }

    public ViolationResetProcessor getViolationResetProcessor() {
        return this.violationResetProcessor;
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public PacketListener getPacketListener() {
        return this.packetListener;
    }

    public EntityMetadataTracker getEntityMetadataTracker() {
        return this.entityMetadataTracker;
    }

    public UserDataInitializer getUserDataInitializer() {
        return this.userDataInitializer;
    }

    public PlayerDataStore getPlayerDataStore() {
        return this.playerDataStore;
    }

    public AlertManager getAlertManager() {
        return this.alertManager;
    }

    public PunishmentHandler getPunishmentHandler() {
        return this.punishmentHandler;
    }

    public ExecutorService getJudgementExecutor() {
        return this.judgementExecutor;
    }

    public ExecutorService getViolationExecutor() {
        return this.violationExecutor;
    }

    public ExecutorService getAlertExecutor() {
        return this.alertExecutor;
    }

    public ExecutorService getPunishmentExecutor() {
        return this.punishmentExecutor;
    }

    public Map<UUID, String> getClientBrands() {
        return this.clientBrands;
    }

    public VulcanPlugin getVulcanPlugin() {
        return this.vulcanPlugin;
    }

    public void setLegacyFloodgateEnabled(boolean legacyFloodgateEnabled2) {
        this.legacyFloodgateEnabled = legacyFloodgateEnabled2;
    }

    public void setFloodgateEnabled(boolean floodgateEnabled2) {
        this.floodgateEnabled = floodgateEnabled2;
    }

    public void setPlaceholderApiEnabled(boolean placeholderApiEnabled2) {
        this.placeholderApiEnabled = placeholderApiEnabled2;
    }

    public void setMcMmoEnabled(boolean mcMmoEnabled2) {
        this.mcMmoEnabled = mcMmoEnabled2;
    }

    public void setCrackshotEnabled(boolean crackshotEnabled2) {
        this.crackshotEnabled = crackshotEnabled2;
    }

    public void setDisguisesEnabled(boolean disguisesEnabled2) {
        this.disguisesEnabled = disguisesEnabled2;
    }

    public void setMythicMobsEnabled(boolean mythicMobsEnabled2) {
        this.mythicMobsEnabled = mythicMobsEnabled2;
    }

    public void setGSitEnabled(boolean gSitEnabled2) {
        this.gSitEnabled = gSitEnabled2;
    }

    public void setBreweryEnabled(boolean breweryEnabled2) {
        this.breweryEnabled = breweryEnabled2;
    }

    public void setMythicMobs5(boolean mythicMobs52) {
        this.mythicMobs5 = mythicMobs52;
    }

    public void setFoliaServer(boolean foliaServer2) {
        this.foliaServer = foliaServer2;
    }

    public void setGrimEnabled(boolean grimEnabled2) {
        this.grimEnabled = grimEnabled2;
    }

    public static Scheduler getScheduler() {
        return scheduler;
    }

    public int getPunishmentsInWindow() {
        return this.punishmentsInWindow;
    }

    public void setPunishmentsInWindow(int punishmentsInWindow2) {
        this.punishmentsInWindow = punishmentsInWindow2;
    }
}
