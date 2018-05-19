package fun.rubicon.io;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class Config {

    private final File file;
    private final JSONObject jsonObject;

    public Config(String file) {
        this(new File(file));
    }

    public Config(File file) {
        this.file = file;

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            jsonObject = new JSONObject();
            writeConfig();
        } else
            jsonObject = new JSONObject(readConfigContent());
    }

    public Object get(String key) {
        return jsonObject.get(key);
    }

    public String getString(String key) {
        return (String) jsonObject.get(key);
    }

    public Object getElementFromArray(String key, int index) {
        return jsonObject.getJSONArray(key).length() >= (index - 1) ? jsonObject.getJSONArray(key).get(index) : null;
    }

    public int getInt(String key) {
        return (Integer) jsonObject.get(key);
    }

    public double getDouble(String key) {
        return (Double) jsonObject.get(key);
    }

    public long getLong(String key) {
        return (Long) jsonObject.get(key);
    }

    public void set(String key, int value) {
        jsonObject.put(key, value);
        writeConfig();
    }

    public void set(String key, double value) {
        jsonObject.put(key, value);
        writeConfig();
    }

    public void set(String key, long value) {
        jsonObject.put(key, value);
        writeConfig();
    }

    public void set(String key, Object object) {
        jsonObject.put(key, object.toString());
        writeConfig();
    }

    public boolean has(String key) {
        return jsonObject.has(key);
    }

    public void setDefault(String key, String value) {
        if (has(key))
            return;
        jsonObject.put(key, value);
        writeConfig();
    }

    public void setDefault(String key, int value) {
        if (has(key))
            return;
        jsonObject.put(key, value);
        writeConfig();
    }

    public void setDefault(String key, double value) {
        if (has(key))
            return;
        jsonObject.put(key, value);
        writeConfig();
    }

    public void setDefault(String key, long value) {
        if (has(key))
            return;
        jsonObject.put(key, value);
        writeConfig();
    }

    public void setDefault(String key, List<?> value) {
        if(has(key))
            return;
        jsonObject.put(key, value);
        writeConfig();
    }

    private String readConfigContent() {
        try {
            Scanner scanner = new Scanner(file);
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine())
                stringBuilder.append(scanner.nextLine());
            scanner.close();
            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeConfig() {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}