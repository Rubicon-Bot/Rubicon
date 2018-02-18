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
     * @deprecated Use CommandHandler(String[], CommandCategory, PermissionRequirements, String, String) instead to
     * prevent empty data.
     */
    @Deprecated
    protected CommandHandler(String[] invocationAliases, CommandCategory category,
                             PermissionRequirements permissionRequirements) {
        this(invocationAliases, category, permissionRequirements, "", "");
    }

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
     * @param parsedCommandInvocation the parsed command invocation.
     * @return a response that will be sent and deleted by the caller.
     */
    public Message call(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        if (disabled) {
            return new MessageBuilder().setEmbed(EmbedUtil.info("Command disabled", "Command is currently disabled.").setFooter("RubiconBot Dev Team", null).build()).build();
        }
        if (CommandMaintenance.maintenance) {
            ArrayList<Long> authors = new ArrayList<>(Arrays.asList(Info.BOT_AUTHOR_IDS));
            if (!authors.contains(parsedCommandInvocation.getAuthor().getIdLong())) {
                return EmbedUtil.message(EmbedUtil.info("Maintenance!", "Bots maintenance is enabled. Please be patient."));
            }
        }
        UserPermissions userPermissions = new UserPermissions(parsedCommandInvocation.getMessage().getAuthor(),
                parsedCommandInvocation.getMessage().getGuild());
        // check permission
        if (permissionRequirements.coveredBy(userPermissions)) {
            // execute command
            try {
                ServerLogHandler.logCommand(parsedCommandInvocation);
                return execute(parsedCommandInvocation, userPermissions);
            } catch (Exception e) { // catch exceptions in command and provide an answer
                Logger.error("Unknown error during the execution of the '" + parsedCommandInvocation.getCommandInvocation() + "' command. ");
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
     * Method to be implemented by actual command handlers.
     *
     * @param parsedCommandInvocation the command arguments with prefix and command head removed.
     * @param userPermissions         an object to query the invoker's permissions.
     * @return a response that will be sent and deleted by the caller.
     */
    protected abstract Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions);

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
     * @param invocation data source for prefix and alias to use in the Message.
     * @return the generated Message.
     */
    public Message createHelpMessage(CommandManager.ParsedCommandInvocation invocation) {
        return createHelpMessage(invocation.getPrefix(), invocation.getCommandInvocation());
    }

    /**
     * Generates a usage message for this command.
     *
     * @param serverPrefix which prefix should be used in this message?
     * @param aliasToUse   which alias should be used in this message?
     */
    public Message createHelpMessage(String serverPrefix, String aliasToUse) {
        StringBuilder usage = new StringBuilder();
        for (String part : getParameterUsage().split("\n")) {
            usage.append(serverPrefix + aliasToUse + " " + part + "\n");
        }
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
