package de.rubicon.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

public class Configuration {

    private static File file;
    private static JsonObject json;
    public static JsonParser jsonParser;

    public Configuration(final File file){

        this.file = file;
        String cont = null;
        this.jsonParser = new JsonParser();

        try {
            if(file.exists()){
                cont = IOUtils.toString(new BufferedInputStream(new FileInputStream(this.file)), "UTF-8");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        if(cont == null || cont.equals("")){
            cont = "{}";
        }
        json = jsonParser.parse(cont).getAsJsonObject();

    }

    public Configuration set(final String key, final String val) {
        if (this.json.has(key)) {
            this.json.remove(key);
        }
        if (val != null) {
            this.json.addProperty(key, val);
        }
        return this.save();
    }

    public Configuration set(final String key, final int val)
    {
        if(this.json.has(key))
        {
            this.json.remove(key);
        }
        this.json.addProperty(key, val);
        return this.save();
    }

    private Configuration unset(final String key){
        if(this.json.has(key)){
            this.json.remove(key);
        }
        return this.save();
    }
    private Configuration save()
    {
        try
        {
            if(json.entrySet().size() == 0)
            {
                if(this.file.exists())
                {
                    this.file.delete();
                }
            }
            else
            {
                if(!this.file.exists())
                {
                    this.file.createNewFile();
                }
                IOUtils.write(json.toString(), new FileOutputStream(this.file), "UTF-8");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return this;
    }


    public  String getString(final String key){
        try{
            return this.json.get(key).getAsString();
        } catch (Exception fuckyou){
            fuckyou.printStackTrace();
        }
        return "";
    }

    public  int getInt(final String key){
        if(this.json.has(key)){
            return this.json.get(key).getAsInt();
        }
        return 0;
    }


    public boolean has(final String key){
        return this.json.has(key);
    }



}
