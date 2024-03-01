import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import plant.Plant;
import plant.PlantTrigger;
import plant.SensorData;

import java.io.DataOutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Float.parseFloat;
import static spark.Spark.*;

public class Main {
    public static int post_embedded(String url, String payload) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(payload);
            wr.flush();
            wr.close();
            return con.getResponseCode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        String ip = System.getenv("REDIS_HOST");
        int port = Integer.parseInt(System.getenv("REDIS_PORT"));

        System.out.println("Redis HOST: " + ip);
        System.out.println("Redis Port: " + port);

        RedisHandler rh = new RedisHandler(ip, port);
        TreeMap<String, String> sensors_map = new TreeMap<>();

        // check redis connection
        if (rh.ping()) {
            System.out.println("Redis connection established");
        } else {
            System.out.println("Redis connection failed");
            System.exit(1);
        }

        path("/sensors", () -> {
            get("/:name", (req, res) -> {
                String sensor_plant = req.params(":name");
                if (!sensors_map.containsKey(sensor_plant)) {
                    res.status(404);
                    return new Gson().toJson("Sensor " + sensor_plant + " not found");
                }
                res.status(200);
                return new Gson().toJson(sensors_map.get(sensor_plant));
            });

            post("/register", (req, res) -> {
                String requestBody = req.body();
                Type type = new TypeToken<HashMap<String, String>>() {
                }.getType();
                HashMap<String, String> data = new Gson().fromJson(requestBody, type);
                String sensorPlant = data.get("name");
                String sensorIp = data.get("ip");
                sensors_map.put(sensorPlant, sensorIp);

                // retrieve trigger data from redis
                String db_trigger_key = "trigger_" + sensorPlant;
                PlantTrigger trigger = rh.getTrigger(db_trigger_key);

                String url = "http://" + sensorIp + "/update";
                String payload = trigger.toJson();
                int responseCode = post_embedded(url, payload);
                if (responseCode != 200) {
                    res.status(500);
                    return new Gson().toJson("Error updating sensor " + sensorPlant);
                }
                res.status(200);
                return new Gson().toJson("Sensor " + sensorPlant + " registered at " + sensorIp);
            });

            put("/triggers", (req, res) -> {
                String requestBody = req.body();
                Type type = new TypeToken<HashMap<String, String>>() {
                }.getType();
                HashMap<String, String> data = new Gson().fromJson(requestBody, type);
                String sensorPlant = data.get("name");
                String sensorIp;
                try {
                    sensorIp = sensors_map.get(sensorPlant);
                }
                catch (Exception e)
                {
                    res.status(404);
                    return new Gson().toJson("Sensor " + sensorPlant + " not found");
                }
                if (sensorIp == null) {
                    res.status(404);
                    return new Gson().toJson("Sensor " + sensorPlant + " not found");
                }
                boolean trigger_status = parseBoolean(data.get("trigger_status"));
                float temperature_trigger = parseFloat(data.get("temperature_trigger"));
                float light_trigger = parseFloat(data.get("light_trigger"));
                float humidity_trigger = parseFloat(data.get("humidity_trigger"));
                float moisture_trigger = parseFloat(data.get("moisture_trigger"));

                PlantTrigger trigger = new PlantTrigger(sensorPlant, trigger_status, temperature_trigger, humidity_trigger, light_trigger, moisture_trigger);
                String db_trigger_key = "trigger_" + sensorPlant;
                rh.saveTrigger(db_trigger_key, trigger.toJson());

                String url = "http://" + sensorIp + "/update";
                String payload = "{\"trigger_status\":" + trigger_status + ",\"humidity_trigger\":" + humidity_trigger + ",\"light_trigger\":" + light_trigger + ",\"temperature_trigger\":" + temperature_trigger + ",\"moisture_trigger\":" + moisture_trigger + "}";
                int responseCode = post_embedded(url, payload);
                res.status(200);
                return new Gson().toJson(responseCode);
            });

            get("/triggers/:name", (req, res) -> {
                String plant = req.params(":name");
                String db_trigger_key = "trigger_" + plant;
                PlantTrigger trigger = rh.getTrigger(db_trigger_key);
                if (trigger == null) {
                    res.status(404);
                    return new Gson().toJson("Trigger data for " + plant + " not found");
                }
                res.status(200);
                return trigger.toJson();
            });

            put("/water", (req, res) -> {
                String requestBody = req.body();
                String plant = new Gson().fromJson(requestBody, String.class);
                String sensor_ip = sensors_map.get(plant);
                System.out.println(plant);
                if (sensor_ip == null) {
                    res.status(404);
                    return new Gson().toJson("Sensor " + plant + " not found");
                }
                String url = "http://" + sensor_ip + "/water";
                String payload = "{\"water_plant\":true}";
                int responseCode = post_embedded(url, payload);
                res.status(200);
                return new Gson().toJson(responseCode);
            });
        });

        path("/plants", () -> {
            post("/register", (req, res) -> {
                String requestBody = req.body();
                Type type = new TypeToken<HashMap<String, String>>() {
                }.getType();
                System.out.println(requestBody);
                HashMap<String, String> plantData = new Gson().fromJson(requestBody, type);
                String plant_name = plantData.get("name");
                String plant_type = plantData.get("type");
                Plant plant = new Plant(plant_name, plant_type, new Date());

                PlantTrigger trigger = new PlantTrigger(plant_name, false, (float) 25, (float) 80, (float) 600, (float) 300);
                String db_trigger_key = "trigger_" + plant_name;
                rh.saveTrigger(db_trigger_key, trigger.toJson());

                if (rh.getPlant(plant_name) != null) {
                    res.status(409);
                    return new Gson().toJson("Plant " + plant_name + " already exists");
                }
                res.status(201);
                return rh.savePlant(plant);
            });

            get("/all", (req, res) -> rh.getAllPlants());

            get("/names", (req, res) -> rh.getAllPlantsNames());

            get("/info", (req, res) -> rh.getAllPlantsInfo());

            put("/update", (req, res) -> {
                String requestBody = req.body();
                Type type = new TypeToken<HashMap<String, String>>() {
                }.getType();
                HashMap<String, String> plantData = new Gson().fromJson(requestBody, type);
                String plant_name = plantData.get("name");
                Plant plant = rh.getPlant(plant_name);
                if (plant == null) {
                    res.status(404);
                    return new Gson().toJson("Plant " + plant_name + " not found");
                }
                SensorData data = new SensorData(
                        parseFloat(plantData.get("temperature")),
                        parseFloat(plantData.get("humidity")),
                        parseFloat(plantData.get("light")),
                        parseFloat(plantData.get("moisture")));
                plant.addData(data);
                rh.savePlant(plant);
                res.status(200);
                return new Gson().toJson("Plant " + plant_name + " updated");
            });

            get("/:name/timespan", (req, res) -> {
                Plant plant = rh.getPlant(req.params(":name"));
                if (plant == null) {
                    res.status(404);
                    return new Gson().toJson("Plant " + req.params(":name") + " not found");
                }
                res.status(200);
                return plant.getData(new Date(Long.parseLong(req.queryParams("from"))), new Date(Long.parseLong(req.queryParams("to"))));
            });

            get("/:name/info", (req, res) -> {
                Plant plant = rh.getPlant(req.params(":name"));
                if (plant == null) {
                    res.status(404);
                    return new Gson().toJson("Plant " + req.params(":name") + " not found");
                }
                res.status(200);
                return plant.getInfo();
            });

            get("/:name", (req, res) -> {
                Plant plant = rh.getPlant(req.params(":name"));
                if (plant == null) {
                    res.status(404);
                    return new Gson().toJson("Plant " + req.params(":name") + " not found");
                }
                res.status(200);
                return plant.toJson();
            });

            delete("/:name", (req, res) -> {
                rh.deletePlant(req.params(":name"));
                res.status(200);
                return new Gson().toJson("Plant " + req.params(":name") + " deleted");
            });
        });
    }
}
