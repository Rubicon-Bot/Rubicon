package fun.rubicon.listener;

import fun.rubicon.commands.moderation.CommandMute;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class TextChannelListener extends ListenerAdapter{

    @Override
    public void onTextChannelCreate(TextChannelCreateEvent event) {
        CommandMute.handleTextChannelCreation(event);
    }
}
