package de.foryasee.httprequest;

import java.io.InputStream;

public class RequestResponse {

    private int responseCode;
    private String response;
    private String fullURL;
    private InputStream inputStream;

    public RequestResponse(int responseCode, String response, InputStream inputStream, String fullURL) {
        this.responseCode = responseCode;
        this.response = response;
        this.fullURL = fullURL;
        this.inputStream = inputStream;
    }

    public String getResponse() {
        return response;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getFullURL() {
        return fullURL;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
