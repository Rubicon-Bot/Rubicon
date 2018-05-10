package fun.rubicon.util;

import de.foryasee.httprequest.HttpRequestBuilder;
import de.foryasee.httprequest.RequestResponse;
import de.foryasee.httprequest.RequestType;
import org.json.JSONObject;

import javax.xml.ws.http.HTTPException;

/**
 * @author Michael Rittmeister / Schlaubi
 */

public class BitlyAPI {

    private final String API_BASE_URL = "https://api-ssl.bitly.com/v3";

    private String token;

    public BitlyAPI(String apitoken) {
        this.token = apitoken;
    }

    /**
     * Short a URL with rucb.co
     *
     * @param longurl URL that should be shortn
     * @return The shortened URL
     * @throws IllegalArgumentException if the long URI was invalid.
     * @throws HTTPException            if the bit.ly API returns a response code unlike 200 'OK'.
     * @throws RuntimeException         if the http request threw an unknown error.
     */
    public String shortURL(String longurl) throws RuntimeException {
        String SHORT_API_URL = API_BASE_URL + "/shorten";
        HttpRequestBuilder req = new HttpRequestBuilder(SHORT_API_URL, RequestType.GET)
                .addParameter("access_token", token)
                .addParameter("longurl", longurl)
                .addParameter("format", "json");
        RequestResponse result;
        try {
            result = req.sendRequest();
        } catch (Exception e) {
            throw new RuntimeException("An error ocurred while fetching API response");
        }
        JSONObject response = new JSONObject(result.getResponseMessage());
        if (response.getString("status_txt").equals("INVALID_URI"))
            throw new IllegalArgumentException("The provided URL '" + longurl + "' is not valid");
        if (response.getInt("status_code") == 400)
            throw new HTTPException(response.getInt("status_code"));
        Logger.debug(result.getResponseMessage());
        return response.getJSONObject("data").getString("url");
    }
}
