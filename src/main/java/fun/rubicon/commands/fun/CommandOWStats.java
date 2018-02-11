package fun.rubicon.commands.fun;

import com.google.api.client.json.Json;
import de.foryasee.httprequest.HttpRequest;
import de.foryasee.httprequest.RequestResponse;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
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
public class CommandOWStats extends CommandHandler {

    public CommandOWStats() {
        super(new String[]{"overwatch","overwatchstats","owstats"}, CommandCategory.FUN, new PermissionRequirements(PermissionLevel.EVERYONE, "command.owstats"), "Get some Overwatch Stats about a Batteltag", "<platform(pc ,ps4, xbox)> <region(eu, us ,asia)> <batteltag(example: LordLee#21645)");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions){
        if(parsedCommandInvocation.getArgs().length<3)
            return createHelpMessage();

        try {
        HttpRequest request = new HttpRequest("https://ow-api.com/v1/stats/"+ parsedCommandInvocation.getArgs()[0]+"/"+ parsedCommandInvocation.getArgs()[1]+"/"+ parsedCommandInvocation.getArgs()[2].replace("#","-")+"/profile");
            RequestResponse response = request.sendGETRequest();
            JSONObject root = (JSONObject) new JSONParser().parse(response.getResponse());
            JSONObject quick = (JSONObject) root.get("quickPlayStats");
            JSONObject ranked = (JSONObject) root.get("competitiveStats");
            JSONObject quickgames = (JSONObject) quick.get("games");
            JSONObject rankedgames = (JSONObject) ranked.get("games");

            EmbedBuilder builder = new EmbedBuilder()
                    .setAuthor((String)root.get("name"),null,(String)root.get("icon"))
                    .setDescription("**Quickplay:**\n    - KD :"+quick.get("soloKillsAvg")+"\n    - Won Games :"+quickgames.get("won")+"\n    - Played Games :"+quick.get("played")+"**Ranked:**\n    - KD :"+ranked.get("soloKillsAvg")+"\n    - Won Games :"+rankedgames.get("won")+"\n    - Played Games :"+rankedgames.get("played"));

            SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(),builder.build(),40);
        } catch (Exception e) {
            SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), EmbedUtil.error("Player not found!","Is everything correct?").build(),40);
        }


        return null;
    }
}