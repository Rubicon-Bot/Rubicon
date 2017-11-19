package fun.rubicon.commands.fun;

import de.foryasee.httprequest.HttpRequest;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.JSONObject;

public class CommandJoke extends Command {

    /**
     * Rubicon Discord bot
     *
     * @author Yannick Seeger / ForYaSee
     * @copyright Rubicon Dev Team 2017
     * @license MIT License <http://rubicon.fun/license>
     * @package fun.rubicon.commands.fun
     */

    public CommandJoke(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        HttpRequest request = new HttpRequest("http://api.icndb.com/jokes/random");
        try {
            String json = request.sendGETRequest().getResponse();
            JSONObject obj = new JSONObject(json);
            Logger.debug(obj.getJSONObject("value").getString("joke"));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public String getDescription() {
        return "Sends some (funny) jokes.";
    }

    @Override
    public String getUsage() {
        return "joke";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
