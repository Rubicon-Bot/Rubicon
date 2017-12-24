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
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CommandWarn extends CommandHandler {
    public CommandWarn() {
        super(new String[]{"warn"}, CommandCategory.MODERATION, new PermissionRequirements(PermissionLevel.WITH_PERMISSION, "command.warn"), "Warn a user.", "<User Mention> <reason> ");
    }

    public static void WarnUser(User target, Guild guild, User author, String reason) {
        MySQL sql = RubiconBot.getMySQL();
        sql.createWarning(guild, target, author, reason);
        int oldcase = 0;
        try {
            oldcase = Integer.parseInt(sql.getGuildValue(guild, "cases"));
        } catch (NumberFormatException err) {
            err.printStackTrace();
        }
        String nowcase = String.valueOf(oldcase + 1);
        sql.updateGuildValue(guild, "cases", nowcase);
        if (!sql.getGuildValue(guild, "logchannel").equals("0")) {
            TextChannel ch = guild.getTextChannelById(sql.getGuildValue(guild, "logchannel"));
            ch.sendMessage(EmbedUtil.info("**[CASE " + sql.getGuildValue(guild, "cases") + "]** Warned" + target.getAsMention(), "Warned by:" + author.getAsMention() + "\nReason:" + reason).build()).queue();
        }
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.args.length < 2) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("", "Not enough arguments!\n" + getParameterUsage()).build()).build();
        }
        if (parsedCommandInvocation.invocationMessage.getMentionedUsers().size() < 1) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("", "Please Mention someone!\n" + getParameterUsage()).build()).build();
        }
        User targ = parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0);
        Guild g = parsedCommandInvocation.invocationMessage.getGuild();
        User auth = parsedCommandInvocation.invocationMessage.getAuthor();
        String reas = "";
        for (int i = 1; i < parsedCommandInvocation.args.length; i++) {
            reas += parsedCommandInvocation.args[i] + " ";
        }
        WarnUser(targ, g, auth, reas);
        return new MessageBuilder().setEmbed(EmbedUtil.success("", "I warned " + targ.getAsMention() + " for `" + reas + "`").build()).build();
    }
}
