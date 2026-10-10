package me.frep.vulcan.spigot.bukkit;

import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import com.google.gson.JsonElement;

public class MultiLineChart extends CustomChart {

    private final Callable<?> callable;

    private static final String text = "values";

    public MultiLineChart(String text2, Callable<?> callable2) {
        super(text2);
        this.callable = callable2;
    }

    @Override
    protected JsonObject getValue() throws Exception {
        Map<?, ?> map;
        JsonObject jsonObject;
        JsonObject jsonObject2;
        jsonObject2 = new JsonObject();
        jsonObject = new JsonObject();
        map = (Map<?, ?>) this.callable.call();
        if ((map == null)) {
            return null;
        }
        if ((map.isEmpty())) {
            return null;
        }
        boolean condition = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if ((Integer) entry.getValue() == 0) {
                continue;
            }
            condition = false;
            jsonObject.addProperty((String) entry.getKey(), (Number) entry.getValue());
        }
        if (condition) {
            return null;
        }
        jsonObject2.add(text, (JsonElement) jsonObject);
        return jsonObject2;
    }
}
