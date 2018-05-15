package fun.rubicon.listener.events;

import fun.rubicon.RubiconBot;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.Event;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class RubiconEvent extends Event {

    public RubiconEvent(JDA api, long responseNumber) {
        super(api, responseNumber);
    }


}
