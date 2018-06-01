package fun.rubicon.features.portal;

/**
 * @author Yannick Seeger / ForYaSee
 */
public enum PortalMessageType {
    WEBHOOk("wh"),
    EMBED("embed");

    private String key;

    PortalMessageType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
