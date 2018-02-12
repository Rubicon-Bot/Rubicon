package fun.rubicon.commands.fun;

import com.google.api.client.json.Json;
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
public class CommandOWStats extends CommandHandler {

    public CommandOWStats() {
        super(new String[]{"overwatch","overwatchstats","owstats"}, CommandCategory.FUN, new PermissionRequirements("command.overwatch", false, true), "Get some Overwatch Stats about a Batteltag", "<region(eu/us)> <batteltag(example: LordLee#21645)");

    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if(parsedCommandInvocation.getArgs().length<2)
            return createHelpMessage();
        new Ow_Thread("Owstats",parsedCommandInvocation).start();


        return null;
    }

    public class Ow_Thread implements Runnable {
        private Thread t;
        private String threadName;
        private CommandManager.ParsedCommandInvocation parsedCommandInvocation;

        Ow_Thread(String name, CommandManager.ParsedCommandInvocation Invocation) {
            threadName = name;
            parsedCommandInvocation = Invocation;
        }

        @Override
        public void run() {
            try {
                HttpRequest request = new HttpRequest("https://owapi.net/api/v3/u/"+ parsedCommandInvocation.getArgs()[1].replace("#","-")+"/stats");
                request.setRequestHeader(new RequestHeader().addField("User-Agent","RubiconBot"));
                RequestResponse response = request.sendGETRequest();
                JSONObject reg = (JSONObject) new JSONParser().parse(response.getResponse());
                JSONObject plat = (JSONObject) reg.get(parsedCommandInvocation.getArgs()[0]);
                JSONObject root = (JSONObject) plat.get("stats");
                JSONObject quick = (JSONObject) root.get("quickplay");
                JSONObject ranked = (JSONObject) root.get("competitive");


                EmbedBuilder builder = new EmbedBuilder()
                        .setAuthor((String)root.get("name"),null,(String)root.get("icon"))
                        .setDescription("**Quickplay:**\n    - KD :"+((JSONObject)quick.get("game_stats")).get("kpd")+"\n    - Won Games :"+((JSONObject)quick.get("overall_stats")).get("wins")+"\n    - Played Games :"+((JSONObject)quick.get("overall_stats")).get("games")+"\n**Ranked:**\n    - KD :"+((JSONObject)ranked.get("game_stats")).get("kpd")+"\n    - Won Games :"+((JSONObject)ranked.get("overall_stats")).get("wins")+"\n    - Played Games :"+((JSONObject)ranked.get("overall_stats")).get("games")).setColor(Colors.COLOR_SECONDARY);

                SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(),builder.build(),40);
            } catch (Exception e) {
                e.printStackTrace();
                SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), EmbedUtil.error("Player not found!","Is everything correct?").build(),40);
            }
        }

        public void start() {
            if (t == null) {
                t = new Thread(this, threadName);
                t.start();
            }
        }

    }

}