package fun.rubicon.core.rpg;

import fun.rubicon.core.rpg.items.ArmorItem;
import fun.rubicon.core.rpg.items.EmptyItem;
import fun.rubicon.core.rpg.items.WeaponItem;

import java.util.HashMap;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class RPGItemRegistry {

    private HashMap<Integer, RPGItem> items;

    public RPGItemRegistry() {
        items = new HashMap<>();

        initItems();
    }

    private void add(RPGItem item) {
        items.put(item.getId(), item);
    }

    private void initItems() {
        add(new EmptyItem(0, "Empty", ItemRareness.COMMON, 1));

        //Armor
        add(new ArmorItem(1, "Leather Helmet", ItemRareness.COMMON, 1, 0.5, 55));
        add(new ArmorItem(2, "Leather Chest", ItemRareness.COMMON, 1, 1.5, 70));
        add(new ArmorItem(3, "Leather Pants", ItemRareness.COMMON, 1, 1, 80));
        add(new ArmorItem(4, "Leather Shoes", ItemRareness.COMMON, 1, 0.5, 55));

        //Weapons
        add(new WeaponItem(100, "Wooden Sword", ItemRareness.COMMON, 1, 1, 60));

        //Tools
    }

    public RPGItem getItemById(int id) {
        return items.get(id);
    }
}
