package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.entities.Message;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2018
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.fun
 */
public class CommandAsciiText extends CommandHandler {


    public CommandAsciiText() {
        super(new String[]{"ascii", "asciitext", "texttoascii"}, CommandCategory.FUN, new PermissionRequirements("command.ascii", false, true), "Make a Senteces to Ascii Art", "<text>", false);
    }


    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String text = parsedCommandInvocation.getMessage().getContentDisplay().replace(parsedCommandInvocation.getPrefix(), "").replace(parsedCommandInvocation.getCommandInvocation(), "").replace(" ", "+");
        Request req = new Request.Builder()
                .url("http://artii.herokuapp.com/make?text=" + text)
                .build();
        Response response = null;
        try {
            response = new OkHttpClient().newCall(req).execute();
            SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), "```fix\n" + response.body().string() + "```");
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.close();
        return null;
    }
}
