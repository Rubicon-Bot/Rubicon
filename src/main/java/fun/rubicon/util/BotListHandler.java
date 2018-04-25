/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import fun.rubicon.RubiconBot;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.io.IOException;

public class BotListHandler {

    public static void postStats(boolean silent) {
        // check if bot has already been initialized
        if (RubiconBot.getShardManager() == null) {
            Logger.warning("No Shardmanager found! Terminating all Stats Poster.");
            return;
        }

        if (!RubiconBot.getConfiguration().getString("discord_pw_token").isEmpty())
            postDBL(silent);
        else Logger.warning("No discordbots.org Token found! Skipping Stats Posting.");

        if (!RubiconBot.getConfiguration().getString("dbl_token").isEmpty())
            postDPW(silent);
        else Logger.warning("No bots.discord.pw Token found! Skipping Stats Posting.");

        if (!RubiconBot.getConfiguration().getString("dbl_token").isEmpty())
            postBDF(silent);
        else Logger.warning("No botsfordiscord.com Token found! Skipping Stats Posting.");


    }

    private static void postBDF(boolean silent) {
        JSONObject json = new JSONObject().put("server_count", RubiconBot.getShardManager().getGuilds().size());
        RequestBody bfdbody = RequestBody.create(MediaType.parse("application/json"), json.toString());
        Request bdfreq = new Request.Builder()
                .addHeader("Authorization", RubiconBot.getConfiguration().getString("botsfordiscordtoken"))
                .url("https://botsfordiscord.com/api/v1/bots/" + RubiconBot.getShardManager().getApplicationInfo().getJDA().getSelfUser().getId())
                .post(bfdbody)
                .build();
        try {
            new OkHttpClient().newCall(bdfreq).execute().close();
        } catch (IOException e) {
            if (!silent)
                Logger.error(e);
        }
    }

    private static void postDPW(boolean silent) {
        JSONObject dpwBody = new JSONObject().put("server_count", RubiconBot.getShardManager().getGuilds().size());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), dpwBody.toString());
        //Post stats to bots.discord.pw
        Request req = new Request.Builder()
                .url("https://bots.discord.pw/api/bots/" + RubiconBot.getSelfUser().getId() + "/stats")
                .addHeader("Authorization", RubiconBot.getConfiguration().getString("discord_pw_token"))
                .post(body)
                .build();

        try {
            new OkHttpClient().newCall(req).execute().close();
        } catch (IOException e) {
            if (!silent)
                Logger.error(e);
        }
    }

    private static void postDBL(boolean silent) {
        JSONObject object = new JSONObject().put("server_count", RubiconBot.getShardManager().getGuilds().size());

        Request request = new Request.Builder()
                .url("https://discordbots.org/api/bots/" + RubiconBot.getSelfUser().getId() + "/stats")
                .addHeader("Authorization", RubiconBot.getConfiguration().getString("dbl_token"))
                .put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object.toString()))
                .build();

        try {
            new OkHttpClient().newCall(request).execute().close();
        } catch (IOException e) {
            if (!silent)
                Logger.error(e);
        }


    }


}
