/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.command2;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandParser;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

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
    private final int requiredPermissionLevel;
    protected CommandHandler(String[] invokeAliases, CommandCategory category, int requiredPermissionLevel) {
        this.invokeAliases = invokeAliases;
        this.category = category;
        this.requiredPermissionLevel = requiredPermissionLevel;
    }

    /**
     * Checks permission, safely calls the execute method and ensures response.
     * @param args the command arguments with prefix and command head removed.
     * @param event the invoking event.
     */
    public void call(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        // TODO permission checks

        // execute command and obtain response
        Message response;
        try {
            response = execute(parsedCommandInvocation);
        } catch (Exception e) { // catch exceptions in command and provide an answer
            response = new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setAuthor("Error", null, RubiconBot.getJDA().getSelfUser().getEffectiveAvatarUrl())
                    .setDescription("An error occured while executing your command.")
                    .setColor(Colors.COLOR_ERROR)
                    .setFooter(timeStampFormat.format(new Date()), null)
                    .build()).build();
        }

        // send response message and delete it after defaultDeleteIntervalSeconds
        parsedCommandInvocation.invocationMessage.getChannel().sendMessage(response)
                .queue(msg -> msg.delete().queueAfter(defaultDeleteIntervalSeconds, TimeUnit.SECONDS));
    }

    /**
     * Method to be implemented by actual command handlers.
     * @param args the command arguments with prefix and command head removed.
     * @return a response that will be sent and deleted by the caller.
     */
    protected abstract Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation);

    /**
     * @return all aliases this CommandHandler wants to listen to.
     */
    public String[] getInvokeAliases() {
        return invokeAliases;
    }
}
