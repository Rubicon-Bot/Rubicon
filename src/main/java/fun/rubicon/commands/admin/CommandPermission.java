/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.admin;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.core.permission.PermissionManager;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class CommandPermission extends Command {

    public CommandPermission(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        Role role = null;
        Member member = null;
        if (e.getMessage().getMentionedUsers().size() == 1) {
            member = e.getGuild().getMember(e.getMessage().getMentionedUsers().get(0));
        }
        if (e.getMessage().getMentionedRoles().size() == 1) {
            role = e.getMessage().getMentionedRoles().get(0);
        }

        if (member == null && role == null) {
            sendErrorMessage("You have to mention one user or role!");
            return;
        }
        if (args.length < 2) {
            sendErrorMessage("You have to use more arguments!");
            return;
        }

        if (member != null) {
            int nameLength = member.getEffectiveName().split(" ").length;
            String operator;
            String command = null;
            try {
                operator = args[nameLength].toLowerCase();
            } catch (ArrayIndexOutOfBoundsException ex) {
                sendUsageMessage();
                return;
            }
            try {
                command = args[nameLength + 1].toLowerCase();
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }

            switch (operator) {
                case "add":
                    if (isCommandAvailable(command)) {
                        PermissionManager authorManager = new PermissionManager(e.getMember(), CommandHandler.getCommandFromName(command));
                        if(authorManager.hasPermission()) {
                            PermissionManager memberManager = new PermissionManager(member, CommandHandler.getCommandFromName(command));
                            if(!memberManager.hasPermission()) {
                                memberManager.addPermissions(command);
                                EmbedBuilder builder = new EmbedBuilder();
                                builder.setAuthor("Permissions - " + member.getEffectiveName(), null, member.getUser().getEffectiveAvatarUrl());
                                builder.setColor(Colors.COLOR_NOT_IMPLEMENTED);
                                builder.setDescription("Successfully added `" + command + "`");
                                e.getTextChannel().sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(3, TimeUnit.MINUTES));
                            } else {
                                sendErrorMessage("User already has this permission!");
                                return;
                            }
                        } else {
                            sendErrorMessage("You do not have permissions to give the command to others!");
                            return;
                        }
                    } else {
                        sendErrorMessage("Command does not exist!");
                        return;
                    }
                    break;
                case "remove":
                    if (isCommandAvailable(command)) {
                        PermissionManager authorManager = new PermissionManager(e.getMember(), CommandHandler.getCommandFromName(command));
                        if(authorManager.hasPermission()) {
                            PermissionManager memberManager = new PermissionManager(member, CommandHandler.getCommandFromName(command));
                            if(memberManager.hasPermission()) {
                                memberManager.removePermission(command);
                                EmbedBuilder builder = new EmbedBuilder();
                                builder.setAuthor("Permissions - " + member.getEffectiveName(), null, member.getUser().getEffectiveAvatarUrl());
                                builder.setColor(Colors.COLOR_NOT_IMPLEMENTED);
                                builder.setDescription("Successfully removed `" + command + "`");
                                e.getTextChannel().sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(3, TimeUnit.MINUTES));
                            } else {
                                sendErrorMessage("User hasn't this permission!");
                                return;
                            }
                        } else {
                            sendErrorMessage("You do not have permissions to remove the command from others!");
                            return;
                        }
                    } else {
                        sendErrorMessage("Command does not exist!");
                        return;
                    }
                    break;
                case "list":
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setAuthor(member.getEffectiveName() + "'s Commands", null, member.getUser().getEffectiveAvatarUrl());
                    builder.setDescription("Loading Commands...");
                    builder.setColor(Colors.COLOR_NOT_IMPLEMENTED);
                    Message message = e.getTextChannel().sendMessage(builder.build()).complete();
                    builder.setDescription(generatePermissionList(member));
                    builder.setFooter("Allowed Commands: " + getAmountOfAllowedCommands(member), null);
                    message.editMessage(builder.build()).queue(msg -> msg.delete().queueAfter(defaultDeleteSeconds, TimeUnit.SECONDS));
                    break;
                default:
                    sendUsageMessage();
            }
        }
    }

    private String generatePermissionList(Member member) {
        PermissionManager perm = new PermissionManager(member, this);
        String allPermissions = perm.getAllAllowedCommands();
        String[] arr = allPermissions.split(",");
        String res = "";
        for (String anArr : arr) {
            res += ":small_blue_diamond: **" + anArr + "**\n";
        }
        return res;
    }

    private boolean isCommandAvailable(String cmd) {
        try {
            if (CommandHandler.getCommands().get(cmd) != null || RubiconBot.getCommandManager().getCommandAssociations().containsKey(cmd)) {
                return true;
            }
        } catch (NullPointerException ex) {
            Logger.debug("Command not found");
        }
        return false;
    }

    private int getAmountOfAllowedCommands(Member member) {
        PermissionManager perm = new PermissionManager(member, this);
        String allPermissions = perm.getAllAllowedCommands();
        String[] arr = allPermissions.split(",");
        return arr.length;
    }

    @Override
    public String getDescription() {
        return "Manages permissions of a user or a role.";
    }

    @Override
    public String getUsage() {
        return "<@User> <add/remove> <command>\n" +
                "<@User> <list>";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }
}
