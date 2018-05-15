package fun.rubicon.listener.events;

import fun.rubicon.listener.bot.AllShardsLoadedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class RubiconEventAdapter extends ListenerAdapter implements EventListener{


    public void onCommandExecution(CommandExecutedEvent event) {
    }

    public void onAllShardsLoaded(AllShardsLoadedEvent event){

    }

    public final void onEvent(RubiconEvent event) {
        if(event instanceof CommandExecutedEvent)
            this.onCommandExecution((CommandExecutedEvent) event);
        else if (event instanceof AllShardsLoadedEvent)
            this.onAllShardsLoaded((AllShardsLoadedEvent) event);
    }
}
