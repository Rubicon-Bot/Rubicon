package fun.rubicon.core.entities.cache;

import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.core.Cache;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.io.deprecated_rethink.Rethink;
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.List;

/**
 * @author ForYaSee / Yannick Seeger
 */
@Deprecated
public class RubiconUserCache extends Cache {

    public Rethink rethink;
    private Table table;

    public RubiconUserCache() {
        rethink = RubiconBot.getRethink();
        table = rethink.db.table("users");
    }

    public RubiconUser getUser(User user) {
        if (contains(user.getId()))
            return (RubiconUser) get(user.getId());
        return (RubiconUser) update(user.getId(), retrieveUser(user));
    }

    private RubiconUser retrieveUser(User user) {
        Cursor cursor = table.filter(rethink.rethinkDB.hashMap("userId", user.getId())).run(rethink.getConnection());
        List<?> list = cursor.toList();
        if (list.size() == 0)
            return null;
        HashMap<String, ?> map = (HashMap<String, ?>) list.get(0);
        return new RubiconUser(user, map);
    }
}
