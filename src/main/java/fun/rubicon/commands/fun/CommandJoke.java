package fun.rubicon.commands.fun;

import de.foryasee.httprequest.HttpRequest;
import de.foryasee.httprequest.RequestHeader;
import de.foryasee.httprequest.RequestResponse;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
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
public class CommandJoke extends CommandHandler {

    public CommandJoke() {
        super(new String[]{"joke"}, CommandCategory.FUN, new PermissionRequirements("command.joke", false, true), "Get some shitty Joke", "");

    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        HttpRequest request = new HttpRequest("https://icanhazdadjoke.com/");
        RequestHeader header = new RequestHeader();
        header.addField("Accept","application/json");
        header.addField("User-Agent","RubiconBot (https://github.com/Rubicon-Bot/Rubicon)");
        request.setRequestHeader(header);
        try {
            RequestResponse response = request.sendGETRequest();
            JSONObject json = (JSONObject) new JSONParser().parse(response.getResponse());
            SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(),new EmbedBuilder().setTitle("Joke").setDescription((String) json.get("joke")).setColor(Colors.COLOR_SECONDARY).build());
        } catch (Exception e) {
            e.printStackTrace();
            SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), EmbedUtil.error("Error 404","No joke was found!").build(),30);
        }


        return null;
    }
}