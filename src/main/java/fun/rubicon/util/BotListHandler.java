/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import fun.rubicon.RubiconBot;
import net.dv8tion.jda.core.entities.User;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class BotListHandler {

    private static final JSONObject DEFAULT_OBJECT = new JSONObject().put("shard_id", RubiconBot.getConfiguration().getString("shard_id")).put("shard_count", RubiconBot.getConfiguration().getString("shard_count")).put("server_count", RubiconBot.getShardManager().getGuilds().size());


    public static void postStats(boolean silent) {
        // check if bot has already been initialized
        if (RubiconBot.getShardManager() == null) {
            Logger.error("No Shardmanager found! Terminating all Stats Poster.");
            return;
        }

        if (!RubiconBot.getConfiguration().getString("dbl_token").isEmpty())
            postDBL(silent);
        else Logger.error("No discordbots.org Token found! Skipping Stats Posting.");
        if (!RubiconBot.getConfiguration().getString("discord_pw_token").isEmpty())
            postDPW(silent);
        else Logger.error("No bots.discord.pw Token found! Skipping Stats Posting.");

        if (!RubiconBot.getConfiguration().getString("rubiconfun_token").isEmpty())
            postRubiconFunGuildCount(silent);
        else Logger.error("No rubicon.fun Token found! Skipping Stats posting.");
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
                e.printStackTrace();
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
                e.printStackTrace();
        }
    }

    private static void postDPW(boolean silent) {
        post(silent, "https://bots.discord.pw/api/bots/" + RubiconBot.getSelfUser().getId() + "/stats", RequestBody.create(MediaType.parse("application/json"), DEFAULT_OBJECT.toString()), RubiconBot.getConfiguration().getString("discord_pw_token"));
    }

    private static void postDBL(boolean silent) {
        System.out.println(DEFAULT_OBJECT.toString());
        post(silent, "https://discordbots.org/api/bots/" + RubiconBot.getSelfUser().getId() + "/stats" ,RequestBody.create(MediaType.parse("application/json; charset=utf-8"), DEFAULT_OBJECT.toString()), RubiconBot.getConfiguration().getString("dbl_token"));
    }

    //TODO: Add BFD stats posting when BFD adds shard support
    //TODO: Add BLS stats posting when BLS adds shard support, that is not such shit like the actual one


    private static void post(boolean silent, String url, RequestBody body, String token){
        System.out.println(url);
        Request req = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token)
                .post(body)
                .build();

        try {
            Response res = new OkHttpClient().newCall(req).execute();
            System.out.println("API RETURNED: " +  res.body().string());
            res.close();
        } catch (IOException e){
            System.out.println("HEY");
            if(!silent)
                Logger.error("An error occured while posting stats to: " + url + e);
        }
    }
}
