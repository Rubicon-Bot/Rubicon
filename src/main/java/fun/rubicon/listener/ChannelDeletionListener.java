package fun.rubicon.listener;

import fun.rubicon.commands.settings.CommandBlacklist;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ChannelDeletionListener extends ListenerAdapter {

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        CommandBlacklist.handleChannelDeletion(event);
    }
}
