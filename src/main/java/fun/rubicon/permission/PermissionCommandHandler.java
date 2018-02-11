/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.permission;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;

import static fun.rubicon.util.EmbedUtil.*;

/**
 * Handles the 'permission' command which interfaces the permissions system in a discord command.
 *
 * @author tr808axm
 */
public class PermissionCommandHandler extends CommandHandler {
    private static final PermissionRequirements MODIFY_PERMISSIONS = new PermissionRequirements(
            "permissions.modify", false, false),
            LIST_PERMISSIONS = new PermissionRequirements("permissions.list", false, false);

    /**
     * Initializes the command handler.
     */
    protected PermissionCommandHandler() {
        super(new String[]{"permission", "permit", "permissions", "perm", "perms"}, CommandCategory.ADMIN,
                new PermissionRequirements("command.permission", false, false),
                "Allows modifying and listing permissions.",
                "add/remove <user/role/dperm> <userId/roleId> <command>\n" +
                        "list <user/role/dperm> <command>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        try {
            if (invocation.getArgs().length == 0)
                return createHelpMessage(invocation);

            else if (invocation.getArgs()[0].equalsIgnoreCase("show-discord-permissions")) {
                StringBuilder discordPermissions = new StringBuilder("— `ID`  `Name`\n");
                for (net.dv8tion.jda.core.Permission permission : net.dv8tion.jda.core.Permission.values())
                    discordPermissions.append("  \u2022 `").append(String.format("%02d", permission.getOffset())).append("`  ").append(permission.getName()).append("\n");
                return message(info("List of discord permissions", discordPermissions.toString()));

            } else if (invocation.getArgs()[0].equalsIgnoreCase("list")) {
                // check permissions
                if (!LIST_PERMISSIONS.coveredBy(userPermissions))
                    return message(no_permissions());
                // check and parse arguments
                if (invocation.getArgs().length < 3)
                    return message(error("Missing arguments", "Some parameters seem to be missing. Use `" +
                            invocation.getPrefix() + invocation.getCommandInvocation() + "` for usage information."));
                PermissionTarget target = parseTarget(invocation.getGuild(), invocation.getArgs()[1], invocation.getArgs()[2]);
                if (!target.exists())
                    return message(error("Target does not exit", "`" + target.toString() + "` is not on this server!"));

                String permissionsListString;
                List<Permission> permissionList = RubiconBot.sGetPermissionManager().getPermissions(target);
                if (permissionList.size() == 0)
                    permissionsListString = "\n\u2022 *No permissions were set for this target.*";
                else {
                    StringBuilder builder = new StringBuilder();
                    for (Permission permission : permissionList)
                        builder.append("\n\u2022 `").append(permission.toString()).append("`");
                    permissionsListString = builder.toString();
                }
                return message(info("Permission list", target.toString() + "'s permissions:" + permissionsListString));

            } else if (invocation.getArgs()[0].equalsIgnoreCase("add")) {
                // check permission
                if (!MODIFY_PERMISSIONS.coveredBy(userPermissions))
                    return message(no_permissions());
                // check and parse arguments
                if (invocation.getArgs().length < 4)
                    return message(error("Missing arguments", "Some parameters seem to be missing. Use `" +
                            invocation.getPrefix() + invocation.getCommandInvocation() + "` for usage information."));
                PermissionTarget target = parseTarget(invocation.getMessage().getGuild(), invocation.getArgs()[1], invocation.getArgs()[2]);
                if (!target.exists())
                    return message(error("Target does not exit", "`" + target.toString() + "` is not on this server!"));

                return RubiconBot.sGetPermissionManager().addPermission(target, Permission.parse(invocation.getArgs()[3]))
                        ? message(success("Updated permissions", "Successfully added `" +
                        invocation.getArgs()[3] + "` to `" + target.toString() + "`."))
                        : message(error("Entry already exists", "There already is a `" +
                        Permission.parse(invocation.getArgs()[3]).getPermissionString() + "` entry for `" +
                        target.toString() + "` on this guild. Use `" + invocation.getPrefix() +
                        invocation.getCommandInvocation() + " list " + invocation.getArgs()[1] + ' ' + invocation.getArgs()[2] +
                        "` to get a list of permission entries for this target."));

            } else if (invocation.getArgs()[0].equalsIgnoreCase("remove")) {
                // check permission
                if (!MODIFY_PERMISSIONS.coveredBy(userPermissions))
                    return message(no_permissions());
                // check and parse arguments
                if (invocation.getArgs().length < 4)
                    return message(error("Missing arguments", "Some parameters seem to be missing. Use `" +
                            invocation.getPrefix() + invocation.getCommandInvocation() + "` for usage information."));
                PermissionTarget target = parseTarget(invocation.getGuild(), invocation.getArgs()[1], invocation.getArgs()[2]);
                if (!target.exists())
                    return message(error("Target does not exit", "`" + target.toString() + "` is not on this server!"));

                return RubiconBot.sGetPermissionManager().removePermission(target, Permission.parse(invocation.getArgs()[3]))
                        ? message(success("Updated permissions", "Successfully removed `" +
                        invocation.getArgs()[3] + "` from `" + target.toString() + "`."))
                        : message(error("Entry does not exist", "There is no `" + invocation.getArgs()[3] +
                        "` entry for `" + target.toString() + "` on this guild. Use `" + invocation.getPrefix() +
                        invocation.getCommandInvocation() + " list " + invocation.getArgs()[1] + ' ' + invocation.getArgs()[2] +
                        "` to get a list of permission entries for this target."));

            } else {
                throw new IllegalArgumentException("`" + invocation.getArgs()[0] + "` is not a " +
                        "valid argument.");
            }
        } catch (IllegalArgumentException e) {
            return message(error("Invalid arguments", e.getMessage() + " Use `" +
                    invocation.getPrefix() + invocation.getCommandInvocation() + "` for a " + "command manual."));
        }
    }

    /**
     * Parses a {@link PermissionTarget PermissionTarget} out of user arguments.
     *
     * @param guild     the {@link Guild} this permission target is set in.
     * @param typeInput the {@link String} to parse a {@link PermissionTarget.Type} from.
     * @param idInput   the {@link String} to parse an id for the type from.
     * @return the parsed {@link PermissionTarget}.
     * @throws IllegalArgumentException if the inputs can not be resolved.
     */
    private PermissionTarget parseTarget(Guild guild, String typeInput, String idInput) {
        PermissionTarget.Type type;
        switch (typeInput) {
            case "u":
            case "user":
                type = PermissionTarget.Type.USER;
                if (Message.MentionType.USER.getPattern().matcher(idInput).matches())
                    idInput = idInput.substring(2, idInput.length() - 1);
                break;
            case "g":
            case "group":
            case "r":
            case "role":
                type = PermissionTarget.Type.ROLE;
                if (Message.MentionType.ROLE.getPattern().matcher(idInput).matches())
                    idInput = idInput.substring(3, idInput.length() - 1);
                break;
            case "dp":
            case "dperm":
            case "discordpermission":
                type = PermissionTarget.Type.DISCORD_PERMISSION;
                break;
            default:
                throw new IllegalArgumentException("`" + typeInput + "` is not a valid permission target type.");
        }
        long id;
        try {
            id = Long.parseLong(idInput);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("`" + idInput + "` is not a valid " + type.getName() + " id.");
        }
        return new PermissionTarget(guild, type, id);
    }

    @Override
    public Message createHelpMessage(String serverPrefix, String aliasToUse) {
        StringBuilder usage = new StringBuilder();
        for (String part : getParameterUsage().split("\n"))
            usage.append(serverPrefix + aliasToUse + " " + part + "\n");
        return message(info('\'' + aliasToUse + "' command help", getDescription())
                .addField("Aliases", String.join(", ", getInvocationAliases()), false)
                .addField("Usage", usage.toString(), false)
                .addField("Parameters",
                        "`<target-type>`\n" +
                                "Use `user` for user targets, `role` for role targets and `discordpermission` for " +
                                "discord-permission targets.\n\n" +

                                "`<target-id>`\n" +
                                "Mention the target or specify it's id. User the user-id for users, the role id for roles and the " +
                                "permission offset for discord-permissions. Type `" + serverPrefix + aliasToUse + " " +
                                "show-discord-permissions` for a list of discord permissions.\n\n" +

                                "`<permission-node>`\n" +
                                "Id of the permission you want to add. If you want to add a command\n" +
                                "you have to use `command.yourCommand`\n" +
                                "All permission-nodes are displayed next to the commands at our [documentation](http://rubicon.fun)", false));
    }
}
