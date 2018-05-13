package fun.rubicon.features.portal;

import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.features.portal.impl.PortalInviteImpl;
import fun.rubicon.rethink.Rethink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author ForYaSee / Yannick Seeger
 */
public final class PortalInviteManager {

    private final Rethink rethink;
    private final Table table;

    public PortalInviteManager() {
        rethink = RubiconBot.getRethink();
        table = rethink.db.table("portal_invites");
    }

    public List<PortalInvite> getIncomingInvites(String guildId) {
        Cursor cursor = table.filter(rethink.rethinkDB.hashMap("receiver", guildId)).run(rethink.getConnection());
        List<HashMap<String, String>> res = cursor.toList();
        ArrayList<PortalInvite> invites = new ArrayList<>();
        for (HashMap<String, String> entry : res) {
            invites.add(new PortalInviteImpl(entry.get("sender"), entry.get("receiver")));
        }
        return invites;
    }

    public List<PortalInvite> getOutcomingInvites(String guildId) {
        Cursor cursor = table.filter(rethink.rethinkDB.hashMap("senders", guildId)).run(rethink.getConnection());
        List<HashMap<String, String>> res = cursor.toList();
        ArrayList<PortalInvite> invites = new ArrayList<>();
        for (HashMap<String, String> entry : res) {
            invites.add(new PortalInviteImpl(entry.get("sender"), entry.get("receiver")));
        }
        return invites;
    }

    public boolean sendInvite(String sender, String receiver) {
        List<PortalInvite> invites = getOutcomingInvites(sender);
        for (PortalInvite invite : invites) {
            if (invite.getReceiver().equals(receiver))
                return false;
        }
        rethink.db.table("portal_invites").insert(rethink.rethinkDB.array(rethink.rethinkDB.hashMap("sender", sender).with("receiver", receiver))).run(rethink.getConnection());
        return true;
    }
}
