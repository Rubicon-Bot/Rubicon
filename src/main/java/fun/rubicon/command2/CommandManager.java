/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.command2;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandParser;
import fun.rubicon.core.Main;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintains command invocation associations.
 * @author tr808axm
 */
public class CommandManager extends ListenerAdapter {
    private final Map<String, CommandHandler> commandAssociations = new HashMap<>();

    public CommandManager() {
        RubiconBot.getJDA().addEventListener(this);
    }

    public void registerCommandHandlers(CommandHandler... commandHandlers) {
        for(CommandHandler commandHandler : commandHandlers)
            registerCommandHandler(commandHandler);
    }

    /**
     * Registers a CommandHandler with it's invocation aliases.
     * @param commandHandler the {@link CommandHandler} to be registered.
     */
    public void registerCommandHandler(CommandHandler commandHandler) {
        for(String invokeAlias : commandHandler.getInvokeAliases())
            // only register if alias is not taken
            if(commandAssociations.containsKey(invokeAlias.toLowerCase()))
                Logger.error("WARNING: The '" + commandHandler.toString()
                        + "' CommandHandler tried to register the alias '" + invokeAlias
                        + "' which is already taken by the '" + commandAssociations.get(invokeAlias).toString()
                        + "' CommandHandler.");
            else
                commandAssociations.put(invokeAlias.toLowerCase(), commandHandler);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        super.onMessageReceived(event);
        ParsedCommandInvocation commandInvocation = parse(event.getMessage());
        if(commandInvocation != null)
            call(commandInvocation);
    }

    public void call(ParsedCommandInvocation commandInvocation) {
        CommandHandler commandHandler = commandAssociations.get(commandInvocation.invocationCommand);
        if(commandHandler != null)
            commandHandler.call(commandInvocation);
    }

    /**
     * Parses a raw message into command components.
     * @param message the discord message to parse.
     * @return a {@link ParsedCommandInvocation} with the parsed arguments or null if the message could not be
     * resolved to a command.
     */
    private static ParsedCommandInvocation parse(Message message) {
        // get server prefix
        String prefix = RubiconBot.getMySQL().getGuildValue(message.getGuild(), "prefix");

        // resolve messages with '<server-bot-prefix>majorcommand [arguments...]'
        if(message.getContent().startsWith(prefix)) {
            // cut off command prefix
            String beheaded = message.getContent().substring(prefix.length(), message.getContent().length());
            // split arguments
            String[] allArgs = beheaded.split(" ");
            // create an array of the actual command arguments (exclude invocation arg)
            String[] args = new String[allArgs.length - 1];
            System.arraycopy(allArgs, 1, args, 0, args.length);

            return new ParsedCommandInvocation(message, allArgs[0], args);
        }
        // TODO resolve messages with '@botmention majorcommand [arguments...]'
        // return null if no strategy could parse a command.
        return null;
    }

    public static final class ParsedCommandInvocation {
        public final Message invocationMessage;
        public final String invocationCommand;
        public final String[] args;

        private ParsedCommandInvocation(Message invocationMessage, String invocationCommand, String[] args) {
            this.invocationMessage = invocationMessage;
            this.invocationCommand = invocationCommand;
            this.args = args;
        }
    }
}
