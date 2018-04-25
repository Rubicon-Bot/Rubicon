package fun.rubicon.setup;

import fun.rubicon.RubiconBot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class SetupListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        RubiconBot.getSetupManager().handleMessage(event);
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        RubiconBot.getSetupManager().handleReaction(event);
    }
}
