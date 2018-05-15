package fun.rubicon.listener.events;

import fun.rubicon.core.entities.PunishmentType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class UnpunishEvent extends GenericPunishmentEvent{

    private User user;

    public UnpunishEvent(JDA api, long responseNumber, Guild guild, Member member, PunishmentType type) {
        super(api, responseNumber, guild, member, type);
        this.user = member.getUser();
    }

    public UnpunishEvent(JDA api, long responseNumber, Guild guild, User user, PunishmentType type) {
        super(api, responseNumber, guild, null, type);
        this.user = user;
    }

    @Override
    public User getUser() {
        return user;
    }
}
