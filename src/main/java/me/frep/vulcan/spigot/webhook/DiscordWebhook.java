package me.frep.vulcan.spigot.webhook;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.net.ssl.HttpsURLConnection;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.message.format.MessageBuilder;

public final class DiscordWebhook {

    private final String url;

    private String content;

    private String username;

    private String avatarUrl;

    private boolean textToSpeech;

    private final List<MessageBuilder> embeds = new ArrayList<>();

    public DiscordWebhook(String url) {
        this.url = Objects.requireNonNull(url, "url");
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setTextToSpeech(boolean textToSpeech) {
        this.textToSpeech = textToSpeech;
    }

    public void addEmbed(MessageBuilder embed) {
        embeds.add(Objects.requireNonNull(embed, "embed"));
    }

    public JsonObject toJson() {
        if ((content == null || content.isEmpty()) && embeds.isEmpty())
            throw new IllegalArgumentException("Webhook has no content or embeds");
        JsonObject body = new JsonObject();
        if (content != null)
            body.addProperty("content", content);
        if (username != null)
            body.addProperty("username", username);
        if (avatarUrl != null)
            body.addProperty("avatar_url", avatarUrl);
        body.addProperty("tts", textToSpeech);
        if (!embeds.isEmpty()) {
            JsonArray array = new JsonArray();
            for (MessageBuilder embed : embeds) array.add(embed.toJson());
            body.add("embeds", array);
        }
        return body;
    }

    public void send() {
        byte[] payload = toJson().toString().getBytes(StandardCharsets.UTF_8);
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(10_000);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("User-Agent", "VulcanWebhook");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(payload.length);
            try (OutputStream output = connection.getOutputStream()) {
                output.write(payload);
            }
            int status = connection.getResponseCode();
            if (status < 200 || status >= 300)
                throw new IOException("Webhook returned HTTP " + status);
            try (var response = connection.getInputStream()) {
                response.transferTo(OutputStream.nullOutputStream());
            }
        } catch (IOException exception) {
            PluginServices.INSTANCE.getVulcanPlugin().getLogger().warning("Cannot send Discord webhook: " + exception.getMessage());
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }
}
