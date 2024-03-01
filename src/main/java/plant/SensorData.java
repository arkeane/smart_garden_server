package plant;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class SensorData implements java.io.Serializable {
    @SerializedName("date")
    private final Date date;
    @SerializedName("temperature")
    private final float temperature;
    @SerializedName("humidity")
    private final float humidity;
    @SerializedName("light")
    private final float light;
    @SerializedName("moisture")
    private final float moisture;

    public SensorData(float temperature, float humidity, float light, float moisture) {
        this.date = new Date();
        this.temperature = temperature;
        this.humidity = humidity;
        this.light = light;
        this.moisture = moisture;
    }

    public Date getDate() {
        return date;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
