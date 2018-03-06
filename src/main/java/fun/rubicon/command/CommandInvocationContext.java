/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.command;

import fun.rubicon.RubiconBot;
import fun.rubicon.features.translation.TranslationLocale;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.sql.GuildSQL;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.entities.*;

/**
 * Contains parsed information and contextual methods.
 * @author tr808axm
 */
public class CommandInvocationContext {
    private final RubiconBot rubiconBot;
    private final Message message;
    private final String[] argsNew;
    private final String mainCommand;
    private final String usedPrefix;

    // Objects only retrieved when needed
    private TranslationLocale translationLocale;

    /**
     * Initializes this data container.
     * @param rubiconBot the bot instance that should handle this command execution.
     * @param invocationMessage the message that invoked this command execution.
     * @param usedPrefix the prefix used in the message.
     * @param mainCommand the used main command alias.
     * @param args array of command arguments
     */
    CommandInvocationContext(RubiconBot rubiconBot, Message invocationMessage, String usedPrefix, String mainCommand, String[] args) {
        this.rubiconBot = rubiconBot;
        this.message = invocationMessage;
        this.usedPrefix = usedPrefix;
        this.mainCommand = mainCommand;
        this.argsNew = args;
    }

    /**
     * @return the message that invoked this command execution.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * @return the guild the invocation message was sent on.
     */
    public Guild getGuild() {
        return message.getGuild();
    }

    /**
     * @return the command arguments (invocation message without prefix and main command).
     */
    public String[] getArgs() {
        return argsNew;
    }

    /**
     * @return the used main command alias.
     * @deprecated Use {@link #getMainCommand()} instead.
     */
    @Deprecated
    public String getCommandInvocation() {
        return mainCommand;
    }

    /**
     * @return the used main command alias.
     */
    public String getMainCommand() {
        return mainCommand;
    }

    /**
     * @return the used prefix.
     */
    public String getPrefix() {
        return usedPrefix;
    }

    /**
     * @return the member object on the guild for this bot.
     */
    public Member getSelfMember() {
        return message.getGuild().getSelfMember();
    }

    /**
     * @return the user who invoked the command execution.
     */
    public User getAuthor() {
        return message.getAuthor();
    }

    /**
     * @return the user who invoked the command execution.
     */
    public User getInvoker() {
        return message.getAuthor();
    }

    /**
     * @return the member who invoked the command execution. null if the command was invoked in a private channel.
     * @see #getAuthor() for the channel-type-independent {@link User} object.
     */
    public Member getMember() {
        return message.getMember();
    }

    /**
     * @return the text channel the invocation message was sent in. null if the command was invoked in a private channel.
     * @see #getChannel() for the channel-type-independent {@link MessageChannel} object.
     */
    public TextChannel getTextChannel() {
        return message.getTextChannel();
    }

    /**
     * @return the (channel-type-independent) message channel the command was invoked in.
     */
    public MessageChannel getChannel() {
        return message.getChannel();
    }

    /**
     * @return a user permissions interface that applies to this command.
     */
    public UserPermissions getUserPermissions() {
        return new UserPermissions(message.getAuthor(), message.getGuild());
    }

    /**
     * @return the locale that applies to the invoker.
     */
    public TranslationLocale getTranslationLocale() {
        if(translationLocale == null)
            translationLocale = rubiconBot.getTranslationManager().getUserLocale(getAuthor());
        return translationLocale;
    }

    /**
     * Gets the translation value for a key in the user's language and automatically replaces %prefix% with the used
     * prefix and %command% with the used command alias.
     * @param key the identifier for the message to translate.
     * @return the translation for the specified key.
     */
    public String translate(String key) {
        return getTranslationLocale().getResourceBundle().getString(key)
                .replaceAll("%prefix%", getPrefix())
                .replaceAll("%command%", getMainCommand());
    }

    /**
     * @return the bot instance that should handle this command execution.
     */
    public RubiconBot getRubiconBot() {
        return rubiconBot;
    }

    /**
     * Parses a raw message into command components.
     *
     * @param rubiconBot the bot instance that should handle this command execution.
     * @param message the discord message to parse.
     * @return an object with the parsed arguments or null if the message could not be
     * resolved to a command.
     */
    static CommandInvocationContext parse(RubiconBot rubiconBot, Message message) {
        String prefix = null;
        // react to mention: '@botmention<majorcommand> [arguments]'
        if (message.getContentRaw().startsWith(message.getJDA().getSelfUser().getAsMention())) {
            prefix = message.getJDA().getSelfUser().getAsMention();
            // react to default prefix: 'rc!<majorcommand> [arguments]'
        } else if (message.getContentRaw().toLowerCase().startsWith(Info.BOT_DEFAULT_PREFIX.toLowerCase())) {
            prefix = message.getContentRaw().substring(0, Info.BOT_DEFAULT_PREFIX.length());
        }
        // react to custom server prefix: '<custom-server-prefix><majorcommand> [arguments...]'
        else if (message.getChannelType() == ChannelType.TEXT) { // ensure bot is on a server
            String serverPrefix = new GuildSQL(RubiconBot.getMySQL(), message.getGuild()).get("prefix");
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
            return new CommandInvocationContext(rubiconBot, message, prefix, allArgs[0], args);
        }
        // else
        return null; // = message is not a command
    }
}
