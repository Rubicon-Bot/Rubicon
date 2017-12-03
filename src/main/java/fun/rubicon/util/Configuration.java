package fun.rubicon.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
// import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.stream.Collectors;

public class Configuration {
    /**
     * @author Schlaubi
     * @version
     */

    private static File file;
    private static JsonObject json;
    public static JsonParser jsonParser;

    public Configuration(final File file) {

        this.file = file;
        String cont = null;
        this.jsonParser = new JsonParser();

        try {
            if (file.exists()) {
                cont = new BufferedReader(new FileReader(this.file)).lines().collect(Collectors.joining("\n"));
                //cont = IOUtils.toString(new BufferedInputStream(new FileInputStream(this.file)), "UTF-8");
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
        if (this.json.has(key)) {
            this.json.remove(key);
        }
        if (val != null) {
            this.json.addProperty(key, val);
        }
        return this.save();
    }

    /**
     * @param key
     * @param val
     * @description Sets tha value of a key in config
     */
    public Configuration set(final String key, final int val) {
        if (this.json.has(key)) {
            this.json.remove(key);
        }
        this.json.addProperty(key, val);
        return this.save();
    }

    /**
     * @param key
     * @description Removes key from config
     */
    private Configuration unset(final String key) {
        if (this.json.has(key)) {
            this.json.remove(key);
        }
        return this.save();
    }

    /**
     * @description Saves the config
     */
    private Configuration save() {
        try {
            if (json.entrySet().size() == 0) {
                if (this.file.exists()) {
                    this.file.delete();
                }
            } else {
                if (!this.file.exists()) {
                    this.file.createNewFile();
                }

                BufferedWriter br = new BufferedWriter(new FileWriter(this.file));
                br.write(json.toString());
                br.close();
                //IOUtils.write(json.toString(), new FileOutputStream(this.file), "UTF-8");
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
            return this.json.get(key).getAsString();
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
        if (this.json.has(key)) {
            return this.json.get(key).getAsInt();
        }
        return 0;
    }

    /**
     * @param key
     * @return If key exists
     */
    public boolean has(final String key) {
        try {
            return this.json.has(key);
        } catch (NullPointerException ex) {
            return false;
        }
    }


}