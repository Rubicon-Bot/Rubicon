package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandRip extends CommandHandler {

    public CommandRip() {
        super(new String[]{"rip", "tombstone"}, CommandCategory.FUN, new PermissionRequirements("rip", false, true), "Creates a tombstone with a custom text.", "<text>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) throws Exception {
        if (invocation.getArgs().length == 0)
            return createHelpMessage();

        String content = invocation.getArgsString();
        for (User user : invocation.getMessage().getMentionedUsers()) {
            content.replace(user.getAsMention(), user.getName());
        }
        String tombstoneUrl = "http://tombstonebuilder.com/generate.php"
                + "?top1=R.I.P"
                + "&top2=" + URLEncoder.encode(content.substring(0, Math.min(25, content.length())), StandardCharsets.UTF_8.toString())
                + "&top3=" + (content.length() > 25 ? URLEncoder.encode(content.substring(25, Math.min(50, content.length())), StandardCharsets.UTF_8.toString()) : "")
                + "&top4=" + (content.length() > 50 ? URLEncoder.encode(content.substring(50, Math.min(75, content.length())), StandardCharsets.UTF_8.toString()) : "");
        InputStream inputStream = new URL(tombstoneUrl).openStream();
        if(invocation.getMember().hasPermission(invocation.getTextChannel(), Permission.MESSAGE_ATTACH_FILES)) {
            invocation.getTextChannel().sendFile(inputStream, "RIP.png").queue();
        }
        return null;
    }
}
