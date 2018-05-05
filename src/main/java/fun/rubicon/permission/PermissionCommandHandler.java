/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.permission;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.util.Colors;
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        super(new String[]{"permissions", "permit", "permission", "perm", "perms"}, CommandCategory.ADMIN,
                new PermissionRequirements("permission", false, false),
                "Allows modifying and listing permissions.",
                "allow/deny <@User/@Role/dp:id> <command-permission>\n" +
                        "list <@User/@Role/dp:id> <command>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        try {
            Message message = invocation.getMessage();
            if (invocation.getArgs().length == 0)
                return createHelpMessage(invocation);

            //Define subcommand
            String subCommand = invocation.getArgs()[0];

            if (subCommand.equalsIgnoreCase("show-discord-permissions") || subCommand.equalsIgnoreCase("discord-permissions") || subCommand.equalsIgnoreCase("dp")) {
                StringBuilder discordPermissions = new StringBuilder("— `ID`  `" + invocation.translate("command.perm.dp.name") + "\n");
                for (net.dv8tion.jda.core.Permission permission : net.dv8tion.jda.core.Permission.values())
                    discordPermissions.append("  \u2022 `").append(String.format("%02d", permission.getOffset())).append("`  ").append(permission.getName()).append("\n");
                return message(info(invocation.translate("command.perm.dp.title"), discordPermissions.toString()));
            }

            if (invocation.getArgs().length < 2)
                return createHelpMessage();

            User mentionedUser = null;
            Role mentionedRole = null;
            String discordPermission = null;
            String targetString = invocation.getArgs()[1];
            if (message.getMentionedUsers().size() == 1)
                mentionedUser = message.getMentionedUsers().get(0);
            else if (message.getMentionedRoles().size() == 1)
                mentionedRole = message.getMentionedRoles().get(0);
            else
                discordPermission = targetString;

            PermissionTarget target;
            if (discordPermission != null)
                target = parseDiscordPermissionTarget(invocation.getGuild(), discordPermission);
            else
                target = mentionedRole == null ? new PermissionTarget(invocation.getGuild(), PermissionTarget.Type.USER, mentionedUser.getIdLong()) : new PermissionTarget(invocation.getGuild(), PermissionTarget.Type.ROLE, mentionedRole.getIdLong());
            if (!target.exists())
                return message(error(invocation.translate("command.perm.misstarget.title"), "`" + target.toString() + "` " + invocation.translate("command.perm.misstarget.desc")));

            if (subCommand.equalsIgnoreCase("list")) {
                //Check Permissions
                if (!LIST_PERMISSIONS.coveredBy(userPermissions))
                    return message(no_permissions());
                List<CommandHandler> filteredCommandList = RubiconBot.getCommandManager().getCommandAssociations().values().stream().filter(commandHandler -> commandHandler.getCategory() != CommandCategory.BOT_OWNER).collect(Collectors.toList());
                List<Permission> permissionEntries = RubiconBot.sGetPermissionManager().getPermissions(target);
                StringBuilder deniedPermissionString = new StringBuilder();
                StringBuilder allowedPermissionString = new StringBuilder();
                ArrayList<CommandHandler> alreadyAdded = new ArrayList<>();
                for (CommandHandler commandHandler : filteredCommandList) {
                    if (alreadyAdded.contains(commandHandler))
                        continue;
                    List<Permission> commandPermissions = permissionEntries.stream().filter(permission -> permission.getPermissionString().replaceFirst("!", "").equalsIgnoreCase(commandHandler.getPermissionRequirements().getRequiredPermissionNode())).collect(Collectors.toList());
                    if (commandPermissions.size() == 0) {
                        if (!commandHandler.getPermissionRequirements().isDefault()) {
                            deniedPermissionString.append("`").append(commandHandler.getInvocationAliases()[0]).append("` ");
                            alreadyAdded.add(commandHandler);
                            continue;
                        }
                    } else {
                        if (commandPermissions.get(0).isNegated()) {
                            deniedPermissionString.append("`").append(commandHandler.getInvocationAliases()[0]).append("` ");
                            alreadyAdded.add(commandHandler);
                            continue;
                        }
                    }
                    allowedPermissionString.append("`" + commandHandler.getInvocationAliases()[0] + "` ");
                    alreadyAdded.add(commandHandler);
                }
                EmbedBuilder listBuilder = new EmbedBuilder();
                listBuilder.setColor(Colors.COLOR_SECONDARY);
                listBuilder.setTitle(":information_source: " + invocation.translate("command.perm.list.title"));
                listBuilder.setDescription(invocation.translate("command.perm.list.desc") + " " + target.toString());
                listBuilder.addField(invocation.translate("command.perm.list.allowed"), allowedPermissionString.toString(), true);
                listBuilder.addField(invocation.translate("command.perm.list.denied"), deniedPermissionString.toString(), true);
                return message(listBuilder);
            }

            if (invocation.getArgs().length < 3)
                return createHelpMessage();
            if (!MODIFY_PERMISSIONS.coveredBy(userPermissions))
                return message(no_permissions());

            String permissionString = invocation.getArgs()[2];

            if (subCommand.equalsIgnoreCase("allow") || subCommand.equalsIgnoreCase("add")) {
                Permission permission = Permission.parse(permissionString);
                if (RubiconBot.sGetPermissionManager().hasPermission(target, permission, true)) {
                    for (Permission p : RubiconBot.sGetPermissionManager().getPermissions(target)) {
                        if(p.getPermissionString().contains(permissionString)) {
                            if (!p.isNegated()) {
                                return message(error(invocation.translate("command.perm.exist.title"), String.format(invocation.translate("command.perm.exist.desc"),
                                        Permission.parse(permissionString).getPermissionString(), target.toString(), invocation.getPrefix() + invocation.getCommandInvocation() + " list " + (targetString))));
                            } else {
                                RubiconBot.sGetPermissionManager().removePermission(target, permission.setNegated(true));
                                return message(success(invocation.translate("command.perm.updated.title"), String.format(invocation.translate("command.perm.allow.desc"), permissionString, target.toString())));
                            }
                        }
                    }
                } else {
                    RubiconBot.sGetPermissionManager().addPermission(target, Permission.parse(permissionString));
                    return message(success(invocation.translate("command.perm.updated.title"), String.format(invocation.translate("command.perm.allow.desc"), permissionString, target.toString())));
                }
                // Check Permissions
                return RubiconBot.sGetPermissionManager().addPermission(target, Permission.parse(permissionString))
                        ? message(success(invocation.translate("command.perm.updated.title"), String.format(invocation.translate("command.perm.allow.desc"), permissionString, target.toString())))
                        : message(error(invocation.translate("command.perm.exist.title"), String.format(invocation.translate("command.perm.exist.desc"),
                        Permission.parse(permissionString).getPermissionString(), target.toString(), invocation.getPrefix() + invocation.getCommandInvocation() + " list " + (targetString))));

            } else if (subCommand.equalsIgnoreCase("deny") || subCommand.equalsIgnoreCase("remove")) {
                Permission permission = Permission.parse(permissionString);
                if (RubiconBot.sGetPermissionManager().hasPermission(target, permission, true)) {
                    for (Permission p : RubiconBot.sGetPermissionManager().getPermissions(target)) {
                        if(p.getPermissionString().contains(permissionString)) {
                            if (!p.isNegated()) {
                                RubiconBot.sGetPermissionManager().removePermission(target, permission);
                                return message(success(invocation.translate("command.perm.updated.title"), String.format(invocation.translate("command.perm.denied.desc"), permissionString, target.toString())));
                            } else {
                                return message(error(invocation.translate("command.perm.exist.title"), String.format(invocation.translate("command.perm.exist.desc"),
                                        Permission.parse(permissionString).getPermissionString(), target.toString(), invocation.getPrefix() + invocation.getCommandInvocation() + " list " + (targetString))));
                            }
                        }
                    }
                } else {
                    RubiconBot.sGetPermissionManager().addPermission(target, Permission.parse("!" + permissionString));
                    return message(success(invocation.translate("command.perm.updated.title"), String.format(invocation.translate("command.perm.denied.desc"), permissionString, target.toString())));
                }
            }
            return null;
        } catch (IllegalArgumentException e) {
            return createHelpMessage();
        }
    }

    /**
     * Parses a {@link net.dv8tion.jda.core.Permission Discord Permission} out of user input string.
     *
     * @param input the user input
     * @return the parsed {@link PermissionTarget}.
     */
    private PermissionTarget parseDiscordPermissionTarget(Guild guild, String input) throws IllegalArgumentException {
        String[] splitted = input.split(":");
        if (splitted.length == 1)
            throw new IllegalArgumentException("`" + input + "` is not a valid permission id.");
        if (!StringUtil.isNumeric(splitted[1]))
            throw new IllegalArgumentException("`" + input + "` must be a numeric parameter.");
        int id = Integer.parseInt(splitted[1]);
        return new PermissionTarget(guild, PermissionTarget.Type.DISCORD_PERMISSION, id);
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
                        "`command-permission`\n" +
                                "Permission of the command. If you want to add a command\n" +
                                "you have to use `yourCommand`\n" +
                                "All command-permissions are displayed next to the commands at our [documentation](https://rubicon.fun)", false));
    }
}