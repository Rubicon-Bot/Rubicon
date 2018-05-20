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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */public class CommandRainbow extends CommandHandler {

    public CommandRainbow(){
        super(new String[]{"rainbow","rainbowsixsiege","r6"}, CommandCategory.FUN,new PermissionRequirements("rainbow",false,true),"Get Rainbow Six Siege stats","<username> <uplay/xbox/playstation>");
    }
    
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        //https://api.r6stats.com/api/v1/players/ForYaSee/?platform=uplay
        if(invocation.getArgs().length<2)
            return createHelpMessage(invocation);

        new R6_Thread("R6Stats",invocation).start();


        return null;
    }

    private class R6_Thread implements Runnable {
        private Thread t;
        private String threadName;
        private CommandManager.ParsedCommandInvocation parsedCommandInvocation;

        R6_Thread(String name, CommandManager.ParsedCommandInvocation Invocation) {
            threadName = name;
            parsedCommandInvocation = Invocation;
        }

        @Override
        public void run() {
            try {
                Request request = new Request.Builder()
                        .url("https://api.r6stats.com/api/v1/players/" + parsedCommandInvocation.getArgs()[0] + "?platform=" + parsedCommandInvocation.getArgs()[1].toLowerCase())
                        .addHeader("User-Agent", "RubiconBot")
                        .build();
                Response response = new OkHttpClient().newCall(request).execute();
                JSONObject reg = (JSONObject) new JSONObject(response.body().string()).get("player");
                JSONObject root = (JSONObject) reg.get("stats");
                JSONObject quick = (JSONObject) root.get("casual");
                JSONObject level = (JSONObject) root.get("progression");
                response.close();

                EmbedBuilder builder = new EmbedBuilder()
                        .setColor(Colors.COLOR_SECONDARY)
                        .setAuthor(parsedCommandInvocation.getArgs()[0], null, "http://emblemsbf.com/img/90869.jpg")
                        .addField(parsedCommandInvocation.translate("command.overwatch.level"), String.valueOf(level.get("level")), true)
                        .addField(parsedCommandInvocation.translate("command.overwatch.wins"), String.valueOf(quick.get("wins")), true)
                        .addField(parsedCommandInvocation.translate("command.overwatch.kd"), String.valueOf(quick.get("kd")), true)
                        .addField(parsedCommandInvocation.translate("command.overwatch.death"), String.valueOf(quick.getInt("deaths")), true)
                        .addField(parsedCommandInvocation.translate("command.overwatch.kills"), String.valueOf(quick.getInt("kills")), true)
                        .addField(parsedCommandInvocation.translate("command.r6.playtime"), String.valueOf((Math.round(quick.getInt("playtime"))/60)/60)+"h", true);
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