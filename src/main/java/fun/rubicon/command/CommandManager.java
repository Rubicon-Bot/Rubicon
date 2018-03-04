/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.command;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.music.MusicManager;
import fun.rubicon.sql.GuildSQL;
import fun.rubicon.util.DevCommandLog;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.GlobalBlacklist;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Maintains command invocation associations.
 *
 * @author tr808axm
 */
public class CommandManager extends ListenerAdapter {
    private final RubiconBot rubiconBot;
    private final Map<String, CommandHandler> commandAssociations = new HashMap<>();

    /**
     * Constructs and registers the command manager.
     */
    public CommandManager(RubiconBot rubiconBot) {
        this.rubiconBot = rubiconBot;
        RubiconBot.registerEventListener(this);
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

    /**
     * Registers multiple CommandHandlers with their invocation aliases.
     *
     * @param commandHandlers the CommandHandlers to register.
     */
    public void registerCommandHandlers(CommandHandler... commandHandlers) {
        for (CommandHandler commandHandler : commandHandlers)
            registerCommandHandler(commandHandler);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // ignore bots
        if (event.getAuthor().isBot())
            return;

        // ignore private chat
        if (event.isFromType(ChannelType.PRIVATE)) return;
        GuildSQL guildSQL = new GuildSQL(RubiconBot.getMySQL(), event.getGuild());

        // ignore blacklisted / not whitelisted channels
        if((guildSQL.isBlacklistEnabled() && guildSQL.isBlacklisted(event.getTextChannel()))
                || (guildSQL.isWhitelistEnabled() && !guildSQL.isWhitelisted(event.getTextChannel())))
                return;

        // pass event to music manager TODO move this to a new listener
        MusicManager.handleTrackChoose(event);

        CommandInvocationContext commandInvocationContext = CommandInvocationContext.parse(event.getMessage());

        if (commandInvocationContext != null && !event.getAuthor().isBot() && !event.getAuthor().isFake() && !event.isWebhookMessage()) {
            if (GlobalBlacklist.isOnBlacklist(event.getAuthor())) {
                event.getTextChannel().sendMessage(EmbedUtil.message(EmbedUtil.error("Blacklisted", "You are on the RubiconBot blacklist! ;)"))).queue(msg -> msg.delete().queueAfter(20, TimeUnit.SECONDS));
                return;
            }
            call(commandInvocationContext);
        }
    }

    /**
     * Call the CommandHandler for commandInvocation.
     *
     * @param commandInvocationContext the parsed message.
     */
    private void call(CommandInvocationContext commandInvocationContext) {
        CommandHandler commandHandler = getCommandHandler(commandInvocationContext.getCommandInvocation());
        Message response;
        if (commandHandler == null) {
            /*response = EmbedUtil.message(EmbedUtil.withTimestamp(EmbedUtil.error("Unknown command", "'" + parsedCommandInvocation.serverPrefix + parsedCommandInvocation.invocationCommand
                    + "' could not be resolved to a command.\nType '" + parsedCommandInvocation.serverPrefix
                    + "help' to get a list of all commands.")));*/
            return;
        } else {
            DevCommandLog.log(ParsedCommandInvocation.fromNewType(commandInvocationContext));
            response = commandHandler.call(commandInvocationContext);
        }

        // respond
        if (response != null)
            EmbedUtil.sendAndDeleteOnGuilds(commandInvocationContext.getMessage().getChannel(), response);

        // delete invocation message
        if (commandInvocationContext.getGuild() != null) {
            if(!commandInvocationContext.getGuild().getSelfMember().getPermissions(commandInvocationContext.getTextChannel()).contains(Permission.MESSAGE_MANAGE)) return; // Do not try to delete message when bot is not allowed to
            commandInvocationContext.getMessage().delete().queue(null, msg -> {
            }); // suppress failure
        }
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

    /**
     * @deprecated Use {@link fun.rubicon.command.CommandInvocationContext} instead.
     */
    @Deprecated
    public static final class ParsedCommandInvocation extends CommandInvocationContext {
        private ParsedCommandInvocation(Message invocationMessage, String serverPrefix, String invocationCommand, String[] args) {
            super(invocationMessage, serverPrefix, invocationCommand, args);
        }

        static ParsedCommandInvocation fromNewType(CommandInvocationContext newObj) {
            return new ParsedCommandInvocation(newObj.getMessage(), newObj.getPrefix(), newObj.getCommandInvocation(),
                    newObj.getArgs());
        }
    }
}
