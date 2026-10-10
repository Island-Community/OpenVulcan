package me.frep.vulcan.spigot.bukkit;

import com.google.gson.JsonObject;
import java.util.concurrent.Callable;

public class SingleLineChart extends CustomChart {

    private final Callable<?> callable;

    private static final String text = "value";

    public SingleLineChart(String text2, Callable<?> callable2) {
        super(text2);
        this.callable = callable2;
    }

    @Override
    protected JsonObject getValue() throws Exception {
        JsonObject jsonObject = new JsonObject();
        int index = (Integer) this.callable.call();
        if (index == 0) {
            return null;
        }
        jsonObject.addProperty(text, index);
        return jsonObject;
    }
}
