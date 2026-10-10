package me.frep.vulcan.spigot.bukkit;

import com.google.gson.JsonObject;
import java.util.concurrent.Callable;

public class SimplePie extends CustomChart {

    private final Callable<?> callable;

    private static final String text = "value";

    public SimplePie(String text2, Callable<?> callable2) {
        super(text2);
        this.callable = callable2;
    }

    @Override
    protected JsonObject getValue() throws Exception {
        JsonObject jsonObject = new JsonObject();
        String text2 = (String) this.callable.call();
        if (text2 == null) {
            return null;
        }
        if (text2.isEmpty()) {
            return null;
        }
        jsonObject.addProperty(text, text2);
        return jsonObject;
    }
}
