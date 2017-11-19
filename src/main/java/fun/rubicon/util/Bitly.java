package fun.rubicon.util;

import de.foryasee.httprequest.HttpRequest;
import de.foryasee.httprequest.RequestResult;
import org.json.JSONObject;

public class Bitly {


    /**
     * Rubicon Discord bot
     *
     * @author Schlaubi
     * @copyright Rubicon Dev Team 2017
     * @license MIT License <http://rubicon.fun/license>
     * @package fun.rubicon.util
     */
    private String BITLY_TOKEN;
    private String BITLY_API;

    public Bitly(String token){
        this.BITLY_TOKEN = token;
        this.BITLY_API = "https://api-ssl.bitly.com/v3/shorten?access_token=" + this.BITLY_TOKEN +"&longUrl=LONG_URL";
    }

    public String generateShortLink(String longurl){
        String URL = this.BITLY_API.replace("LONG_URL", longurl);
        try {
            HttpRequest request = new HttpRequest(URL);
            String result = request.sendGETRequest().getResponse();
            JSONObject json = new JSONObject(result);
            if(!json.getString("status_txt").equals("OK"))
                throw new Exception();
            return json.getJSONObject("data").getString("url");
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
