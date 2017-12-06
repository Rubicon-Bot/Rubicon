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

/**
 * Utility class that posts statistical bot information to the https://discordbots.org/ bot-list.
 * @author DRSchlaubi, tr808axm
 */
public class DBLUtil {
    private static DiscordBotsAPI discordBotsOrgAPI;

    /**
     * Posts bot statistics to https://discordbots.org/ and suppresses potential {@link PostingException} that occur
     * when the token is invalid.
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
            Logger.error("WARNING: Could not post discordbots.org stats as the bot has not been initialized yet.");
            return;
        }

        // init api if necessary
        if (discordBotsOrgAPI == null)
            discordBotsOrgAPI = new DiscordBotsAPI(Info.DBL_TOKEN);

        try {
            // post stats
            discordBotsOrgAPI.postStats(new int[]{RubiconBot.getJDA().getGuilds().size()});
        } catch (PostingException e) {
            // suppress warning if silent
            if (!silent)
                Logger.error("WARNING: Could not post discordbots.org stats: " + e.getMessage());
        }
    }
}
