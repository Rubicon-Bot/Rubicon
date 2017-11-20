package de.foryasee.httprequest;

/**
 * HttpRequests
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Yannick Seeger 2017
 * @license MIT License
 * @package de.foryasee.httprequest
 */

public class RequestResult {

    private int responseCode;
    private String response;
    private HttpRequest.RequestType type;
    private String fullURL;

    public RequestResult(int responseCode, String response, String fullURL, HttpRequest.RequestType type) {
        this.responseCode = responseCode;
        this.response = response;
        this.fullURL = fullURL;
        this.type = type;
    }

    public String getResponse() {
        return response;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public HttpRequest.RequestType getType() {
        return type;
    }

    public void setType(HttpRequest.RequestType type) {
        this.type = type;
    }

    public String getFullURL() {
        return fullURL;
    }

    public void setFullURL(String fullURL) {
        this.fullURL = fullURL;
    }
}
