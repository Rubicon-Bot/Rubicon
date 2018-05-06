package fun.rubicon.features.portal.impl;

import fun.rubicon.RubiconBot;
import fun.rubicon.features.portal.PortalInvite;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class PortalInviteImpl implements PortalInvite {

    private final String sender;
    private final String receiver;

    public PortalInviteImpl(String sender, String receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public void delete() {
        RubiconBot.getRethink().db.table("portal_invites").filter(RubiconBot.getRethink().rethinkDB.hashMap("sender", sender).with("receiver", receiver)).run(RubiconBot.getRethink().getConnection());
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public String getReceiver() {
        return receiver;
    }
}
