package fun.rubicon.commands.general;

import de.foryasee.httprequest.HttpRequestBuilder;
import de.foryasee.httprequest.RequestHeader;
import de.foryasee.httprequest.RequestResponse;
import de.foryasee.httprequest.RequestType;
import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.DateUtil;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */
public class CommandYTSearch extends CommandHandler {

    public CommandYTSearch() {
        super(new String[]{"ytsearch"}, CommandCategory.GENERAL, new PermissionRequirements("search", false, true), "Search something", "<Search-Term>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length < 1)
            return createHelpMessage(invocation);

        String query = invocation.getArgsString();
        HttpRequestBuilder builder = new HttpRequestBuilder("https://www.googleapis.com/youtube/v3/search", RequestType.GET)
                .setRequestHeader(new RequestHeader().addField("User-Agent", "RubiconBot (https://github.com/Rubicon-Bot/Rubicon)"))
                .addParameter("type", "video")
                .addParameter("q", query)
                .addParameter("part", "snippet")
                .addParameter("key", RubiconBot.getConfiguration().getString("google_token"));
        try {
            RequestResponse requestResponse = builder.sendRequest();
            if (requestResponse.getResponseCode() != 200)
                return message(EmbedUtil.error(invocation.translate("command.ytsearch.nfound"), invocation.translate("command.ytsearch.nfound.description")));
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(requestResponse.getResponseMessage());
            JSONArray data = (JSONArray) jsonObject.get("items");
            JSONObject snippet = (JSONObject) ((JSONObject) data.get(0)).get("snippet");
            JSONObject id = (JSONObject) ((JSONObject) data.get(0)).get("id");
            JSONObject thumbnails = (JSONObject) snippet.get("thumbnails");
            Date date = Date.from(Instant.parse((CharSequence) snippet.get("publishedAt")));
            String formated = DateUtil.formatDate(date, invocation.translate("date.format"));
            EmbedBuilder message = new EmbedBuilder()
                    .setColor(Colors.COLOR_PRIMARY)
                    .setTitle((String) snippet.get("title"), "https://youtu.be/" + id.get("videoId"))
                    .setAuthor(invocation.getAuthor().getName(), null, invocation.getAuthor().getAvatarUrl())
                    .setFooter(invocation.translate("command.ytsearch.result.footer") + formated, null)
                    .setThumbnail((String) ((JSONObject) thumbnails.get("default")).get("url"))
                    .addField(invocation.translate("command.ytsearch.result.description"), (String) snippet.get("description"), false)
                    .addField(invocation.translate("command.ytsearch.result.title"), (String) snippet.get("channelTitle"), true);
            return message(message);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return message(EmbedUtil.error(invocation.translate("command.ytsearch.nfound"), invocation.translate("command.ytsearch.nfound.description")));
        } catch (IndexOutOfBoundsException ignored) {
            return message(EmbedUtil.error(invocation.translate("command.ytsearch.nfound"), invocation.translate("command.ytsearch.nfound.description")));
        }
    }

}
