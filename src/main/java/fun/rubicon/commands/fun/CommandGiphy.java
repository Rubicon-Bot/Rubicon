package fun.rubicon.commands.fun;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.entities.Message;
import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import static fun.rubicon.util.EmbedUtil.error;
import static fun.rubicon.util.EmbedUtil.message;

/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */
public class CommandGiphy extends CommandHandler {

    public CommandGiphy() {
        super(new String[]{"gif", "giphy"}, CommandCategory.FUN, new PermissionRequirements("gif", false, true), "Search a gif on Giphy and sends it in the channel", "<keywords>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        String[] args = invocation.getArgs();
        if (args.length < 1) {
            return createHelpMessage();
        }
        String query = invocation.getMessage().getContentDisplay().replace(invocation.getPrefix() + invocation.getCommandInvocation(), "");
        HttpUrl.Builder httpBuider = HttpUrl.parse("https://api.giphy.com/v1/gifs/search").newBuilder()
                .addQueryParameter("api_key", RubiconBot.getConfiguration().getString("gif_token"))
                .addQueryParameter("q", query);

        Request request = new Request.Builder()
                .url(httpBuider.build())
                .build();
        try {
            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    SafeMessage.sendMessage(invocation.getTextChannel(), error("Error!", "Found no gif.").build());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        JSONObject json = (JSONObject) new JSONParser().parse(response.body().string());
                        JSONArray data = (JSONArray) json.get("data");
                        if (data.isEmpty()) {
                            SafeMessage.sendMessage(invocation.getTextChannel(), error("Error!", "Found no gif.").build());
                            return;
                        }
                        SafeMessage.sendMessage(invocation.getTextChannel(), (String) ((JSONObject) data.get(0)).get("url"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        SafeMessage.sendMessage(invocation.getTextChannel(), error("Error!", "Found no gif.").build());
                    }
                }
            });
        } catch (Exception e) {
            return message(error("Error!", "Found no gif."));
        }
        return null;
    }

}