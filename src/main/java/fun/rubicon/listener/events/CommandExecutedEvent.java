package fun.rubicon.listener.events;

import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Schlaubi / Michael Rittmeister
 */
@Deprecated
public class CommandExecutedEvent extends GenericRubiconGuildEvent {

    private Message msg;
    private CommandManager.ParsedCommandInvocation invocation;
    private Member member;
    private CommandHandler handler;

    public CommandExecutedEvent(JDA api, long responseNumber, Message message, CommandManager.ParsedCommandInvocation invocation, CommandHandler handler) {
        super(api, responseNumber, invocation.getGuild());
        this.msg = message;
        this.invocation = invocation;
        this.member = invocation.getMember();
        this.handler = handler;

    }

    public Message getMessage() {
        return msg;
    }

    public CommandManager.ParsedCommandInvocation getInvocation() {
        return invocation;
    }

    public Member getMember() {
        return member;
    }

    public CommandHandler getHandler() {
        return handler;
    }
}
