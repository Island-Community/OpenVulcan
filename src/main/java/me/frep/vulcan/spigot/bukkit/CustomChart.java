package me.frep.vulcan.spigot.bukkit;

import com.google.gson.JsonObject;

public abstract class CustomChart {

    final public String text;

    CustomChart(String text2) {
        if (!(text2 != null && !text2.isEmpty())) {
            throw new IllegalArgumentException("ChartId cannot be null or empty!");
        }
        this.text = text2;
    }

    private JsonObject getChartData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("chartId", this.text);
        try {
            JsonObject data = this.getValue();
            if (data == null)
                return null;
            jsonObject.add("data", data);
            return jsonObject;
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(CustomChart.class.getName()).log(java.util.logging.Level.WARNING, "Cannot collect chart: " + text, ex);
            return null;
        }
    }

    protected abstract JsonObject getValue() throws Exception;

    static JsonObject getValue(CustomChart customChart) {
        return customChart.getChartData();
    }
}
