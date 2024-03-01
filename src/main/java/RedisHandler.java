import plant.Plant;
import plant.PlantTrigger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

public class RedisHandler {
    private final String REDIS_HOST;
    private final int REDIS_PORT;

    public RedisHandler(String host, int port) {
        this.REDIS_HOST = host;
        this.REDIS_PORT = port;
    }

    public String savePlant(Plant plant) {
        try (JedisPool pool = new JedisPool(REDIS_HOST, REDIS_PORT)) {
            try (Jedis jedis = pool.getResource()) {
                jedis.set(plant.getName(), plant.toJson());
                return plant.toJson();
            }
        }
    }

    public Plant getPlant(String name) {
        try (JedisPool pool = new JedisPool(REDIS_HOST, REDIS_PORT)) {
            try (Jedis jedis = pool.getResource()) {
                String json = jedis.get(name);
                if (json == null) {
                    return null;
                }
                return new Plant(json);
            }
        }
    }

    public String getAllPlants() {
        try (JedisPool pool = new JedisPool(REDIS_HOST, REDIS_PORT)) {
            try (Jedis jedis = pool.getResource()) {
                Set<String> keys = jedis.keys("*");
                StringBuilder json = new StringBuilder("[");
                for (String key : keys) {
                    if (!key.startsWith("trigger_")) {
                        json.append(new Plant(jedis.get(key)).toJson()).append(",");
                    }
                }
                json = new StringBuilder(json.substring(0, json.length() - 1));
                json.append("]");
                if (json.length() == 1) {
                    return "[]";
                }
                return json.toString();
            }
        }
    }

    public String getAllPlantsInfo() {
        try (JedisPool pool = new JedisPool(REDIS_HOST, REDIS_PORT)) {
            try (Jedis jedis = pool.getResource()) {
                Set<String> keys = jedis.keys("*");
                StringBuilder json = new StringBuilder("[");
                for (String key : keys) {
                    if (!key.startsWith("trigger_")) {
                        json.append(new Plant(jedis.get(key)).toJson()).append(",");
                    }
                }
                json = new StringBuilder(json.substring(0, json.length() - 1));
                json.append("]");
                if (json.length() == 1) {
                    return "[]";
                }
                return json.toString();
            }
        }
    }

    public void deletePlant(String name) {
        try (JedisPool pool = new JedisPool(REDIS_HOST, REDIS_PORT)) {
            try (Jedis jedis = pool.getResource()) {
                jedis.del(name);
            }
        }
    }

    public Object getAllPlantsNames() {
        try (JedisPool pool = new JedisPool(REDIS_HOST, REDIS_PORT)) {
            try (Jedis jedis = pool.getResource()) {
                Set<String> keys = jedis.keys("*");
                StringBuilder json = new StringBuilder("[");
                for (String key : keys) {
                    if (!key.startsWith("trigger_")) {
                        json.append(new Plant(jedis.get(key)).getNames()).append(",");
                    }
                }
                json = new StringBuilder(json.substring(0, json.length() - 1));
                json.append("]");
                if (json.length() == 1) {
                    return "[]";
                }
                return json.toString();
            }
        }
    }

    public boolean ping() {
        try (JedisPool pool = new JedisPool(REDIS_HOST, REDIS_PORT)) {
            try (Jedis jedis = pool.getResource()) {
                return jedis.ping().equals("PONG");
            }
        }
    }

    public PlantTrigger getTrigger(String dbTriggerKey) {
        try (JedisPool pool = new JedisPool(REDIS_HOST, REDIS_PORT)) {
            try (Jedis jedis = pool.getResource()) {
                String json = jedis.get(dbTriggerKey);
                if (json == null) {
                    return null;
                }
                return new PlantTrigger(json);
            }
        }
    }

    public void saveTrigger(String dbTriggerKey, String trigger_json) {
        try (JedisPool pool = new JedisPool(REDIS_HOST, REDIS_PORT)) {
            try (Jedis jedis = pool.getResource()) {
                jedis.set(dbTriggerKey, trigger_json);
            }
        }
    }
}
