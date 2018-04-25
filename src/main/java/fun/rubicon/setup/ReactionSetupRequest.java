package fun.rubicon.setup;

import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public abstract class ReactionSetupRequest extends SetupRequest {

    public abstract void handleReaction(GuildMessageReactionAddEvent event);

}
