package fun.rubicon.util;


import de.foryasee.httprequest.HttpRequest;
import de.foryasee.httprequest.RequestResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Date;
import java.util.HashMap;

public class MojangUtil {

    private static String fetchUUID(String playername){
        HttpRequest request = new HttpRequest("https://api.mojang.com/users/profiles/minecraft/" + playername);
        RequestResponse response = null;
        try {
            response = request.sendGETRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(response.getResponseCode() != 200)
            return null;
        JSONObject json = new JSONObject(response.getResponse());
        return json.getString("id");
    }

    private static String fetchName(String playername){
        HttpRequest request = new HttpRequest("https://api.mojang.com/users/profiles/minecraft/" + playername);
        RequestResponse response = null;
        try {
            response = request.sendGETRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(response.getResponseCode() != 200)
            return null;
        JSONObject json = new JSONObject(response.getResponse());
        return json.getString("name");
    }

    private static String fetchNameHistory(String uuid){
        HttpRequest request = new HttpRequest("https://api.mojang.com/user/profiles/" + uuid + "/names");
        RequestResponse response = null;
        try {
            response = request.sendGETRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(response.getResponseCode() != 200)
            return null;
        return response.getResponse();
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

        public String uuid;
        public String name;
        public HashMap<String, Date> namechanges;

        private MinecraftPlayer(String uuid, String name, HashMap<String, Date> namechanges){
            this.uuid = uuid;
            this.name = name;
            this.namechanges = namechanges;
        }
    }

    public static MinecraftPlayer fromName(String name){
        JSONObject jsonObject = new JSONObject(fetchNameHistory(fetchUUID(name)));
        HashMap<String, Date> history = new HashMap<>();
        try {
            JSONArray data = (JSONArray) new JSONParser().parse(fetchNameHistory(fetchUUID(name)));
            if(data.length() != 0) {
                data.remove(0);
                data.forEach(d -> {
                    JSONObject object = (JSONObject)
                });
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new MinecraftPlayer(fetchUUID(name), fetchName(name), history);
    }
}
