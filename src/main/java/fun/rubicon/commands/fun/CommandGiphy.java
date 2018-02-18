package fun.rubicon.commands.fun;

import de.foryasee.httprequest.HttpRequest;
import de.foryasee.httprequest.RequestResponse;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.entities.Message;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2018
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.fun
 */
public class CommandGiphy extends CommandHandler {

    public CommandGiphy() {
        super(new String[]{"giphy", "gif",}, CommandCategory.FUN, new PermissionRequirements("command.giphy", false, true), "Search a gif on Giphy and sends it in the channel", "<keyword>", false);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.getArgs();
        if (args.length < 1) {
            return createHelpMessage();
        }
        String query = parsedCommandInvocation.getMessage().getContentDisplay().replace(parsedCommandInvocation.getPrefix() + parsedCommandInvocation.getCommandInvocation(), "");
        HttpRequest request = new HttpRequest("https://api.giphy.com/v1/gifs/search");
        request.addParameter("api_key", Info.GIPHY_TOKEN);
        request.addParameter("q", query);
        try {
            RequestResponse response = request.sendGETRequest();
            JSONObject json = (JSONObject) new JSONParser().parse(response.getResponse());
            JSONArray data = (JSONArray) json.get("data");
            parsedCommandInvocation.getTextChannel().sendMessage((String) ((JSONObject) data.get(0)).get("url")).queue();
        } catch (Exception e) {
            return EmbedUtil.message(EmbedUtil.error("Error!", "Found no gif."));
        }
        return null;
    }
}
