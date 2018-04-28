package fun.rubicon.core.rpg.items;

import fun.rubicon.core.rpg.ItemRareness;
import fun.rubicon.core.rpg.ItemType;
import fun.rubicon.core.rpg.RPGItem;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class ArmorItem extends RPGItem {

    private final double armorPoints;
    private final double health;

    public ArmorItem(int id, String displayName, ItemRareness itemRareness, int maxStackCount, double armorPoints, double health) {
        super(id, displayName, ItemType.ARMOR, itemRareness, maxStackCount);
        this.armorPoints = armorPoints;
        this.health = health;
    }

    public double getArmorPoints() {
        return armorPoints;
    }

    public double getHealth() {
        return health;
    }
}
