package fun.rubicon.core.rpg.items;

import fun.rubicon.core.rpg.ItemRareness;
import fun.rubicon.core.rpg.ItemType;
import fun.rubicon.core.rpg.RPGItem;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class EmptyItem extends RPGItem {

    public EmptyItem(int id, String displayName, ItemRareness itemRareness, int maxStackCount) {
        super(id, displayName, ItemType.EMPTY, itemRareness, maxStackCount);
    }
}
