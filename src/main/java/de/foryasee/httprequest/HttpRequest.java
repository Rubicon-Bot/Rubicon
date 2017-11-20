package de.foryasee.httprequest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpRequests
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Yannick Seeger 2017
 * @license MIT License
 * @package de.foryasee.httprequest
 */

public class HttpRequest {

    private String urlP;
    private RequestParameter[] params;
    private String fullURL;
    private HashMap<String, String> header;

    public HttpRequest(String url, RequestParameter... params) {
        this.urlP = url;
        this.params = params;
    }

    public HttpRequest(String url, RequestParameter[] params, HashMap<String, String> header) {
        this.urlP = url;
        this.params = params;
        this.header = header;
    }

    public RequestResult sendGETRequest() throws Exception {
        if (urlP == null)
            throw new Exception("Please specify the URL!");
        fullURL = urlP + createURLParameter();
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
        return new RequestResult(code, responseBuilder.toString(), fullURL, RequestType.GET);
    }

    public RequestResult sendPOSTRequest() throws Exception {
        if (urlP == null)
            throw new Exception("Please specify the URL!");
        fullURL = urlP;
        URL url = new URL(urlP);
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
        return new RequestResult(code, responseBuilder.toString(), fullURL, RequestType.GET);
    }

    private String createURLParameter() throws UnsupportedEncodingException {
        if (params == null) {
            return "";
        }
        String paramsLine = "";

        List<RequestParameter> pList = Arrays.asList(params);
        for (RequestParameter p : pList) {
            if (!paramsLine.equals(""))
                paramsLine += "&";
            paramsLine += URLEncoder.encode(p.getKey(), java.nio.charset.StandardCharsets.UTF_8.toString()) + "=" + URLEncoder.encode(p.getValue(), java.nio.charset.StandardCharsets.UTF_8.toString());
        }
        return "?" + paramsLine;
    }

    private void setHeader(HttpURLConnection conn) {
        if(header != null) {
            for(Map.Entry<String, String> entry : header.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    public enum RequestType {
        POST,
        GET
    }

    public void setHeader(HashMap<String, String> header) {
        this.header = header;
    }

    public void setURL(String url) {
        this.urlP = url;
    }

    public String getURL() {
        return urlP;
    }

    public RequestParameter[] getParameters() {
        return params;
    }

    public HashMap<String, String> getHeader() {
        return header;
    }
}
