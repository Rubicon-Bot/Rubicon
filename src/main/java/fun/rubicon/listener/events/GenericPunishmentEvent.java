package fun.rubicon.listener.events;

import fun.rubicon.core.entities.PunishmentType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class GenericPunishmentEvent extends GenericRubiconGuildEvent{

    private Member member;
    private PunishmentType type;
    private Member moderator;

    public GenericPunishmentEvent(JDA api, long responseNumber, Guild guild, Member member, Member moderator, PunishmentType type) {
        super(api, responseNumber, guild);
        this.member = member;
        this.moderator = moderator;
        this.type = type;
    }

    public Member getMember() {
        return member;
    }

    public User getUser(){
        return member.getUser();
    }

    public PunishmentType getType() {
        return type;
    }

    public Member getModerator() {
        return moderator;
    }
}
