package fun.rubicon.core.webpanel;

/**
 * @author Yannick Seeger / ForYaSee
 */
public enum WebpanelData {

    BASE_URL("/"),
    MESSAGE_COUNT(BASE_URL.getUrl() + ""),

    //Guild
    MEMBER_JOINED(BASE_URL.getUrl() + ""),
    MEMBER_LEFT(BASE_URL.getUrl() + ""),

    //Updates;
    MEMBER_COUNT_UPDATE(BASE_URL.getUrl() + "");

    private String url;

    WebpanelData(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
