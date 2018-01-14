/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.command;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.music.MusicManager;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.GlobalBlacklist;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Maintains command invocation associations.
 *
 * @author tr808axm
 */
public class CommandManager extends ListenerAdapter {
    private final Map<String, CommandHandler> commandAssociations = new HashMap<>();

    /**
     * Constructs and registers the command manager.
     */
    public CommandManager() {
        RubiconBot.registerEventListener(this);
    }

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
        if (event.isFromType(ChannelType.PRIVATE)) return;
        if (RubiconBot.getMySQL().isBlacklisted(event.getTextChannel())) return;
        MusicManager.handleTrackChoose(event);
        super.onMessageReceived(event);
        ParsedCommandInvocation commandInvocation = parse(event.getMessage());
        if (commandInvocation != null && !event.getAuthor().isBot() && !event.getAuthor().isFake() && !event.isWebhookMessage()) {
            if (event.getAuthor().getId().equals("343825218718007296")) {
                event.getTextChannel().sendMessage(new EmbedBuilder()
                        .setTitle(":rotating_light: __**ERROR**__ :rotating_light:")
                        .setDescription("403 WRONG GUY")
                        .setColor(Color.RED)
                        .build()).queue();
                return;
            }
            if (GlobalBlacklist.isOnBlacklist(event.getAuthor())) {
                event.getTextChannel().sendMessage(EmbedUtil.message(EmbedUtil.error("Blacklisted", "You are on the RubiconBot blacklist! ;)"))).queue(msg -> msg.delete().queueAfter(20, TimeUnit.SECONDS));
                return;
            }
            call(commandInvocation);
        }
    }

    /**
     * Call the CommandHandler for commandInvocation.
     *
     * @param parsedCommandInvocation the parsed message.
     */
    private void call(ParsedCommandInvocation parsedCommandInvocation) {
        CommandHandler commandHandler = getCommandHandler(parsedCommandInvocation.invocationCommand);
        Message response;
        if (commandHandler == null) {
            /*response = EmbedUtil.message(EmbedUtil.withTimestamp(EmbedUtil.error("Unknown command", "'" + parsedCommandInvocation.serverPrefix + parsedCommandInvocation.invocationCommand
                    + "' could not be resolved to a command.\nType '" + parsedCommandInvocation.serverPrefix
                    + "help' to get a list of all commands.")));*/
            return;
        } else
            response = commandHandler.call(parsedCommandInvocation);

        // respond
        if (response != null)
            EmbedUtil.sendAndDeleteOnGuilds(parsedCommandInvocation.invocationMessage.getChannel(), response);

        // delete invocation message
        if (parsedCommandInvocation.invocationMessage.getGuild() != null) {
            parsedCommandInvocation.invocationMessage.delete().queue(null, msg -> {
            }); // suppress failure
        }
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
        if (message.getContentRaw().startsWith(RubiconBot.getJDA().getSelfUser().getAsMention())) {
            prefix = RubiconBot.getJDA().getSelfUser().getAsMention();
            // react to default prefix: 'rc!<majorcommand> [arguments]'
        } else if (message.getContentRaw().toLowerCase().startsWith(Info.BOT_DEFAULT_PREFIX.toLowerCase()))
            prefix = Info.BOT_DEFAULT_PREFIX;
            // react to custom server prefix: '<custom-server-prefix><majorcommand> [arguments...]'
        else if (message.getChannelType() == ChannelType.TEXT) { // ensure bot is on a server
            String serverPrefix = RubiconBot.getMySQL().getGuildValue(message.getGuild(), "prefix");
            if (message.getContentRaw().toLowerCase().startsWith(serverPrefix.toLowerCase()))
                prefix = serverPrefix;
        }

        if (prefix != null) {
            // cut off command prefix
            String beheaded = message.getContentRaw().substring(prefix.length(), message.getContentRaw().length()).trim();
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
        @Deprecated
        public final Message invocationMessage;
        @Deprecated
        public final String serverPrefix;
        @Deprecated
        public final String invocationCommand;
        @Deprecated
        public final String[] args;

        private final String[] argsNew;
        private final String commandInvocation;
        private final Message message;
        private final String prefix;

        private ParsedCommandInvocation(Message invocationMessage, String serverPrefix, String invocationCommand, String[] args) {
            this.invocationMessage = invocationMessage;
            this.message = invocationMessage;
            this.serverPrefix = serverPrefix;
            this.prefix = serverPrefix;
            this.invocationCommand = invocationCommand;
            this.commandInvocation = invocationCommand;
            this.args = args;
            this.argsNew = args;
        }

        public Message getMessage() {
            return message;
        }

        public Guild getGuild() {
            return message.getGuild();
        }

        public String[] getArgs() {
            return argsNew;
        }

        public String getCommandInvocation() {
            return commandInvocation;
        }

        public String getPrefix() {
            return prefix;
        }

        public Member getSelfMember() {
            return message.getGuild().getSelfMember();
        }

        public User getAuthor() {
            return message.getAuthor();
        }

        public Member getMember() {
            return message.getMember();
        }

        public TextChannel getTextChannel() {
            return message.getTextChannel();
        }
    }
}
