package fun.rubicon.core.rpg.inventory;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.rpg.RPGItem;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class RPGInventoryItem {

    private final RPGItem rpgItem;
    private final int id;
    private int amount;

    public RPGInventoryItem(int id, int amount) {
        this.id = id;
        this.amount = amount;
        this.rpgItem = RubiconBot.getRPGItemRegistry().getItemById(id);
    }

    public RPGInventoryItem(RPGItem item, int amount) {
        this.rpgItem = item;
        this.id = rpgItem.getId();
        this.amount = amount;
    }

    public RPGInventoryItem(String raw) {
        String[] splitted = raw.split(":");
        id = Integer.parseInt(splitted[0]);
        amount = Integer.parseInt(splitted[1]);
        rpgItem = RubiconBot.getRPGItemRegistry().getItemById(id);
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public RPGItem getRpgItem() {
        return rpgItem;
    }

    public void add(int amount) {
        this.amount += amount;
    }

    public void remove(int amount) {
        this.amount -= amount;
    }

    public boolean moreThan(int amount) {
        return this.amount > amount;
    }

    @Override
    public String toString() {
        return id + ":" + amount;
    }
}
