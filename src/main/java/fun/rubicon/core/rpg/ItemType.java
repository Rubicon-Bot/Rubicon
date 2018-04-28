package fun.rubicon.core.rpg;

public enum ItemType {
    EMPTY("empty"),
    TOOL("Tool"),
    WEAPON("Weapon"),
    ARMOR("Armor");

    private String displayName;

    ItemType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
