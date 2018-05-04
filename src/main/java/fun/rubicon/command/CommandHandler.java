/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.command;

import fun.rubicon.RubiconBot;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;

/**
 * Handles a command.
 *
 * @author tr808axm
 */
public abstract class CommandHandler extends EmbedUtil {
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
    public CommandHandler(String[] invocationAliases, CommandCategory category,
                          PermissionRequirements permissionRequirements, String description, String parameterUsage) {
        this.invocationAliases = invocationAliases;
        this.category = category;
        this.permissionRequirements = permissionRequirements;
        this.description = description;
        this.parameterUsage = parameterUsage;
    }

    public CommandHandler(String[] invocationAliases, CommandCategory category,
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
            return new MessageBuilder().setEmbed(info(parsedCommandInvocation.translate("command.disabled"), parsedCommandInvocation.translate("command.disabled.description")).build()).build();
        }
        UserPermissions userPermissions = new UserPermissions(parsedCommandInvocation.getMessage().getAuthor(),
                parsedCommandInvocation.getMessage().getGuild());
        //Check for Rubicon Permissions
        if (!parsedCommandInvocation.getGuild().getMember(RubiconBot.getSelfUser()).hasPermission(Permission.MESSAGE_EMBED_LINKS)) {
            if (parsedCommandInvocation.getGuild().getMember(RubiconBot.getSelfUser()).hasPermission(parsedCommandInvocation.getTextChannel(),Permission.MESSAGE_WRITE)) {
                SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), parsedCommandInvocation.translate("permissions.links"));
            } else {
                try {
                    parsedCommandInvocation.getAuthor().openPrivateChannel().complete().sendMessage(parsedCommandInvocation.translate("permissions.links")).queue();
                } catch (Exception ignored) {
                }
            }
        }
            // check permission
            if (permissionRequirements.coveredBy(userPermissions)) {
                // execute command
                try {
                    return execute(parsedCommandInvocation, userPermissions);
                } catch (Exception e) { // catch exceptions in command and provide an answer
                    Logger.error("Unknown error during the execution of the '" + parsedCommandInvocation.getCommandInvocation() + "' command. ");
                    Logger.error(e);
                    return new MessageBuilder().setEmbed(new EmbedBuilder()
                            .setAuthor(parsedCommandInvocation.translate("error.internal"), null, RubiconBot.getSelfUser().getEffectiveAvatarUrl())
                            .setDescription(parsedCommandInvocation.translate("error.internal.description")+" ```" + e.getMessage() + "```")
                            .setColor(Colors.COLOR_ERROR)
                            .setFooter(RubiconBot.getNewTimestamp(), null)
                            .build()).build();
                }
            } else
                // respond with 'no-permission'-message
                return new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setAuthor(parsedCommandInvocation.translate("permissions"), null, RubiconBot.getSelfUser().getEffectiveAvatarUrl())
                        .setDescription(parsedCommandInvocation.translate("permissions.description"))
                        .setColor(Colors.COLOR_NO_PERMISSION)
                        .setFooter(RubiconBot.getNewTimestamp(), null)
                        .build()).build();
    }

    /**
     * Method to be implemented by actual command handlers.
     *
     * @param invocation      the command arguments with prefix and command head removed.
     * @param userPermissions an object to query the invoker's permissions.
     * @return a response that will be sent and deleted by the caller.
     */
    protected abstract Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) throws Exception;

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
     * Permissions of the command executer
     *
     * @return UserPermissions
     */


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
        return message(info('\'' + aliasToUse + "' command help", getDescription())
                .addField("Aliases", String.join(", ", getInvocationAliases()), false)
                .addField("Usage", usage.toString(), false));
    }


}
