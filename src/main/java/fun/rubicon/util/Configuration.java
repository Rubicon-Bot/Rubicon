package fun.rubicon.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// import org.apache.commons.io.IOUtils;

public class Configuration {
    /**
     * @author Schlaubi
     * @version
     */

    private File file;
    private JsonObject json;
    public JsonParser jsonParser;

    public Configuration(final File file) {

        this.file = file;
        String cont = null;
        jsonParser = new JsonParser();

        try {
            if (file.exists()) {
                cont = new BufferedReader(new FileReader(file)).lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cont == null || cont.equals("")) {
            cont = "{}";
        }
        json = jsonParser.parse(cont).getAsJsonObject();
    }

    /**
     * @param key
     * @param val
     * @description Sets tha value of a key in config
     */
    public Configuration set(final String key, final String val) {
        if (json.has(key)) {
            json.remove(key);
        }
        if (val != null) {
            json.addProperty(key, val);
        }
        return this.save();
    }

    /**
     * @param key
     * @param val
     * @description Sets tha value of a key in config
     */
    public Configuration set(final String key, final int val) {
        if (json.has(key)) {
            json.remove(key);
        }
        this.json.addProperty(key, val);
        return this.save();
    }

    /**
     * @param key
     * @description Removes key from config
     */
    public Configuration unset(final String key) {
        if (json.has(key))
            json.remove(key);

        return this.save();
    }

    /**
     * @description Saves the config
     */
    private Configuration save() {
        try {
            if (json.entrySet().size() == 0) {
                if (file.exists()) {
                    file.delete();
                }
            } else {
                if (!file.exists()) {
                    file.createNewFile();
                }

                BufferedWriter br = new BufferedWriter(new FileWriter(file));
                br.write(json.toString());
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * @param key
     * @return Value of key in config as string
     */
    public String getString(final String key) {
        try {
            return json.get(key).getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param key
     * @return Value of key in config as integer
     */
    public int getInt(final String key) {
        if (json.has(key)) {
            return json.get(key).getAsInt();
        }
        return 0;
    }

    /**
     * @param key
     * @return If key exists
     */
    public boolean has(final String key) {
        try {
            return json.has(key);
        } catch (NullPointerException ex) {
            return false;
        }
    }

    public List<String> keySet() {
        List<String> keys = new ArrayList<>();
        Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            keys.add(entry.getKey());
        }
        return keys;
    }

    public List<String> values() {
        List<String> values = new ArrayList<>();
        Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            values.add(entry.getValue().getAsString());
        }
        return values;
    }


}