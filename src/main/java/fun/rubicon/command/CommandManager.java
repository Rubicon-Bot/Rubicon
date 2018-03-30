/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.command;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.core.entities.RubiconMember;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.*;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.*;

/**
 * Maintains command invocation associations.
 *
 * @author tr808axm
 */
public class CommandManager extends ListenerAdapter {
    private final Map<String, CommandHandler> commandAssociations = new HashMap<>();

    private boolean maintenance = false;

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
        if (!RubiconBot.allShardsInitialised())
            return;
        if (event.isFromType(ChannelType.PRIVATE)) return;

        super.onMessageReceived(event);

        //Check Database Entries
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(event.getGuild());
        RubiconMember rubiconMember = RubiconMember.fromMember(event.getMember());

        ParsedCommandInvocation commandInvocation = parse(event.getMessage());
        if (commandInvocation != null && !event.getAuthor().isBot() && !event.getAuthor().isFake() && !event.isWebhookMessage()) {
            if (maintenance && !(Arrays.asList(Info.BOT_AUTHOR_IDS).contains(event.getAuthor().getIdLong()) || Arrays.asList(Info.COMMUNITY_STAFF_TEAM).contains(event.getAuthor().getIdLong()))) {
                SafeMessage.sendMessage(event.getTextChannel(), EmbedUtil.info("Maintenance", "Bot is currently in maintenance mode.").build(), 5);
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
        CommandHandler commandHandler = getCommandHandler(parsedCommandInvocation.getCommandInvocation());
        Message response;
        if (commandHandler == null) {
            return;
        } else {
            response = commandHandler.call(parsedCommandInvocation);
        }

        // respond
        if (response != null)
            SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), response, 60);

        // delete invocation message
        if (parsedCommandInvocation.getGuild() != null) {
            if (!parsedCommandInvocation.getGuild().getSelfMember().getPermissions(parsedCommandInvocation.getTextChannel()).contains(Permission.MESSAGE_MANAGE))
                return; // Do not try to delete message when bot is not allowed to
            try {
                parsedCommandInvocation.getMessage().delete().queue(null, msg -> {
                }); // suppress failure
            } catch (Exception e) {
                // Ignored
            }
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
        if (message.getContentRaw().startsWith(RubiconBot.getSelfUser().getAsMention())) {
            prefix = RubiconBot.getSelfUser().getAsMention();
            // react to default prefix: 'rc!<majorcommand> [arguments]'
        } else if (message.getContentRaw().toLowerCase().startsWith(Info.BOT_DEFAULT_PREFIX.toLowerCase())) {
            prefix = message.getContentRaw().substring(0, Info.BOT_DEFAULT_PREFIX.length());
        }
        // react to custom server prefix: '<custom-server-prefix><majorcommand> [arguments...]'
        else if (message.getChannelType() == ChannelType.TEXT) { // ensure bot is on a server
            String serverPrefix = RubiconGuild.fromGuild(message.getGuild()).getPrefix();
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

        private ResourceBundle language;
        private final ResourceBundle defaultResourceBundle;
        private final String[] args;
        private final String commandInvocation;
        private final Message message;
        private final String prefix;
        private final String argsString;
        private final JDA jda;

        private ParsedCommandInvocation(Message invocationMessage, String serverPrefix, String invocationCommand, String[] args) {
            this.message = invocationMessage;
            this.prefix = serverPrefix;
            this.commandInvocation = invocationCommand;
            this.args = args;
            this.jda = message.getJDA();
            this.argsString = message.getContentDisplay().replace(prefix + invocationCommand + " ", "");

            RubiconGuild.fromGuild(message.getGuild());
            RubiconMember.fromMember(message.getMember());

            this.defaultResourceBundle = RubiconBot.sGetTranslations().getDefaultTranslationLocale().getResourceBundle();
            try {
                this.language = RubiconBot.sGetTranslations().getUserLocale(invocationMessage.getAuthor()).getResourceBundle();
            } catch (NullPointerException | MissingResourceException e) {
                this.language = defaultResourceBundle;
                RubiconUser.fromUser(getAuthor()).setLanguage("en-US");
            }
        }

        public Message getMessage() {
            return message;
        }

        public Guild getGuild() {
            return message.getGuild();
        }

        public String[] getArgs() {
            return args;
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

        public String getArgsString() {
            return argsString;
        }

        public JDA getJDA() {
            return jda;
        }

        public ResourceBundle getLanguage() {
            return language;
        }

        public ResourceBundle getDefaultLanguage() {
            return defaultResourceBundle;
        }

        public UserPermissions getPerms() {
            return new UserPermissions(getAuthor(), getGuild());
        }

        public String translate(String key) {
            String entry;
            try {
                entry = language.getString(key);
            } catch (MissingResourceException e) {
                try {
                    entry = defaultResourceBundle.getString(key);
                } catch (MissingResourceException e2) {
                    entry = "Unable to find language string for \"" + key + "\"";
                }
            }
            return entry;
        }
    }

    public boolean isMaintenanceEnabled() {
        return maintenance;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }
}