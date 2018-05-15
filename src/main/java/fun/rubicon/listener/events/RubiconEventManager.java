package fun.rubicon.listener.events;

import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.IEventManager;
import net.dv8tion.jda.core.hooks.InterfacedEventManager;

import java.util.Iterator;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class RubiconEventManager extends InterfacedEventManager implements IEventManager {


    public void handle(Event event) {
        Iterator var2 = getRegisteredListeners().iterator();
        while (var2.hasNext()) {
            EventListener listener = (EventListener) var2.next();

            try {
                if (event instanceof RubiconEvent && listener instanceof RubiconEventAdapter)
                    ((RubiconEventAdapter) listener).onEvent((RubiconEvent) event);
                else
                    listener.onEvent(event);

            } catch (Throwable var5) {
                JDAImpl.LOG.error("One of the EventListeners had an uncaught exception", var5);
            }
        }

    }
}
