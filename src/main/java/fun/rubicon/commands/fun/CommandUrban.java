package fun.rubicon.commands.fun;

import de.foryasee.httprequest.HttpRequest;
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
public class CommandUrban extends CommandHandler {
    public CommandUrban() {
        super(new String[]{"urban"}, CommandCategory.FUN, new PermissionRequirements("command.urban", false, true), "Search for a Term on Urban dictionary", "<Define query>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.getArgs();
        if (args.length < 1)
            return createHelpMessage(parsedCommandInvocation);

        Urban_Thread thread = new Urban_Thread("Urban Thread", parsedCommandInvocation);
        thread.start();

        return null;
    }

    public class Urban_Thread implements Runnable {
        private Thread t;
        private String threadName;
        private CommandManager.ParsedCommandInvocation parsedCommandInvocation;

        Urban_Thread(String name, CommandManager.ParsedCommandInvocation Invocation) {
            threadName = name;
            parsedCommandInvocation = Invocation;
        }

        @Override
        public void run() {
            String[] args = parsedCommandInvocation.getArgs();

            HttpRequest request = new HttpRequest("http://api.urbandictionary.com/v0/define");
            request.addParameter("term", args[0]);
            try {
                RequestResponse response = request.sendGETRequest();

                JSONObject json = (JSONObject) new JSONParser().parse(response.getResponse());
                JSONArray data = (JSONArray) json.get("list");
                String likes = String.valueOf(((JSONObject) data.get(0)).get("thumbs_up"));
                String down = String.valueOf(((JSONObject) data.get(0)).get("thumbs_down"));

                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("Definition of " + args[0], (String) ((JSONObject) data.get(0)).get("permalink"))
                        .setDescription((String) ((JSONObject) data.get(0)).get("definition"))
                        .addField("Example", (String) ((JSONObject) data.get(0)).get("example"), false)
                        .addField("\uD83D\uDC4D", likes, true)
                        .addField("\uD83D\uDC4E", down, true)
                        .setColor(Colors.COLOR_SECONDARY);

                parsedCommandInvocation.getTextChannel().sendMessage(embedBuilder.build()).queue();
            } catch (Exception e) {
                SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), EmbedUtil.error("Error!", "Found no definition.").build(), 10);
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
