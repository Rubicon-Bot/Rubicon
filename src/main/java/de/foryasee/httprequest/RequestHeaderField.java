package de.foryasee.httprequest;

public class RequestHeaderField {

    private final String key;
    private final String value;

    public RequestHeaderField(String key, String value) {
        if (key != null || value != null) {
            this.key = key;
            this.value = value;
        } else
            throw new NullPointerException("Key or value can not be null.");
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
