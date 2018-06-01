package fun.rubicon.listener.events;

import fun.rubicon.core.entities.PunishmentType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

/**
 * @author Schlaubi / Michael Rittmeister
 */
@Deprecated
public class PunishmentEvent extends GenericPunishmentEvent {

    private long expiry;

    public PunishmentEvent(JDA api, long responseNumber, Guild guild, Member member, Member moderator, PunishmentType type, long expiry) {
        super(api, responseNumber, guild, member, moderator, type);
        this.expiry = expiry;
    }

    public long getExpiry() {
        return expiry;
    }
}
