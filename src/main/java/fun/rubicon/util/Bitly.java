/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import de.foryasee.httprequest.HttpRequest;
import de.foryasee.httprequest.RequestParameter;
import de.foryasee.httprequest.RequestResult;
import org.json.JSONObject;

import javax.xml.ws.http.HTTPException;

/**
 * Bit.ly link shortening API.
 *
 * @author DRSchlaubi, tr808axm
 */
public class Bitly {
    private static final String API_URL = "https://api-ssl.bitly.com/v3/shorten";

    /**
     * Shortens a URL with bit.ly.
     * @param longURL the URL to shorten.
     * @return the shortened URL.
     * @throws IllegalArgumentException if the long URI was invalid.
     * @throws HTTPException if the bit.ly API returns a response code unlike 200 'OK'.
     * @throws RuntimeException if the http request threw an unknown error.
     */
    public static String shorten(String longURL) {
        HttpRequest request = new HttpRequest(API_URL, new RequestParameter("access_token", Info.BITLY_TOKEN),
                new RequestParameter("longUrl", longURL), new RequestParameter("format", "json"));
        RequestResult result;
        try {
            result = request.sendGETRequest();
        } catch (Exception e) {
            // catch 'anonymous' exceptions
            throw new RuntimeException("An unknown exception occurred while fetching a bit.ly http request", e);
        }
        JSONObject response = new JSONObject(result.getResponse());
        // check if uri was valid
        if (response.getString("status_txt").equals("INVALID_URI"))
            throw new IllegalArgumentException("'" + longURL + "' is not a valid URL.");
            // ensure 'OK' status response
        else if (response.getInt("status_code") == 400)
            throw new HTTPException(response.getInt("status_code"));
        
        // return shortened url
        return response.getJSONObject("data").getString("url");
    }
}
