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

/**
 * Handles a command.
 *
 * @author tr808axm
 */
public abstract class CommandHandler {
    private final String[] invocationAliases;
    private final CommandCategory category;
    private final PermissionRequirements permissionRequirements;
    private final String description;
    private final String usage;

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
     * @param usage                  the usage message.
     */
    protected CommandHandler(String[] invocationAliases, CommandCategory category,
                             PermissionRequirements permissionRequirements, String description, String usage) {
        this.invocationAliases = invocationAliases;
        this.category = category;
        this.permissionRequirements = permissionRequirements;
        this.description = description;
        this.usage = usage;
    }

    /**
     * Checks permission, safely calls the execute method and ensures response.
     *
     * @param parsedCommandInvocation the parsed command invocation.
     * @return a response that will be sent and deleted by the caller.
     */
    public Message call(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        UserPermissions userPermissions = new UserPermissions(parsedCommandInvocation.invocationMessage.getAuthor(),
                parsedCommandInvocation.invocationMessage.getGuild());
        // check permission
        if (permissionRequirements.coveredBy(userPermissions)) {
            // execute command
            try {
                return execute(parsedCommandInvocation, userPermissions);
            } catch (Exception e) { // catch exceptions in command and provide an answer
                Logger.error("Unknown error during the execution of the '" + parsedCommandInvocation.invocationCommand + "' command. ");
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
     * @return the usage message of this command.
     */
    public String getUsage() {
        return usage;
    }
}
