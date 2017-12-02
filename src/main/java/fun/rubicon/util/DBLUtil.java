package fun.rubicon.util;

import com.github.natanbc.discordbotsapi.DiscordBotsAPI;
import net.dv8tion.jda.core.JDA;

/**
 * Rubicon Discord bot
 *
 * @author Michael Rittmeister / Schlaubi
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.util
 */
public class DBLUtil {

    static DiscordBotsAPI dbl = new DiscordBotsAPI(Info.DBL_TOKEN);

    /**
     *
     * @param jda
     */
    public static void postStats(JDA jda){
        dbl.postStats(new int[]{jda.getGuilds().size()});
    }
}
