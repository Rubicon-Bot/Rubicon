/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import fun.rubicon.RubiconBot;
import net.dv8tion.jda.core.entities.User;
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

        if (!RubiconBot.getConfiguration().getString("rubiconfun_token").isEmpty())
            postRubiconFunGuildCount(silent);
        else Logger.warning("No rubicon.fun Token found! Skipping Stats posting.");
    }

    private static void postRubiconFunGuildCount(boolean silent) {
        int guildCount = RubiconBot.getShardManager().getGuilds().size();
        try {
            new OkHttpClient().newCall(new Request.Builder()
                    .url("https://rubicon.fun/api/v1/?action=updateGuildCount" +
                            "&token=" + RubiconBot.getConfiguration().getString("rubiconfun_token") +
                            "&value=" + guildCount)
                    .get()
                    .build()).execute().close();
        } catch (IOException e) {
            if (!silent)
                Logger.error(e);
        }
    }

    public static void postRubiconFunUserCounts(boolean silent) {
        int totalUserCount = RubiconBot.getShardManager().getUsers().size();
        long botUserCount = RubiconBot.getShardManager().getUsers().stream().filter(User::isBot).count();
        long actualUserCount = totalUserCount - botUserCount;
        try {
            new OkHttpClient().newCall(new Request.Builder()
                    .url("https://rubicon.fun/api/v1/?action=updateUserCount" +
                            "&token=" + RubiconBot.getConfiguration().getString("rubiconfun_token") +
                            "&value=" + actualUserCount)
                    .get()
                    .build()).execute().close();
            new OkHttpClient().newCall(new Request.Builder()
                    .url("https://rubicon.fun/api/v1/?action=updateBotCount" +
                            "&token=" + RubiconBot.getConfiguration().getString("rubiconfun_token") +
                            "&value=" + botUserCount)
                    .get()
                    .build()).execute().close();
        } catch (IOException e) {
            if (!silent)
                Logger.error(e);
        }
    }

    private static void postBDF(boolean silent) {
        try {
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
        } catch (NullPointerException e) {
            Logger.error("BotListHandler - BDF: null");
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
