package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public class CommandOverwatch extends CommandHandler {

    public CommandOverwatch() {
        super(new String[]{"overwatch"}, CommandCategory.FUN, new PermissionRequirements("overwatch", false, true), "Get Overwatch stats!", "<PLATFORM(pc / ps4 /...)> <REGION(eu / us / kr)> <BATTLETAG>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length < 3)
            return createHelpMessage();
        new Ow_Thread("Owstats", invocation).start();

        return null;
    }

    private class Ow_Thread implements Runnable {
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
                Request request = new Request.Builder()
                        .url("https://owapi.net/api/v3/u/" + parsedCommandInvocation.getArgs()[2].replace("#", "-") + "/stats?platform=" + parsedCommandInvocation.getArgs()[0])
                        .addHeader("User-Agent", "RubiconBot")
                        .build();
                Response response = new OkHttpClient().newCall(request).execute();
                JSONObject reg = new JSONObject(response.body().string());
                JSONObject plat = (JSONObject) reg.get(parsedCommandInvocation.getArgs()[1]);
                JSONObject root = (JSONObject) plat.get("stats");
                JSONObject quick = (JSONObject) root.get("quickplay");
                JSONObject overall = (JSONObject) quick.get("overall_stats");
                JSONObject game = (JSONObject) quick.get("game_stats");
                JSONObject ranked = (JSONObject) root.get("competitive");
                JSONObject rankover = (JSONObject) ranked.get("overall_stats");
                response.close();

                EmbedBuilder builder = new EmbedBuilder()
                        .setColor(Colors.COLOR_SECONDARY)
                        .setAuthor(parsedCommandInvocation.getArgs()[2], null, "http://www.stickpng.com/assets/images/586273b931349e0568ad89df.png")
                        .setThumbnail(overall.getString("avatar"))
                        .addField(parsedCommandInvocation.translate("command.overwatch.level"), String.valueOf(overall.get("level")), true)
                        .addField(parsedCommandInvocation.translate("command.overwatch.wins"), String.valueOf(overall.get("wins")), true)
                        .addField(parsedCommandInvocation.translate("command.overwatch.kd"), String.valueOf(game.get("kpd")), true)
                        .addField(parsedCommandInvocation.translate("command.overwatch.death"), String.valueOf(Math.round(game.getInt("deaths"))), true)
                        .addField(parsedCommandInvocation.translate("command.overwatch.kills"), String.valueOf(Math.round(game.getInt("solo_kills"))), true)
                        .addField(parsedCommandInvocation.translate("command.overwatch.medals"), String.valueOf(Math.round(game.getInt("medals_gold"))), true);
                if (String.valueOf(rankover.get("comprank")).equals("null"))
                    builder.addField(parsedCommandInvocation.translate("command.overwatch.comp"), "Unranked", true);
                else
                    builder.addField(parsedCommandInvocation.translate("command.overwatch.comp"), String.valueOf(rankover.get("comprank")), true);
                builder.addField(parsedCommandInvocation.translate("command.overwatch.cards"), String.valueOf(Math.round(game.getInt("cards"))), true);
                SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), builder.build(), 40);
            } catch (Exception ignored) {
                SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), error(parsedCommandInvocation.translate("command.overwatch.error"), parsedCommandInvocation.translate("command.overwatch.error.title")).build(), 20);
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