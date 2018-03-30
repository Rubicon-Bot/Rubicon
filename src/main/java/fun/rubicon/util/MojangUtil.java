package fun.rubicon.util;


import de.foryasee.httprequest.HttpRequestBuilder;
import de.foryasee.httprequest.RequestResponse;
import de.foryasee.httprequest.RequestType;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Date;
import java.util.HashMap;

public class MojangUtil {

    private JSONParser parser = new JSONParser();


    public String fetchUUID(String playername){
        HttpRequestBuilder request = new HttpRequestBuilder("https://api.mojang.com/users/profiles/minecraft/" + playername, RequestType.GET);
        RequestResponse response = null;
        try {
            response = request.sendRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response.getResponseCode() != 200)
            return null;
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.getResponseMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return json.get("id").toString();
    }


    private String fetchName(String playername){
        HttpRequestBuilder request = new HttpRequestBuilder("https://api.mojang.com/users/profiles/minecraft/" + playername, RequestType.GET);
        RequestResponse response = null;
        try {
            response = request.sendRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response.getResponseCode() != 200)
            return null;
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.getResponseMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return json.get("name").toString();
    }


    public JSONArray fetchStatus(){
        HttpRequestBuilder request = new HttpRequestBuilder("https://status.mojang.com/check", RequestType.GET);
        RequestResponse response = null;
        try {
            response = request.sendRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONArray res = null;
        try{
            res = ((JSONArray) parser.parse(response.getResponseMessage()));
        } catch (ParseException e){
            Logger.error(e);
        }
        return res;
    }



    private String fetchNameHistory(String uuid){
        HttpRequestBuilder request = new HttpRequestBuilder("https://api.mojang.com/user/profiles/" + uuid + "/names", RequestType.GET);
        RequestResponse response = null;
        try {
            response = request.sendRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response.getResponseCode() != 200)
            return null;
        return response.getResponseMessage();
    }

    private String fetchFirstNamme(String uuid) {
        JSONArray data = null;
        try {
            data = (JSONArray) parser.parse(fetchNameHistory(uuid));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ((JSONObject) data.get(0)).get("name").toString();
    }

    public static class MinecraftPlayer {
        public String getUuid() {
            return uuid;
        }

        public String getName() {
            return name;
        }

        public HashMap<String, Date> getNamechanges() {
            return namechanges;
        }

        private String uuid;
        private String name;
        private HashMap<String, Date> namechanges;
        private String firstName;

        private MinecraftPlayer(String uuid, String name, HashMap<String, Date> namechanges, String firstName) {
            this.uuid = uuid;
            this.name = name;
            this.firstName = firstName;
            this.namechanges = namechanges;
        }

        public String getFirstName() {
            return firstName;
        }
    }

    public MinecraftPlayer fromName(String name) {
        HashMap<String, Date> history = new HashMap<>();
        try {
            JSONArray data = (JSONArray) new JSONParser().parse(fetchNameHistory(fetchUUID(name)));
            if (data.size() != 0) {
                data.remove(0);
                data.forEach(d -> {
                    JSONObject object = (JSONObject) d;
                    history.put(object.get("name").toString(), new Date(Long.parseLong(object.get("changedToAt").toString())));
                });
            }

        } catch (ParseException e) {
            Logger.error(e);
        }
        return new MinecraftPlayer(fetchUUID(name), fetchName(name), history, fetchFirstNamme(fetchUUID(name)));
    }

    public MinecraftPlayer fromUUID(String uuid) {
        try {
            JSONArray data = (JSONArray) new JSONParser().parse(fetchNameHistory(uuid));
            String name = ((JSONObject) data.get(data.size() - 1)).get("name").toString();
            return fromName(name);
        } catch (ParseException e) {
            Logger.error(e);
            return null;
        }
    }
}
