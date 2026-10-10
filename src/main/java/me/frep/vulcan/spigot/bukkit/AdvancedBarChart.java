package me.frep.vulcan.spigot.bukkit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import com.google.gson.JsonElement;

public class AdvancedBarChart extends CustomChart {

    private final Callable<?> callable;

    private static final String text = "values";

    public AdvancedBarChart(String text2, Callable<?> callable2) {
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
            if (((int[]) entry.getValue()).length == 0) {
                continue;
            }
            condition = false;
            JsonArray jsonArray = new JsonArray();
            for (int index : (int[]) entry.getValue()) {
                jsonArray.add((JsonElement) new JsonPrimitive((Number) index));
            }
            jsonObject.add((String) entry.getKey(), (JsonElement) jsonArray);
        }
        if (condition) {
            return null;
        }
        jsonObject2.add(text, (JsonElement) jsonObject);
        return jsonObject2;
    }
}
