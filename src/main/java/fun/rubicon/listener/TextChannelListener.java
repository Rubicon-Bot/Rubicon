package fun.rubicon.listener;

import fun.rubicon.commands.moderation.CommandMute;
import fun.rubicon.commands.settings.CommandBlacklist;
import fun.rubicon.commands.settings.CommandWhitelist;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class TextChannelListener extends ListenerAdapter {

    @Override
    public void onTextChannelCreate(TextChannelCreateEvent event) {
        CommandMute.handleTextChannelCreation(event);
    }

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        CommandWhitelist.handleTextChannelDeletion(event);
        CommandBlacklist.handleTextChannelDeletion(event);
    }
}
