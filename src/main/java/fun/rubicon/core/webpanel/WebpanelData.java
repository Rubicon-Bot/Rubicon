package fun.rubicon.core.webpanel;

/**
 * @author Yannick Seeger / ForYaSee
 */
public enum WebpanelData {

    MESSAGE_COUNT("message_count"),

    //Guild
    MEMBER_JOIN("member_join"),
    MEMBER_LEAVE("member_leave"),

    //Updates;
    MEMBER_COUNT_UPDATE("guild_member_update");

    private String key;
    public static final String BASE_URL = "http://api.lucsoft.de/update/rubiconserverdata.php";

    WebpanelData(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
