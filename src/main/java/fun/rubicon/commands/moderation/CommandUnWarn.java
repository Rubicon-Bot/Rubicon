/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.moderation;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.MySQL;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class CommandUnWarn extends CommandHandler {

    public CommandUnWarn() {
        super(new String[]{"unwarn", "pardon"}, CommandCategory.MODERATION, new PermissionRequirements(PermissionLevel.WITH_PERMISSION, "command.unwarn"), "Unwarns a user!", "<Mention>");
    }


    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.invocationMessage.getMentionedUsers().size() < 1) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("Oh something went wrong!", getUsage()).build()).build();
        }
        MySQL sql = RubiconBot.getMySQL();
        User unmuteuser = parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0);
        if (!sql.ifWarning(unmuteuser, parsedCommandInvocation.invocationMessage.getGuild())) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("Oh " + unmuteuser.getName() + " is not warned!", "warn <User Mention> <reason>").build()).build();
        }
        sql.deleteWarning(unmuteuser, parsedCommandInvocation.invocationMessage.getGuild());
        return new MessageBuilder().setEmbed(EmbedUtil.success("", "Unwarned User " + unmuteuser.getAsMention()).build()).build();
    }
}
