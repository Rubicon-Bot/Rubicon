/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import com.github.natanbc.discordbotsapi.DiscordBotsAPI;
import com.github.natanbc.discordbotsapi.PostingException;
import fun.rubicon.RubiconBot;
import net.dv8tion.jda.core.JDA;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Utility class that posts statistical bot information to the https://discordbots.org/ bot-list.
 *
 * @author DRSchlaubi, tr808axm
 */
public class DBLUtil {
    private static DiscordBotsAPI discordBotsOrgAPI;

    /**
     * Posts bot statistics to https://discordbots.org/ and suppresses potential {@link PostingException} that occur
     * when the token is invalid.
     *
     * @param jda unnecessary as it will be statically retrieved from RubiconBot.
     * @deprecated Use postStats() instead.
     */
    @Deprecated
    public static void postStats(JDA jda) {
        postStats(false);
    }

    /**
     * Posts bot statistics to https://discordbots.org/.
     *
     * @param silent should this print a warning if the token is invalid?
     */
    public static void postStats(boolean silent) {
        // check if bot has already been initialized
        if (RubiconBot.getJDA() == null) {
            Logger.warning("Could not post discordbots.org stats as the bot has not been initialized yet.");
            return;
        }

        // init api if necessary
        if (discordBotsOrgAPI == null)
            discordBotsOrgAPI = new DiscordBotsAPI(Info.DBL_TOKEN);

        try {
            // post stats to discordbots.org
            discordBotsOrgAPI.postStats(new int[]{RubiconBot.getJDA().getGuilds().size()});
        } catch (PostingException e) {
            // suppress warning if silent
            if (!silent)
                Logger.warning("Could not post discordbots.org stats: " + e.getMessage());
        }

        JSONObject json = new JSONObject();

        json.put("server_count", RubiconBot.getJDA().getGuilds().size());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());
        //Post stats to bots.discord.pw
        Request req = new Request.Builder()
                .url("https://bots.discord.pw/api/bots/" + RubiconBot.getJDA().getSelfUser().getId() + "/stats")
                .addHeader("Authorization", Info.DISCORD_PW_TOKEN)
                .post(body)
                .build();
        Response res = null;
        try {
            res = new OkHttpClient().newCall(req).execute();
        } catch (IOException e) {
            if(!silent)
            Logger.error(e);
        }
        res.close();


    }
}
