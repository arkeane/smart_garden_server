package plant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class PlantTrigger {
    @SerializedName("name")
    private String name;
    @SerializedName("trigger_status")
    private Boolean status;
    @SerializedName("temperature_trigger")
    private Float temperature;
    @SerializedName("humidity_trigger")
    private Float humidity;
    @SerializedName("light_trigger")
    private Float light;
    @SerializedName("moisture_trigger")
    private Float moisture;

    public PlantTrigger(String name, Boolean status, Float temperature, Float humidity, Float light, Float moisture) {
        this.name = name;
        this.status = status;
        this.temperature = temperature;
        this.humidity = humidity;
        this.light = light;
        this.moisture = moisture;
    }

    public PlantTrigger(String json){
        Gson gson = new GsonBuilder()
                .create();
        PlantTrigger trig = gson.fromJson(json, PlantTrigger.class);
        this.name = trig.getName();
        this.status = trig.getStatus();
        this.temperature = trig.getTemperature();
        this.humidity = trig.getHumidity();
        this.light = trig.getLight();
        this.moisture = trig.getMoisture();
    }

    public String toJson() {
        Gson gson = new GsonBuilder().
                create();
        return gson.toJson(this);
    }

    public String getName() {
        return name;
    }

    public Boolean getStatus() {
        return status;
    }

    public Float getTemperature() {
        return temperature;
    }

    public Float getHumidity() {
        return humidity;
    }

    public Float getLight() {
        return light;
    }

    public Float getMoisture() {
        return moisture;
    }
}
