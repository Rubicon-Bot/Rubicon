package fun.rubicon.core.entities;


import net.dv8tion.jda.core.entities.Member;

import java.util.Date;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class RubiconWarn {

    private String id;
    private Member member;
    private String reason;
    private Member moderator;
    private Date issueTime;

    public RubiconWarn(String id, Member member, String reason, Member moderator, Date issueTime) {
        this.id = id;
        this.member = member;
        this.reason = reason;
        this.moderator = moderator;
        this.issueTime = issueTime;
    }

    public String getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public String getReason() {
        return reason;
    }

    public Member getModerator() {
        return moderator;
    }

    public Date getIssueTime() {
        return issueTime;
    }
}
