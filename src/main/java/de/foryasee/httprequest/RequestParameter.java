package de.foryasee.httprequest;


import org.json.simple.JSONArray;

public class RequestParameter {

    private String key;
    private String value = null;
    private int intValue = 0;


    public RequestParameter(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public RequestParameter(String key, int value) {
        this.key = key;
        this.intValue = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        if (value == null)
            return "int;" + intValue;
        return value;
    }
}
