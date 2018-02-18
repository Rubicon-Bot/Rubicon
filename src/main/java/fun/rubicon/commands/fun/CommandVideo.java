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
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Date;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2018
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.fun
 */
public class CommandVideo extends CommandHandler {
    public CommandVideo() {
        super(new String[]{"video", "ytsearch", "youtube"}, CommandCategory.FUN, new PermissionRequirements("command.video", false, true), "Search for a YT Video and get Information about it.", "<Search-Query>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        String[] args = invocation.getArgs();

        if (args.length < 1)
            createHelpMessage(invocation);

        String query = invocation.getMessage().getContentDisplay().replace(invocation.getPrefix() + invocation.getCommandInvocation(), "");
        HttpRequest request = new HttpRequest("https://www.googleapis.com/youtube/v3/search");
        request.addParameter("type", "video");
        request.addParameter("q", query);
        request.addParameter("part", "snippet");
        request.addParameter("key", Info.GOOGLE_TOKEN);
        try {
            RequestResponse response = request.sendGETRequest();
            JSONObject json = (JSONObject) new JSONParser().parse(response.getResponse());
            JSONArray data = (JSONArray) json.get("items");
            JSONObject snippet = (JSONObject) ((JSONObject) data.get(0)).get("snippet");
            JSONObject id = (JSONObject) ((JSONObject) data.get(0)).get("id");
            JSONObject thumbnails = (JSONObject) snippet.get("thumbnails");
            EmbedBuilder message = new EmbedBuilder()
                    .setColor(Colors.COLOR_PRIMARY)
                    .setTitle((String) snippet.get("title"), "https://youtu.be/" + id.get("videoId"))
                    .setAuthor(invocation.getAuthor().getName(), null, invocation.getAuthor().getAvatarUrl())
                    .setTimestamp(new Date().toInstant())
                    .setThumbnail((String) ((JSONObject) thumbnails.get("default")).get("url"))
                    .addField("Video Description", (String) snippet.get("description"), false)
                    .addField("Channel Name", (String) snippet.get("channelTitle"), true);

            invocation.getTextChannel().sendMessage(message.build()).queue();
        } catch (Exception e) {
            e.printStackTrace();
            return EmbedUtil.message(EmbedUtil.error("Error!", "Found no Video."));
        }
        return null;
    }
}
