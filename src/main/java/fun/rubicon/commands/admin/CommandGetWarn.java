package fun.rubicon.commands.admin;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.MySQL;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.admin
 */
public class CommandGetWarn extends CommandHandler {


    public CommandGetWarn() {
        super(new String[]{"getwarn", "getwarning", "getwarns"}, CommandCategory.MODERATION, new PermissionRequirements(2, "command.getwarn"), "Get The Warning of a User", "getwarn <Mention>");
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
        Guild g = parsedCommandInvocation.invocationMessage.getGuild();
        String Reason = sql.getWarning(unmuteuser, g, "reason");
        User author = g.getMemberById(sql.getWarning(unmuteuser, g, "authorid")).getUser();
        return new MessageBuilder().setEmbed(EmbedUtil.info("Warn of " + unmuteuser.getName(), "Warned by: " + author.getAsMention() + "\nReason: `" + Reason + "`!").build()).build();
    }
}
