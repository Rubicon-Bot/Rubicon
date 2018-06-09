package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CommandMotel extends CommandHandler {

    public CommandMotel() {
        super(new String[] {"motel", "motelsign"}, CommandCategory.FUN, new PermissionRequirements("Motel", false, true), "Creates a custom motel-sign for you", "<text>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) throws Exception {
        if(invocation.getArgs().length == 0)
            return createHelpMessage();

        String content = invocation.getArgsString();
        for (User user : invocation.getMessage().getMentionedUsers()) {
            content.replace(user.getAsMention(), user.getName());
        }
        String motelSign = "http://www.custommotelsign.com/generate.php"
                + "?line1=" + URLEncoder.encode(content.substring(0, Math.min(15, content.length())), StandardCharsets.UTF_8.toString())
                + "&line2=" + (content.length() > 15 ? URLEncoder.encode(content.substring(15, Math.min(30, content.length())), StandardCharsets.UTF_8.toString()) : "")
                + "&line3=" + (content.length() > 30 ? URLEncoder.encode(content.substring(30, Math.min(45, content.length())), StandardCharsets.UTF_8.toString()) : "")
                + "&line4=" + (content.length() > 45 ? URLEncoder.encode(content.substring(45, Math.min(60, content.length())), StandardCharsets.UTF_8.toString()) : "");

        InputStream stream = new URL(motelSign).openStream();

        if (invocation.getMember().hasPermission(invocation.getTextChannel(), Permission.MESSAGE_ATTACH_FILES)) {
            invocation.getTextChannel().sendFile(stream, "MOTEL.png").queue();
        }

        SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.motel.success.title"), invocation.translate("command.motel.success.description"))));

        return null;
    }
}