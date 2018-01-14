/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.moderation;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.sql.MySQL;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

/**
 * Handles the 'getwarn' command.
 * @author Leon Kappes / Lee
 */
public class CommandGetWarn extends CommandHandler {


    public CommandGetWarn() {
        super(new String[]{"warns", "getwarn", "getwarning", "getwarns"}, CommandCategory.MODERATION, new PermissionRequirements(PermissionLevel.WITH_PERMISSION, "command.getwarn"), "Get the warning of a user.", "<Mention>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.getMessage().getMentionedUsers().size() < 1) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("Oh something went wrong!", getParameterUsage()).build()).build();
        }
        MySQL sql = RubiconBot.getMySQL();
        User unmuteuser = parsedCommandInvocation.getMessage().getMentionedUsers().get(0);
        if (!sql.ifWarning(unmuteuser, parsedCommandInvocation.getMessage().getGuild())) {
            return new MessageBuilder().setEmbed(EmbedUtil.info(unmuteuser.getName() + " has 0 warns!", "There are no warns.").build()).build();
        }
        Guild g = parsedCommandInvocation.getMessage().getGuild();
        String Reason = sql.getWarning(unmuteuser, g, "reason");
        User author = g.getMemberById(sql.getWarning(unmuteuser, g, "authorid")).getUser();
        return new MessageBuilder().setEmbed(EmbedUtil.info("Warn of " + unmuteuser.getName(), "Warned by: " + author.getAsMention() + "\nReason: `" + Reason + "`!").build()).build();
    }
}
