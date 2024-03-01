package plant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Plant implements java.io.Serializable {
    @SerializedName("data_set")
    @DataSet
    private ArrayList<SensorData> data_set = new ArrayList<>();
    @SerializedName("name")
    private String name;
    @SerializedName("type")
    private String type;
    @SerializedName("planted")
    private Date planted;

    public Plant(String name, String type, Date planted) {
        this.name = name;
        this.type = type;
        this.planted = planted;
    }

    public Plant(String json) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss a", Locale.US);

        Gson gson = new GsonBuilder()
                .setDateFormat(dateFormat.toPattern())
                .create();
        Plant plant = gson.fromJson(json, Plant.class);
        this.name = plant.getName();
        this.type = plant.getType();
        this.planted = plant.getPlanted();
        this.data_set = plant.getData_set();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Date getPlanted() {
        return planted;
    }

    public ArrayList<SensorData> getData_set() {
        return data_set;
    }

    public synchronized void addData(SensorData data) {
        data_set.add(data);
    }



    public String getData(Date from, Date to) {
        StringBuilder json = new StringBuilder("[");
        for (SensorData data : data_set) {
            Date date = data.getDate();
            if (date.after(from) && date.before(to)) {
                json.append(data.toJson()).append(",");
            }
        }
        json = new StringBuilder(json.substring(0, json.length() - 1));
        json.append("]");
        if (json.length() == 1) {
            return "[]";
        }
        return json.toString();
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
    public String getInfo() {
        Gson gson = new GsonBuilder().
                setExclusionStrategies(new ExclusionStrategyPlant()).
                create();

        return gson.toJson(this);
    }

    public String getNames() {
        return new Gson().toJson(this.name);
    }
}
