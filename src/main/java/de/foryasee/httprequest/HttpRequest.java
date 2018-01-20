package de.foryasee.httprequest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class HttpRequest {

    private String requestURL = "http://localhost";
    private List<RequestParameter> params;
    private String fullURL;
    private RequestHeader requestHeader;

    /**
     * @param url Website URL
     */
    public HttpRequest(String url) {
        this.requestURL = url;
        this.params = new ArrayList<>();
    }

    public HttpRequest() {
        this.params = new ArrayList<>();
    }

    /**
     * Executes a GET request query and returns the response
     *
     * @return RequestResponse
     * @throws Exception Exception
     */
    public RequestResponse sendGETRequest() throws Exception {
        fullURL = requestURL + "?" + createURLParameter();
        URL url = new URL(fullURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        setHeader(conn);

        int code = conn.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder responseBuilder = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null)
            responseBuilder.append(line);
        in.close();
        return new RequestResponse(code, responseBuilder.toString(), conn.getInputStream(), fullURL);
    }

    /**
     * Executes a POST request query and returns the response
     *
     * @return RequestResponse
     * @throws Exception Exception
     */
    public RequestResponse sendPOSTRequest() throws Exception {
        if (requestURL == null)
            throw new Exception("Please specify the URL!");
        fullURL = requestURL;
        URL url = new URL(requestURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        setHeader(conn);

        conn.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeBytes(createURLParameter());
        out.flush();
        out.close();

        int code = conn.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder responseBuilder = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null)
            responseBuilder.append(line);
        in.close();
        return new RequestResponse(code, responseBuilder.toString(), conn.getInputStream(), fullURL);
    }

    private String createURLParameter() throws UnsupportedEncodingException {
        if (params.size() == 0) {
            return "";
        }
        String paramsLine = "";

        for (RequestParameter p : params) {
            if (!paramsLine.equals(""))
                paramsLine += "&";
            paramsLine += URLEncoder.encode(p.getKey(), java.nio.charset.StandardCharsets.UTF_8.toString()) + "=" + URLEncoder.encode((p.getValue().startsWith("int;") ? p.getValue().replaceFirst("int;", "") : p.getValue()), java.nio.charset.StandardCharsets.UTF_8.toString());
        }
        return paramsLine;
    }

    private void setHeader(HttpURLConnection conn) {
        if (requestHeader != null) {
            for (RequestHeaderField field : requestHeader.getRequestFields()) {
                conn.setRequestProperty(field.getKey(), field.getValue());
            }
        }
    }

    /**
     * Add an request parameter to the http request
     *
     * @param requestParameter Request Parameter
     */
    public void addParameter(RequestParameter requestParameter) {
        params.add(requestParameter);
    }

    /**
     * Same as {@link #addParameter(RequestParameter)}
     *
     * @param key   Parameter Key
     * @param value Parameter Value
     */
    public void addParameter(String key, String value) {
        params.add(new RequestParameter(key, value));
    }

    /**
     * Same as {@link #addParameter(RequestParameter)}
     *
     * @param key   Parameter Key
     * @param value Parameter Value
     */
    public void addParameter(String key, int value) {
        params.add(new RequestParameter(key, value));
    }

    //Getter & Setter

    /**
     * @return String
     */
    public String getRequestURL() {
        return requestURL;
    }

    /**
     * @return String
     */
    public String getFullURL() {
        return fullURL;
    }


    /**
     * @param requestURL the urls where the request should be executed
     */
    public void setURL(String requestURL) {
        this.requestURL = requestURL;
    }

    /**
     * @return List
     */
    public List<RequestParameter> getRequestParameters() {
        return params;
    }

    /**
     * @return RequestHeader
     */
    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    /**
     * Sets the request header
     *
     * @param requestHeader The request header
     */
    public void setRequestHeader(RequestHeader requestHeader) {
        this.requestHeader = requestHeader;
    }
}