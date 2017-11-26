/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.command2;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Handles a command.
 * @author tr808axm
 */
public abstract class CommandHandler {
    private static final SimpleDateFormat timeStampFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final long defaultDeleteIntervalSeconds = 15;

    private final String[] invokeAliases;

    private final CommandCategory category;
    private final PermissionRequirements permissionRequirements;

    protected CommandHandler(String[] invokeAliases, CommandCategory category, PermissionRequirements permissionRequirements) {
        this.invokeAliases = invokeAliases;
        this.category = category;
        this.permissionRequirements = permissionRequirements;
    }

    /**
     * Checks permission, safely calls the execute method and ensures response.
     * @param parsedCommandInvocation the parsed command invocation.
     */
    public void call(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        // response container
        Message response;
        UserPermissions userPermissions = new UserPermissions(parsedCommandInvocation.invocationMessage.getAuthor(),
                parsedCommandInvocation.invocationMessage.getGuild());
        // check permission
        if (permissionRequirements.coveredBy(userPermissions)) {
            // execute command
            try {
                response = execute(parsedCommandInvocation, userPermissions);
            } catch (Exception e) { // catch exceptions in command and provide an answer
                Logger.error("Unknown error during the execution of the '" + parsedCommandInvocation.invocationCommand + "' command. ");
                Logger.error(e);
                response = new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setAuthor("Error", null, RubiconBot.getJDA().getSelfUser().getEffectiveAvatarUrl())
                        .setDescription("An unknown error occured while executing your command.")
                        .setColor(Colors.COLOR_ERROR)
                        .setFooter(timeStampFormat.format(new Date()), null)
                        .build()).build();
            }
        } else
            // respond with 'no-permission'-message
            response = new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setAuthor("Missing permissions", null, RubiconBot.getJDA().getSelfUser().getEffectiveAvatarUrl())
                    .setDescription("You are not permitted to execute this command.")
                    .setColor(Colors.COLOR_NO_PERMISSION)
                    .setFooter(timeStampFormat.format(new Date()), null)
                    .build()).build();

        // respond
        if(response != null)
            // send response message and delete it after defaultDeleteIntervalSeconds
            parsedCommandInvocation.invocationMessage.getChannel().sendMessage(response)
                    .queue(msg -> msg.delete().queueAfter(defaultDeleteIntervalSeconds, TimeUnit.SECONDS));
    }

    /**
     * Method to be implemented by actual command handlers.
     * @param parsedCommandInvocation the command arguments with prefix and command head removed.
     * @param userPermissions an object to query the invoker's permissions.
     * @return a response that will be sent and deleted by the caller.
     */
    protected abstract Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions);

    /**
     * @return all aliases this CommandHandler wants to listen to.
     */
    public String[] getInvokeAliases() {
        return invokeAliases;
    }
}
