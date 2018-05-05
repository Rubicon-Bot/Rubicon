package fun.rubicon.commands.fun;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public class CommandFortnite extends CommandHandler {

    public CommandFortnite() {
        super(new String[]{"fortnite"}, CommandCategory.FUN, new PermissionRequirements("fortnite", false, true), "Get Fortnite stats!", "<Platform(pc,xbl,psn)> <UserName>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length < 2)
            return createHelpMessage(invocation);
        Request request = new Request.Builder()
                .url("https://api.fortnitetracker.com/v1/profile/" + invocation.getArgs()[0] + "/" + invocation.getArgs()[1])
                .header("TRN-Api-Key", RubiconBot.getConfiguration().getString("fortnite_key")).build();
        try {
            Response response = new OkHttpClient().newCall(request).execute();
            if(response.code() != 200)
                return message(error(invocation.translate("command.fortnite.error"), invocation.translate("command.fortnite.error.description")));
            JSONObject root = new JSONObject(response.body().string());
            if (root.has("error"))
                return message(error(invocation.translate("command.fortnite.error"), invocation.translate("command.fortnite.error.description")));
            JSONArray lifetime = (JSONArray) root.get("lifeTimeStats");
            String matches = (String) ((JSONObject) lifetime.get(7)).get("value");
            String wins = (String) ((JSONObject) lifetime.get(8)).get("value");
            String kills = (String) ((JSONObject) lifetime.get(10)).get("value");
            String kd = (String) ((JSONObject) lifetime.get(11)).get("value");

            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor(invocation.getArgs()[1],null,"https://data.lucsoft.de/uploads/trhasdfcftuawdfjzwefuigzef.png")
                    .addField(invocation.translate("command.fortnite.matches"), matches, true)
                    .addField(invocation.translate("command.fortnite.wins"), wins, true)
                    .addField(invocation.translate("command.fortnite.kills"), kills, true)
                    .addField(invocation.translate("command.fortnite.kd"), kd, true)
                    .setColor(Colors.COLOR_SECONDARY);
            response.close();

            return message(eb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}