package fun.rubicon.core.rpg;

/**
 * @author ForYaSee / Yannick Seeger
 */
public enum ItemRareness {

    COMMON("Common"),
    RARE("Rare"),
    EPIC("Epic"),
    LEGENDARY("Legendary");

    private String displayName;

    ItemRareness(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
