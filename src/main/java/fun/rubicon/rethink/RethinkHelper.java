package fun.rubicon.rethink;

import com.rethinkdb.net.Cursor;

import java.util.Map;

/**
 * @author ForYaSee / Yannick Seeger
 */
public abstract class RethinkHelper {

    protected String getString(Cursor cursor, String key) {
        Map map = parse(cursor);
        if (map == null) {
            return "";
        }
        Object res = map.get(key);
        return res == null ? "" : (String) res;
    }

    protected int getInt(Cursor cursor, String key) {
        Map map = parse(cursor);
        if (map == null)
            return 0;
        Object res = map.get(key);
        return res == null ? 0 : (int) res;
    }

    protected boolean exist(Cursor cursor) {
        return cursor.toList().size() != 0;
    }

    protected long getLong(Cursor cursor, String key) {
        Map map = parse(cursor);
        if (map == null)
            return 0;
        Object res = map.get(key);
        return res == null ? 0 : (long) res;
    }

    private Map parse(Cursor cursor) {
        try {
            return (Map) cursor.next();
        } catch (IndexOutOfBoundsException ignored) {
            return null;
        }
    }
}
