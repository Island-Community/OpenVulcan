package me.frep.vulcan.spigot.message.format;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.Color;

public final class MessageBuilder {

    private final JsonObject embed = new JsonObject();

    private final JsonArray fields = new JsonArray();

    public MessageBuilder setTitle(String title) {
        embed.addProperty("title", title);
        return this;
    }

    public MessageBuilder setDescription(String description) {
        embed.addProperty("description", description);
        return this;
    }

    public MessageBuilder setUrl(String url) {
        embed.addProperty("url", url);
        return this;
    }

    public MessageBuilder setColor(Color color) {
        embed.addProperty("color", color.getRGB() & 0xffffff);
        return this;
    }

    public MessageBuilder setFooter(String text, String iconUrl) {
        JsonObject footer = new JsonObject();
        footer.addProperty("text", text);
        footer.addProperty("icon_url", iconUrl);
        embed.add("footer", footer);
        return this;
    }

    public MessageBuilder setThumbnail(String url) {
        return setMedia("thumbnail", url);
    }

    public MessageBuilder setImage(String url) {
        return setMedia("image", url);
    }

    private MessageBuilder setMedia(String name, String url) {
        JsonObject media = new JsonObject();
        media.addProperty("url", url);
        embed.add(name, media);
        return this;
    }

    public MessageBuilder setAuthor(String name, String url, String iconUrl) {
        JsonObject author = new JsonObject();
        author.addProperty("name", name);
        author.addProperty("url", url);
        author.addProperty("icon_url", iconUrl);
        embed.add("author", author);
        return this;
    }

    public MessageBuilder addField(String name, String value, boolean inline) {
        JsonObject field = new JsonObject();
        field.addProperty("name", name);
        field.addProperty("value", value);
        field.addProperty("inline", inline);
        fields.add(field);
        return this;
    }

    public JsonObject toJson() {
        JsonObject result = embed.deepCopy();
        if (!fields.isEmpty())
            result.add("fields", fields.deepCopy());
        return result;
    }
}
