package me.frep.vulcan.spigot.bukkit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

public class Metrics {

    public static final int B_STATS_VERSION = 1;

    private static final String SUBMISSION_URL = "https://bStats.org/submitData/bukkit";

    private final Plugin plugin;

    private final int pluginId;

    private final List<CustomChart> charts = new ArrayList<>();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(task -> {
        Thread thread = new Thread(task, "bStats-Metrics");
        thread.setDaemon(true);
        return thread;
    });

    private final boolean enabled;

    private final boolean logFailedRequests;

    private final boolean logSentData;

    private final boolean logResponseStatusText;

    private final String serverUUID;

    public Metrics(Plugin plugin, int pluginId) {
        if (plugin == null)
            throw new IllegalArgumentException("Plugin cannot be null!");
        this.plugin = plugin;
        this.pluginId = pluginId;
        File file = new File(new File(plugin.getDataFolder().getParentFile(), "bStats"), "config.yml");
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        if (!configuration.isSet("serverUuid")) {
            configuration.addDefault("enabled", true);
            configuration.addDefault("serverUuid", UUID.randomUUID().toString());
            configuration.addDefault("logFailedRequests", false);
            configuration.addDefault("logSentData", false);
            configuration.addDefault("logResponseStatusText", false);
            configuration.options().copyDefaults(true);
            try {
                configuration.save(file);
            } catch (IOException failure) {
                plugin.getLogger().warning("Cannot save bStats configuration: " + failure.getMessage());
            }
        }
        enabled = configuration.getBoolean("enabled", true);
        serverUUID = configuration.getString("serverUuid");
        logFailedRequests = configuration.getBoolean("logFailedRequests", false);
        logSentData = configuration.getBoolean("logSentData", false);
        logResponseStatusText = configuration.getBoolean("logResponseStatusText", false);
        if (enabled) {
            boolean alreadyRegistered = false;
            for (Class<?> service : Bukkit.getServicesManager().getKnownServices()) {
                try {
                    service.getField("B_STATS_VERSION");
                    alreadyRegistered = true;
                    break;
                } catch (NoSuchFieldException absent) {
                }
            }
            Bukkit.getServicesManager().register(Metrics.class, this, plugin, ServicePriority.Normal);
            if (!alreadyRegistered)
                startSubmitting();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void addCustomChart(CustomChart chart) {
        if (chart == null)
            throw new IllegalArgumentException("Chart cannot be null!");
        charts.add(chart);
    }

    private void startSubmitting() {
        long initialDelay = (long) (60000.0 * (3.0 + Math.random() * 3.0));
        long secondDelay = (long) (60000.0 * Math.random() * 30.0);
        scheduler.schedule(this::scheduleSubmission, initialDelay, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(this::scheduleSubmission, initialDelay + secondDelay, 1800000L, TimeUnit.MILLISECONDS);
    }

    private void scheduleSubmission() {
        if (!plugin.isEnabled()) {
            scheduler.shutdown();
            return;
        }
        me.frep.vulcan.spigot.lifecycle.PluginServices.INSTANCE.getScheduler().execute(this::submitData);
    }

    public JsonObject getPluginData() {
        JsonObject data = new JsonObject();
        data.addProperty("pluginName", plugin.getDescription().getName());
        data.addProperty("id", pluginId);
        data.addProperty("pluginVersion", plugin.getDescription().getVersion());
        JsonArray customCharts = new JsonArray();
        for (CustomChart chart : charts) {
            JsonObject value = CustomChart.getValue(chart);
            if (value != null)
                customCharts.add(value);
        }
        data.add("customCharts", customCharts);
        return data;
    }

    private JsonObject getServerData() {
        JsonObject data = new JsonObject();
        data.addProperty("serverUUID", serverUUID);
        data.addProperty("playerAmount", Bukkit.getOnlinePlayers().size());
        data.addProperty("onlineMode", Bukkit.getOnlineMode() ? 1 : 0);
        data.addProperty("bukkitVersion", Bukkit.getVersion());
        data.addProperty("bukkitName", Bukkit.getName());
        data.addProperty("javaVersion", System.getProperty("java.version"));
        data.addProperty("osName", System.getProperty("os.name"));
        data.addProperty("osArch", System.getProperty("os.arch"));
        data.addProperty("osVersion", System.getProperty("os.version"));
        data.addProperty("coreCount", Runtime.getRuntime().availableProcessors());
        return data;
    }

    private void submitData() {
        JsonObject data = getServerData();
        JsonArray plugins = new JsonArray();
        for (Class<?> service : Bukkit.getServicesManager().getKnownServices()) {
            try {
                service.getField("B_STATS_VERSION");
                for (RegisteredServiceProvider<?> registration : Bukkit.getServicesManager().getRegistrations(service)) {
                    Object value = service.getMethod("getPluginData").invoke(registration.getProvider());
                    if (value != null)
                        plugins.add(JsonParser.parseString(value.toString()).getAsJsonObject());
                }
            } catch (ReflectiveOperationException | IllegalArgumentException failure) {
                if (logFailedRequests)
                    plugin.getLogger().warning("Cannot collect metrics for " + service.getName() + ": " + failure.getMessage());
            }
        }
        data.add("plugins", plugins);
        scheduler.execute(() -> {
            try {
                sendData(data);
            } catch (IOException failure) {
                if (logFailedRequests)
                    plugin.getLogger().warning("Cannot submit bStats metrics: " + failure.getMessage());
            }
        });
    }

    private void sendData(JsonObject data) throws IOException {
        if (Bukkit.isPrimaryThread())
            throw new IllegalStateException("Metrics submission must run asynchronously");
        if (logSentData)
            plugin.getLogger().info("Sending data to bStats: " + data);
        byte[] compressed = compress(data.toString());
        HttpsURLConnection connection = (HttpsURLConnection) new URL(SUBMISSION_URL).openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Encoding", "gzip");
        connection.setRequestProperty("User-Agent", "MC-Server/1");
        connection.setFixedLengthStreamingMode(compressed.length);
        connection.setDoOutput(true);
        try {
            try (var output = connection.getOutputStream()) {
                output.write(compressed);
            }
            try (var input = connection.getInputStream()) {
                String response = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                if (logResponseStatusText)
                    plugin.getLogger().info("bStats response: " + response);
            }
        } finally {
            connection.disconnect();
        }
    }

    private static byte[] compress(String text) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(bytes)) {
            gzip.write(text.getBytes(StandardCharsets.UTF_8));
        }
        return bytes.toByteArray();
    }
}
