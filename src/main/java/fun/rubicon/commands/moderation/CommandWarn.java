package fun.rubicon.commands.moderation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.WarnManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.sql.WarnSQL;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandWarn extends CommandHandler {

    public CommandWarn() {
        super(new String[]{"warn", "warns"}, CommandCategory.MODERATION, new PermissionRequirements("command.warn", false, false), "Warn user and let them kick/ban automatically.", "<@User> <reason>\n" +
                "list [@User]\n" +
                "unwarn <@User> <index> (Use `rc!warn list` to get warns of a user.)");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.getArgs().length <= 0) {
            return createHelpMessage();
        }
        switch (parsedCommandInvocation.getArgs()[0]) {
            case "list":
                return warnList(parsedCommandInvocation);
            case "remove":
            case "unwarn":
                return unwarnUser(parsedCommandInvocation);
            default:
                return warnUser(parsedCommandInvocation);
        }
    }

    private Message warnList(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        User warnedUser;

        if (parsedCommandInvocation.getMessage().getMentionedUsers().size() == 0) {
            warnedUser = parsedCommandInvocation.getAuthor();
        } else {
            warnedUser = parsedCommandInvocation.getMessage().getMentionedUsers().get(0);
        }
        return EmbedUtil.message(WarnManager.listWarns(warnedUser, parsedCommandInvocation.getGuild()));
    }

    private Message unwarnUser(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        User warnedUser;

        if (parsedCommandInvocation.getMessage().getMentionedUsers().size() != 1)
            return EmbedUtil.message(EmbedUtil.error("Error!", "You have to mention one user."));
        warnedUser = parsedCommandInvocation.getMessage().getMentionedUsers().get(0);
        int mustHaveLength = 1 + parsedCommandInvocation.getGuild().getMember(warnedUser).getEffectiveName().split(" ").length + 1;
        if (parsedCommandInvocation.getArgs().length != mustHaveLength)
            return createHelpMessage();
        if (!StringUtil.isNumeric(parsedCommandInvocation.getArgs()[mustHaveLength - 1]))
            return createHelpMessage();
        int index = Integer.parseInt(parsedCommandInvocation.getArgs()[mustHaveLength - 1]);
        int userWarns = new WarnSQL().getWarns(warnedUser, parsedCommandInvocation.getGuild()).size();
        if (index > userWarns) {
            return EmbedUtil.message(EmbedUtil.error("Error!", "User has only `" + userWarns + "` warns."));
        }
        WarnManager.removeWarn(warnedUser, parsedCommandInvocation.getGuild(), index - 1);
        return EmbedUtil.message(EmbedUtil.success("Removed Warn", "Successfully removed warn from `" + warnedUser.getName() + "`"));
    }

    private Message warnUser(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        //Warn User
        User warnedUser;
        User executor = parsedCommandInvocation.getAuthor();
        String reason;

        //Get Warned User
        if (parsedCommandInvocation.getMessage().getMentionedUsers().size() != 1)
            return EmbedUtil.message(EmbedUtil.error("Error!", "You have to mention one user."));
        warnedUser = parsedCommandInvocation.getMessage().getMentionedUsers().get(0);

        //Author cannot warn himself
        if (warnedUser == executor)
            return EmbedUtil.message(EmbedUtil.error("Error!", "You cannot warn yourself."));

        //Get Reason
        reason = parsedCommandInvocation.getMessage().getContentDisplay().replace(parsedCommandInvocation.getPrefix() + parsedCommandInvocation.getCommandInvocation() + " @" + parsedCommandInvocation.getGuild().getMember(parsedCommandInvocation.getMessage().getMentionedUsers().get(0)).getEffectiveName() + " ", "");
        WarnManager.addWarn(warnedUser, parsedCommandInvocation.getGuild(), executor, reason);
        return EmbedUtil.message(EmbedUtil.success("Warned user!", "Warned " + warnedUser.getAsMention() + " for `" + reason + "`"));
    }
}
