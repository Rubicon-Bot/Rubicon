package fun.rubicon.commands.fun;

import de.foryasee.httprequest.HttpRequest;
import de.foryasee.httprequest.RequestResponse;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2018
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.fun
 */
public class CommandAscciText extends CommandHandler{
    public CommandAscciText() {
        super(new String[]{"asciitext"}, CommandCategory.FUN, new PermissionRequirements(0,"command.asciitext"), "Make some text to a fancy Ascii Text", "<Text>", false );
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.getArgs().length<1)
            createHelpMessage(parsedCommandInvocation);

        String text = parsedCommandInvocation.getMessage().getContentDisplay().replace(parsedCommandInvocation.getPrefix() + parsedCommandInvocation.getCommandInvocation(), "");

        HttpRequest request = new HttpRequest("https://artii.herokuapp.com/make");
                request.addParameter("text",text);
        try {
            RequestResponse response = request.sendGETRequest();
            parsedCommandInvocation.getTextChannel().sendMessage("```\n"++"```").queue();
        } catch (Exception e) {
            return EmbedUtil.message(EmbedUtil.error("Error!", "Couldn't build text!"));
        }

        return null;
    }
}
