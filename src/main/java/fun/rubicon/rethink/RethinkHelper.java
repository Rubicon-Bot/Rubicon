package fun.rubicon.rethink;

import com.rethinkdb.net.Cursor;

import java.util.List;
import java.util.Map;

/**
 * @author ForYaSee / Yannick Seeger
 */
public abstract class RethinkHelper {

    protected static String getString(Cursor cursor, String key) {
        Map map = parse(cursor);
        if (map == null) {
            return null;
        }
        Object res = map.get(key);
        return res == null ? null : (String) res;
    }

    public static boolean getBoolean(Cursor cursor, String key) {
        Map map = parse(cursor);
        if (map == null)
            return false;
        Object res = map.get(key);
        return res != null && (boolean) res;
    }

    protected static int getInt(Cursor cursor, String key) {
        Map map = parse(cursor);
        if (map == null)
            return 0;
        Object res = map.get(key);
        return res == null ? 0 : (int) res;
    }

    protected static boolean exist(Cursor cursor) {
        return cursor.toList().size() != 0;
    }

    protected static long getLong(Cursor cursor, String key) {
        Map map = parse(cursor);
        if (map == null)
            return 0;
        Object res = map.get(key);
        return res == null ? 0 : (long) res;
    }

    protected static Map parse(Cursor cursor) {
        List list = cursor.toList();
        if(list.size() == 0)
            return null;
        return (Map) list.get(0);
    }
}
