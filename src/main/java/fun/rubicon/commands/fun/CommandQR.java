package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class CommandQR extends CommandHandler {
    public CommandQR() {
        super(new String[] {"qr", "qrcode"}, CommandCategory.FUN, new PermissionRequirements("qr", false, true), "Easily generate QR codes (If your code does not work please report this to Google, they have made the api", "<text>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if(invocation.getArgs().length == 0)
            return createHelpMessage();
        InputStream image = null;
        try {
            image = new URL("https://chart.googleapis.com/chart?cht=qr&chl=" + invocation.getArgsString().replace(" ", "+") + "&choe=UTF-8&chld=L&chs=250x250").openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SafeMessage.sendFile(invocation.getTextChannel(), new MessageBuilder().setContent(invocation.translate("command.qr.sent")).build(), image);
        return null;
    }
}
