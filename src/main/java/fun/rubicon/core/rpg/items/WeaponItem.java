package fun.rubicon.core.rpg.items;

import fun.rubicon.core.rpg.ItemRareness;
import fun.rubicon.core.rpg.ItemType;
import fun.rubicon.core.rpg.RPGItem;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class WeaponItem extends RPGItem {

    private final double damage;
    private final double health;

    public WeaponItem(int id, String displayName, ItemRareness itemRareness, int maxStackCount, double damage, double health) {
        super(id, displayName, ItemType.WEAPON, itemRareness, maxStackCount);

        this.damage = damage;
        this.health = health;
    }

    public double getDamage() {
        return damage;
    }

    public double getHealth() {
        return health;
    }
}
