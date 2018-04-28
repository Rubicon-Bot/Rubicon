package fun.rubicon.core.rpg;

/**
 * @author ForYaSee / Yannick Seeger
 */
public abstract class RPGItem {

    private final int id;
    private final String displayName;
    private final ItemType itemType;
    private final ItemRareness itemRareness;
    private final int maxStackCount;

    public RPGItem(int id, String displayName, ItemType itemType, ItemRareness itemRareness, int maxStackCount) {
        this.id = id;
        this.displayName = displayName;
        this.itemType = itemType;
        this.itemRareness = itemRareness;
        this.maxStackCount = maxStackCount;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public int getMaxStackCount() {
        return maxStackCount;
    }

    public ItemRareness getItemRareness() {
        return itemRareness;
    }

    public boolean isEmpty() {
        return id == 0;
    }
}