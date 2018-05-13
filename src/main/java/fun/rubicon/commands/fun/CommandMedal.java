package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandMedal extends CommandHandler {

    public CommandMedal() {
        super(new String[]{"medal"}, CommandCategory.FUN, new PermissionRequirements("medal", false, true), "Creates a custom medal for you.", "<text>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) throws Exception {
        if (invocation.getArgs().length == 0)
            return createHelpMessage();
        if (!RubiconUser.fromUser(invocation.getAuthor()).isPremium())
            return message(noPremium());

        String content = invocation.getArgsString();
        for (User user : invocation.getMessage().getMentionedUsers()) {
            content.replace(user.getAsMention(), user.getName());
        }
        String tombstoneUrl = "http://www.getamedal.com/generate.php"
                + "?top1=" + URLEncoder.encode(content.substring(0, Math.min(23, content.length())), StandardCharsets.UTF_8.toString())
                + "&top2=" + (content.length() > 23 ? URLEncoder.encode(content.substring(23, Math.min(48, content.length())), StandardCharsets.UTF_8.toString()) : "")
                + "&top3=" + (content.length() > 48 ? URLEncoder.encode(content.substring(48, Math.min(73, content.length())), StandardCharsets.UTF_8.toString()) : "")
                + "&top4=" + (content.length() > 73 ? URLEncoder.encode(content.substring(73, Math.min(98, content.length())), StandardCharsets.UTF_8.toString()) : "");
        InputStream inputStream = new URL(tombstoneUrl).openStream();
        if (invocation.getMember().hasPermission(invocation.getTextChannel(), Permission.MESSAGE_ATTACH_FILES)) {
            invocation.getTextChannel().sendFile(inputStream, "MEDAL.png").queue();
        }
        return null;
    }
}
