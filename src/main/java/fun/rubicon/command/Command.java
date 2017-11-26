/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.command;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.permission.PermissionManager;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.RestAction;

import java.awt.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee, tr808axm
 * @deprecated Use {@link fun.rubicon.command2.CommandHandler} for command handlers instead.
 */
@Deprecated
public abstract class Command {
    protected int defaultDeleteSeconds = 30;

    protected String command;
    protected String[] args;
    protected MessageReceivedEvent e;
    protected CommandCategory category;
    protected PermissionManager permissionManager;
    protected String[] aliases = new String[0];

    public Command(String command, CommandCategory category) {
        this.command = command;
        this.category = category;
    }

    public void call(String[] args, MessageReceivedEvent e) throws ParseException {
        this.args = args;
        this.e = e;
        //if(!BotPermissionChecker.hasAllPermissions(e)) {
        //    return;
        //}
        this.permissionManager = new PermissionManager(e.getMember(), this);
        if (permissionManager.hasPermission()) {
            execute(args, e);
        } else {
            sendNoPermissionMessage();
        }
        try {
            e.getMessage().delete().queue();
        } catch (Exception e1) {
            Logger.error(e1);
        }
    }

    /**
     * Sends a simple embedded message as a response.
     * @param message the description of the embedded.
     * @deprecated
     */
    @Deprecated
    protected void sendEmbededMessage(String message) {
        sendSimpleEmbeddedMessage(e.getTextChannel(), null, null, null, Colors.COLOR_PRIMARY,
                message, defaultDeleteSeconds);
    }

    /**
     * Sends an embedded message in a text channel and automatically removes it.
     * @param ch the text channel to send the message in.
     * @param title the title (author in this case) of the embedded.
     * @param color the side-color of the embedded.
     * @deprecated
     */
    @Deprecated
    protected void sendEmbededMessage(TextChannel ch, String title, Color color, String message) {
        sendSimpleEmbeddedMessage(ch, title, e.getJDA().getSelfUser().getEffectiveAvatarUrl(), null, color,
                message, defaultDeleteSeconds);
    }

    /**
     * Sends an embedded message in a private channel.
     * @param pc the private channel to send the message in.
     * @param title the title (author in this case) of the message.
     * @param color the side-color of the embedded.
     * @param message the description of the embedded.
     * @deprecated
     */
    @Deprecated
    protected void sendEmbededMessage(PrivateChannel pc, String title, Color color, String message) {
        sendSimpleEmbeddedMessage(pc, title, e.getJDA().getSelfUser().getEffectiveAvatarUrl(), null, color,
                message, -1);
    }

    /**
     * Sends the error-template as a response.
     *
     * @param message the string used as the embedded description
     */
    protected void sendErrorMessage(String message) {
        sendSimpleEmbeddedMessage(e.getTextChannel(), "Error", e.getJDA().getSelfUser().getEffectiveAvatarUrl(),
                null, Colors.COLOR_ERROR, message, defaultDeleteSeconds);
    }

    /**
     * Sends the 'no-permission'-template as a response.
     */
    protected void sendNoPermissionMessage() {
        sendSimpleEmbeddedMessage(e.getTextChannel(), "No Permissions",
                e.getJDA().getSelfUser().getEffectiveAvatarUrl(), null, Colors.COLOR_NO_PERMISSION,
                "You don't have permissions to do this!", defaultDeleteSeconds);
    }

    /**
     * Sends the usage-template as a response.
     */
    protected void sendUsageMessage() {
        sendSimpleEmbeddedMessage(e.getTextChannel(), "Usage of " + getCommand(),
                e.getJDA().getSelfUser().getEffectiveAvatarUrl(), null, Colors.COLOR_SECONDARY, getUsage(),
                defaultDeleteSeconds);
    }

    /**
     * Sends the 'not-implemented'-template as a response.
     */
    protected void sendNotImplementedMessage() {
        sendSimpleEmbeddedMessage(e.getTextChannel(), null, null, "Not implemented yet",
                Colors.COLOR_NOT_IMPLEMENTED, "Command is not implemented yet!", defaultDeleteSeconds);
    }

    /**
     * Sends an embedded message of the given parameters to channel and removes it afterwards.
     *
     * @param channel        the channel the embedded message should be sent to.
     * @param title          the title of the embedded.
     * @param color          the side-color of the embedded.
     * @param description    the description of the embedded.
     * @param removeInterval the time interval in seconds after which the message should be deleted. -1 means that the
     *                       message will not be deleted.
     */
    protected void sendSimpleEmbeddedMessage(MessageChannel channel, String author, String iconUrl, String title, Color color, String description, int removeInterval) {
        RestAction<Message> sendAction = channel.sendMessage(new EmbedBuilder()
                .setAuthor(author, null, iconUrl)
                .setTitle(title)
                .setDescription(description)
                .setColor(color)
                .setFooter(RubiconBot.getNewTimestamp(), null).build());
        if (removeInterval == -1)
            sendAction.queue();
        else
            sendAction.queue(msg -> msg.delete().queueAfter(removeInterval, TimeUnit.SECONDS));
    }

    public String getCommand() {
        return command;
    }

    protected abstract void execute(String[] args, MessageReceivedEvent e) throws ParseException;

    /**
     * @return this command's category.
     */
    public CommandCategory getCategory() {
        return category;
    }

    /**
     * Sets the aliases array to the given Strings.
     *
     * @param aliases the new aliases.
     * @return this Command.
     */
    public Command addAliases(String... aliases) {
        this.aliases = aliases;
        return this;
    }

    /**
     * @return all aliases as a list.
     */
    public List<String> getAliases() {
        return Arrays.asList(aliases);
    }

    /**
     * Joins the aliases array with a comma.
     * @return the formatted string.
     */
    public String getFormattedAliases() {
        return aliases.length == 0 ? "No Aliases" : "[" + String.join(", ", aliases) + "]";
    }

    /**
     * @return a short description of this Command.
     */
    public abstract String getDescription();

    /**
     * @return the usage message as a String.
     */
    public abstract String getUsage();

    /**
     * Permissions
     * 0 - everyone
     * 1 - With Permissions
     * 2 - Admisitrator
     * 3 - Server Owner
     * 4 - Bot Owner
     * @return this Command's required permission level.
     */
    public abstract int getPermissionLevel();

    /**
     * Generates a timestamp from the current time.
     * @return the generated timestamp.
     */
    private String generateTimeStamp() {
        return RubiconBot.getNewTimestamp();
    }
}

