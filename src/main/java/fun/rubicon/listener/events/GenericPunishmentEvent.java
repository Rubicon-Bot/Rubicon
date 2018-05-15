package fun.rubicon.listener.events;

import fun.rubicon.core.entities.PunishmentType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class GenericPunishmentEvent extends RubiconEvent{
    private Guild guild;
    private Member member;
    private PunishmentType type;

    public GenericPunishmentEvent(JDA api, long responseNumber, Guild guild, Member member, PunishmentType type) {
        super(api, responseNumber);
        this.guild = guild;
        this.member = member;
        this.type = type;
    }

    public Guild getGuild() {
        return guild;
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
}
