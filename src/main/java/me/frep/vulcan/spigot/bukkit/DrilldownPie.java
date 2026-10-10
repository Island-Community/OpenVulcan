package me.frep.vulcan.spigot.bukkit;

import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import com.google.gson.JsonElement;

public class DrilldownPie extends CustomChart {

    private final Callable<?> callable;

    private static final String text = "values";

    public DrilldownPie(String text2, Callable<?> callable2) {
        super(text2);
        this.callable = callable2;
    }

    @Override
    public JsonObject getValue() throws Exception {
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
            JsonObject jsonObject3 = new JsonObject();
            boolean condition2 = true;
            for (Map.Entry<?, ?> entry2 : ((Map<?, ?>) map.get(entry.getKey())).entrySet()) {
                jsonObject3.addProperty((String) entry2.getKey(), (Number) entry2.getValue());
                condition2 = false;
            }
            if (condition2)
                continue;
            condition = false;
            jsonObject.add((String) entry.getKey(), (JsonElement) jsonObject3);
        }
        if (condition) {
            return null;
        }
        jsonObject2.add(text, (JsonElement) jsonObject);
        return jsonObject2;
    }
}
