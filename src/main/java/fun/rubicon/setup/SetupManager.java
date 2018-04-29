package fun.rubicon.setup;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.HashMap;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class SetupManager {

    public HashMap<String, SetupRequest> requestStorage = new HashMap<>();

    public void handleMessage(GuildMessageReceivedEvent event){
        new Thread(() -> {
            if (!requestStorage.containsKey(event.getMember().getUser().getId())) return;
            SetupRequest request = requestStorage.get(event.getMember().getUser().getId());
            if (!event.getMember().equals(request.author)) return;
            event.getMessage().delete().queue();
            if(event.getMessage().getContentDisplay().equals("abort")) { request.unregister(); return; }
            request.next(event.getMessage());
        }, "SetupMessageHandlingThread-" + event.getMessage().getId()).start();

    }

    public void handleReaction(GuildMessageReactionAddEvent event){
        new Thread(() -> {
            if (!requestStorage.containsKey(event.getUser().getId())) return;
            SetupRequest request = requestStorage.get(event.getUser().getId());
            if (!event.getMember().equals(request.author)) return;
            if (!(request instanceof ReactionSetupRequest)) return;
            ((ReactionSetupRequest) request).handleReaction(event);
        }, "SetupReactingHandlingThread-" + event.getMessageId()).start();

    }
}
