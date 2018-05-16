package fun.rubicon.listener.events;

import fun.rubicon.core.entities.RubiconGuild;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class GenericRubiconGuildEvent extends RubiconEvent{

    private Guild guild;

    public GenericRubiconGuildEvent(JDA api, long responseNumber, Guild guild) {
        super(api, responseNumber);
        this.guild = guild;
    }

    public Guild getGuild() {
        return guild;
    }

    public RubiconGuild getRubiconGuild(){
        return RubiconGuild.fromGuild(guild);
    }
}
