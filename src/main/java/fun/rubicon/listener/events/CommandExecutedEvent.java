package fun.rubicon.listener.events;

import fun.rubicon.command.CommandManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class CommandExecutedEvent extends RubiconEvent {

    private Message msg;
    private CommandManager.ParsedCommandInvocation invocation;

    public CommandExecutedEvent(JDA api, long responseNumber, Message message, CommandManager.ParsedCommandInvocation invocation) {
        super(api, responseNumber);
        this.msg = message;
        this.invocation = invocation;
    }

    public Message getMessage(){
        return msg;
    }

    public CommandManager.ParsedCommandInvocation getInvocation(){
        return invocation;
    }
}
