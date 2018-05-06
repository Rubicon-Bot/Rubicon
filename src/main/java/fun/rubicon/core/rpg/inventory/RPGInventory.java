package fun.rubicon.core.rpg.inventory;

import com.rethinkdb.gen.ast.Filter;
import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.core.rpg.RPGItem;
import fun.rubicon.rethink.Rethink;
import fun.rubicon.rethink.RethinkHelper;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class RPGInventory extends RethinkHelper {

    private final User user;
    private final Rethink rethink;
    private final Table table;
    private final Filter dbInv;

    public RPGInventory(User user) {
        this.user = user;
        rethink = RubiconBot.getRethink();
        table = rethink.db.table("rpg_inventories");
        dbInv = table.filter(rethink.rethinkDB.hashMap("userId", user.getId()));

        createInventoryIfNotExist();
    }

    public int getSlotCount() {
        return Math.toIntExact(getLong(dbInv.run(rethink.getConnection()), "slotcount"));
    }

    public void addItem(RPGInventoryItem item) {
        if (!hasFreeSpace())
            return;
        List<RPGInventoryItem> items = getItems();
        if (hasItem(item.getRpgItem(), 1, items)) {
            RPGInventoryItem invItem = getInventoryItemByItem(items, item.getRpgItem());
            invItem.add(item.getAmount());
            items = replaceRPGItemFromList(items, invItem);
        } else {
            items.add(item);
        }
        dbInv.update(rethink.rethinkDB.hashMap("slots", convertItemsListToStringList(items))).run(rethink.getConnection());
    }

    public void removeItem(RPGInventoryItem item) {
        List<RPGInventoryItem> items = getItems();
        if (!hasItem(item.getRpgItem(), 1, items)) {
            return;
        }
        RPGInventoryItem invItem = getInventoryItemByItem(items, item.getRpgItem());
        invItem.remove(item.getAmount());
        items = replaceRPGItemFromList(items, invItem);
        dbInv.update(rethink.rethinkDB.hashMap("slots", convertItemsListToStringList(items))).run(rethink.getConnection());
    }

    private List<RPGInventoryItem> replaceRPGItemFromList(List<RPGInventoryItem> items, RPGInventoryItem newItem) {
        List<RPGInventoryItem> result = new ArrayList<>();
        if (newItem.getAmount() > 0)
            result.add(newItem);
        for (RPGInventoryItem item : items) {
            if (item.getRpgItem() == newItem.getRpgItem())
                continue;
            result.add(item);
        }
        return result;
    }

    private RPGInventoryItem getInventoryItemByItem(List<RPGInventoryItem> items, RPGItem item) {
        for (RPGInventoryItem invItem : items) {
            if (invItem.getRpgItem() != item)
                continue;
            return invItem;
        }
        return null;
    }

    public boolean hasItem(RPGItem item, int count) {
        return hasItem(item, count, getItems());
    }

    public boolean hasItem(RPGItem item, int count, List<RPGInventoryItem> items) {
        for (RPGInventoryItem invItem : items) {
            if (invItem.getRpgItem() != item)
                continue;
            if (invItem.getAmount() >= count)
                return true;
            else
                return false;
        }
        return false;
    }

    public boolean hasItem(RPGItem item) {
        return hasItem(item, 1);
    }

    public int getFreeSpace() {
        return getSlotCount() - getItems().size();
    }

    public boolean hasFreeSpace() {
        return getSlotCount() > getItems().size();
    }

    public List<RPGInventoryItem> getItems() {
        List<RPGInventoryItem> items = new ArrayList<>();
        Cursor cursor = dbInv.run(rethink.getConnection());
        for (Object obj : cursor) {
            Map map = (Map) obj;
            List<?> list = (List<?>) map.get("slots");
            for (Object e : list) {
                items.add(new RPGInventoryItem(e.toString()));
            }
        }
        return items;
    }

    public RPGInventoryItem getHelmet() {
        return new RPGInventoryItem(getString(dbInv.run(rethink.getConnection()), "a_helmet"));
    }

    public RPGInventoryItem getChest() {
        return new RPGInventoryItem(getString(dbInv.run(rethink.getConnection()), "a_chest"));
    }

    public RPGInventoryItem getPants() {
        return new RPGInventoryItem(getString(dbInv.run(rethink.getConnection()), "a_pants"));
    }

    public RPGInventoryItem getShoes() {
        return new RPGInventoryItem(getString(dbInv.run(rethink.getConnection()), "a_shoes"));
    }

    public RPGInventoryItem getWeapon() {
        return new RPGInventoryItem(getString(dbInv.run(rethink.getConnection()), "weapon"));
    }

    public RPGInventoryItem getTool() {
        return new RPGInventoryItem(getString(dbInv.run(rethink.getConnection()), "tool"));
    }

    public boolean exist() {
        return exist(dbInv.run(rethink.getConnection()));
    }

    public void createInventoryIfNotExist() {
        if (exist())
            return;
        table.insert(
                rethink.rethinkDB.array(rethink.rethinkDB.hashMap("userId", user.getId())
                        .with("slotcount", 5)
                        .with("slots", rethink.rethinkDB.array())
                        .with("a_helmet", new RPGInventoryItem(0, 0).toString())
                        .with("a_chest", new RPGInventoryItem(0, 0).toString())
                        .with("a_pants", new RPGInventoryItem(0, 0).toString())
                        .with("a_shoes", new RPGInventoryItem(0, 0).toString())
                        .with("weapon", new RPGInventoryItem(0, 0).toString())
                        .with("tool", new RPGInventoryItem(0, 0).toString())
                )).run(rethink.getConnection());
    }

    public static RPGInventory fromUser(User user) {
        return new RPGInventory(user);
    }

    private List<String> convertItemsListToStringList(List<RPGInventoryItem> items) {
        List<String> result = new ArrayList<>();
        for (RPGInventoryItem item : items)
            result.add(item.toString());
        return result;
    }
}
