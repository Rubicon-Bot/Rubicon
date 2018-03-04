/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.command;

import fun.rubicon.RubiconBot;
import fun.rubicon.commands.admin.CommandVerification;
import fun.rubicon.commands.botowner.CommandMaintenance;
import fun.rubicon.listener.ServerLogHandler;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang.NotImplementedException;

import java.util.ArrayList;
import java.util.Arrays;

import static fun.rubicon.util.EmbedUtil.info;
import static fun.rubicon.util.EmbedUtil.message;

/**
 * Handles a command.
 *
 * @author tr808axm
 */
public abstract class CommandHandler {
    private final String[] invocationAliases;
    private final CommandCategory category;
    private final PermissionRequirements permissionRequirements;
    private String description;
    private final String parameterUsage;
    private boolean disabled = false;

    /**
     * Constructs a new CommandHandler.
     *
     * @param invocationAliases      the invocation commands (aliases). First entry is the 'main' alias.
     * @param category               the {@link CommandCategory} this command belongs to.
     * @param permissionRequirements all permission requirements a user needs to meet to execute a command.
     * @param description            a short command description.
     * @param parameterUsage         the usage message.
     */
    protected CommandHandler(String[] invocationAliases, CommandCategory category,
                             PermissionRequirements permissionRequirements, String description, String parameterUsage) {
        this.invocationAliases = invocationAliases;
        this.category = category;
        this.permissionRequirements = permissionRequirements;
        this.description = description;
        this.parameterUsage = parameterUsage;
    }

    protected CommandHandler(String[] invocationAliases, CommandCategory category,
                             PermissionRequirements permissionRequirements, String description, String parameterUsage, boolean disabled) {
        this.invocationAliases = invocationAliases;
        this.category = category;
        this.permissionRequirements = permissionRequirements;
        this.description = description;
        this.parameterUsage = parameterUsage;
        this.disabled = disabled;
    }

    /**
     * Checks permission, safely calls the execute method and ensures response.
     *
     * @param commandInvocationContext the parsed command invocation.
     * @return a response that will be sent and deleted by the caller.
     */
    public Message call(CommandInvocationContext commandInvocationContext) {
        if (disabled) {
            return new MessageBuilder().setEmbed(EmbedUtil.info("Command disabled", "Command is currently disabled.").setFooter("RubiconBot Dev Team", null).build()).build();
        }
        if (CommandMaintenance.maintenance) {
            ArrayList<Long> authors = new ArrayList<>(Arrays.asList(Info.BOT_AUTHOR_IDS));
            if (!authors.contains(commandInvocationContext.getAuthor().getIdLong())) {
                return EmbedUtil.message(EmbedUtil.info("Maintenance!", "Bots maintenance is enabled. Please be patient."));
            }
        }
        // check permission
        if (permissionRequirements.coveredBy(commandInvocationContext.getUserPermissions())) {
            // execute command
            try {
                ServerLogHandler.logCommand(CommandManager.ParsedCommandInvocation.fromNewType(commandInvocationContext));
                try {
                    return execute(commandInvocationContext);
                } catch (UnsupportedOperationException e) {
                    try {
                        return execute(CommandManager.ParsedCommandInvocation.fromNewType(commandInvocationContext),
                                commandInvocationContext.getUserPermissions());
                    } catch (UnsupportedOperationException e2) {
                        throw new NotImplementedException("CommandHandler for command "
                                + commandInvocationContext.getCommandInvocation() + " not found.");
                    }
                }
            } catch (Exception e) { // catch exceptions in command and provide an answer
                Logger.error("Unknown error during the execution of the '" + commandInvocationContext.getCommandInvocation() + "' command. ");
                Logger.error(e);
                return new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setAuthor("Error", null, RubiconBot.getJDA().getSelfUser().getEffectiveAvatarUrl())
                        .setDescription("An unknown error occured while executing your command.")
                        .setColor(Colors.COLOR_ERROR)
                        .setFooter(RubiconBot.getNewTimestamp(), null)
                        .build()).build();
            }
        } else
            // respond with 'no-permission'-message
            return new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setAuthor("Missing permissions", null, RubiconBot.getJDA().getSelfUser().getEffectiveAvatarUrl())
                    .setDescription("You are not permitted to execute this command.")
                    .setColor(Colors.COLOR_NO_PERMISSION)
                    .setFooter(RubiconBot.getNewTimestamp(), null)
                    .build()).build();
    }

    /**
     * @deprecated Implement {@link #execute(CommandInvocationContext)} instead.
     */
    @Deprecated
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        throw new UnsupportedOperationException();
    }

    /**
     * Method to be implemented by actual command handlers.
     *
     * @param commandInvocationContext object containing invocation details and contextual methods.
     * @return a response that will be sent and deleted by the caller.
     */
    protected Message execute(CommandInvocationContext commandInvocationContext) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return all aliases this CommandHandler wants to listen to.
     * @deprecated Use getInvocationAliases instead.
     */
    @Deprecated
    public String[] getInvokeAliases() {
        return invocationAliases;
    }

    /**
     * @return all aliases this CommandHandler wants to listen to.
     */
    public String[] getInvocationAliases() {
        return invocationAliases;
    }

    /**
     * @return the category this command belongs to.
     */
    public CommandCategory getCategory() {
        return category;
    }

    /**
     * @return the permission requirements a user needs to meet to execute a command.
     */
    public PermissionRequirements getPermissionRequirements() {
        return permissionRequirements;
    }

    /**
     * @return the short description of this command.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Updates the description of the command
     *
     * @param description the new command description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the usage message of this command.
     * @deprecated Use createHelpMessage instead.
     */
    @Deprecated
    public String getUsage() {
        return invocationAliases[0] + ' ' + parameterUsage;
    }

    /**
     * Use createHelpMessage for a full help message.
     *
     * @return the parameter usage String.
     */
    public String getParameterUsage() {
        return parameterUsage;
    }

    /**
     * Generates a usage message for this command with the default prefix and alias.
     *
     * @return the generated Message.
     */
    public Message createHelpMessage() {
        return createHelpMessage(Info.BOT_DEFAULT_PREFIX, invocationAliases[0]);
    }

    /**
     * Generates a usage message for this command.
     *
     * @param context data source for prefix and alias to use in the Message.
     * @return the generated Message.
     */
    public Message createHelpMessage(CommandInvocationContext context) {
        return createHelpMessage(context.getPrefix(), context.getCommandInvocation());
    }

    /**
     * Generates a usage message for this command.
     *
     * @param serverPrefix which prefix should be used in this message?
     * @param aliasToUse   which alias should be used in this message?
     */
    public Message createHelpMessage(String serverPrefix, String aliasToUse) {
        StringBuilder usage = new StringBuilder();
        for (String part : getParameterUsage().split("\n"))
            usage.append(serverPrefix).append(aliasToUse).append(" ").append(part).append("\n");

        if (this instanceof CommandVerification) {
            if (CommandVerification.showInspired) {
                setDescription("Let you members accept rules before posting messages.");
            } else {
                setDescription("Let you members accept rules before posting messages\n\nThis feature is partially inspired by [Flashbot](https://flashbot.de)");
            }
        }
        return message(info('\'' + aliasToUse + "' command help", getDescription())
                .addField("Aliases", String.join(", ", getInvocationAliases()), false)
                .addField("Usage", usage.toString(), false));
    }
}
