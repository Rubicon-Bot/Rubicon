/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.command2;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintains command invocation associations.
 *
 * @author tr808axm
 */
public class CommandManager extends ListenerAdapter {
    private final Map<String, CommandHandler> commandAssociations = new HashMap<>();

    /**
     * Registers multiple CommandHandlers with their invocation aliases.
     *
     * @param commandHandlers the CommandHandlers to register.
     */
    public void registerCommandHandlers(CommandHandler... commandHandlers) {
        for (CommandHandler commandHandler : commandHandlers)
            registerCommandHandler(commandHandler);
    }

    /**
     * Registers a CommandHandler with it's invocation aliases.
     *
     * @param commandHandler the {@link CommandHandler} to be registered.
     */
    public void registerCommandHandler(CommandHandler commandHandler) {
        for (String invokeAlias : commandHandler.getInvocationAliases())
            // only register if alias is not taken
            if (commandAssociations.containsKey(invokeAlias.toLowerCase()))
                Logger.warning("The '" + commandHandler.toString()
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
        if (commandInvocation != null) // if it is a command invocation
            call(commandInvocation);
    }

    /**
     * Call the CommandHandler for commandInvocation.
     *
     * @param parsedCommandInvocation the parsed message.
     */
    public void call(ParsedCommandInvocation parsedCommandInvocation) {
        CommandHandler commandHandler = getCommandHandler(parsedCommandInvocation.invocationCommand);
        Message response;
        if (commandHandler == null) {
            /*response = EmbedUtil.message(EmbedUtil.withTimestamp(EmbedUtil.error("Unknown command", "'" + parsedCommandInvocation.serverPrefix + parsedCommandInvocation.invocationCommand
                    + "' could not be resolved to a command.\nType '" + parsedCommandInvocation.serverPrefix
                    + "help' to get a list of all commands.")));*/
            return;
        }else
            response = commandHandler.call(parsedCommandInvocation);

        // respond
        if (response != null)
            EmbedUtil.sendAndDeleteOnGuilds(parsedCommandInvocation.invocationMessage.getChannel(), response);

        // delete invocation message
        parsedCommandInvocation.invocationMessage.delete().queue(null, msg -> {
        }); // suppress failure
    }

    /**
     * Parses a raw message into command components.
     *
     * @param message the discord message to parse.
     * @return a {@link ParsedCommandInvocation} with the parsed arguments or null if the message could not be
     * resolved to a command.
     */
    private static ParsedCommandInvocation parse(Message message) {
        String prefix = null;
        // react to mention: '@botmention<majorcommand> [arguments]'
        if (message.getRawContent().startsWith(RubiconBot.getJDA().getSelfUser().getAsMention())) {
            prefix = RubiconBot.getJDA().getSelfUser().getAsMention();
            // react to default prefix: 'rc!<majorcommand> [arguments]'
        } else if (message.getRawContent().toLowerCase().startsWith(Info.BOT_DEFAULT_PREFIX.toLowerCase()))
            prefix = Info.BOT_DEFAULT_PREFIX;
            // react to custom server prefix: '<custom-server-prefix><majorcommand> [arguments...]'
        else if (message.getChannelType() == ChannelType.TEXT) { // ensure bot is on a server
            String serverPrefix = RubiconBot.getMySQL().getGuildValue(message.getGuild(), "prefix");
            if (message.getRawContent().toLowerCase().startsWith(serverPrefix.toLowerCase()))
                prefix = serverPrefix;
        }

        if (prefix != null) {
            // cut off command prefix
            String beheaded = message.getRawContent().substring(prefix.length(), message.getRawContent().length()).trim();
            // split arguments
            String[] allArgs = beheaded.split("\\s+");
            // create an array of the actual command arguments (exclude invocation arg)
            String[] args = new String[allArgs.length - 1];
            System.arraycopy(allArgs, 1, args, 0, args.length);

            return new ParsedCommandInvocation(message, prefix, allArgs[0], args);
        }
        // else
        return null; // = message is not a command
    }

    /**
     * @param invocationAlias the key property to the CommandHandler.
     * @return the associated CommandHandler or null if none is associated.
     */
    public CommandHandler getCommandHandler(String invocationAlias) {
        return commandAssociations.get(invocationAlias.toLowerCase());
    }

    /**
     * @return a clone of all registered command associations.
     */
    public Map<String, CommandHandler> getCommandAssociations() {
        return new HashMap<>(commandAssociations);
    }

    public static final class ParsedCommandInvocation {
        public final Message invocationMessage;
        public final String serverPrefix;
        public final String invocationCommand;
        public final String[] args;

        private ParsedCommandInvocation(Message invocationMessage, String serverPrefix, String invocationCommand, String[] args) {
            this.invocationMessage = invocationMessage;
            this.serverPrefix = serverPrefix;
            this.invocationCommand = invocationCommand;
            this.args = args;
        }
    }
}
